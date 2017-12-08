package File;

import Extraction.Picture;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public interface FileLoader {

    LinkedList<Picture> loadTrainingDataSet(File[] files, boolean trainingData) throws IOException;
}
