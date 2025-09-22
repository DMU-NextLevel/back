package NextLevel.demo.img;

import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.repository.ImgRepository;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.img.service.ImgServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.internal.matchers.CapturesArguments;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class ImgServiceTest {

    @Mock
    private ImgRepository imgRepository;

    @InjectMocks
    @Spy
    private ImgServiceImpl imgService;

    private ImgEntity mockImg;

    @BeforeEach
    public void setup(){
        mockImg = new ImgEntity("uri");
        Mockito.lenient().when(imgRepository.getImgCount(Mockito.anyInt(), Mockito.anyString())).thenReturn(1L);
        Mockito.lenient().when(imgRepository.save(Mockito.any(ImgEntity.class))).then(
                (joinPoint)->{
                    ImgEntity img = (ImgEntity) joinPoint.getArguments()[0];
                    img.setId(1L);
                    return img;
                }
        );

        try {
            Field field = ImgServiceImpl.class.getDeclaredField("IMG_DEFAULT_PATH");
            field.setAccessible(true);
            ImgServiceImpl.class.getDeclaredMethod("addImgNumber", String.class).setAccessible(true);
            field.set(imgService, "/img/");
        } catch (Exception e) {;}
    }

    @Test
    public void addImgNumberTest() throws Exception {
        Field maxImgLen = ImgServiceImpl.class.getDeclaredField("MAX_IMG_LEN");
        maxImgLen.setAccessible(true);
        maxImgLen.set(imgService, 10);
        Method addImgNumber = ImgServiceImpl.class.getDeclaredMethod("addImgNumber", String.class);
        addImgNumber.setAccessible(true);
        String fileName = "test";
        String nullName = "";
        String longName = "abcdefgijklnmopqrstuvwxyz.png";

        Assertions.assertAll(
                ()->Assertions.assertTrue(( (String)addImgNumber.invoke(imgService, fileName) ).contains(fileName), "check file name"),
                ()->Assertions.assertTrue(( (String)addImgNumber.invoke(imgService, fileName+".png") ).contains(fileName), "check file name"),
                ()->Assertions.assertDoesNotThrow(()->addImgNumber.invoke(imgService, nullName), "when no file name"),
                ()->Assertions.assertTrue(((String)addImgNumber.invoke(imgService, longName)).length() <= 20, "when too long file name")
        );
    }

//    @Test
//    public void saveImgTest() throws Exception {
//        Field maxImgLen = ImgServiceImpl.class.getDeclaredField("MAX_IMG_LEN");
//        maxImgLen.setAccessible(true);
//        maxImgLen.set(imgService, 20);
//        String fileName = "fileName";
//        byte[] imgData = "img byte".getBytes();
//        MultipartFile imgFile = new MockMultipartFile(fileName, fileName, "content type", imgData);
//        Mockito.mockStatic(Files.class).when(()->Files.write(Mockito.any(), Mockito.any(byte[].class))).thenReturn(Paths.get("uri"));
//        Mockito.mockStatic(Paths.class).when(()->Paths.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).then(
//                (joinPoint)->{
//                    String defaultPath = (String) joinPoint.getArguments()[1];
//                    String imgPath = (String) joinPoint.getArguments()[2];
//                    return Paths.get(defaultPath + imgPath);
//                }
//        );
//
//        ArrayList<Path> paths = new ArrayList<>();
//        ImgEntity imgEntity = imgService.saveImg(imgFile, paths);
//
//        Assertions.assertAll(
//                ()->Assertions.assertTrue(imgEntity != null,"return img entity is not null"),
//                ()->Assertions.assertTrue(imgEntity.getUri().contains(fileName), "img entity contains uri"),
//                ()->Assertions.assertTrue(paths.size() == 1, "check ArrayList<Path>")
//        );
//    }

//    @Test
//    // testImg 폴더에 실제 저장! (resources/static/testImg 폴더 반듯이 필요!!)
//    public void socialImg() throws Exception {
//        Field maxImgLen = ImgServiceImpl.class.getDeclaredField("MAX_IMG_LEN");
//        maxImgLen.setAccessible(true);
//        maxImgLen.set(imgService, 20);
//        Field imgDefaultPath = ImgServiceImpl.class.getDeclaredField("IMG_DEFAULT_PATH");
//        imgDefaultPath.setAccessible(true);
//        imgDefaultPath.set(imgService, "/src/main/resources/static/testImg/");
//        String imgURL = "https://s.pstatic.net/shopping.phinf/20250829_19/8c4b6691-aa7d-4eef-a043-135b8150f9ec.jpg";
//        imgService.saveSocialImg(imgURL);
//    }
}
