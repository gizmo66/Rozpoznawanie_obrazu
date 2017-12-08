package File;

import Extraction.Picture;
import Image.ImageUtils;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

public class ImageFileLoader implements FileLoader {

    @Override
    public LinkedList<Picture> loadTrainingDataSet(File[] files, boolean isTrainingData) {
        LinkedList<Picture> pictures = new LinkedList<>();
        for (File file : Arrays.asList(files)) {
            Image image = ImageUtils.fileToImage(file);
            String[] fileNameWithExtension = file.getName().split("[.]");
            String fileName = fileNameWithExtension[0];
            String label = fileName.replaceAll("\\P{L}+", "");
            String number = fileName.replace(label, "");
            Image labelImage = null;
            if (!isTrainingData) {
                String path = file.getAbsolutePath().replaceAll("(\\w+)[.]", "label" + number + ".");
                File labelImageFile = new File(path);
                if (labelImageFile.exists()) {
                    labelImage = ImageUtils.fileToImage(labelImageFile);
                }
            }
            pictures.add(new Picture(image, labelImage, label, fileName));
        }
        return pictures;
    }
}
