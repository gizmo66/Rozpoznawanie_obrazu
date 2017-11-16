package Extraction;

public enum FeaturesEnum {

    LINE_ENDS("LINE_ENDS"),
    CROSSING_POINTS("CROSSING_POINTS");

    private final String name;

    FeaturesEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
