package Classification;

import Core.ImageRecognizer;
import Extraction.Picture;

import java.util.*;

public class KNearestNeighborsClassifier extends ClassifierImpl implements Classifier {

    @Override
    public LinkedList<ResultData> classify(LinkedList<Picture> picturesToClassify, int K) {
        LinkedList<ResultData> result = new LinkedList<>();
        for (Picture picture : picturesToClassify) {
            LinkedList<Picture> neighbors = findKNearestNeighbors(ImageRecognizer.trainingData.getPictures(), picture, K);
            String foundClass = classify(neighbors);
            result.add(new ResultData(picture.getType(), foundClass));
        }
        return result;
    }

    private String classify(LinkedList<Picture> neighbors) {
        HashMap<String, Double> map = new HashMap<>();

        for (Picture temp : neighbors) {
            String key = temp.getType();
            if (!map.containsKey(key)) {
                map.put(key, 1 / temp.getDistance());
            } else {
                double value = map.get(key);
                value += 1 / temp.getDistance();
                map.put(key, value);
            }
        }

        double maxSimilarity = 0;
        String returnLabel = "";
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

}
