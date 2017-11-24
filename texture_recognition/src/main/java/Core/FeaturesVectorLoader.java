package Core;

import Extraction.FeaturesVector;
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

        //Read File Line By Line
        for(String strLine; (strLine = br.readLine()) != null; ) {
            if (strLine.contains(FeaturesVector.CLASS_TAG_START)) {
                String imageClass = strLine.split("\"")[1];
                Map<String, LinkedList<Number>> featureNameToValuesMap = new LinkedHashMap<>();
                while (true){
                    strLine = br.readLine();
                    if(strLine.contains(FeaturesVector.CLASS_TAG_END)) {
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

    public void fillUpTrainingSets()
    {
        LinkedList<Picture> tempTrainingList = new LinkedList<>();

        for (String imageClass : FeaturesVector.imageClassToFeaturesValuesMap.keySet()) {
            Map<String, LinkedList<Number>> featureNameToValuesMap = FeaturesVector.imageClassToFeaturesValuesMap.get
                    (imageClass);
            for(int i = 0; i < featureNameToValuesMap.entrySet().iterator().next().getValue().size(); i++) {
                LinkedList<Number> features = new LinkedList<>();
                for(String feature : featureNameToValuesMap.keySet()) {
                    features.add(featureNameToValuesMap.get(feature).get(i));
                }
                Picture tempPicture = new Picture(imageClass, imageClass, features);
                tempTrainingList.add(tempPicture);
            }
        }

        KNN.baseTrainingFile = tempTrainingList;
    }

    public boolean readFile() {
        try {
            //read file
            FileInputStream fstream = new FileInputStream(FEATURES_VECTOR_FILE_NAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            //read features
            readFeaturesVector(br);

            //Close the input stream
            br.close();
            return true;
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e);
            return false;
        }
    }
}
