package NextLevel.demo.project.batch;

import NextLevel.demo.project.ProjectStatus;
import NextLevel.demo.project.project.entity.ProjectEntity;
import jakarta.persistence.EntityManagerFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ProjectStatusBatchService {

    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public JpaPagingItemReader<ProjectEntity> projectReader() {
        return new JpaPagingItemReaderBuilder<ProjectEntity>()
                .name("expiredBoardReader")
                .queryString(
                        """
                            select p 
                            from ProjectEntity p
                            where p.projectStatus = :projectStatus and p.expiredAt <= :date
                        """)
                .parameterValues(Map.of(
                        "projectStatus", ProjectStatus.PROGRESS,
                        "date", LocalDate.now()
                ))
                .pageSize(10)
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    @JobScope
    public Step projectSelectStep(
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("checkProjectStatus", jobRepository)
                .<ProjectEntity, ProjectEntity> chunk(10, transactionManager)
                .reader(projectReader())
                .processor((p)->p) // nothing to do
                .writer(projectChunk->{
                    StepExecution stepExecution = StepSynchronizationManager.getContext().getStepExecution();
                    List<ProjectSerializableDto> dtoList = projectChunk.getItems().stream().map(ProjectSerializableDto::of).toList();
                    stepExecution.getJobExecution().getExecutionContext().put("projectDtoList", dtoList);
                })
                .build();
    }

    @Bean
    @JobScope
    public JdbcCursorItemReader<ProjectAndFundingPriceDto> projectFundingPriceReader(
            @Value("#{jobExecutionContext['projectDtoList']}") List<ProjectSerializableDto> projectChunk
    ) {
//        StepExecution stepExecution = StepSynchronizationManager.getContext().getStepExecution();
//        Chunk<ProjectEntity> projectChunk = (Chunk<ProjectEntity>)stepExecution.getJobExecution().getExecutionContext().get("projectChunk");
        Map<Long, ProjectSerializableDto> projectMap = projectChunk.stream().collect(Collectors.toMap(ProjectSerializableDto::getProjectId, p -> p));
        return new JdbcCursorItemReaderBuilder<ProjectAndFundingPriceDto>()
                .name("projectFundingPriceReader")
                .sql("""
                     select 
                       p.id as projectId,
                       COALESCE( (select sum(ff.price) from FreeFundingEntity ff where ff.project.id = p.id), 0)\s
                        +
                        COALESCE( (select sum(of.count * of.option.price) from OptionFundingEntity of where of.option.project.id = p.id), 0)
                        as fundingPrice
                     from ProjectEntity p
                     where p.id in :projectIdList
                     """)
                .queryArguments(projectChunk.stream().map(ProjectSerializableDto::getProjectId).toList())
                .rowMapper(new RowMapper() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        long projectId = rs.getLong("projectId");
                        int fundingPrice = rs.getInt("fundingPrice");
                        return new ProjectAndFundingPriceDto(projectMap.get(projectId), fundingPrice);
                    }
                })
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public ItemProcessor<ProjectAndFundingPriceDto, ProjectAndFundingPriceDto> projectProcessor() {
        return (dto)-> {
            ProjectStatus status = dto.getFundingPrice() >= dto.getProjectSerializableDto().getProjectGoal() ? ProjectStatus.SUCCESS : ProjectStatus.FAIL;
            dto.setProjectStatus(status);
            return dto;
        };
    }

    @Bean
    public JdbcBatchItemWriter<ProjectAndFundingPriceDto> projectWriter() {
        return new JdbcBatchItemWriterBuilder<ProjectAndFundingPriceDto>()
                .sql("""
                        update project
                        set projectStatus = :projectStatus
                        where project.id = :projectId
                        """)
                .dataSource(dataSource)
                .itemPreparedStatementSetter(new ItemPreparedStatementSetter<ProjectAndFundingPriceDto>() {
                    @Override
                    public void setValues(ProjectAndFundingPriceDto projectDto, PreparedStatement ps) throws SQLException {
                        ps.setObject(1, projectDto.getProjectStatus());
                        ps.setLong(1, projectDto.getProjectSerializableDto().getProjectId());
                    }
                })
                .beanMapped()
                .build();
    }

    @Bean
    public Step expiredProjectStep(
            PlatformTransactionManager transactionManager,
            JdbcCursorItemReader<ProjectAndFundingPriceDto> projectFundingPriceReader
    ) {
        return new StepBuilder("expiredProjectStep", jobRepository)
                .<ProjectAndFundingPriceDto, ProjectAndFundingPriceDto> chunk(10, transactionManager)
                .reader(projectFundingPriceReader)
                .processor(projectProcessor())
                .writer(projectWriter())
                .build();
    }

    @Bean
    public Job projectStatusJob(
            JobRepository jobRepository,
            Step projectSelectStep,
            Step expiredProjectStep
    ) {
        return new JobBuilder("projectStatusJob", jobRepository)
                .start(projectSelectStep)
                .next(expiredProjectStep)
                .build();
    }
}
