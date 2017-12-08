package Extraction;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.LinkedList;

@Getter
@Setter
public class Picture {

    private Image image;
    private Image labelImage;
    private Image spectrum;
    private String type;
    private LinkedList<Number> features;
    private double distance;
    private String originalFileName;

    public Picture(Image image, Image labelImage, String type, String originalFileName) {
        this.image = image;
        this.labelImage = labelImage;
        this.type = type;
        this.originalFileName = originalFileName;
        distance = 0;
    }

    public Picture(String type, LinkedList<Number> features) {
        this.features = features;
        this.type = type;
        image = null;
        distance = 0;
    }
}
