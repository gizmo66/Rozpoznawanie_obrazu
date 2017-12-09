package View.Panel;

import Classification.Classifier;
import Classification.KNearestNeighbors;
import Classification.NaiveBayes;
import Classification.ResultData;
import Core.ImageRecognizer;
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

    public void setPictures(LinkedList<Picture> pictures) {
        this.pictures = pictures;
    }

    public ImageRecognitionPanel(View.Window.Window window) {
        this.window = window;
        window.setTitle(WindowTitleEnum.RECOGNIZING_IMAGE.getName());

        recognitionBtn = new JButton("RECOGNIZE whole image with KNN");
        recognitionBtn.addActionListener(this);
        recognitionBtn.setSize(50, 25);
        add(recognitionBtn, BorderLayout.CENTER);

        recognitionBtn1 = new JButton("RECOGNIZE whole image with NaiveBayes");
        recognitionBtn1.addActionListener(this);
        recognitionBtn1.setSize(50, 25);
        add(recognitionBtn1, BorderLayout.CENTER);

        recognitionBtn2 = new JButton("RECOGNIZE regions with KNN");
        recognitionBtn2.addActionListener(this);
        recognitionBtn2.setSize(50, 25);
        add(recognitionBtn2, BorderLayout.CENTER);

        recognitionBtn3 = new JButton("RECOGNIZE regions with NaiveBayes");
        recognitionBtn3.addActionListener(this);
        recognitionBtn3.setSize(50, 25);
        add(recognitionBtn3, BorderLayout.CENTER);

        recognitionBtn4 = new JButton("RECOGNIZE regions with both classifiers");
        recognitionBtn4.addActionListener(this);
        recognitionBtn4.setSize(50, 25);
        add(recognitionBtn4, BorderLayout.CENTER);

        window.setSize(600, 700);
        window.pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        ImageRecognizer imageRecognizer = new ImageRecognizer();
        if (e.getSource().equals(recognitionBtn) || e.getSource().equals(recognitionBtn1)) {
            if (imageRecognizer.loadFeaturesVector()) {
                java.util.LinkedList<Picture> picturesWithExtractedFeatures = new LinkedList<>();

                for (Picture picture : pictures) {
                    picturesWithExtractedFeatures.add(imageRecognizer.calculateFeatureInOnePicture(picture));
                }

                Classifier classifier;
                if (e.getSource().equals(recognitionBtn)) {
                    classifier = new KNearestNeighbors(imageRecognizer);
                } else {
                    classifier = new NaiveBayes(imageRecognizer);
                }
                java.util.List<ResultData> result = classifier.classify(picturesWithExtractedFeatures, 10);
                window = WindowTestRecognizer.getTestWindows(result);
            }
            window.pack();
            window.setVisible(true);
        } else {
            int i = 0;
            for (Picture picture : pictures) {
                java.util.List<ImageRecognizer> imageRecognizers = new ArrayList<>();
                ImageRecognizer imageRecognizer1 = new ImageRecognizer();
                ImageRecognizer imageRecognizer2 = new ImageRecognizer();
                imageRecognizers.add(imageRecognizer1);
                imageRecognizers.add(imageRecognizer2);

                imageRecognizer1.loadFeaturesVector();
                imageRecognizer2.loadFeaturesVector();

                if (e.getSource().equals(recognitionBtn4)) {
                    java.util.List<Classifier> classifiers = new ArrayList<>();
                    classifiers.add(new KNearestNeighbors(imageRecognizer1));
                    classifiers.add(new NaiveBayes(imageRecognizer2));

                    for (int n = 0; n < classifiers.size(); n++) {
                        recognizeTexturesInPicture(picture, imageRecognizers.get(n), classifiers.get
                                (n), i);
                        i++;
                    }
                } else {
                    Classifier classifier;
                    if (e.getSource().equals(recognitionBtn2)) {
                        classifier = new KNearestNeighbors(imageRecognizer1);
                    } else {
                        classifier = new NaiveBayes(imageRecognizer1);
                    }

                    recognizeTexturesInPicture(picture, imageRecognizer1, classifier, i);
                    i++;
                }
            }
        }
    }

    private void recognizeTexturesInPicture(Picture picture, ImageRecognizer imageRecognizer1, Classifier classifier,
                                            int i) {
        Window window1 = new Window("Recognizing textures");
        JPanel panel = new JPanel();
        window1.add(panel);

        window1.setLocation(20, 20 + 100 * i);
        window1.setSize(1050, 580);
        window1.setVisible(true);

        ImageIcon imageIcon = new ImageIcon();
        JLabel pictureFrame = new JLabel(imageIcon);
        panel.add(pictureFrame);

        BufferedImage labelImage = (BufferedImage) picture.getLabelImage();
        JLabel pictureFrame1 = new JLabel(new ImageIcon(labelImage));
        panel.add(pictureFrame1);

        new Thread(() -> recognizeTexturesInPicture(window1, classifier, picture, imageIcon,
                imageRecognizer1)).start();
    }

    private void recognizeTexturesInPicture(Window window1, Classifier classifier, Picture picture, ImageIcon
            imageIcon, ImageRecognizer imageRecognizer) {

        final BufferedImage[] imageWithRecognizedTextures = new BufferedImage[1];
        final double[] correctlyRecognizedPixelsPercentage = {0};

        BufferedImage labelImage = (BufferedImage) picture.getLabelImage();
        imageWithRecognizedTextures[0] = imageRecognizer.recognizeTextures(picture, classifier, imageIcon, window1);
        int imageWidth = labelImage.getWidth();
        int imageHeight = labelImage.getHeight();
        BufferedImage tempImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        for (int w = 0; w < imageWidth; w++) {
            for (int h = 0; h < imageHeight; h++) {
                tempImage.setRGB(w, h, imageWithRecognizedTextures[0].getRGB(w, h));
            }
        }
        correctlyRecognizedPixelsPercentage[0] = imageRecognizer
                .getCorrectlyRecognizedPixelsPercentage(tempImage, labelImage, true);
        showWindowWithResult(picture, tempImage, correctlyRecognizedPixelsPercentage[0]);
    }

    private void showWindowWithResult(Picture picture, BufferedImage imageWithMarkedPixels, double v) {
        String originalFileName = picture.getOriginalFileName();
        String resultWindowTitle = "Result for " + originalFileName + ": " +
                String.format("%.1f", v) + "% recognized";

        Window resultWindow = new Window(resultWindowTitle);
        JPanel panel1 = new JPanel();

        JLabel pictureFrame2 = new JLabel(new ImageIcon(imageWithMarkedPixels));
        panel1.add(pictureFrame2);

        resultWindow.add(panel1);
        resultWindow.setLocation(20, 20);
        resultWindow.setSize(1300, 700);
        resultWindow.pack();
        resultWindow.setVisible(true);
    }
}