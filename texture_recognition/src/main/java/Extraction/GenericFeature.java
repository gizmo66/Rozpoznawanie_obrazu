package Extraction;

public interface GenericFeature {

    default String getFeatureName() {
        return this.getClass().getSimpleName();
    }
}
