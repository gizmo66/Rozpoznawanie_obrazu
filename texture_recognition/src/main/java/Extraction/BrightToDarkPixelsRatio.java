package Extraction;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BrightToDarkPixelsRatio implements Feature {

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public Number calculateValue(Picture picture) {
        BufferedImage image = (BufferedImage) picture.getImage();
        Color darkestColor = Color.WHITE;
        Color brightestColor = Color.BLACK;
        for(int w = 0; w < image.getWidth(); w++) {
            for (int h = 0; h < image.getHeight(); h++) {
                Color currentColor = new Color(image.getRGB(w, h));
                if(isBrighter(currentColor, brightestColor)) {
                    brightestColor = currentColor;
                }
                if(isDarker(currentColor, darkestColor)) {
                    darkestColor = currentColor;
                }
            }
        }
        int mid = (brightestColor.getRed() + darkestColor.getRed()) / 2;
        Color midBrightnessColor = new Color(mid, mid, mid);
        double brightPixelsQuantity = 0;
        double darkPixelsQuantity = 0;
        for(int w = 0; w < image.getWidth(); w++) {
            for (int h = 0; h < image.getHeight(); h++) {
                Color currentColor = new Color(image.getRGB(w, h));
                if (currentColor.getRed() > midBrightnessColor.getRed()) {
                    brightPixelsQuantity++;
                } else {
                    darkPixelsQuantity++;
                }
            }
        }
        return brightPixelsQuantity / darkPixelsQuantity;
    }

    private boolean isDarker(Color currentColor, Color darkestColor) {
        return currentColor.getRed() < darkestColor.getRed();
    }

    private boolean isBrighter(Color currentColor, Color brightestColor) {
        return currentColor.getRed() > brightestColor.getRed();
    }
}
