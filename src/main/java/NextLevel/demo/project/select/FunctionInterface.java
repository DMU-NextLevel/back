package NextLevel.demo.project.select;
import com.querydsl.core.types.dsl.EntityPathBase;

@FunctionalInterface
public interface FunctionInterface<RT, T extends EntityPathBase> {

    RT function(T entity);

}
