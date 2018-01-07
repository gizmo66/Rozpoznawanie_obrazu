package File;

import Core.Picture;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public interface FileLoader {

    LinkedList<Picture> loadImages(File[] files) throws IOException;
}
