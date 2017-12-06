package Core;

import Classification.Classifier;
import Classification.ResultData;
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

    private LinkedList<Picture> loadPictures = new LinkedList<>();
    public TrainingData trainingData = new TrainingData();
    private FeaturesVectorLoader featuresVectorLoader;
    private FeaturesExtractor featuresExtractor;

    public void loadTrainingData(File[] files, FileChoosePanel fileChoosePanel, Window window) {
        if (files != null && files.length > 0) {
            init(files, fileChoosePanel, window);
            if (featuresExtractor == null) {
                featuresExtractor = new FeaturesExtractor();
            }
            FeaturesVector featuresVector = featuresExtractor.extractFeaturesVector(loadPictures);
            featuresVector.saveToFile();
            window.add(new TrainingDataLoadingPanel(loadPictures, window));
        }
    }

    public void initImageRecognition(File[] files, FileChoosePanel fileChoosePanel, Window window) {
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

    private void init(File[] files, FileChoosePanel fileChoosePanel, Window window) {
        window.remove(fileChoosePanel);
        loadPictures = loadPictures(files);
    }

    private LinkedList<Picture> loadPictures(File[] files) {
        LinkedList<Picture> pictures;
        ImageFileLoader imageFileLoader = new ImageFileLoader();
        pictures = imageFileLoader.loadTrainingDataSet(files);
        return pictures;
    }

    public void addImage(Image image, JPanel panel, float scale) {
        Image upscaleImage = ImageUtils.upscaleImage((BufferedImage) image, scale);
        ImageIcon icon = new ImageIcon(upscaleImage);
        JLabel label = new JLabel(icon);
        panel.add(label);
    }

    public boolean loadFeaturesVector() {
        if (featuresVectorLoader == null) {
            featuresVectorLoader = new FeaturesVectorLoader(this);
        }
        return featuresVectorLoader.loadFeaturesVector();
    }

    public Picture calculateFeatureInOnePicture(Picture picture) {
        if (featuresExtractor == null) {
            featuresExtractor = new FeaturesExtractor();
        }
        return featuresExtractor.calculateFeaturesInOnePicture(picture);
    }

    public void recognizeTextures(Picture picture, Classifier classifier, ImageIcon imageIcon, Window window1) {
        BufferedImage image = (BufferedImage) picture.getImage();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        //linen (224), salt (160), straw (96), wood (32)

        Color linenLabel = new Color(224, 224, 224);
        Color saltLabel = new Color(160, 160, 160);
        Color strawLabel = new Color(96, 96, 96);
        Color woodLabel = new Color(32, 32, 32);

        BufferedImage resultImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        int partToRecognizeSize = 64;
        BufferedImage tempImage = new BufferedImage(partToRecognizeSize, partToRecognizeSize, BufferedImage.TYPE_INT_RGB);
        Picture tempPicture = new Picture(tempImage, picture.getType(), picture.getOriginalFileName());
        Picture tempPictureWithExtractedFeatures;
        ResultData result = new ResultData("", "");
        Color color;

        for (int w = 0; w < imageWidth; w += partToRecognizeSize) {
            for (int h = 0; h < imageHeight; h += partToRecognizeSize) {

                for (int x = 0; x < partToRecognizeSize; x++) {
                    for (int y = 0; y < partToRecognizeSize; y++) {
                        tempImage.setRGB(x, y, image.getRGB(w + x, h + y));
                    }
                }

                tempPicture.setImage(tempImage);
                tempPictureWithExtractedFeatures = calculateFeatureInOnePicture(tempPicture);
                classifier.classify(tempPictureWithExtractedFeatures, 10, result);

                switch (result.resultOfKnn) {
                    case "linen":
                        color = linenLabel;
                        break;
                    case "salt":
                        color = saltLabel;
                        break;
                    case "straw":
                        color = strawLabel;
                        break;
                    case "wood":
                        color = woodLabel;
                        break;
                    default:
                        color = Color.red;
                        break;
                }

                for (int x = w; x < w + partToRecognizeSize; x++) {
                    for (int y = h; y < h + partToRecognizeSize; y++) {
                        resultImage.setRGB(x, y, color.getRGB());
                    }
                }
                imageIcon.setImage(resultImage);
                SwingUtilities.updateComponentTreeUI(window1);
            }
        }

        ImageUtils.save(resultImage, "result", "bmp");
    }
}
