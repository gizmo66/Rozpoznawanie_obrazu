package Extraction;

public class Brightness implements Feature {

    @Override
    public Number calculateValue(Picture picture) {
        //TODO akolodziejek
        return 0;
    }

    @Override
    public String getFeatureName() {
        return FeatureNameEnum.BRIGHTNESS.getName();
    }
}
