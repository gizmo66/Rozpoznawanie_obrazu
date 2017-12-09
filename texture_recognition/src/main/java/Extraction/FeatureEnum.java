package Extraction;

import Extraction.Features.Image.BrightToDarkPixelsRatio;
import Extraction.Features.Spectrum.BluePixelsInSpectrumPercentage;
import Extraction.Features.Spectrum.GreenPixelsInSpectrumPercentage;
import Extraction.Features.Spectrum.RedPixelsInSpectrumPercentage;
import Extraction.Features.Spectrum.YellowPixelsInSpectrumPercentage;
import lombok.Getter;

@Getter
public enum FeatureEnum {

    //from spectrum
    F1(BluePixelsInSpectrumPercentage.class.getSimpleName(), true),
    F2(YellowPixelsInSpectrumPercentage.class.getSimpleName(), true),
    F3(GreenPixelsInSpectrumPercentage.class.getSimpleName(), true),
    F4(RedPixelsInSpectrumPercentage.class.getSimpleName(), true),

    //from image
    F5(BrightToDarkPixelsRatio.class.getSimpleName(), true);

    private final String simpleName;
    private final boolean isActive;

    FeatureEnum(String simpleName, boolean isActive) {
        this.simpleName = simpleName;
        this.isActive = isActive;
    }
}
