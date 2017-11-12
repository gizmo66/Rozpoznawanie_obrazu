package Core;

import lombok.Getter;

import java.awt.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.Arrays;

@Getter
public class Picture {

    private final Image image;
    private final String type;

    private float surface;
    private float verticalLine;
    private float horizontalLine;
    private float endedNumber;

    public int label;
    public double distance;

    public Picture(Image image, String type) {
        this.image = image;
        this.type = type;

        distance = 0;
    }

    public Picture(String type, int lable, float s, float v, float h, float e) {
        this.surface = s;
        this.verticalLine = v;
        this.horizontalLine = h;
        this.endedNumber = e;
        this.type = type;
        this.label = lable;
        image = null;
        distance = 0;
    }

    public Picture(Image image, String type, float s, float v, float h, float e) {
        this.surface = s;
        this.verticalLine = v;
        this.horizontalLine = h;
        this.endedNumber = e;
        this.image = image;
        this.type = type;
        distance = 0;
    }

    public ArrayList<Float> getCharasteristic()
    {
        return new ArrayList<Float>(
                Arrays.asList(surface,verticalLine,horizontalLine,endedNumber));
    }

    public Image getImage()
    {
        return image;
    }

    public String getType() {
        return type;
    }
}
