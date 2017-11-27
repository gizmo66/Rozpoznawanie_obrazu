package Extraction;

public interface Feature {

    String CALCULATE_FEATURE_METHOD = "calculateValue";
    String GET_FEATURE_NAME_METHOD = "getFeatureName";

    FeatureNameEnum featureName = FeatureNameEnum.DEFAULT;

    Number calculateValue(Picture picture);
    String getFeatureName();
}
