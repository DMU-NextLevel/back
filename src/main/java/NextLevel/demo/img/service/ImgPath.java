package NextLevel.demo.img.service;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ImgPath {

    private List<Path> saved;
    private List<Path> deleted;

    public void save(Path path){
        saved.add(path);
    }

    public void delete(Path path){
        deleted.add(path);
    }

    public ImgPath(){
        saved = new ArrayList<>();
        deleted = new ArrayList<>();
    }

}
