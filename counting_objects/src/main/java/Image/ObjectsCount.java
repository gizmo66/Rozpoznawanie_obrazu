package Image;

import lombok.Getter;

@Getter
public class ObjectsCount {

    private final int lightObjectsCount;
    private final int darkObjectsCount;

    public ObjectsCount(int lightObjectsCount, int darkObjectsCount) {
        this.lightObjectsCount = lightObjectsCount;
        this.darkObjectsCount = darkObjectsCount;
    }

}
