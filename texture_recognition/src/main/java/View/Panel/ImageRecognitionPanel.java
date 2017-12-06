package View.Panel;

import Classification.Classifier;
import Classification.KNearestNeighborsClassifier;
import Classification.NaiveBayesClassifier;
import Classification.ResultData;
import Core.ImageRecognizer;
import Extraction.Picture;
import View.Window.*;
import View.Window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class ImageRecognitionPanel extends JPanel implements ActionListener {

    private JButton recognitionBtn;
    private JButton recognitionBtn1;
    private JButton recognitionBtn2;
    private JButton recognitionBtn3;
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

        window.pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(recognitionBtn) || e.getSource().equals(recognitionBtn1)) {
            if (window.imageRecognizer.loadFeaturesVector()) {
                java.util.LinkedList<Picture> picturesWithExtractedFeatures = new LinkedList<>();

                for (Picture picture : pictures) {
                    picturesWithExtractedFeatures.add(window.imageRecognizer.calculateFeatureInOnePicture(picture));
                }

                Classifier classifier;
                if (e.getSource().equals(recognitionBtn)) {
                    classifier = new KNearestNeighborsClassifier(window.imageRecognizer);
                } else {
                    classifier = new NaiveBayesClassifier(window.imageRecognizer);
                }
                java.util.List<ResultData> result = classifier.classify(picturesWithExtractedFeatures, 10);
                window = WindowTestRecognizer.getTestWindows(result, window.imageRecognizer);
            }
            window.pack();
            window.setVisible(true);
        } else {
            if (window.imageRecognizer.loadFeaturesVector()) {
                Window window1 = new Window("Recognized", window.imageRecognizer);
                JPanel panel = new JPanel();
                window1.add(panel);

                window1.setLocation(20, 20);
                window1.setSize(700, 700);
                window1.setVisible(true);

                Classifier classifier;
                if (e.getSource().equals(recognitionBtn2)) {
                    classifier = new KNearestNeighborsClassifier(window.imageRecognizer);
                } else {
                    classifier = new NaiveBayesClassifier(window.imageRecognizer);
                }

                for (Picture picture : pictures) {
                    ImageIcon imageIcon = new ImageIcon();
                    JLabel pictureFrame = new JLabel(imageIcon);
                    panel.add(pictureFrame);
                    new Thread(() -> window.imageRecognizer.recognizeTextures(picture, classifier, imageIcon, window1))
                            .start();
                }
            }
        }
    }
}