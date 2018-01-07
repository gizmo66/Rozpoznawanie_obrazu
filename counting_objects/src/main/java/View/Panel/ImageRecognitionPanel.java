package View.Panel;

import Core.ImageRecognizer;
import Core.Picture;
import View.Window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class ImageRecognitionPanel extends JPanel implements ActionListener {

    private JButton countBtn;
    private View.Window.Window window;
    private LinkedList<Picture> pictures;
    private ImageRecognizer imageRecognizer;

    public void setPictures(LinkedList<Picture> pictures) {
        this.pictures = pictures;
    }

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
        LinkedHashMap<Picture, LinkedHashMap<java.awt.Image, String>> pictureToTransformationMap = imageRecognizer
                .countObjectsInPictures();

        for (Map.Entry<Picture, LinkedHashMap<Image, String>> pictureToTransformations : pictureToTransformationMap.entrySet()) {

            Window window = new Window("");
            window.setLocation(30, 30);
            window.setSize(1300, 700);
            window.setVisible(true);

            for (Map.Entry<Image, String> imageToString : pictureToTransformations.getValue().entrySet()) {
                JPanel panel = new JPanel();
                imageRecognizer.addImage(imageToString.getKey(), panel, 0.5, 0.5, imageToString.getValue());
                window.add(panel);
            }
        }
    }


}