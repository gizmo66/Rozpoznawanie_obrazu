package Core;

import Extraction.FeaturesVectorLoader;
import View.FileChoosePanel;
import View.ImageFileChoosePanel;
import View.Window;

public class Application {

    public static void main(String args[]) {
        FeaturesVectorLoader featuresVectorLoader = new FeaturesVectorLoader();

        Window window;
        if (featuresVectorLoader.loadFeaturesVector()) {
            window = new Window("Choose image to recognize");
            window.add(new ImageFileChoosePanel());
        } else {
            window = new Window("Choose training data");
            window.add(new FileChoosePanel());
        }
        window.pack();
        window.setVisible(true);
    }
}
