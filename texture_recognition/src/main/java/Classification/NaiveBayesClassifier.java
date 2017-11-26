package Classification;

import Core.ImageRecognizer;
import Extraction.Picture;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class NaiveBayesClassifier extends ClassifierImpl implements Classifier {

    @Override
    public List<ResultData> classify(LinkedList<Picture> picturesToClassify, int K) {
        LinkedList<ResultData> result = new LinkedList<>();
        for (Picture picture : picturesToClassify) {
            LinkedList<Picture> trainingPictures = ImageRecognizer.trainingData.getPictures();
            LinkedList<Picture> neighbors = findKNearestNeighbors(trainingPictures, picture, K);
            String foundClass = getMostLikelyClass(neighbors, trainingPictures.size());
            result.add(new ResultData(picture.getType(), foundClass));
        }
        return result;
    }

    private String getMostLikelyClass(LinkedList<Picture> neighbors, int trainingDataSize) {
        LinkedHashMap<String, Integer> classToQuantityMap = ImageRecognizer.trainingData.getClassToQuantityMap();

        String result = "";
        double probability = 0;
        double currentProbability;
        for(String className : classToQuantityMap.keySet()) {
            double neighborsInTheSameClassQuantity = neighbors.stream().filter(n -> n.getType().equals(className))
                    .collect(Collectors.toList()).size();
            double classProbability = classToQuantityMap.get(className) / ((double)trainingDataSize);
            double probabilityAccordingToNeighbors = neighborsInTheSameClassQuantity / classToQuantityMap.get(className);
            currentProbability = classProbability * probabilityAccordingToNeighbors;
            if(currentProbability > probability) {
                probability = currentProbability;
                result = className;
            }
        }
        return result;
    }
}
