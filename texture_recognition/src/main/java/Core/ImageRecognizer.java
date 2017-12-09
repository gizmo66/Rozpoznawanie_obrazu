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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ImageRecognizer {

    //linen (224), salt (160), straw (96), wood (32)
    private static Color linenLabel = new Color(224, 224, 224);
    private static Color saltLabel = new Color(160, 160, 160);
    private static Color strawLabel = new Color(96, 96, 96);
    private static Color woodLabel = new Color(32, 32, 32);
    private static final String DOUBLE_FORMAT = "%.1f";

    private LinkedList<Picture> loadPictures = new LinkedList<>();
    public TrainingData trainingData = new TrainingData();
    private FeaturesVectorLoader featuresVectorLoader;
    private FeaturesExtractor featuresExtractor;

    private static int imageWidth;
    private static int imageHeight;

    public void loadTrainingData(File[] files, FileChoosePanel fileChoosePanel, Window window) {
        if (files != null && files.length > 0) {
            init(files, fileChoosePanel, window, true);
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
            init(files, fileChoosePanel, window, false);

            ImageRecognitionPanel panel = new ImageRecognitionPanel(window);
            window.add(panel);

            LinkedList<Picture> temp = new LinkedList<>();
            window.setLocation(20, 20);
            window.setSize(700, 700);
            for (Picture picture : loadPictures) {
                addImage(ImageUtils.upscaleImage((BufferedImage) picture.getImage(), 0.5f), panel, 1f);
                temp.add(picture);
            }
            panel.setPictures(temp);
            SwingUtilities.updateComponentTreeUI(window);
        }
    }

    private void init(File[] files, FileChoosePanel fileChoosePanel, Window window, boolean isTrainingData) {
        window.remove(fileChoosePanel);
        loadPictures = loadPictures(files, isTrainingData);
    }

    private LinkedList<Picture> loadPictures(File[] files, boolean isTrainingData) {
        LinkedList<Picture> pictures;
        ImageFileLoader imageFileLoader = new ImageFileLoader();
        pictures = imageFileLoader.loadTrainingDataSet(files, isTrainingData);
        return pictures;
    }

    private void addImage(Image image, JPanel panel, float scale) {
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

    public BufferedImage recognizeTextures(Picture picture, Classifier classifier, ImageIcon imageIcon, Window window1,
                                           BufferedImage labelImage) {
        BufferedImage image = (BufferedImage) picture.getImage();
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();

        BufferedImage resultImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        int partToRecognizeSize = 64;
        BufferedImage tempImage = new BufferedImage(partToRecognizeSize, partToRecognizeSize,
                BufferedImage.TYPE_INT_RGB);
        Picture tempPicture = new Picture(tempImage, picture.getLabelImage(), picture.getType(),
                picture.getOriginalFileName());
        ResultData result = new ResultData("", "");
        String fileName = picture.getOriginalFileName() + "_" + classifier.getClass().getSimpleName();

        String originalWindowTitle = window1.getTitle();
        Map<Point2D, Map<String, Integer>> classMap = new LinkedHashMap<>();

        boolean simulate = true;
        int bigCounter = 0;
        double iterations = 0;
        double counter = 0;
        double recognized = 0;
        for (int a = 0; a < 2; a++) {
            for (int n = (partToRecognizeSize / 2) - 2; n > 7; n /= 2, bigCounter++) {
                for (int offsetX = 0; offsetX < partToRecognizeSize - 1; offsetX += n) {
                    for (int offsetY = 0; offsetY < partToRecognizeSize - 1; offsetY += n) {
                        for (int w = offsetX; w < imageWidth - offsetX; w += partToRecognizeSize) {
                            for (int h = offsetY; h < imageHeight - offsetY; h += partToRecognizeSize) {
                                if (!simulate) {
                                    classifyPart(classifier, image, partToRecognizeSize, tempImage, tempPicture, result,
                                            w, h);
                                    fillPixelsClassCountMap(partToRecognizeSize, result, classMap, w, h);
                                    determinateAndMarkPixelClass(resultImage, partToRecognizeSize, classMap, w, h);
                                    recognized = countAndMarkCorrectlyRecognizedPixelsPercentage(resultImage,
                                            labelImage, false);
                                    counter++;
                                    updatePreview(imageIcon, window1, resultImage, originalWindowTitle,
                                            iterations, counter, recognized);
                                } else {
                                    iterations++;
                                }
                            }
                        }
                    }
                }
            }
            simulate = false;
        }

        java.util.List<String> featureIds = featuresExtractor.getFeatureIds().stream().sorted()
                .collect(Collectors.toList());
        String resultFileName = fileName + "_" + featureIds + "_" + String.format(DOUBLE_FORMAT, recognized) + "%";

        BufferedImage tempResultImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < imageWidth; w++) {
            for (int h = 0; h < imageHeight; h++) {
                tempResultImage.setRGB(w, h, resultImage.getRGB(w, h));
            }
        }

        saveResultToFile(resultImage, "raw_" + resultFileName, "bmp");
        countAndMarkCorrectlyRecognizedPixelsPercentage(tempResultImage, labelImage, true);
        saveResultToFile(tempResultImage, "marked_" + resultFileName,"bmp");

        return tempResultImage;
    }

    private void updatePreview(ImageIcon imageIcon, Window window1, BufferedImage resultImage,
                               String originalWindowTitle, double iterations, double counter, double recognized) {
        imageIcon.setImage(resultImage);
        SwingUtilities.updateComponentTreeUI(window1);
        double progress = (counter / iterations) * 100;
        updateRecognitionWindowTitle(window1, originalWindowTitle, progress, recognized);
    }

    private void updateRecognitionWindowTitle(Window window1, String originalWindowTitle, double progress, double recognized) {
        window1.setTitle(originalWindowTitle + ": " + String.format(DOUBLE_FORMAT, progress) +
                "%" + " (recognized: " + String.format(DOUBLE_FORMAT, recognized) + "%)");
    }

    private void classifyPart(Classifier classifier, BufferedImage image, int partToRecognizeSize,
                              BufferedImage tempImage, Picture tempPicture, ResultData result, int w, int h) {
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

    private void saveResultToFile(BufferedImage imageForSaving, String fileName, String extension) {
        ImageUtils.save(imageForSaving, fileName, extension);
    }

    private void determinateAndMarkPixelClass(BufferedImage resultImage, int partToRecognizeSize, Map<Point2D,
            Map<String, Integer>> classMap, int w, int h) {
        for (int x = w; x < w + partToRecognizeSize; x++) {
            for (int y = h; y < h + partToRecognizeSize; y++) {
                if (x < imageWidth && y < imageHeight) {
                    String dominatingClassName = "";
                    Integer maxQuantity = 0;
                    Point2D pixelCoordinates = new Point2D.Double(x, y);
                    if (classMap.get(pixelCoordinates) != null) {
                        for (String className1 : classMap.get(pixelCoordinates).keySet()) {
                            Integer quantity = classMap.get(pixelCoordinates).get(className1);
                            if (quantity > maxQuantity) {
                                maxQuantity = quantity;
                                dominatingClassName = className1;
                            }
                        }
                    }
                    resultImage.setRGB(x, y, getRGBForClass(dominatingClassName));
                }
            }
        }
    }

    private void fillPixelsClassCountMap(int partToRecognizeSize, ResultData result,
                                         Map<Point2D, Map<String, Integer>> classMap, int w, int h) {
        String className = result.resultOfKnn;
        if (!Objects.equals(className, "")) {
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
                color = Color.yellow;
                break;
        }
        return color.getRGB();
    }

    public double countAndMarkCorrectlyRecognizedPixelsPercentage(BufferedImage imageWithRecognizedTextures,
                                                                  BufferedImage labelImage, boolean markPixels) {
        double count = 0;
        double correctQuantity = 0;
        for (int w = 0; w < imageWidth; w++) {
            for (int h = 0; h < imageHeight; h++) {
                Color recognizedColor = new Color(imageWithRecognizedTextures.getRGB(w, h));
                Color labelColor = new Color(labelImage.getRGB(w, h));
                if (recognizedColor.getRGB() == labelColor.getRGB()) {
                    correctQuantity++;
                } else if (markPixels) {
                    int red = labelColor.getRed() + 50;
                    red = Math.min(red, 255);
                    red = Math.max(red, 0);
                    Color temp = new Color(red, labelColor.getGreen(), labelColor.getBlue());
                    imageWithRecognizedTextures.setRGB(w, h, temp.getRGB());
                }
                count++;
            }
        }
        return (correctQuantity / count) * 100;
    }
}
