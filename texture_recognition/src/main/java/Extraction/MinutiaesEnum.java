package Extraction;

public enum MinutiaesEnum {

    ENDING_POINT("ENDING_POINT"),
    CROSSING_POINT("CROSSING_POINT"),
    EDGES_LENGTH("EDGES_LENGTH");

    private final String name;

    MinutiaesEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
