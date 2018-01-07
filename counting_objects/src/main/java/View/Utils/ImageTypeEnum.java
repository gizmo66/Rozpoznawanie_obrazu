package View.Utils;

import File.FileUtils;
import lombok.Getter;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public enum ImageTypeEnum {

    JPEG("jpeg", "JPEG Image", FileUtils.createImageIcon("../images/jpgIcon.gif"), Arrays.asList("jpg", "jpeg")),
    PNG("png", "PNG Image", FileUtils.createImageIcon("../images/pngIcon.png"), Collections.singletonList("png")),
    BMP("bmp", "BMP Image", FileUtils.createImageIcon("../images/jpgIcon.gif"), Collections.singletonList
            ("bmp"));

    private final String type;
    private final String description;
    private final Icon icon;
    private final List<String> extensions;

    ImageTypeEnum(String type, String description, Icon icon, List<String> extensions) {
        this.type = type;
        this.description = description;
        this.icon = icon;
        this.extensions = extensions;
    }
}
