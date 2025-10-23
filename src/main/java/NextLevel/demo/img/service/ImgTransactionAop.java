package NextLevel.demo.img.service;

import java.nio.file.Path;
import java.util.ArrayList;

import NextLevel.demo.img.MustLastParameterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ImgTransactionAop {
    private final ImgServiceImpl imgService;

    /*
        joinPoint :: args :: last argument is must ImgPath.class
        success : delete all deleted Img File
        fail : delete add saved Img File
     */

    @Around("@annotation(imgTransaction)")
    public Object around(ProceedingJoinPoint joinPoint, ImgTransaction imgTransaction) throws Throwable {
        ImgPath path = new ImgPath();
        log.info("img transaction start");
        try{
            Object[] args = joinPoint.getArgs();
            Object lastParameter = args[args.length-1];

            // 추후 수정 필요! (귀찮!)
//            if(!(lastParameter instanceof ImgPath))
//                throw new MustLastParameterException("last parameter must be of type ImgPath.class");

            args[args.length-1] = path;

            Object result = joinPoint.proceed(args);
            imgService.deleteImgFile(path.getDeleted());
            return result;
        } catch (Exception e){
            log.info("img transaction :: error :: roll back all img files");
            imgService.deleteImgFile(path.getSaved());
            throw e;
        }
    }
}
