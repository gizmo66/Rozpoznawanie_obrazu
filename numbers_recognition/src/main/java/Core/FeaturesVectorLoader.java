package Core;

import Extraction.FeaturesVector;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeaturesVectorLoader {

    public boolean loadFeaturesVector() {
        boolean featuresVectorLoaded = false;

        //TODO: wczytać wektor cech z zapisanego pliku
        readFile();
        return featuresVectorLoaded;
    }

    private <T> Map<String,T> readFeature(BufferedReader br, String featureName) throws IOException {

        Map<String,T> returnValues = new HashMap<>();
        boolean featureWasFound = false;
        //Read File Line By Line
        for(String strLine; (strLine = br.readLine()) != null; ) {

            if(!featureWasFound && strLine.contains(FeaturesVector.FEATURE_TAG_START) && strLine.contains(featureName)) {
                featureWasFound = true;
            }

            if(strLine.contains(FeaturesVector.FEATURE_TAG_END) && strLine.contains(featureName))
                break;

            if(featureWasFound && !strLine.contains(FeaturesVector.FEATURE_TAG_START)) {
                String key = strLine.substring(0,strLine.indexOf(FeaturesVector.DIGITS_VALUE_SEPARATOR));
                String value = strLine.substring(strLine.indexOf(FeaturesVector.DIGITS_VALUE_SEPARATOR), strLine.length());
                returnValues.put(key,(T)value);
            }
        }

        //DEBUG println
        for(String key : returnValues.keySet())
            System.out.println(key + " " + returnValues.get(key).toString());

        return returnValues;
    }

    public void readFile() {
        try{
            //read file
            FileInputStream fstream = new FileInputStream("FeaturesVector.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            //read features
            FeaturesVector.surfaceFeatures = readFeature(br,"SURFACE");
            FeaturesVector.verticalFeatures = readFeature(br,"VERTICAL_LINE");
            FeaturesVector.horizontalFeatures = readFeature(br,"HORIZONTAL_LINE");
            FeaturesVector.numberOfEndedFeatures = readFeature(br,"ENDED_NUMBER");

            //Close the input stream
            br.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
