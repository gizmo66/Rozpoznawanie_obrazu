package Core;

import File.ImageFileLoader;
import Image.ImageUtils;
import View.Panel.FileChoosePanel;
import View.Panel.ImageRecognitionPanel;
import View.Window.Window;
import View.Window.WindowTitleEnum;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;

@Slf4j
public class ImageRecognizer {

    private static final double WINDOW_SIZE_X = 1350;
    private static final double WINDOW_SIZE_Y = 700;

    private ImageRecognitionPanel imageRecognitionPanel;
    private LinkedList<Picture> loadedPictures = new LinkedList<>();

    public ImageRecognizer() {
    }

    public void initObjectCounting(File[] files, FileChoosePanel fileChoosePanel, Window window) {
        if (files != null && files.length > 0) {
            init(files, fileChoosePanel, window);

            imageRecognitionPanel = initWindow(window);
            addPicturesToPanel(imageRecognitionPanel, loadedPictures);
        }
    }

    public void addPicturesToPanel(JPanel panel, LinkedList<Picture> pictures) {
        double picturesQuantity = pictures.size();

        double originalMaxWidth = WINDOW_SIZE_X - 20;
        double originalMaxHeight = WINDOW_SIZE_Y - 60;

        double wholeHeight = 0.0;
        double rows = 1.0;
        double columns = picturesQuantity;
        double maxWidthForPicture = 1.0;

        double maxHeight = originalMaxHeight;
        double maxWidth = originalMaxWidth;

        while (wholeHeight < maxHeight) {
            maxWidthForPicture += 1.0;

            maxHeight = originalMaxHeight - 10.0 * rows;
            maxWidth = originalMaxWidth - 35.0 * columns;

            if (maxWidthForPicture * columns > maxWidth) {
                if (picturesQuantity > 1) {
                    columns -= 1.0;
                }
                if (rows * columns < picturesQuantity) {
                    rows += 1.0;
                }
            }

            int pictureInRowIndex = 0;
            wholeHeight = 0.0;
            for (double r = 0; r < rows; r++) {
                double maxHeightInRow = 0.0;
                for (int p = pictureInRowIndex; p < columns; p++) {
                    BufferedImage image = (BufferedImage) pictures.get(p).getImage();
                    double width = (double) image.getWidth();
                    double scale = maxWidthForPicture / width;
                    double height = (double) image.getHeight();
                    if (height * scale > maxHeightInRow) {
                        maxHeightInRow = height * scale;
                    }
                    pictureInRowIndex = p;
                }
                wholeHeight += maxHeightInRow;
            }
        }

        LinkedList<Picture> temp = new LinkedList<>();
        for (int i = 0; i < pictures.size(); i++) {
            Picture picture = pictures.get(i);
            BufferedImage image = (BufferedImage) picture.getImage();
            double width = (double) image.getWidth();
            double scale = maxWidthForPicture / width;
            addImage(picture.getImage(), panel, scale, scale, String.valueOf(i + 1));
            temp.add(picture);
        }
        imageRecognitionPanel.setPictures(temp);
    }

    private ImageRecognitionPanel initWindow(Window window) {
        ImageRecognitionPanel panel = new ImageRecognitionPanel(window, this);
        window.add(panel);
        window.setTitle(WindowTitleEnum.COUNTING_OBJECTS.getName());
        window.setLocation(10, 10);
        window.setSize((int) WINDOW_SIZE_X, (int) WINDOW_SIZE_Y);
        return panel;
    }

    private void init(File[] files, FileChoosePanel fileChoosePanel, Window window) {
        window.remove(fileChoosePanel);
        loadedPictures = loadPictures(files);
    }

    private LinkedList<Picture> loadPictures(File[] files) {
        LinkedList<Picture> pictures;
        ImageFileLoader imageFileLoader = new ImageFileLoader();
        pictures = imageFileLoader.loadImages(files);
        return pictures;
    }

    public void addImage(Image image, JPanel panel, double scaleX, double scaleY, String desc) {
        Image scaledImage = ImageUtils.scaleImage((BufferedImage) image, scaleX, scaleY);
        ImageIcon icon = new ImageIcon(scaledImage, desc);
        JLabel label = new JLabel(desc, icon, SwingConstants.CENTER);
        label.setVerticalTextPosition(JLabel.CENTER);
        label.setHorizontalTextPosition(JLabel.LEFT);
        label.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        panel.add(label);
    }

    private void saveResultToFile(BufferedImage imageForSaving, String fileName, String extension) {
        ImageUtils.save(imageForSaving, fileName, extension);
    }

    public LinkedHashMap<Picture, LinkedHashMap<String, Image>> countObjectsInPictures() {
        LinkedHashMap<Picture, LinkedHashMap<String, Image>> result = new LinkedHashMap<>();
        for (Picture picture : loadedPictures) {
            Image image = picture.getImage();
            LinkedHashMap<String, Image> descToImageMap = new LinkedHashMap<>();

            Image imageNoBackground = ImageUtils.removeBackground(image);
            descToImageMap.put("no background", imageNoBackground);

            Image imageMarkedRegions = ImageUtils.markRegions(imageNoBackground);
            descToImageMap.put("marked regions", imageMarkedRegions);

            result.put(picture, descToImageMap);
        }
        return result;
    }


}
