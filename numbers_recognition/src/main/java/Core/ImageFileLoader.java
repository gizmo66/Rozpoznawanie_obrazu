package Core;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ImageFileLoader implements FileLoader {

    @Override
    public List<Picture> loadTrainingDataSet(File file) {
        return loadTrainingDataSet(new File[]{ file });
    }

    @Override
    public LinkedList<Picture> loadTrainingDataSet(File[] files) {
        LinkedList<Picture> pictures = new LinkedList<>();
        for(File file : Arrays.asList(files)) {
            Image image = ImageUtils.fileToImage(file);
            pictures.add(new Picture(image, file.getName().split("[.]")[0].split("[_]")[0]));
        }
        return pictures;
    }
}
