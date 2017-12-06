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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ImageRecognizer {

    //linen (224), salt (160), straw (96), wood (32)
    private static Color linenLabel = new Color(224, 224, 224);
    private static Color saltLabel = new Color(160, 160, 160);
    private static Color strawLabel = new Color(96, 96, 96);
    private static Color woodLabel = new Color(32, 32, 32);

    private LinkedList<Picture> loadPictures = new LinkedList<>();
    public TrainingData trainingData = new TrainingData();
    private FeaturesVectorLoader featuresVectorLoader;
    private FeaturesExtractor featuresExtractor;

    private static int imageWidth;
    private static int imageHeight;

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
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();

        BufferedImage resultImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        BufferedImage imageForSaving = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        int partToRecognizeSize = 64;
        BufferedImage tempImage = new BufferedImage(partToRecognizeSize, partToRecognizeSize, BufferedImage.TYPE_INT_RGB);
        Picture tempPicture = new Picture(tempImage, picture.getType(), picture.getOriginalFileName());
        ResultData result = new ResultData("", "");
        String fileName = picture.getOriginalFileName() + "_" + classifier.getClass().getSimpleName() + "_";

        Map<Point2D, Map<String, Integer>> classMap = new LinkedHashMap<>();
        int counter = 0;
        for (int n = (partToRecognizeSize / 2) - 2; n > 7; n /= 2, counter++) {
            for (int offsetX = 0; offsetX < partToRecognizeSize - 1; offsetX += n) {
                for (int offsetY = 0; offsetY < partToRecognizeSize - 1; offsetY += n) {
                    for (int w = offsetX; w < imageWidth - offsetX; w += partToRecognizeSize) {
                        for (int h = offsetY; h < imageHeight - offsetY; h += partToRecognizeSize) {
                            classifyPart(classifier, image, partToRecognizeSize, tempImage, tempPicture, result, w, h);
                            fillPixelsClassProbabilityMap(partToRecognizeSize, result, classMap, w, h);
                            determinateAndMarkPixelClass(resultImage, imageForSaving, partToRecognizeSize, classMap, w, h);
                            updatePreview(imageIcon, window1, resultImage);
                            saveResultToFile(imageForSaving, fileName + counter);
                        }
                    }
                }
            }
        }
        System.out.println("recognized: " + fileName + counter);
    }

    private void classifyPart(Classifier classifier, BufferedImage image, int partToRecognizeSize, BufferedImage tempImage, Picture tempPicture, ResultData result, int w, int h) {
        Picture tempPictureWithExtractedFeatures;
        for (int x = 0; x < partToRecognizeSize; x++) {
            for (int y = 0; y < partToRecognizeSize; y++) {
                if (w + x < imageWidth && h + y < imageHeight) {
                    tempImage.setRGB(x, y, image.getRGB(w + x, h + y));
                }
            }
        }
        tempPicture.setImage(tempImage);
        tempPictureWithExtractedFeatures = calculateFeatureInOnePicture(tempPicture);
        classifier.classify(tempPictureWithExtractedFeatures, 10, result);
    }

    private void saveResultToFile(BufferedImage imageForSaving, String fileName) {
        ImageUtils.save(imageForSaving, fileName, "bmp");
    }

    private void updatePreview(ImageIcon imageIcon, Window window1, BufferedImage resultImage) {
        imageIcon.setImage(resultImage);
        SwingUtilities.updateComponentTreeUI(window1);
    }

    private void determinateAndMarkPixelClass(BufferedImage resultImage, BufferedImage imageForSaving, int partToRecognizeSize, Map<Point2D, Map<String, Integer>> classMap, int w, int h) {
        for (int x = w; x < w + partToRecognizeSize; x++) {
            for (int y = h; y < h + partToRecognizeSize; y++) {
                if (x < imageWidth && y < imageHeight) {
                    String dominatingClassName = "";
                    Integer maxQuantity = 0;
                    for (String className1 : classMap.get(new Point2D.Double(x, y)).keySet()) {
                        Integer quantity = classMap.get(new Point2D.Double(x, y)).get(className1);
                        if (quantity > maxQuantity) {
                            maxQuantity = quantity;
                            dominatingClassName = className1;
                        }
                    }
                    resultImage.setRGB(x, y, getRGBForClass(dominatingClassName));
                    imageForSaving.setRGB(x, y, getRGBForClass(dominatingClassName));
                }
            }
        }
    }

    private void fillPixelsClassProbabilityMap(int partToRecognizeSize, ResultData result, Map<Point2D, Map<String, Integer>> classMap, int w, int h) {
        String className = result.resultOfKnn;
        for (int x = w; x < w + partToRecognizeSize; x++) {
            for (int y = h; y < h + partToRecognizeSize; y++) {
                if (x < imageWidth && y < imageHeight) {
                    Point2D.Double coordinates = new Point2D.Double(x, y);
                    if (classMap.get(coordinates) != null) {
                        classMap.get(coordinates).merge(className, 1, (a, b) -> a + b);
                    } else {
                        Map<String, Integer> temp = new LinkedHashMap<>();
                        temp.put(className, 1);
                        classMap.put(coordinates, temp);
                    }
                }
            }
        }
    }

    private int getRGBForClass(String dominatingClassName) {
        Color color;
        switch (dominatingClassName) {
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
        return color.getRGB();
    }
}
