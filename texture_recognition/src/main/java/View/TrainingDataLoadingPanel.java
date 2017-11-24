package View;

import Core.ContextEnum;
import Core.Picture;
import Core.ImageUtils;
import Core.WindowTitleEnum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.LinkedList;

public class TrainingDataLoadingPanel extends JPanel implements ActionListener {

    private JButton recognitionProgramBtn;
    private Window window;

    public TrainingDataLoadingPanel(LinkedList<Picture> pictures, Window window) {
        this.window = window;
        window.setTitle(WindowTitleEnum.TRAINING_DATA.getName());
        window.setLocation(20, 20);
        window.setSize(1300, 700);
        for(int i = 0; i < (pictures.size() <= 18 ? pictures.size() : 18); i++) {
            Picture picture = pictures.get(i);
            addImage(picture.getImage(), 1f);
        }

        recognitionProgramBtn = new JButton("Go To RECOGNITION");
        recognitionProgramBtn.addActionListener(this);
        recognitionProgramBtn.setSize(200, 25);
        window.add(recognitionProgramBtn, BorderLayout.CENTER);
    }

    private void addImage(Image image, float scale) {
        Image upscaleImage = ImageUtils.upscaleImage((BufferedImage) image, scale);
        ImageIcon icon = new ImageIcon(upscaleImage);
        JLabel label = new JLabel(icon);
        this.add(label);
    }

    public void actionPerformed(ActionEvent e) {
        window.remove(this);
        window = new Window(WindowTitleEnum.CHOOSE_IMAGE_TO_RECOGNIZE.getName());
        window.add(new ImageFileChoosePanel(ContextEnum.RECOGNITION, window));
        window.pack();
        window.setVisible(true);
    }
}
