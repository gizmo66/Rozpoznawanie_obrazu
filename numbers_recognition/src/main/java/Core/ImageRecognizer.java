package Core;

import Extraction.FeaturesExtractor;
import Extraction.FeaturesVector;
import View.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageRecognizer {

    public static List<Picture> loadPictures = new ArrayList<>();

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
                    loadPictures = mnistFilesLoader.loadTrainingDataSet(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //potrzebne dopiero w 2 zadaniu
                //pictures = ImagesLoader.loadTrainingDataSet(files);
            }

            Window window1;
            /*Map<String,Boolean> tempMap = new HashMap<>();
            tempMap.put("1",false);
            tempMap.put("3",false);
            tempMap.put("2",false);
            if(pictures.size() > 0)
                tempMap.put(String.valueOf(pictures.size()),true);*/

            window1 = WindowTestRecognizer.getDebugWindows(FeaturesExtractor.getMidSurfaceOfNumber(pictures),"SURFACE");
            window1.pack();
            window1.setVisible(true);

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
