package View.Panel;

import Classification.Classifier;
import Classification.KNearestNeighbors;
import Classification.NaiveBayes;
import Classification.ResultData;
import Core.FeaturesVectorLoader;
import Core.ImageRecognizer;
import Core.TrainingData;
import Extraction.Picture;
import View.Window.Window;
import View.Window.WindowTestRecognizer;
import View.Window.WindowTitleEnum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

public class ImageRecognitionPanel extends JPanel implements ActionListener {

    private JButton recognitionBtn;
    private JButton recognitionBtn1;
    private JButton recognitionBtn2;
    private JButton recognitionBtn3;
    private JButton recognitionBtn4;
    private View.Window.Window window;
    private LinkedList<Picture> pictures;
    private int currentWindowPosition = 0;

    public void setPictures(LinkedList<Picture> pictures) {
        this.pictures = pictures;
    }

    public ImageRecognitionPanel(View.Window.Window window) {
        this.window = window;
        initButtons();
        initWindow(window);
    }

    private void initButtons() {
        //TODO akolodziejek: przenieść nazwy button'ów do pól
        this.recognitionBtn = initButton("RECOGNIZE whole image with KNN");
        this.recognitionBtn1 = initButton("RECOGNIZE whole image with NaiveBayes");
        this.recognitionBtn2 = initButton("RECOGNIZE regions with KNN");
        this.recognitionBtn3 = initButton("RECOGNIZE regions with NaiveBayes");
        this.recognitionBtn4 = initButton("RECOGNIZE regions with both classifiers");
    }

    private void initWindow(Window window) {
        window.setTitle(WindowTitleEnum.RECOGNIZING_IMAGE.getName());
        window.setSize(600, 700);
        window.pack();
        window.setVisible(true);
    }

    private JButton initButton(String text) {
        JButton jButton = new JButton(text);
        jButton.addActionListener(this);
        jButton.setSize(50, 25);
        add(jButton, BorderLayout.CENTER);
        return jButton;
    }

    public void actionPerformed(ActionEvent e) {
        if (recognizeWholeImageWithKNN(e) || recognizeWholeImageWithBayes(e)) {
            recognizeWholePictures(e);
        } else {
            recognizeTextures(e);
        }
    }

    private boolean recognizeWholeImageWithBayes(ActionEvent e) {
        return e.getSource().equals(recognitionBtn1);
    }

    private boolean recognizeWholeImageWithKNN(ActionEvent e) {
        return e.getSource().equals(recognitionBtn3);
    }

    private boolean recognizeTexturesWithBayes(ActionEvent e) {
        return e.getSource().equals(recognitionBtn3);
    }

    private boolean recognizeTexturesImageWithKNN(ActionEvent e) {
        return e.getSource().equals(recognitionBtn2);
    }

    private boolean recognizeTexturesWithAllClassifiers(ActionEvent e) {
        return e.getSource().equals(recognitionBtn4);
    }

    private void recognizeTextures(ActionEvent e) {
        for (Picture picture : pictures) {
            if (recognizeTexturesImageWithKNN(e)) {
                recognizeTexturesInPicture(picture, new ImageRecognizer(new KNearestNeighbors(getTrainingData())));
            } else if (recognizeTexturesWithBayes(e)) {
                recognizeTexturesInPicture(picture, new ImageRecognizer(new NaiveBayes(getTrainingData())));
            } else if (recognizeTexturesWithAllClassifiers(e)) {
                recognizeTexturesWithAllClassifiers(picture);
            }
        }
    }

    private void recognizeTexturesWithAllClassifiers(Picture picture) {
        java.util.List<Classifier> classifiers = new ArrayList<>();

        //TODO akolodziejek: wyszukać wszystkie implementacje z pomocą refleksji
        classifiers.add(new KNearestNeighbors(getTrainingData()));
        classifiers.add(new NaiveBayes(getTrainingData()));

        for (Classifier classifier : classifiers) {
            recognizeTexturesInPicture(picture, new ImageRecognizer(classifier));
        }
    }

    private void recognizeWholePictures(ActionEvent e) {
        Classifier classifier;
        if (recognizeWholeImageWithKNN(e)) {
            classifier = new KNearestNeighbors(getTrainingData());
        } else {
            classifier = new NaiveBayes(getTrainingData());
        }
        ImageRecognizer imageRecognizer = new ImageRecognizer();
        java.util.List<ResultData> result = imageRecognizer.recognizePictures(classifier, pictures);
        window = WindowTestRecognizer.getTestWindows(result);
        window.pack();
        window.setVisible(true);
        currentWindowPosition++;
    }

    private TrainingData getTrainingData() {
        FeaturesVectorLoader featuresVectorLoader = new FeaturesVectorLoader();
        return featuresVectorLoader.loadFeaturesVector();
    }

    private void recognizeTexturesInPicture(Picture picture, ImageRecognizer imageRecognizer1) {
        Window window = new Window(WindowTitleEnum.TEXTURES_RECOGNIZING.getName());
        JPanel panel = new JPanel();
        window.add(panel);

        window.setLocation(20, 20 + 100 * currentWindowPosition);
        window.setSize(1050, 580);
        window.setVisible(true);
        currentWindowPosition++;

        ImageIcon imageIcon = new ImageIcon();
        JLabel pictureFrame = new JLabel(imageIcon);
        panel.add(pictureFrame);

        BufferedImage labelImage = (BufferedImage) picture.getLabelImage();
        JLabel pictureFrame1 = new JLabel(new ImageIcon(labelImage));
        panel.add(pictureFrame1);

        new Thread(() -> recognizeTexturesInPicture(window, picture, imageIcon, imageRecognizer1)).start();
    }

    private void recognizeTexturesInPicture(Window window1, Picture picture, ImageIcon
            imageIcon, ImageRecognizer imageRecognizer) {

        final BufferedImage[] imageWithRecognizedTextures = new BufferedImage[1];
        final double[] correctlyRecognizedPixelsPercentage = {0};

        imageWithRecognizedTextures[0] = imageRecognizer.recognizeTextures(picture, imageIcon, window1);
        BufferedImage tempImage = imageRecognizer.copyImage(imageWithRecognizedTextures[0]);
        correctlyRecognizedPixelsPercentage[0] = imageRecognizer.getCorrectlyRecognizedPixelsPercentage(tempImage,
                true);
        showWindowWithResult(picture, tempImage, correctlyRecognizedPixelsPercentage[0]);
    }

    private void showWindowWithResult(Picture picture, BufferedImage imageWithMarkedPixels, double v) {
        String originalFileName = picture.getOriginalFileName();
        String title = "Result for " + originalFileName + ": " + String.format("%.1f", v) + "% recognized";

        Window window = new Window(title);
        JPanel panel1 = new JPanel();
        JLabel pictureFrame2 = new JLabel(new ImageIcon(imageWithMarkedPixels));
        panel1.add(pictureFrame2);

        window.add(panel1);
        window.setLocation(20, 20);
        window.setSize(1300, 700);
        window.pack();
        window.setVisible(true);
        currentWindowPosition++;
    }
}