package Core;

import Extraction.Picture;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.LinkedList;

@Getter
@Setter
public class TrainingData {

    private LinkedList<Picture> pictures = new LinkedList<>();
    private LinkedHashMap<String, Integer> classToQuantityMap = new LinkedHashMap<>();
}
