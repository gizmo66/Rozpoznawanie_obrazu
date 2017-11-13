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

public class StartWindowPanel extends JPanel implements ActionListener {

    private JTextArea log;
    JFileChooser fileChooser;
    private ContextEnum context;
    private Window window;
    private JButton trainingProgramBtn,recognitionProgramBtn, testProgram, loadVector;
    private Application apk;

    public StartWindowPanel() {
        this.window = new Window("Choose program");

        trainingProgramBtn = new JButton("TRAINING");
        recognitionProgramBtn = new JButton("RECOGNITION");
        testProgram = new JButton("TEST PROGRAM");
        loadVector = new JButton("LOAD VECTOR");

        trainingProgramBtn.addActionListener(this);
        recognitionProgramBtn.addActionListener(this);
        testProgram.addActionListener(this);
        loadVector.addActionListener(this);

        loadVector.setSize(50,25);
        testProgram.setSize(50,25);
        trainingProgramBtn.setSize(50, 25);
        recognitionProgramBtn.setSize(50, 25);

        window.add(loadVector, BorderLayout.AFTER_LINE_ENDS);
        window.add(trainingProgramBtn, BorderLayout.PAGE_START);
        window.add(testProgram, BorderLayout.PAGE_END);
        window.add(recognitionProgramBtn, BorderLayout.CENTER);

        window.pack();
        window.setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(trainingProgramBtn)) {
            context = ContextEnum.TRAINING;
            handleFileAdding(context);
        }
        else if (e.getSource().equals(recognitionProgramBtn)) {
            context = ContextEnum.RECOGNITION;
            handleFileAdding(context);
        }
        else if (e.getSource().equals(testProgram)) {
            context = ContextEnum.TEST;
            handleFileAdding(context);
        }
        else if(e.getSource().equals(loadVector)) {
            context = ContextEnum.LOAD_VECTOR;
            handleFileAdding(context);
        }
    }

    private void handleFileAdding(ContextEnum context) {

        window.dispose();

        if(context.equals(ContextEnum.RECOGNITION)) {
            window = new Window("Choose image to recognize");
            window.add(new ImageFileChoosePanel(ContextEnum.RECOGNITION, window));
        } else if (context.equals(ContextEnum.TRAINING)) {
            window = new Window("Choose training data");
            window.add(new FileChoosePanel(ContextEnum.TRAINING, window));
        }else if (context.equals(ContextEnum.TEST)) {
            MnistFilesLoader mnistFilesLoader = new MnistFilesLoader();
            List<Picture> loadPictures = new ArrayList<>();
            try{

                loadPictures = mnistFilesLoader.loadTrainingDataSet(new File( "data/MNIST_database/t10k-images.idx3-ubyte"));
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
            for(int i = 400 ; i < 405;i++)
            {
                loadPictures.set(i,new Picture(ImageUtils.binarizeImage(ImageUtils.toBufferedImage( loadPictures.get(i).getImage())),
                        loadPictures.get(i).getType()));
                loadPictures.set(i,new Picture(ThinnerImage.Start( loadPictures.get(i)), loadPictures.get(i).getType()));
            }
            java.util.List<Picture> tempTest = new ArrayList<>();
            Random r  = new Random();
            for(int i = 400 ; i < 405 ; i ++)
            {
                //int index = r.nextInt((loadPictures.size() - 0) + 1) + 0;
                tempTest.add(FeaturesExtractor.calculateFeatureInOnePicture(loadPictures.get(i)));
            }
            List<ResultData> result = KNN.knnTEST(KNN.baseTrainingFile,tempTest,8);
            window = WindowTestRecognizer.getTestWindows(result);
        }else if(context.equals(ContextEnum.LOAD_VECTOR)){
            FeaturesVectorLoader t = new FeaturesVectorLoader();
            t.loadFeaturesVector();
        }

        window.pack();
        window.setVisible(true);
    }
}
