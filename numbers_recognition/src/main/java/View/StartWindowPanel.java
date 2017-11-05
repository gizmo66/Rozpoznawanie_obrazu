package View;

import Core.Application;
import Core.ContextEnum;
import Core.ImageRecognizer;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StartWindowPanel extends JPanel implements ActionListener {

    private JTextArea log;
    JFileChooser fileChooser;
    private ContextEnum context;
    private Window window;
    private JButton trainingProgramBtn,recognitionProgramBtn, testProgram;
    private Application apk;

    public StartWindowPanel() {
        this.window = new Window("Choose program");

        trainingProgramBtn = new JButton("TRAINING");
        recognitionProgramBtn = new JButton("RECOGNITION");
        testProgram = new JButton("TEST PROGRAM");

        trainingProgramBtn.addActionListener(this);
        recognitionProgramBtn.addActionListener(this);
        testProgram.addActionListener(this);

        testProgram.setSize(50,25);
        trainingProgramBtn.setSize(50, 25);
        recognitionProgramBtn.setSize(50, 25);

        window.add(trainingProgramBtn, BorderLayout.PAGE_START);
        window.add(testProgram, BorderLayout.PAGE_END);
        window.add(recognitionProgramBtn, BorderLayout.CENTER);

        window.pack();
        window.setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(trainingProgramBtn))
        {
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
            window = new Window("TEST PROGRAM");
            Map<String,Boolean> tempMap = new HashMap<>();
            tempMap.put("1",true);
            tempMap.put("3",false);
            tempMap.put("2",true);
            window = WindowTestRecognizer.getTestWindows(tempMap);
        }

        window.pack();
        window.setVisible(true);
    }
}
