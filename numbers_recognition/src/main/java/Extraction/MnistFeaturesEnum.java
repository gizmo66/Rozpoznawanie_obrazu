package Extraction;

public enum MnistFeaturesEnum {

    SURFACE("SURFACE"),
    QUARTER_SIZE_1("QUARTER_SIZE_1"),
    QUARTER_SIZE_2("QUARTER_SIZE_2"),
    QUARTER_SIZE_3("QUARTER_SIZE_3"),
    QUARTER_SIZE_4("QUARTER_SIZE_4");

    private final String name;

    MnistFeaturesEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
