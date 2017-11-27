package Extraction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class FeaturesVector {

    public static Map<String, Map<String, LinkedList<Number>>> imageClassToFeaturesValuesMap = new LinkedHashMap<>();

    private static final String FEATURE_TAG_START = "<FEATURE name=\"";
    public static final String CLASS_TAG_START = "<CLASS name=\"";
    public static final String CLASS_TAG_END = "</CLASS>";
    private static final String FEATURE_TAG_END = "</FEATURE>";

    FeaturesVector(Map<String, Map<String, LinkedList<Number>>> imageClassToFeaturesValuesMap) {
        FeaturesVector.imageClassToFeaturesValuesMap = imageClassToFeaturesValuesMap;
    }

    private void save(Map<String, Map<String, LinkedList<Number>>> imageClassToFeaturesValuesMap, BufferedWriter bf)
            throws IOException {
        for (Map.Entry<String, Map<String, LinkedList<Number>>> imageClassToFeaturesValues : imageClassToFeaturesValuesMap.entrySet()) {
            bf.newLine();
            bf.write(CLASS_TAG_START + imageClassToFeaturesValues.getKey() + "\">");
            bf.newLine();
            for (Map.Entry<String, LinkedList<Number>> featureNameToValues : imageClassToFeaturesValues.getValue().entrySet
                    ()) {
                bf.write("\t");
                bf.write(FEATURE_TAG_START + featureNameToValues.getKey() + "\">");
                bf.newLine();
                bf.write("\t\t");
                bf.write(featureNameToValues.getValue().toString());
                bf.newLine();
                bf.write("\t");
                bf.write(FEATURE_TAG_END);
                bf.newLine();
            }
            bf.write(CLASS_TAG_END);
            bf.newLine();
        }
    }

    public void saveToFile() {
        try {
            FileWriter fstream = new FileWriter("FeaturesVector.fv");
            BufferedWriter out = new BufferedWriter(fstream);
            save(imageClassToFeaturesValuesMap, out);
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
