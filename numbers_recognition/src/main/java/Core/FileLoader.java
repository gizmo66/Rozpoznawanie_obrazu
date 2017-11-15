package Core;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public interface FileLoader {

    List<Picture> loadTrainingDataSet(File file) throws IOException;

    LinkedList<Picture> loadTrainingDataSet(File[] files) throws IOException;
}
