package View;

import Classification.KNearestNeighborsClassifier;
import Classification.ResultData;
import Core.ContextEnum;
import Core.FeaturesVectorLoader;
import Extraction.FeaturesExtractor;
import Extraction.Picture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class ImageRecognitionPanel extends JPanel implements ActionListener {

    private ContextEnum context;
    private JButton recognitionBtn;
    private Window window;
    private LinkedList<Picture> pictures;

    public void setPictures(LinkedList<Picture> pictures) {
        this.pictures = pictures;
    }

    public ImageRecognitionPanel(Window window) {
        this.window = window;
        window.setTitle(WindowTitleEnum.RECOGNIZING_IMAGE.getName());

        recognitionBtn = new JButton("RECOGNIZE");
        recognitionBtn.addActionListener(this);
        recognitionBtn.setSize(50, 25);
        add(recognitionBtn, BorderLayout.CENTER);

        window.pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(recognitionBtn)) {
            context = ContextEnum.RECOGNITION;
            handleFileAdding(context);
        }
    }

    private void handleFileAdding(ContextEnum context) {
        if (context.equals(ContextEnum.RECOGNITION)) {
            FeaturesVectorLoader featuresVectorLoader = new FeaturesVectorLoader();
            if (featuresVectorLoader.loadFeaturesVector()) {
                java.util.LinkedList<Picture> picturesWithExtractedFeatures = new LinkedList<>();
                for (Picture picture : pictures) {
                    picturesWithExtractedFeatures.add(FeaturesExtractor.calculateFeatureInOnePicture(picture));
                }

                KNearestNeighborsClassifier classifier = new KNearestNeighborsClassifier();
                java.util.List<ResultData> result = classifier.classify(picturesWithExtractedFeatures,10);
                window = WindowTestRecognizer.getTestWindows(result);
            }
        }

        window.pack();
        window.setVisible(true);
    }
}
