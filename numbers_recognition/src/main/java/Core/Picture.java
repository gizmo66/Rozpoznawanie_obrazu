package Core;

import lombok.Getter;

import java.awt.*;
import java.util.List;

@Getter
public class Picture {

    private final Image image;
    private final String type;

    private List<Number> features;

    public int label;
    public double distance;

    public Picture(Image image, String type) {
        this.image = image;
        this.type = type;

        distance = 0;
    }

    public Picture(String type, int lable, List<Number> features) {
        this.features = features;
        this.type = type;
        this.label = lable;
        image = null;
        distance = 0;
    }

    public Picture(Image image, String type, List<Number> features) {
        this.features = features;
        this.image = image;
        this.type = type;
        distance = 0;
    }

    public List<Number> getCharasteristic() {
        return features;
    }

    public Image getImage() {
        return image;
    }

    public String getType() {
        return type;
    }
}
