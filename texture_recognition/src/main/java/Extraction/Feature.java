package Extraction;

public interface Feature {

    String CALCULATE_FEATURE_METHOD = "calculateValue";
    String GET_FEATURE_NAME_METHOD = "getFeatureName";

    Number calculateValue(Picture picture);

    default String getFeatureName() {
        return this.getClass().getSimpleName();
    }
}
