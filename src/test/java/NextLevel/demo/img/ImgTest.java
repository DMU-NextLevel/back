//package NextLevel.demo.img;
//
//import NextLevel.demo.img.entity.ImgEntity;
//import NextLevel.demo.img.repository.ImgRepository;
//import NextLevel.demo.img.service.ImgService;
//import NextLevel.demo.img.service.ImgServiceImpl;
//import NextLevel.demo.img.service.ImgTransaction;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@ExtendWith(MockitoExtension.class)
//public class ImgTest {
//
//    private ImgService imgService;
//    @MockitoSpyBean
//    private ImgRepository imgRepository;
//
//    private MultipartFile mockImgFile;
//    private String imgName = "imgName";
//    private byte[] imgData = "imgData".getBytes();
//
//    @BeforeEach
//    public void setUp() {
//        Mockito.lenient().when(imgRepository.getImgCount(Mockito.anyInt(), Mockito.anyString())).thenReturn(1L); // img count return 1;
//        imgService = new ImgServiceImpl(imgRepository);
//        // db 초기화
//        Mockito.mockStatic(Files.class).when(()->Files.write(Mockito.any(), Mockito.any(byte[].class))).thenReturn(Paths.get("uri"));
//        mockImgFile = new MockMultipartFile(imgName, imgName, "content type", imgData);
//    }
//
//    @Transactional
//    @ImgTransaction
//    public ImgEntity testImgFail(MultipartFile imgFile, RuntimeException e, ImgPath imgPath) {
//        ImgEntity img = imgService.saveImg(imgFile, imgPath);
//        if(e != null)
//            throw e;
//        return img;
//    }
//
//    @Test
//    public void saveImgTest() {
//        ImgEntity img = testImgFail(mockImgFile, null, null);
//        // db에 저장이 되었는지 + 파일이 존재하는지
//        ImgEntity dbImg = imgRepository.findById(img.getId()).get();
//
//        Assertions.assertAll(
//                ()->Assertions.assertEquals(dbImg.getId(), img.getId(), "dbImg is not equal input img"),
//                ()->Assertions.assertEquals(dbImg.getUri(), img.getUri(), "dbImg not equal input img")
//        );
//    }
//
//}
