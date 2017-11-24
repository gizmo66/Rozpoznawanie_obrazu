package Core;

import View.ImageTypeEnum;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class FileUtils {

    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = FileUtils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static Optional<ImageTypeEnum> getImageType(File file) {
        return Arrays.stream(ImageTypeEnum.values()).filter(it -> it.getExtensions()
                .contains(FilenameUtils.getExtension(file.getName()))).findFirst();
    }
}
