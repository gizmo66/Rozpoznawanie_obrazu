package Extraction;

import Core.KNN;
import Core.Picture;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeaturesVector {

    public static Map<String,Float> surfaceFeatures, verticalLenghtFeatures,horizontalLenghtFeatures;
    public static Map<String,Boolean> verticalFeatures,horizontalFeatures;
    public static Map<String,Integer> numberOfEndedFeatures;

    public static final String FEATURE_TAG_START = "FEATURE: ";
    public static final String FEATURE_TAG_END = "END OF: ";
    public static final String DIGITS_VALUE_SEPARATOR = ":";

    public FeaturesVector(Map<String,Float> sF,Map<String,Boolean> vF,Map<String,Boolean> hF,Map<String,Integer> eF,Map<String,Float> hLf,Map<String,Float>  vLf )
    {
        verticalLenghtFeatures = vLf;
        horizontalLenghtFeatures = hLf;
        surfaceFeatures = sF;
        verticalFeatures = vF;
        horizontalFeatures = hF;
        numberOfEndedFeatures = eF;
    }

    private <T> void saveFeature(String featureName, Map<String,T> values, BufferedWriter bf) throws IOException {
        bf.write("---------------------------------");
        bf.newLine();
        bf.write(FEATURE_TAG_START + featureName);
        bf.newLine();
        for(String numberKey:values.keySet())
        {
            bf.write(numberKey+DIGITS_VALUE_SEPARATOR+values.get(numberKey).toString());
            bf.newLine();
        }
        bf.write(FEATURE_TAG_END + featureName);
        bf.newLine();
    }

    public void saveToFile() {
        try{
            // Create file
            FileWriter fstream = new FileWriter( "FeaturesVector.txt");
            BufferedWriter out = new BufferedWriter(fstream);

            //save features
            saveFeature("SURFACE", surfaceFeatures,out);
            //saveFeature("VERTICAL_LINE", verticalFeatures,out);
            //saveFeature("HORIZONTAL_LINE", horizontalFeatures,out);
            saveFeature("VERTICAL_LINE", verticalLenghtFeatures,out);
            saveFeature("HORIZONTAL_LINE", horizontalLenghtFeatures,out);
            saveFeature("ENDED_NUMBER", numberOfEndedFeatures,out);
            //Close the output stream
            out.close();

        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        //TODO: zapisać wektor cech do pliku z własnym rozszerzeniem np. ".fv"
    }
}
