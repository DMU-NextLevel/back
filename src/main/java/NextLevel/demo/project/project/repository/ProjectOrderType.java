package NextLevel.demo.project.project.repository;

import NextLevel.demo.project.project.entity.QProjectEntity;
import NextLevel.demo.project.view.QProjectViewEntity;
import NextLevel.demo.user.entity.QLikeEntity;
import com.querydsl.core.types.dsl.EntityPathBase;

import java.util.Arrays;

public enum ProjectOrderType {
    RECOMMEND(), // like 횟수
    COMPLETION(),
    USER(), // 펀딩 횟수
    CREATED() ,
    EXPIRED(),
    VIEW()
    ;

//    public Class<? extends EntityPathBase> type;
//
//    <T extends EntityPathBase> ProjectOrderType (Class<T> type) {
//        this.type = type;
//    }

    public static <T> ProjectOrderType getType(String typeName) {
        ProjectOrderType type = Arrays.stream(ProjectOrderType.values()).filter(t -> t.name().equals(typeName)).findFirst().orElse(ProjectOrderType.RECOMMEND);
        return type;
    }

}
