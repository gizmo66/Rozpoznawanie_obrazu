package Extraction;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.SortedMap;
import java.util.TreeMap;

public class PixelsBrightnessRatio implements Feature {

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public Number calculateValue(Picture picture) {
        BufferedImage image = (BufferedImage) picture.getImage();
        Color darkestColor = Color.WHITE;
        Color brightestColor = Color.BLACK;
        for (int w = 0; w < image.getWidth(); w++) {
            for (int h = 0; h < image.getHeight(); h++) {
                Color currentColor = new Color(image.getRGB(w, h));
                if (isBrighter(currentColor, brightestColor)) {
                    brightestColor = currentColor;
                }
                if (isDarker(currentColor, darkestColor)) {
                    darkestColor = currentColor;
                }
            }
        }
        int mid = (brightestColor.getRed() + darkestColor.getRed()) / 2;
        Color midBrightnessColor = new Color(mid, mid, mid);
        double darkPixelsQuantity = 0;
        SortedMap<Integer, Double> colorToQuantityMap = new TreeMap<>();
        for (int w = 0; w < image.getWidth(); w++) {
            for (int h = 0; h < image.getHeight(); h++) {
                Color currentColor = new Color(image.getRGB(w, h));
                if (currentColor.getRed() <= midBrightnessColor.getRed()) {
                    darkPixelsQuantity++;
                }
                colorToQuantityMap.merge(currentColor.getRed(), 1.0, (a, b) -> a + b);
            }
        }
        return (colorToQuantityMap.get(colorToQuantityMap.lastKey()) / darkPixelsQuantity) * 1000;
    }

    private boolean isDarker(Color currentColor, Color darkestColor) {
        return currentColor.getRed() < darkestColor.getRed();
    }

    private boolean isBrighter(Color currentColor, Color brightestColor) {
        return currentColor.getRed() > brightestColor.getRed();
    }
}
