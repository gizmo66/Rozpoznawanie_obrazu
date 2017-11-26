package Classification;

import Extraction.Picture;

import java.util.LinkedList;
import java.util.List;

public interface Classifier {

    List<ResultData> classify(LinkedList<Picture> testFiles, int K);
}
