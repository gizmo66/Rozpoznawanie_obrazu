package Core;

import Extraction.FeaturesExtractor;
import Extraction.FeaturesVector;
import View.*;
import View.Window;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //potrzebne dopiero w 2 zadaniu
                //pictures = ImagesLoader.loadTrainingDataSet(files);
            }

            loadPictures = pictures;
            for(int i = 0; i < pictures.size();i++)
            {
                loadPictures.set(i,new Picture(ImageUtils.binarizeImage(ImageUtils.toBufferedImage(loadPictures.get(i).getImage())),
                        loadPictures.get(i).getType()));
                loadPictures.set(i,new Picture(ThinnerImage.Start(loadPictures.get(i)),loadPictures.get(i).getType()));
            }

            FeaturesVector featuresVector = FeaturesExtractor.extractFeaturesVector(pictures);
            /*Window window1;
            window1 = WindowTestRecognizer.getDebugWindows_v1(FeaturesVector.imageClassToFeaturesValuesMap.get
                            ("SURFACE"), FeaturesVector.imageClassToFeaturesValuesMap.get
                            ("VERTICAL_LINES"), FeaturesVector.imageClassToFeaturesValuesMap.get
                            ("HORIZONTAL_LINES"), FeaturesVector.imageClassToFeaturesValuesMap.get
                            ("ENDED_NUMBER"),
                    "Surface","Vertical line lenght","Horizontal line lenght",
                    "Number of ended");

            pictures = loadPictures;
            window1.pack();
            window1.setVisible(true);*/

            if (CollectionUtils.isNotEmpty(pictures)) {
                featuresVector.saveToFile();

                List<Picture> tempTest = new ArrayList<>();
                for(int i = 0 ; i < 200 ; i ++)
                {
                    tempTest.add(FeaturesExtractor.calculateFeatureInOnePicture(pictures.get(i)));
                }
                List<ResultData> result = KNN.knnTEST(KNN.baseTrainingFile,tempTest,10000);
                window = WindowTestRecognizer.getTestWindows(result);
                window.add(new TrainingDataLoadingPanel(pictures, window, isMnist));
                /*for(Picture p :KNN.baseTrainingFile)
                {
                    System.out.println(p.distance + " "+ p.label + " " +p.getCharasteristic().toString());
                }*/
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
