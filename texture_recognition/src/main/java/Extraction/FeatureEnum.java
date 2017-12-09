package Extraction;

import lombok.Getter;

@Getter
public enum FeatureEnum {

    //from spectrum
    F1(Extraction.BluePixelsInSpectrumPercentage.class.getSimpleName(), true),
    F2(Extraction.YellowPixelsInSpectrumPercentage.class.getSimpleName(), true),
    F3(Extraction.GreenPixelsInSpectrumPercentage.class.getSimpleName(), true),
    F4(Extraction.RedPixelsInSpectrumPercentage.class.getSimpleName(), true),

    //from image
    F5(Extraction.BrightToDarkPixelsRatio.class.getSimpleName(), true);

    private final String simpleName;
    private final boolean isActive;

    FeatureEnum(String simpleName, boolean isActive) {
        this.simpleName = simpleName;
        this.isActive = isActive;
    }
}
