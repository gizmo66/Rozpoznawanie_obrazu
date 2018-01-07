package Core;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class Picture {

    private Image image;
    private String originalFileName;

    public Picture(Image image, String originalFileName) {
        this.image = image;
        this.originalFileName = originalFileName;
    }
}
