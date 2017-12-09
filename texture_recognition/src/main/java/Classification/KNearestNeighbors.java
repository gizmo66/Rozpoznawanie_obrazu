package Classification;

import Core.ImageRecognizer;
import Extraction.Picture;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class KNearestNeighbors extends ClassifierImpl implements Classifier {

    public KNearestNeighbors(ImageRecognizer imageRecognizer) {
        super(imageRecognizer);
    }

    @Override
    public LinkedList<ResultData> classify(LinkedList<Picture> picturesToClassify, int K) {
        LinkedList<ResultData> result = new LinkedList<>();
        for (Picture picture : picturesToClassify) {
            LinkedList<Picture> neighbors = findKNearestNeighbors(imageRecognizer.trainingData.getPictures(),
                    picture, K);
            String foundClass = classify(neighbors);
            result.add(new ResultData(picture.getType(), foundClass));
        }
        return result;
    }

    @Override
    public ResultData classify(Picture pictureToClassify, int K, ResultData result) {
        LinkedList<Picture> neighbors = findKNearestNeighbors(imageRecognizer.trainingData.getPictures(),
                pictureToClassify, K);
        String foundClass = classify(neighbors);
        result.pictureType = pictureToClassify.getType();
        result.resultOfKnn = foundClass;
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
        /*if(maxSimilarity < 600) {
            returnLabel = "";
        }*/
        return returnLabel;
    }

}
