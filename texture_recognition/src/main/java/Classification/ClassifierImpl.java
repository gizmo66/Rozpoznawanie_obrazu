package Classification;

import Core.ImageRecognizer;
import Extraction.Picture;

import java.util.LinkedList;
import java.util.List;

abstract class ClassifierImpl implements Classifier {

    protected ImageRecognizer imageRecognizer;

    ClassifierImpl(ImageRecognizer imageRecognizer) {
        this.imageRecognizer = imageRecognizer;
    }

    LinkedList<Picture> findKNearestNeighbors(LinkedList<Picture> trainingData, Picture testRecord, int K) {
        int trainingDataSize = trainingData.size();
        if (K > trainingDataSize) {
            throw new AssertionError("K is lager than the length of trainingSet!");
        }
        LinkedList<Picture> neighbors = new LinkedList<>();

        int index;
        for (index = 0; index < K; index++) {
            trainingData.get(index).setDistance(getEuclideanDistance(trainingData.get(index).getFeatures(),
                    testRecord.getFeatures()));
            neighbors.add(trainingData.get(index));
        }

        for (index = K; index < trainingDataSize; index++) {
            trainingData.get(index).setDistance(getEuclideanDistance(trainingData.get(index).getFeatures(),
                    testRecord.getFeatures()));

            int maxIndex = 0;
            for (int i = 1; i < K; i++) {
                if (neighbors.get(i).getDistance() > neighbors.get(maxIndex).getDistance()) {
                    maxIndex = i;
                }
            }

            if (neighbors.get(maxIndex).getDistance() > trainingData.get(index).getDistance()) {
                neighbors.set(maxIndex, trainingData.get(index));
            }
        }
        return neighbors;
    }

    private double getEuclideanDistance(List<Number> f0, List<Number> f1) {
        if (f1.size() != f0.size()) {
            throw new AssertionError("Features1 and Features2 are different size!");
        }
        int numOfAttributes = f0.size();
        double sum2 = 0;

        for (int i = 0; i < numOfAttributes; i++) {
            sum2 += Math.pow(f0.get(i).floatValue() - f1.get(i).floatValue(), 2);
        }

        return Math.sqrt(sum2);
    }
}
