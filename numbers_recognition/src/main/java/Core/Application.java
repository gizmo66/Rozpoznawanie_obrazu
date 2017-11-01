package Core;

import View.FileChoosePanel;
import View.ImageFileChoosePanel;
import View.Window;
import View.WindowTestRecognizer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application {

    public static void main(String args[]) {

        Window window;
        FeaturesVectorLoader featuresVectorLoader = new FeaturesVectorLoader();
        if (featuresVectorLoader.loadFeaturesVector()) {
            window = new Window("Choose image to recognize");
            window.add(new ImageFileChoosePanel(ContextEnum.RECOGNITION, window));
        } else {
            //jeśli nie ma pliku z wektorem cech, to wyświetl dialog z wyborem danych treningowych
            //póki co operujemy na bazie MNIST:
            //repo\Rozpoznawanie_obrazu\numbers_recognition\data\MNIST_database\train-images.idx3-ubyte
            window = new Window("Choose training data");
            window.add(new FileChoosePanel(ContextEnum.TRAINING, window));

        }

        window.pack();
        window.setVisible(true);
    }
}
