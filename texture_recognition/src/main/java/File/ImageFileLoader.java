package File;

import Extraction.Picture;
import Image.ImageUtils;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

public class ImageFileLoader implements FileLoader {

    @Override
    public LinkedList<Picture> loadTrainingDataSet(File file) {
        return loadTrainingDataSet(new File[]{ file });
    }

    @Override
    public LinkedList<Picture> loadTrainingDataSet(File[] files) {
        LinkedList<Picture> pictures = new LinkedList<>();
        for(File file : Arrays.asList(files)) {
            Image image = ImageUtils.fileToImage(file);
            String fileName = file.getName().split("[.]")[0];
            String label = fileName.replaceAll("\\P{L}+", "");
            pictures.add(new Picture(image, label));
        }
        return pictures;
    }
}
