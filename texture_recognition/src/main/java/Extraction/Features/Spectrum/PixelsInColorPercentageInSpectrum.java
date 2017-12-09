package Extraction.Features.Spectrum;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PixelsInColorPercentageInSpectrum {

    double getPixelsInColorQuantity(BufferedImage spectrum, Color color) {
        double pixelsQuantity = 0;
        for (int w = 0; w < spectrum.getWidth(); w++) {
            for (int h = 0; h < spectrum.getHeight(); h++) {
                Color currentPixelColor = new Color(spectrum.getRGB(w, h));
                if (isSimilar(currentPixelColor, color)) {
                    pixelsQuantity++;
                }
            }
        }
        return pixelsQuantity;
    }

    private boolean isSimilar(Color color1, Color color2) {
        int r1 = color1.getRed();
        int g1 = color1.getGreen();
        int b1 = color1.getBlue();

        int r2 = color2.getRed();
        int g2 = color2.getGreen();
        int b2 = color2.getBlue();

        double distance = (r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2);
        return distance < 60000;
    }
}
