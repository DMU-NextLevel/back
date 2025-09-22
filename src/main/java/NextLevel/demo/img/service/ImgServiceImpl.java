package NextLevel.demo.img.service;

import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.img.entity.ImgEntity;
import NextLevel.demo.img.repository.ImgRepository;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImgServiceImpl implements ImgService {

    @Value("${img.default_dir}")
    private String IMG_DEFAULT_PATH;

    @Value("${img.max_len}")
    private int MAX_IMG_LEN;

    private final ImgRepository imgRepository;

    // @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImgEntity saveImg(MultipartFile imgFile, ImgPath imgPath) {
        if(imgFile == null || imgFile.isEmpty()) return null;
        try {
            byte[] bytes = imgFile.getBytes();
            String fileName = imgFile.getOriginalFilename();

            fileName = addImgNumber(fileName);

            Path path = getPath(fileName); // Paths.get(System.getProperty("user.dir") ,IMG_DEFAULT_PATH, fileName);

            Files.write(path, bytes);
            imgPath.save(path);

            ImgEntity saved = imgRepository.save(new ImgEntity(fileName));

            log.info("Saved image :" + saved.getUri() + " id :" + saved.getId());

            return saved;
        }catch (Exception e){
            e.printStackTrace();
            log.info("save img fail ");
            throw new CustomException(ErrorCode.ERROR_ON_SAVE_IMG);
        }
    }

    // img uri 변경 없이 진짜 파일 값만 덮어쓰기
    public ImgEntity updateImg(MultipartFile imgFile, ImgEntity oldImg, ImgPath imgPath) {
        if(oldImg == null){
            return saveImg(imgFile, imgPath);
        }

        try {
            Path path = getPath(oldImg.getUri()); // Paths.get(System.getProperty("user.dir"), IMG_DEFAULT_PATH, oldImg.getUri());

            Files.write(path, imgFile.getBytes());

            ImgEntity newImg = imgRepository.save(ImgEntity.builder().id(oldImg.getId()).uri(oldImg.getUri()).build());

            log.info("updated image :" + newImg.getUri() + " id :" + newImg.getId());

            return newImg;
        }catch (Exception e){
            e.printStackTrace();
            log.info("update img fail");
            throw new CustomException(ErrorCode.ERROR_ON_SAVE_IMG);
        }
    }

    // @Transactional
    public void deleteImg(ImgEntity img, ImgPath imgPath) {
        if(img == null)
            return;

        // Files.deleteIfExists();
        imgPath.delete(getPath(img.getUri()));
        imgRepository.deleteById(img.getId());
        log.info("deleted image id : " + img.getId());
    }

    public ImgEntity saveSocialImg(String imgURL) {
        if (imgURL == null || imgURL.isEmpty())
            return null;

        try {
            BufferedImage image = ImageIO.read(new URL(imgURL));

            if(image == null) {
                log.info("no supported img file");
                return null;
            }

            File file = new File(System.getProperty("user.dir") + IMG_DEFAULT_PATH + addImgNumber(imgURL));
            file.createNewFile();

            ImageIO.write(image, file.getName().substring(file.getName().indexOf('.') + 1), file);

            ImgEntity saved = imgRepository.save(new ImgEntity(file.getName()));

            return saved;
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.ERROR_ON_SAVE_IMG);
        }
    }

    private String addImgNumber(String imgUri) {
        if(imgUri == null || imgUri.isEmpty()){
            log.info("can not get img uri");
            imgUri = "no_img_name.png";
        }

        if(imgUri.lastIndexOf('/') != -1)
            imgUri = imgUri.substring(imgUri.lastIndexOf('/')+1);

        if(imgUri.length() > MAX_IMG_LEN)
            imgUri = imgUri.substring(imgUri.length()-MAX_IMG_LEN, imgUri.length());

        int lastIndex = imgUri.lastIndexOf(".");
        String extension = null, imgName = null;
        if(lastIndex == -1) {
            extension = ".img";
            imgName = imgUri;
        }else{
            extension = imgUri.substring(imgUri.lastIndexOf('.'));
            imgName = imgUri.substring(0, imgUri.lastIndexOf('.'));
        }

        Long imgCount = imgRepository.getImgCount(imgName.length()+1, imgName);

        return imgName + imgCount + extension;
    }

    public void deleteImgFile(List<Path> filePath) throws IOException {
        for(Path path : filePath){
            log.info("delete img file " + path);
            Files.deleteIfExists(path);
        }
    }

    private Path getPath(String uri) {
        return Paths.get(System.getProperty("user.dir") ,IMG_DEFAULT_PATH, uri);
    }
}
