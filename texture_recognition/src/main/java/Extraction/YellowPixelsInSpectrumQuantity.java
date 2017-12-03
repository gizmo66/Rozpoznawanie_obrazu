package Extraction;

import java.awt.*;
import java.awt.image.BufferedImage;

public class YellowPixelsInSpectrumQuantity extends PixelsInColorQuantityInSpectrum implements Feature {

    @Override
    public Number calculateValue(Picture picture) {
        BufferedImage spectrum = (BufferedImage) picture.getSpectrum();
        return getPixelsInColorQuantity(spectrum, Color.YELLOW);
    }
}
