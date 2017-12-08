package Extraction;

import java.awt.*;
import java.awt.image.BufferedImage;

public class YellowPixelsInSpectrumPercentage extends PixelsInColorPercentageInSpectrum implements Feature {

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public Number calculateValue(Picture picture) {
        BufferedImage spectrum = (BufferedImage) picture.getSpectrum();
        double size = spectrum.getWidth() * spectrum.getHeight();
        return getPixelsInColorQuantity(spectrum, Color.YELLOW) / size;
    }
}
