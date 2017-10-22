package Core;

import Extraction.FeaturesExtractor;
import Extraction.FeaturesVector;
import View.FileChoosePanel;
import View.ImageRecognitionPanel;
import View.TrainingDataLoadingPanel;
import View.Window;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageRecognizer {

    public static void loadTrainingData(File file, FileChoosePanel fileChoosePanel, Window window) {
        if (file != null) {
            window.remove(fileChoosePanel);
            String extension = FilenameUtils.getExtension(file.getName());
            List<Picture> pictures = new ArrayList<>();
            boolean isMnist = false;

            //FIXME akolodziejek: move magic string to field
            if(extension.contains("-ubyte")) {
                isMnist = true;
                MnistFilesLoader mnistFilesLoader = new MnistFilesLoader();
                try {
                    pictures = mnistFilesLoader.loadTrainingDataSet(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //potrzebne dopiero w 2 zadaniu
                //pictures = ImagesLoader.loadTrainingDataSet(files);
            }
            if (CollectionUtils.isNotEmpty(pictures)) {
                window.add(new TrainingDataLoadingPanel(pictures, window, isMnist));
                FeaturesVector featuresVector = FeaturesExtractor.extractFeaturesVector(pictures);
                featuresVector.saveToFile();
            }
        }
    }

    public static void initImageRecognition(File image, FileChoosePanel fileChoosePanel, Window window) {
        if (image != null) {
            window.remove(fileChoosePanel);
            window.add(new ImageRecognitionPanel(image, window));
        }
    }
}
