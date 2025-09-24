package NextLevel.demo.img;

import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.service.ImgPath;
import NextLevel.demo.img.service.ImgService;
import NextLevel.demo.img.service.ImgTransaction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class ImgTest {

    @Autowired
    private ImgService imgService;

    private MultipartFile mockImgFile;
    private String imgName = "imgName";
    private byte[] imgData = "imgData".getBytes();

    @BeforeEach
    public void setUp() {
        // db 초기화
        Mockito.mockStatic(Files.class).when(()->Files.write(Mockito.any(), Mockito.any(byte[].class))).thenReturn(Paths.get("uri"));
        mockImgFile = new MockMultipartFile(imgName, imgName, "content type", imgData);
    }

    @Test
    @ImgTransaction
    public void saveImgTest(ImgPath imgPath) {
        ImgEntity img = imgService.saveImg(mockImgFile, imgPath);
        Assertions.assertThat(img.getUri()).isEqualTo(imgName);
    }

}
