package Classification;

import Core.TrainingData;
import Extraction.Picture;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class NaiveBayes extends ClassifierImpl implements Classifier {

    public NaiveBayes(TrainingData trainingData) {
        super(trainingData);
    }

    @Override
    public List<ResultData> classify(LinkedList<Picture> picturesToClassify, int nearestNeighborsQuantity) {
        LinkedList<ResultData> result = new LinkedList<>();
        for (Picture picture : picturesToClassify) {
            LinkedList<Picture> trainingPictures = trainingData.getPictures();
            LinkedList<Picture> neighbors = findKNearestNeighbors(trainingPictures, picture, nearestNeighborsQuantity);
            String foundClass = getMostLikelyClass(neighbors, trainingPictures.size());
            result.add(new ResultData(picture.getType(), foundClass));
        }
        return result;
    }

    @Override
    public ResultData classify(Picture pictureToClassify, int K, ResultData result) {
        LinkedList<Picture> trainingPictures = trainingData.getPictures();
        LinkedList<Picture> neighbors = findKNearestNeighbors(trainingPictures, pictureToClassify, K);
        String foundClass = getMostLikelyClass(neighbors, trainingPictures.size());
        result.pictureType = pictureToClassify.getType();
        result.resultOfKnn = foundClass;
        return result;
    }

    private String getMostLikelyClass(LinkedList<Picture> neighbors, int trainingDataSize) {
        LinkedHashMap<String, Integer> classToQuantityMap = trainingData.getClassToQuantityMap();

        String result = "";
        double probability = 0;
        double currentProbability;
        for (String className : classToQuantityMap.keySet()) {
            double neighborsInTheSameClassQuantity = neighbors.stream().filter(n -> n.getType().equals(className))
                    .collect(Collectors.toList()).size();
            double classQuantity = classToQuantityMap.get(className);
            double classProbability = classQuantity / ((double) trainingDataSize);
            double probabilityAccordingToNeighbors = neighborsInTheSameClassQuantity / classQuantity;
            currentProbability = classProbability * probabilityAccordingToNeighbors;
            if (currentProbability > probability) {
                probability = currentProbability;
                result = className;
            }
        }
        /*if(probability < 0.004) {
            result = "";
        }*/
        return result;
    }
}
