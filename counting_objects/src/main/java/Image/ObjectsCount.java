package Image;

import lombok.Getter;

@Getter
public class ObjectsCount {

    private final int lightObjectsCount;
    private final int darkObjectsCount;
    private int expectedLightCount;
    private int expectedDarkCount;

    public ObjectsCount(int lightObjectsCount, int darkObjectsCount, int expectedLightCount, int expectedDarkCount) {
        this.lightObjectsCount = lightObjectsCount;
        this.darkObjectsCount = darkObjectsCount;
        this.expectedLightCount = expectedLightCount;
        this.expectedDarkCount = expectedDarkCount;
    }

}
