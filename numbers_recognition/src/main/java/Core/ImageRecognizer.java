package Core;

import Extraction.FeaturesExtractor;
import Extraction.FeaturesVector;
import View.FileChoosePanel;
import View.ImageRecognitionPanel;
import View.TrainingDataLoadingPanel;
import View.Window;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class ImageRecognizer {

    public static LinkedList<Picture> loadPictures = new LinkedList<>();

    public static void loadTrainingData(File file, FileChoosePanel fileChoosePanel, Window window) {
        if (file != null) {
            window.remove(fileChoosePanel);
            String extension = FilenameUtils.getExtension(file.getName());
            LinkedList<Picture> pictures = new LinkedList<>();
            boolean isMnist = false;

            //FIXME akolodziejek: move magic string to field
            if(extension.contains("-ubyte")) {
                isMnist = true;
                MnistFilesLoader mnistFilesLoader = new MnistFilesLoader();
                try {
                    pictures = mnistFilesLoader.loadTrainingDataSet(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //potrzebne dopiero w 2 zadaniu
                //pictures = ImagesLoader.loadTrainingDataSet(files);
            }

            loadPictures = pictures;
            for(int i = 0; i < pictures.size();i++)
            {
                loadPictures.set(i,new Picture(ImageUtils.binarizeImage(ImageUtils.toBufferedImage(loadPictures.get(i)
                        .getImage())), loadPictures.get(i).getType()));
                loadPictures.set(i,new Picture(ThinnerImage.Start(loadPictures.get(i)),loadPictures.get(i).getType()));
            }

            window.add(new TrainingDataLoadingPanel(loadPictures, window, isMnist));
            FeaturesVector featuresVector = FeaturesExtractor.extractFeaturesVector(loadPictures);
            featuresVector.saveToFile();
        }
    }

    public static void initImageRecognition(File[] images, FileChoosePanel fileChoosePanel, Window window) {
        if (images != null) {
            window.remove(fileChoosePanel);

            String extension = FilenameUtils.getExtension(images[0].getName());
            LinkedList<Picture> pictures = new LinkedList<>();
            boolean isMnist = false;

            //FIXME akolodziejek: move magic string to field
            if (extension.contains("-ubyte")) {
                isMnist = true;
                MnistFilesLoader mnistFilesLoader = new MnistFilesLoader();
                try {
                    pictures = mnistFilesLoader.loadTrainingDataSet(images[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //potrzebne dopiero w 2 podpunkcie
                //pictures = ImagesLoader.loadTrainingDataSet(files);
            }

            loadPictures = pictures;
            for (int i = 0; i < pictures.size(); i++) {
                loadPictures.set(i, new Picture(ImageUtils.binarizeImage(ImageUtils.toBufferedImage(loadPictures.get(i)
                        .getImage())), loadPictures.get(i).getType()));
                loadPictures.set(i, new Picture(ThinnerImage.Start(loadPictures.get(i)), loadPictures.get(i).getType()));
            }

            ImageRecognitionPanel panel = new ImageRecognitionPanel(window);
            window.add(panel);

            LinkedList<Picture> temp = new LinkedList<>();
            if (isMnist) {
                window.setLocation(20, 20);
                window.setSize(700, 700);
                for (int i = 0; i < 100; i++) {
                    Picture picture = pictures.get(i);
                    addImage(picture.getImage(), panel);
                    temp.add(picture);
                }
            }
            panel.setPictures(temp);
            SwingUtilities.updateComponentTreeUI(window);
        }
    }

    private static void addImage(Image image, ImageRecognitionPanel panel) {
        image = ImageUtils.upscaleImage((BufferedImage) image, 2);
        ImageIcon icon = new ImageIcon(image);
        JLabel label = new JLabel(icon);
        panel.add(label);
    }
}
