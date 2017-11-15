package View;

import Core.*;
import Extraction.FeaturesExtractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

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
        if(context.equals(ContextEnum.RECOGNITION)) {
            FeaturesVectorLoader t = new FeaturesVectorLoader();
            if(t.loadFeaturesVector()){
                java.util.LinkedList<Picture> tempTest = new LinkedList<>();
                for (Picture picture : pictures) {
                    tempTest.add(FeaturesExtractor.calculateFeatureInOnePicture(picture));
                }

                java.util.List<ResultData> result = KNN.knnTEST(KNN.baseTrainingFile,tempTest,10);
                window = WindowTestRecognizer.getTestWindows(result);
            }
        }

        window.pack();
        window.setVisible(true);
    }
}
