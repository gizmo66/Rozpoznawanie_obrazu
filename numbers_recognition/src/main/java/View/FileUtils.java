package View;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

class FileUtils {

    static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = FileUtils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    static Optional<ImageTypeEnum> getImageType(File file) {
        return Arrays.stream(ImageTypeEnum.values()).filter(it -> it.getExtensions()
                .contains(getExtension(file))).findFirst();
    }

    private static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
