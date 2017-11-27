package View.Panel;

import Classification.Classifier;
import Classification.KNearestNeighborsClassifier;
import Classification.NaiveBayesClassifier;
import Classification.ResultData;
import Core.ImageRecognizer;
import Extraction.Picture;
import View.Window.WindowTestRecognizer;
import View.Window.WindowTitleEnum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class ImageRecognitionPanel extends JPanel implements ActionListener {

    private JButton recognitionBtn;
    private JButton recognitionBtn1;
    private View.Window.Window window;
    private LinkedList<Picture> pictures;

    public void setPictures(LinkedList<Picture> pictures) {
        this.pictures = pictures;
    }

    public ImageRecognitionPanel(View.Window.Window window) {
        this.window = window;
        window.setTitle(WindowTitleEnum.RECOGNIZING_IMAGE.getName());

        recognitionBtn = new JButton("RECOGNIZE with KNN");
        recognitionBtn.addActionListener(this);
        recognitionBtn.setSize(50, 25);
        add(recognitionBtn, BorderLayout.CENTER);

        recognitionBtn1 = new JButton("RECOGNIZE with NaiveBayes");
        recognitionBtn1.addActionListener(this);
        recognitionBtn1.setSize(50, 25);
        add(recognitionBtn1, BorderLayout.CENTER);

        window.pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(recognitionBtn) || e.getSource().equals(recognitionBtn1)) {
            if (ImageRecognizer.loadFeaturesVector()) {
                java.util.LinkedList<Picture> picturesWithExtractedFeatures = new LinkedList<>();

                for (Picture picture : pictures) {
                    picturesWithExtractedFeatures.add(ImageRecognizer.calculateFeatureInOnePicture(picture));
                }

                Classifier classifier;
                if (e.getSource().equals(recognitionBtn)) {
                    classifier = new KNearestNeighborsClassifier();
                } else {
                    classifier = new NaiveBayesClassifier();
                }
                java.util.List<ResultData> result = classifier.classify(picturesWithExtractedFeatures, 10);
                window = WindowTestRecognizer.getTestWindows(result);
            }
            window.pack();
            window.setVisible(true);
        }
    }
}
