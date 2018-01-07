package File;

import Core.Picture;
import Image.ImageUtils;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

public class ImageFileLoader implements FileLoader {

    @Override
    public LinkedList<Picture> loadImages(File[] files) {
        LinkedList<Picture> pictures = new LinkedList<>();
        for (File file : Arrays.asList(files)) {
            Image image = ImageUtils.fileToImage(file);
            String[] fileNameWithExtension = file.getName().split("[.]");
            String fileName = fileNameWithExtension[0];
            pictures.add(new Picture(image, fileName));
        }
        return pictures;
    }
}
