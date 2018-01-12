package View.Panel;

import Core.ImageRecognizer;
import Core.Picture;
import View.Window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImageRecognitionPanel extends JPanel implements ActionListener {

    private View.Window.Window window;
    private ImageRecognizer imageRecognizer;

    public ImageRecognitionPanel(View.Window.Window window, ImageRecognizer imageRecognizer) {
        this.window = window;
        this.imageRecognizer = imageRecognizer;

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem menuItem;

        menuBar.add(menu);
        menuItem = new JMenuItem("Count objects in the pictures");
        menu.add(menuItem);

        menuItem.addActionListener(this);

        this.window.setJMenuBar(menuBar);
        initWindow(window);
    }

    private void initWindow(Window window) {
        window.setSize(700, 700);
        window.pack();
        window.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        LinkedHashMap<Picture, LinkedHashMap<String, java.awt.Image>> pictureToTransformationMap = imageRecognizer
                .countObjectsInPictures();

        for (Map.Entry<Picture, LinkedHashMap<String, Image>> pictureToTransformations : pictureToTransformationMap
                .entrySet()) {

            Window window = new Window("");
            window.setLocation(30, 30);
            window.setSize(1300, 700);
            window.setVisible(true);

            JPanel panel = new JPanel();
            for (Map.Entry<String, Image> descToImage : pictureToTransformations.getValue().entrySet()) {
                BufferedImage image = (BufferedImage) descToImage.getValue();
                double scale = 730.0 / (double) image.getWidth();
                imageRecognizer.addImage(descToImage.getValue(), panel, scale, scale, descToImage.getKey());
                window.add(panel);
            }
        }
    }


}