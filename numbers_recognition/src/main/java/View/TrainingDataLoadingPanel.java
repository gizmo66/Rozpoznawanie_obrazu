package View;

import Core.Picture;
import Core.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.LinkedList;

public class TrainingDataLoadingPanel extends JPanel {

    public TrainingDataLoadingPanel(LinkedList<Picture> pictures, Window window, boolean isMnist) {
        window.setTitle("Training data");

        //TODO: przy 2-gim zadaniu (dla innej bazy) - wyświetlać inaczej
        if (isMnist) {
            window.setLocation(20, 20);
            window.setSize(1250, 700);
            pictures.sort(Comparator.comparing(Picture::getType));
            String currentType = "";
            int j = 0;
            int t = 0;
            for(int i = 0; t < 11 && i < pictures.size(); i++) {
                Picture picture = pictures.get(i);
                String type = picture.getType();
                if(type.equals(currentType)) {
                    j++;
                } else {
                    currentType = type;
                    j = 0;
                    t++;
                }
                if(j < 20) {
                    addImage(picture.getImage());
                }
            }
        }
        SwingUtilities.updateComponentTreeUI(window);
    }

    private void addImage(Image image) {
        image = ImageUtils.upscaleImage((BufferedImage) image, 2);
        ImageIcon icon = new ImageIcon(image);
        JLabel label = new JLabel(icon);
        this.add(label);
    }
}
