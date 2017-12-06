package Classification;

import Extraction.Picture;

import java.util.LinkedList;
import java.util.List;

public interface Classifier {

    List<ResultData> classify(LinkedList<Picture> picturesToClassify, int K);

    ResultData classify(Picture pictureToClassify, int K, ResultData result);
}
