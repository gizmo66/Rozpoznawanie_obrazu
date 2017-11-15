package Core;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.LinkedList;

@Getter
public class Picture {

    private Image image;
    private final String type;

    private LinkedList<Number> features;

    public int label;
    public double distance;

    public Picture(Image image, String type) {
        this.image = image;
        this.type = type;

        distance = 0;
    }

    public Picture(String type, int lable, LinkedList<Number> features) {
        this.features = features;
        this.type = type;
        this.label = lable;
        image = null;
        distance = 0;
    }

    public Picture(Image image, String type, LinkedList<Number> features) {
        this.features = features;
        this.image = image;
        this.type = type;
        distance = 0;
    }

    public LinkedList<Number> getCharasteristic() {
        return features;
    }

    public Image getImage() {
        return image;
    }

    public String getType() {
        return type;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
