package Core;

import lombok.Getter;

import java.awt.*;

@Getter
public class Picture {

    private final Image image;
    private final String type;

    Picture(Image image, String type) {
        this.image = image;
        this.type = type;
    }

    public Image getImage()
    {
        return image;
    }

    public String getType() {
        return type;
    }
}
