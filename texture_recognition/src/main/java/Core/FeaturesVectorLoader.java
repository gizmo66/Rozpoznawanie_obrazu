package Core;

import Extraction.FeaturesVector;
import Extraction.Picture;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class FeaturesVectorLoader {

    private static final String FEATURES_VECTOR_FILE_NAME = "FeaturesVector.fv";

    public boolean loadFeaturesVector() {
        boolean featuresVectorLoaded = readFile();
        fillUpTrainingSets();
        return featuresVectorLoaded;
    }

    private void readFeaturesVector(BufferedReader br) throws IOException {
        for (String strLine; (strLine = br.readLine()) != null; ) {
            if (strLine.contains(FeaturesVector.CLASS_TAG_START)) {
                String imageClass = strLine.split("\"")[1];
                Map<String, LinkedList<Number>> featureNameToValuesMap = new LinkedHashMap<>();
                while (true) {
                    strLine = br.readLine();
                    if (strLine.contains(FeaturesVector.CLASS_TAG_END)) {
                        FeaturesVector.imageClassToFeaturesValuesMap.put(imageClass,
                                featureNameToValuesMap);
                        break;
                    }
                    String featureName = strLine.split("\"")[1];
                    String valuesLine = br.readLine();
                    valuesLine = valuesLine.replaceAll("\\[", "");
                    valuesLine = valuesLine.replaceAll("]", "");
                    valuesLine = valuesLine.replaceAll("\t", "");

                    String[] valuesArray = StringUtils.split(valuesLine, ",");
                    LinkedList<Number> values = new LinkedList<>();
                    for (String strValue : Arrays.asList(valuesArray)) {
                        Float value = Float.valueOf(strValue);
                        values.add(value);
                    }
                    featureNameToValuesMap.put(featureName, values);
                    String featureEndTag = br.readLine();
                }
            }
        }
    }

    private void fillUpTrainingSets() {
        LinkedList<Picture> tempTrainingList = new LinkedList<>();
        LinkedHashMap<String, Integer> classToQuantityMap = new LinkedHashMap<>();

        for (String imageClass : FeaturesVector.imageClassToFeaturesValuesMap.keySet()) {
            Map<String, LinkedList<Number>> featureNameToValuesMap = FeaturesVector.imageClassToFeaturesValuesMap.get
                    (imageClass);
            for (int i = 0; i < featureNameToValuesMap.entrySet().iterator().next().getValue().size(); i++) {
                LinkedList<Number> features = new LinkedList<>();
                for (String feature : featureNameToValuesMap.keySet()) {
                    features.add(featureNameToValuesMap.get(feature).get(i));
                }
                Picture tempPicture = new Picture(imageClass, features);
                tempTrainingList.add(tempPicture);
                if(classToQuantityMap.containsKey(imageClass)) {
                    classToQuantityMap.replace(imageClass, classToQuantityMap.get(imageClass) + 1);
                } else {
                    classToQuantityMap.put(imageClass, 1);
                }
            }
        }

        ImageRecognizer.trainingData.setPictures(tempTrainingList);
        ImageRecognizer.trainingData.setClassToQuantityMap(classToQuantityMap);
    }

    private boolean readFile() {
        try {
            FileInputStream fstream = new FileInputStream(FEATURES_VECTOR_FILE_NAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            readFeaturesVector(br);
            br.close();
            return true;
        } catch (Exception e) {
            System.err.println("Error: " + e);
            return false;
        }
    }
}
