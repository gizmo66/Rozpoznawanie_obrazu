package Core;

import Classification.Classifier;
import Classification.ResultData;
import Extraction.FeaturesExtractor;
import Extraction.FeaturesVector;
import Extraction.Picture;
import File.ImageFileLoader;
import Image.ImageUtils;
import View.Panel.FileChoosePanel;
import View.Panel.ImageFileChoosePanel;
import View.Panel.ImageRecognitionPanel;
import View.Utils.ImageTypeEnum;
import View.Window.Window;
import View.Window.WindowTitleEnum;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ImageRecognizer {

    //linen (224), salt (160), straw (96), wood (32)
    private static Color linenLabel = new Color(224, 224, 224);
    private static Color saltLabel = new Color(160, 160, 160);
    private static Color strawLabel = new Color(96, 96, 96);
    private static Color woodLabel = new Color(32, 32, 32);
    public static final String DOUBLE_FORMAT = "%.1f";

    private LinkedList<Picture> loadPictures = new LinkedList<>();
    public TrainingData trainingData = new TrainingData();
    private FeaturesVectorLoader featuresVectorLoader;
    private FeaturesExtractor featuresExtractor;
    private Map<Point2D, Map<String, Integer>> classMap = new LinkedHashMap<>();
    private Classifier classifier;

    private int partToRecognizeSize = 64;
    private boolean simulate = true;
    private double iterations = 0;
    private double recognized = 0;

    private static int imageWidth;
    private static int imageHeight;
    private BufferedImage labelImage;
    private static boolean MARK_PART = true;
    private BufferedImage previewImage;
    private HashMap<Point2D, Integer> markedPart = new HashMap<>();

    public void loadTrainingData(File[] files, FileChoosePanel fileChoosePanel, Window window) {
        if (files != null && files.length > 0) {
            init(files, fileChoosePanel, window, true);
            if (featuresExtractor == null) {
                featuresExtractor = new FeaturesExtractor();
            }
            window.setTitle(WindowTitleEnum.FEATURES_VECTOR_EXTRACTING.getName());
            FeaturesVector featuresVector = featuresExtractor.extractFeaturesVector(loadPictures, window);
            featuresVector.saveToFile();
            window.dispose();
            window = new Window(WindowTitleEnum.CHOOSE_IMAGE_TO_RECOGNIZE.getName());
            window.add(new ImageFileChoosePanel(ContextEnum.RECOGNITION, window));
            window.pack();
            window.setVisible(true);
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

    public BufferedImage recognizeTextures(Picture picture, Classifier classifier, ImageIcon imageIcon, Window window1) {
        initTexturesRecognition(picture, classifier);
        BufferedImage resultImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        ResultData classificationResult = new ResultData("", "");
        String originalWindowTitle = window1.getTitle();
        previewImage = copyImage(resultImage);

        double counter = 0;
        for (int a = 0; a < 2; a++) {
            for (int offsetX = 0; offsetX < partToRecognizeSize - 1; offsetX += 15) {
                for (int offsetY = 0; offsetY < partToRecognizeSize - 1; offsetY += 15) {
                    for (int w = offsetX; w < imageWidth - offsetX; w += partToRecognizeSize) {
                        for (int h = offsetY; h < imageHeight - offsetY; h += partToRecognizeSize) {
                            if (!simulate) {
                                classifyPart(picture, classificationResult, w, h);
                                fillPixelsClassCountMap(classificationResult, w, h);
                                determinateAndMarkPixelClass(resultImage, w, h);
                                recognized = getCorrectlyRecognizedPixelsPercentage(resultImage,
                                        labelImage, false);
                                counter++;

                                updatePreview(imageIcon, window1, resultImage, originalWindowTitle, w, h, counter);
                            } else {
                                iterations++;
                            }
                        }
                    }
                }
            }
            simulate = false;
        }
        return saveResults(labelImage, resultImage, picture);
    }

    private void updatePreview(ImageIcon imageIcon, Window window1, BufferedImage resultImage,
                               String originalWindowTitle, int w, int h, double counter) {
        if (MARK_PART) {
            markPart(previewImage, w, h);
            updatePreview(imageIcon, window1, previewImage, originalWindowTitle, counter);
        } else {
            updatePreview(imageIcon, window1, resultImage, originalWindowTitle, counter);
        }
    }

    private void markPart(BufferedImage tempImage, int w, int h) {
        for (Map.Entry<Point2D, Integer> pointToRGB : markedPart.entrySet()) {
            Point2D pixel = pointToRGB.getKey();
            tempImage.setRGB((int) pixel.getX(), (int) pixel.getY(), pointToRGB.getValue());
        }
        markedPart.clear();

        for (int x = w; x < w + partToRecognizeSize; x++) {
            for (int y = h; y < h + partToRecognizeSize; y++) {
                if (x < imageWidth && y < imageHeight) {
                    if (x == w || x == w + partToRecognizeSize - 1 || y == h || y == h + partToRecognizeSize - 1) {
                        markedPart.put(new Point2D.Double(x, y), tempImage.getRGB(x, y));
                        tempImage.setRGB(x, y, Color.RED.getRGB());
                    }
                }
            }
        }
    }

    private void initTexturesRecognition(Picture picture, Classifier classifier) {
        BufferedImage image = (BufferedImage) picture.getImage();
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();
        this.classifier = classifier;
        this.labelImage = (BufferedImage) picture.getLabelImage();
    }

    private BufferedImage saveResults(BufferedImage labelImage, BufferedImage resultImage, Picture picture) {
        java.util.List<String> featureIds = featuresExtractor.getFeatureIds().stream().sorted()
                .collect(Collectors.toList());
        String fileName = picture.getOriginalFileName() + "_" + classifier.getClass().getSimpleName();
        String resultFileName = fileName + "_" + featureIds + "_" + String.format(DOUBLE_FORMAT, recognized) + "%";
        BufferedImage tempResultImage = copyImage(resultImage);
        saveResultToFile(resultImage, "./results/raw_" + resultFileName,
                ImageTypeEnum.BMP.getExtensions().get(0));
        getCorrectlyRecognizedPixelsPercentage(tempResultImage, labelImage, true);
        saveResultToFile(tempResultImage, "./results/marked_" + resultFileName,
                ImageTypeEnum.BMP.getExtensions().get(0));
        return tempResultImage;
    }

    private BufferedImage copyImage(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                copy.setRGB(w, h, source.getRGB(w, h));
            }
        }
        return copy;
    }

    private void updatePreview(ImageIcon imageIcon, Window window1, BufferedImage image, String originalWindowTitle,
                               double counter) {
        imageIcon.setImage(image);
        SwingUtilities.updateComponentTreeUI(window1);
        double progress = (counter / iterations) * 100;
        updateRecognitionWindowTitle(window1, originalWindowTitle, progress);
    }

    private void updateRecognitionWindowTitle(Window window1, String originalWindowTitle, double progress) {
        window1.setTitle(originalWindowTitle + ": " + String.format(DOUBLE_FORMAT, progress) +
                "%" + " (recognized: " + String.format(DOUBLE_FORMAT, recognized) + "%)");
    }

    private void classifyPart(Picture picture, ResultData classificationResult, int w, int h) {
        BufferedImage tempImage = new BufferedImage(partToRecognizeSize, partToRecognizeSize,
                BufferedImage.TYPE_INT_RGB);
        Picture tempPicture = new Picture(tempImage, picture.getLabelImage(), picture.getType(),
                picture.getOriginalFileName());
        Picture tempPictureWithExtractedFeatures;
        BufferedImage image = (BufferedImage) picture.getImage();
        for (int x = 0; x < partToRecognizeSize; x++) {
            for (int y = 0; y < partToRecognizeSize; y++) {
                if (w + x < imageWidth && h + y < imageHeight) {
                    tempImage.setRGB(x, y, image.getRGB(w + x, h + y));
                }
            }
        }
        tempPicture.setImage(tempImage);
        tempPictureWithExtractedFeatures = calculateFeatureInOnePicture(tempPicture);
        classifier.classify(tempPictureWithExtractedFeatures, 10, classificationResult);
    }

    private void saveResultToFile(BufferedImage imageForSaving, String fileName, String extension) {
        ImageUtils.save(imageForSaving, fileName, extension);
    }

    private void determinateAndMarkPixelClass(BufferedImage resultImage, int w, int h) {
        for (int x = w; x < w + partToRecognizeSize; x++) {
            for (int y = h; y < h + partToRecognizeSize; y++) {
                if (x < imageWidth && y < imageHeight) {
                    String dominatingClassName = "";
                    Integer maxQuantity = 0;
                    Point2D pixelCoordinates = new Point2D.Double(x, y);
                    if (classMap.get(pixelCoordinates) != null) {
                        for (String className : classMap.get(pixelCoordinates).keySet()) {
                            Integer quantity = classMap.get(pixelCoordinates).get(className);
                            if (quantity > maxQuantity) {
                                maxQuantity = quantity;
                                dominatingClassName = className;
                            }
                        }
                    }
                    resultImage.setRGB(x, y, getRGBForClass(dominatingClassName));
                    previewImage.setRGB(x, y, getRGBForClass(dominatingClassName));
                }
            }
        }
    }

    private void fillPixelsClassCountMap(ResultData classificationResult, int w, int h) {
        String className = classificationResult.resultOfKnn;
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

    public double getCorrectlyRecognizedPixelsPercentage(BufferedImage imageWithRecognizedTextures,
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
