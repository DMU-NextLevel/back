package NextLevel.demo.project.batch;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectBatchService {

    private final JobLauncher jobLauncher;
    private final Job projectStatusJob;

    @Scheduled(cron = "")
    public void runProjectStatusJob() {
        try{
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(projectStatusJob, jobParameters);
            log.info("Project status job finished");
        } catch (Exception e){
            e.printStackTrace();
            throw new CustomException(ErrorCode.SIBAL_WHAT_IS_IT, e.getMessage());
        }
    }

}
