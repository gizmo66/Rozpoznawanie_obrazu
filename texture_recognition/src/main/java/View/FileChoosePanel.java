package View;

import Core.ContextEnum;
import Core.ImageRecognizer;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileChoosePanel extends JPanel implements ActionListener {

    private JTextArea log;
    JFileChooser fileChooser;
    private ContextEnum context;
    private Window window;

    public FileChoosePanel(ContextEnum context, Window window) {
        this.context = context;
        this.window = window;

        log = new JTextArea(5,20);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);

        JButton chooseFileBtn = new JButton("Choose file");
        chooseFileBtn.addActionListener(this);
        chooseFileBtn.setSize(40, 10);

        add(chooseFileBtn, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        if (fileChooser == null) {
            initFileChooser();
        }

        int returnVal = fileChooser.showDialog(this, "Choose");
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            log.append("Attachment cancelled by user." + StringUtils.LF);
        }
        log.setCaretPosition(log.getDocument().getLength());

        handleFileAdding(context, fileChooser);
        fileChooser.setSelectedFile(null);
    }

    private void handleFileAdding(ContextEnum context, JFileChooser fileChooser) {
        if(context.equals(ContextEnum.TRAINING)) {
            ImageRecognizer.loadTrainingData(fileChooser.getSelectedFiles(), this, window);
        } else if (context.equals(ContextEnum.RECOGNITION)) {
            ImageRecognizer.initImageRecognition(fileChooser.getSelectedFiles(), this, window);
        }
    }

    protected void initFileChooser() {
        fileChooser = new JFileChooser("./data/");
        fileChooser.setMultiSelectionEnabled(true);
    }
}
