package Core;

import Extraction.FeaturesExtractor;
import Extraction.FeaturesVector;
import View.FileChoosePanel;
import View.ImageRecognitionPanel;
import View.TrainingDataLoadingPanel;
import View.Window;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class ImageRecognizer {

    public static LinkedList<Picture> loadPictures = new LinkedList<>();

    public static void loadTrainingData(File[] files, FileChoosePanel fileChoosePanel, Window window) {
        if (files != null && files.length > 0) {
            boolean isMnist = init(files, fileChoosePanel, window);
            FeaturesVector featuresVector = FeaturesExtractor.extractFeaturesVector(loadPictures, isMnist);
            featuresVector.saveToFile();
            window.add(new TrainingDataLoadingPanel(loadPictures, window, isMnist));
        }
    }

    public static void initImageRecognition(File[] files, FileChoosePanel fileChoosePanel, Window window) {
        if (files != null && files.length > 0) {
            boolean isMnist = init(files, fileChoosePanel, window);

            ImageRecognitionPanel panel = new ImageRecognitionPanel(window, isMnist);
            window.add(panel);

            LinkedList<Picture> temp = new LinkedList<>();
            if (isMnist) {
                window.setLocation(20, 20);
                window.setSize(700, 700);
                for (int i = 0; i < 100; i++) {
                    Picture picture = loadPictures.get(i);
                    addImage(picture.getImage(), panel, 2);
                    temp.add(picture);
                }
            } else {
                window.setLocation(20, 20);
                window.setSize(700, 700);
                for (Picture picture : loadPictures) {
                    addImage(picture.getImage(), panel, 1f);
                    temp.add(picture);
                }
            }
            panel.setPictures(temp);
            SwingUtilities.updateComponentTreeUI(window);
        }
    }

    private static boolean init(File[] files, FileChoosePanel fileChoosePanel, Window window) {
        window.remove(fileChoosePanel);
        String extension = FilenameUtils.getExtension(files[0].getName());
        boolean isMnist = isMnist(extension);

        loadPictures = loadPictures(isMnist, files);
        for(int i = 0; i < loadPictures.size();i++){
            loadPictures.set(i, new Picture(ImageUtils.binarizeImage(ImageUtils.toBufferedImage(loadPictures.get(i)
                    .getImage()), isMnist), loadPictures.get(i).getType()));
            if (isMnist) {
                loadPictures.set(i, new Picture(ThinnerImage.Start(loadPictures.get(i)), loadPictures.get(i).getType
                        ()));
            }
        }
        return isMnist;
    }

    private static boolean isMnist(String extension) {
        boolean isMnist = false;

        //FIXME akolodziejek: move magic string to field
        if (extension.contains("-ubyte")) {
            isMnist = true;
        }
        return isMnist;
    }

    private static LinkedList<Picture> loadPictures(boolean isMnist, File[] files) {
        LinkedList<Picture> pictures = new LinkedList<>();
        if(isMnist) {
            MnistFilesLoader mnistFilesLoader = new MnistFilesLoader();
            try {
                pictures = mnistFilesLoader.loadTrainingDataSet(files[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ImageFileLoader imageFileLoader = new ImageFileLoader();
            pictures = imageFileLoader.loadTrainingDataSet(files);
        }
        return pictures;
    }

    private static void addImage(Image image, ImageRecognitionPanel panel, float scale) {
        Image upscaleImage = ImageUtils.upscaleImage((BufferedImage) image, scale);
        ImageIcon icon = new ImageIcon(upscaleImage);
        JLabel label = new JLabel(icon);
        panel.add(label);
    }
}
