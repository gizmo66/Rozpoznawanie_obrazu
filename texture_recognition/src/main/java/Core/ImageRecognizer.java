package Core;

import Extraction.FeaturesExtractor;
import Extraction.FeaturesVector;
import Extraction.Picture;
import File.ImageFileLoader;
import Image.ImageUtils;
import View.Panel.FileChoosePanel;
import View.Panel.ImageRecognitionPanel;
import View.Panel.TrainingDataLoadingPanel;
import View.Window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

public class ImageRecognizer {

    private static LinkedList<Picture> loadPictures = new LinkedList<>();
    public static TrainingData trainingData = new TrainingData();
    private static FeaturesVectorLoader featuresVectorLoader;
    private static FeaturesExtractor featuresExtractor;

    public static void loadTrainingData(File[] files, FileChoosePanel fileChoosePanel, Window window) {
        if (files != null && files.length > 0) {
            init(files, fileChoosePanel, window);
            if(featuresExtractor == null) {
                featuresExtractor = new FeaturesExtractor();
            }
            FeaturesVector featuresVector = featuresExtractor.extractFeaturesVector(loadPictures);
            featuresVector.saveToFile();
            window.add(new TrainingDataLoadingPanel(loadPictures, window));
        }
    }

    public static void initImageRecognition(File[] files, FileChoosePanel fileChoosePanel, Window window) {
        if (files != null && files.length > 0) {
            init(files, fileChoosePanel, window);

            ImageRecognitionPanel panel = new ImageRecognitionPanel(window);
            window.add(panel);

            LinkedList<Picture> temp = new LinkedList<>();
            window.setLocation(20, 20);
            window.setSize(700, 700);
            for (Picture picture : loadPictures) {
                addImage(picture.getImage(), panel, 1f);
                temp.add(picture);
            }
            panel.setPictures(temp);
            SwingUtilities.updateComponentTreeUI(window);
        }
    }

    private static void init(File[] files, FileChoosePanel fileChoosePanel, Window window) {
        window.remove(fileChoosePanel);

        loadPictures = loadPictures(files);
        for (int i = 0; i < loadPictures.size(); i++) {
            loadPictures.set(i, new Picture(ImageUtils.binarizeImage(ImageUtils.toBufferedImage(loadPictures.get(i)
                    .getImage())), loadPictures.get(i).getType()));
        }
    }

    private static LinkedList<Picture> loadPictures(File[] files) {
        LinkedList<Picture> pictures;
        ImageFileLoader imageFileLoader = new ImageFileLoader();
        pictures = imageFileLoader.loadTrainingDataSet(files);
        return pictures;
    }

    private static void addImage(Image image, ImageRecognitionPanel panel, float scale) {
        Image upscaleImage = ImageUtils.upscaleImage((BufferedImage) image, scale);
        ImageIcon icon = new ImageIcon(upscaleImage);
        JLabel label = new JLabel(icon);
        panel.add(label);
    }

    public static boolean loadFeaturesVector() {
        if (featuresVectorLoader == null) {
            featuresVectorLoader = new FeaturesVectorLoader();
        }
        return featuresVectorLoader.loadFeaturesVector();
    }

    public static Picture calculateFeatureInOnePicture(Picture picture) {
        if(featuresExtractor == null) {
            featuresExtractor = new FeaturesExtractor();
        }
        return featuresExtractor.calculateFeatureInOnePicture(picture);
    }
}
