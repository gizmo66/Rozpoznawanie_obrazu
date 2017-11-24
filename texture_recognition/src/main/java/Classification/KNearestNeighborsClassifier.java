package Classification;

import Extraction.Picture;

import java.util.*;

public class KNearestNeighborsClassifier {

    public static List<Picture> baseTrainingFile = new ArrayList<>();

    public static List<ResultData> classify(List<Picture> trainingFile, List<Picture> testFiles, int K) {
        List<ResultData> result = new ArrayList<>();
        for(Picture picture : testFiles) {
            List<Picture> neighbors = findKNearestNeighbors(trainingFile, picture, K);
            picture.label = classify(neighbors);

            List<String> tempResult = new ArrayList<>();
            tempResult.add(picture.getType());
            tempResult.add(String.valueOf(picture.label));

            result.add(new ResultData(tempResult.get(0),tempResult.get(1)));
        }

        return result;
    }

    private static List<Picture> findKNearestNeighbors(List<Picture> trainingSet, Picture testRecord, int K){
        int NumOfTrainingSet = trainingSet.size();
        if (K > NumOfTrainingSet) {
            throw new AssertionError("K is lager than the length of trainingSet!");
        }
        List<Picture> neighbors = new ArrayList<>();

        int index;
        for(index = 0; index < K; index++){
            trainingSet.get(index).distance = getEuclideanDistance(trainingSet.get(index).getCharasteristic(),
                    testRecord.getCharasteristic());
            neighbors.add(trainingSet.get(index));
        }

        for(index = K; index < NumOfTrainingSet; index ++){
            trainingSet.get(index).distance = getEuclideanDistance(trainingSet.get(index).getCharasteristic(),
                    testRecord.getCharasteristic());

            int maxIndex = 0;
            for(int i = 1; i < K; i ++){
                if(neighbors.get(i).distance > neighbors.get(maxIndex).distance) {
                    maxIndex = i;
                }
            }

            if(neighbors.get(maxIndex).distance > trainingSet.get(index).distance) {
                neighbors.set(maxIndex, trainingSet.get(index));
            }
        }
        return neighbors;
    }

    private static String classify(List<Picture> neighbors){
        HashMap<String, Double> map = new HashMap<>();

        for (Picture temp : neighbors) {
            String key = temp.label;
            if (!map.containsKey(key))
                map.put(key, 1 / temp.distance);
            else {
                double value = map.get(key);
                value += 1 / temp.distance;
                map.put(key, value);
            }
        }

        double maxSimilarity = 0;
        String returnLabel = "error";
        Set<String> labelSet = map.keySet();

        for (String label : labelSet) {
            double value = map.get(label);
            if (value > maxSimilarity) {
                maxSimilarity = value;
                returnLabel = label;
            }
        }

        return returnLabel;
    }

    private static double getEuclideanDistance(List<Number> f0, List<Number> f1) {
        if (f1.size() != f0.size()) {
            throw new AssertionError("Features1 and Features2 are different size!");
        }
        int numOfAttributes = f0.size();
        double sum2 = 0;

        for(int i = 0; i < numOfAttributes; i ++){
            sum2 += Math.pow(f0.get(i).floatValue() - f1.get(i).floatValue(), 2);
        }

        return Math.sqrt(sum2);
    }

}
