package Extraction;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.LinkedList;

@Getter
@Setter
public class Picture {

    private Image image;
    private String type;
    private LinkedList<Number> features;
    private double distance;

    public Picture(Image image, String type) {
        this.image = image;
        this.type = type;

        distance = 0;
    }

    public Picture(String type, LinkedList<Number> features) {
        this.features = features;
        this.type = type;
        image = null;
        distance = 0;
    }

    public Picture(Image image, String type, LinkedList<Number> features) {
        this.features = features;
        this.image = image;
        this.type = type;
        distance = 0;
    }
}
