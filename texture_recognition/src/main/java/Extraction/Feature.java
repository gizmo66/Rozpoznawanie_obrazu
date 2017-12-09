package Extraction;

public interface Feature extends GenericFeature {

    String CALCULATE_FEATURE_METHOD = "calculateValue";
    String GET_FEATURE_NAME_METHOD = "getFeatureName";

    Number calculateValue(Picture picture);
}
