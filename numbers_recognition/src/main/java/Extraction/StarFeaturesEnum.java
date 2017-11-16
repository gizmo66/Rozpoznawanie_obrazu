package Extraction;

public enum StarFeaturesEnum {

    EDGES_TO_SURFACE("EDGES_TO_SURFACE");

    private final String name;

    StarFeaturesEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
