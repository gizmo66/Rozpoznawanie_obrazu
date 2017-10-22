package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileChoosePanel extends JPanel implements ActionListener {

    private static String newline = "\n";
    private JTextArea log;
    JFileChooser fileChooser;

    public FileChoosePanel() {
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
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            log.append("Attaching file: " + file.getName() + "." + newline);
        } else {
            log.append("Attachment cancelled by user." + newline);
        }
        log.setCaretPosition(log.getDocument().getLength());

        fileChooser.setSelectedFile(null);
    }

    protected void initFileChooser() {
        fileChooser = new JFileChooser("./data/");
    }
}
