package Image;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum Settings {

    grapes_1("grapes_1", 1000, 2, true, 40000, 18, 23),
    grapes_2("grapes_2", 300, 10, true, 40000, 16, 25),
    grapes_3("grapes_3", 400, 10, true, 40000, 19, 23),
    grapes_4("grapes_4", 300, 10, true, 40000, 23, 25),
    grapes_5("grapes_5", 300, 10, true, 40000, 22, 5),
    grapes_6("grapes_6", 800, 8, true, 40000, 11, 9),
    grapes_7("grapes_7", 400, 11, true, 40000, 20, 23),
    grapes_8("grapes_8", 400, 10, true, 40000, 23, 24),
    grapes_9("grapes_9", 400, 11, true, 40000, 20, 18),
    grapes_10("grapes_10", 50, 14, true, 40000, 24, 13),
    grapes_11("grapes_11", 500, 9, true, 40000, 14, 15),
    grapes_12("grapes_12", 30, 14, true, 40000, 30, 30),
    grapes_13("grapes_13", 100, 14, true, 40000, 29, 28),
    grapes_14("grapes_14", 300, 11, true, 40000, 22, 24),
    grapes_15("grapes_15", 50, 10, true, 40000, 25, 22),

    nuts_1("nuts_1", 1000, 0, false, 25000, 12, 13),
    nuts_2("nuts_2", 20, 6, false, 25000, 35, 23),
    nuts_3("nuts_3", 50, 7, false, 25000, 30, 38),
    nuts_4("nuts_4", 1000, 0, false, 15000, 38, 35),
    nuts_5("nuts_5", 50, 7, false, 25000, 87, 52),
    nuts_6("nuts_6", 30, 7, false, 25000, 20, 29),
    nuts_7("nuts_7", 10, 8, false, 25000, 79, 48);

    private String fileName;
    private int minRegionSize;
    private int erosionIterations;
    private boolean useGaussianFilter;
    private int maxDistance;
    private int lightQuantity;
    private int darkQuantity;

    Settings(String fileName, int minRegionSize, int erosionIterations, boolean useGaussianFilter, int maxDistance, int lightQuantity, int darkQuantity) {
        this.fileName = fileName;
        this.minRegionSize = minRegionSize;
        this.erosionIterations = erosionIterations;
        this.useGaussianFilter = useGaussianFilter;
        this.maxDistance = maxDistance;
        this.lightQuantity = lightQuantity;
        this.darkQuantity = darkQuantity;
    }

    public static Settings getSettings(String fileName) {
        Optional<Settings> settings = Arrays.stream(Settings.values()).filter(s -> s.fileName.equals(fileName))
                .findFirst();
        return settings.orElseThrow(() -> new EnumConstantNotPresentException(Settings.class, fileName));
    }

}
