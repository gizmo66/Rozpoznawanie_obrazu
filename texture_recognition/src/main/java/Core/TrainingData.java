package Core;

import Extraction.Picture;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.LinkedList;

@Getter
public class TrainingData {

    private LinkedList<Picture> pictures = new LinkedList<>();
    private LinkedHashMap<String, Integer> classToQuantityMap = new LinkedHashMap<>();

    public TrainingData(LinkedList<Picture> pictures, LinkedHashMap<String, Integer> classToQuantityMap) {
        this.pictures = pictures;
        this.classToQuantityMap = classToQuantityMap;
    }
}
