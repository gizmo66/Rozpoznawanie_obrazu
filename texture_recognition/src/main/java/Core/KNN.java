package Core;

import java.util.*;

public class KNN {

    public static List<Picture> baseTrainingFile = new ArrayList<>();

    public static List<ResultData> knnTEST(List<Picture> trainingFile, List<Picture> testFiles, int K)
    {
        List<ResultData> result = new ArrayList<>();
        for(Picture picture : testFiles)
        {
            List<String> tempResult = knn(trainingFile,picture,K);
            if(tempResult != null)
            {
                result.add(new ResultData(tempResult.get(0),tempResult.get(1)));
            }
        }

        return result;
    }

    //first element expected, second result of knn
    public static List<String> knn(List<Picture> trainingFile, Picture testFile, int K){

        if(K <= 0){
            System.out.println("K should be larger than 0!");
            return null;
        }

        List<Picture> neighbors = findKNearestNeighbors(trainingFile, testFile, K);
        String classLabel = classify(neighbors);
        testFile.label = classLabel;

        List<String> result = new ArrayList<>();
        result.add(testFile.getType());
        result.add(String.valueOf(testFile.label));
        return result;
    }

    // Find K nearest neighbors of testRecord within trainingSet
    static List<Picture> findKNearestNeighbors(List<Picture>trainingSet, Picture testRecord,int K){
        int NumOfTrainingSet = trainingSet.size();
        assert K <= NumOfTrainingSet : "K is lager than the length of trainingSet!";
        List<Picture> neighbors = new ArrayList<>();

        int index;
        for(index = 0; index < K; index++){
            trainingSet.get(index).distance = getEuclideanDistance(trainingSet.get(index).getCharasteristic(), testRecord.getCharasteristic());
            neighbors.add(trainingSet.get(index));
        }

        for(index = K; index < NumOfTrainingSet; index ++){
            trainingSet.get(index).distance = getEuclideanDistance(trainingSet.get(index).getCharasteristic(), testRecord.getCharasteristic());

            //get the index of the neighbor with the largest distance to testRecord
            int maxIndex = 0;
            for(int i = 1; i < K; i ++){
                if(neighbors.get(i).distance > neighbors.get(maxIndex).distance)
                    maxIndex = i;
            }

            //add the current trainingSet[index] into neighbors if applicable
            if(neighbors.get(maxIndex).distance > trainingSet.get(index).distance)      //TODO >
                neighbors.set(maxIndex,trainingSet.get(index));
        }
        return neighbors;
    }

    static String classify(List<Picture> neighbors){
        HashMap<String, Double> map = new HashMap<>();

        for(int index = 0;index < neighbors.size(); index ++){
            Picture temp = neighbors.get(index);
            String key = temp.label;
            if(!map.containsKey(key))
                map.put(key, 1 / temp.distance);
            else{
                double value = map.get(key);
                value += 1 / temp.distance;
                map.put(key, value);
            }
        }

        double maxSimilarity = 0;
        String returnLabel = "error";
        Set<String> labelSet = map.keySet();
        Iterator<String> it = labelSet.iterator();

        while(it.hasNext()){
            String label = it.next();
            double value = map.get(label);
            if(value > maxSimilarity){
                maxSimilarity = value;
                returnLabel = label;
            }
        }

        return returnLabel;
    }

    public static double getEuclideanDistance(List<Number> f0, List<Number> f1) {
        assert f1.size() != f0.size() : "Features1 and Features2 are different size!";
        int numOfAttributes = f0.size();
        double sum2 = 0;

        for(int i = 0; i < numOfAttributes; i ++){
            sum2 += Math.pow(f0.get(i).floatValue() - f1.get(i).floatValue(), 2);
        }

        return Math.sqrt(sum2);
    }

}
