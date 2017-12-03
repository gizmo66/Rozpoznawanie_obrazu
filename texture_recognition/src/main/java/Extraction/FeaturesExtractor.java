package Extraction;

import Image.ImageUtils;
import Math.DiscreteFourierTransform;
import org.apache.commons.math3.complex.Complex;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

import static Core.ReflectionUtils.getSubTypesOf;
import static Core.ReflectionUtils.invokeMethod;
import static Extraction.Feature.CALCULATE_FEATURE_METHOD;
import static Extraction.Feature.GET_FEATURE_NAME_METHOD;

public class FeaturesExtractor {

    private Map<String, Class<? extends Feature>> featureNameToClassMap = new HashMap<>();
    private List<String> featureNames = new ArrayList<>();

    public FeaturesExtractor() {
        for (Class<? extends Feature> featureImpl : getSubTypesOf(getPackageName(), Feature.class)) {
            String featureName = (String) invokeMethod(GET_FEATURE_NAME_METHOD, featureImpl);
            this.featureNameToClassMap.put(featureName, featureImpl);
            this.featureNames.add(featureName);
        }
    }

    private String getPackageName() {
        Package pack = this.getClass().getPackage();
        return pack.getName();
    }

    public FeaturesVector extractFeaturesVector(LinkedList<Picture> pictures) {
        Map<String, Map<String, LinkedList<Number>>> imageClassToFeaturesValuesMap = new LinkedHashMap<>();
        for (Picture picture : pictures) {
            String fileName = "./spectrum/" + picture.getOriginalFileName();
            if(!fileExists(fileName + ".bmp")) {
                Image spectrum = extractSpectrum(picture.getImage());
                picture.setSpectrum(spectrum);
                ImageUtils.save(spectrum, fileName, "bmp");
            } else {
                picture.setSpectrum(ImageUtils.fileToImage(new File(fileName + ".bmp")));
            }

            LinkedHashMap<String, Number> featureNameToValueMap = new LinkedHashMap<>();
            for (String featureName : featureNames) {
                featureNameToValueMap.put(featureName, getFeature(featureName, picture));
            }

            if (imageClassToFeaturesValuesMap.get(picture.getType()) != null) {
                for (String featureName : featureNames) {
                    imageClassToFeaturesValuesMap.get(picture.getType()).get(featureName)
                            .add(featureNameToValueMap.get(featureName));
                }
            } else {
                LinkedHashMap<String, LinkedList<Number>> featureNameToValuesMap = new LinkedHashMap<>();
                for (String featureName : featureNames) {
                    LinkedList<Number> valuesList = new LinkedList<>();
                    valuesList.add(featureNameToValueMap.get(featureName));
                    featureNameToValuesMap.put(featureName, valuesList);
                }
                imageClassToFeaturesValuesMap.put(picture.getType(), featureNameToValuesMap);
            }
        }
        return new FeaturesVector(imageClassToFeaturesValuesMap);
    }

    private Image extractSpectrum(Image image) {
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        Complex[][] input = ImageUtils.imageToComplex(bufferedImage);
        Complex[][] output = DiscreteFourierTransform.calculate(input);
        Image spectrum = ImageUtils.complexToImage(output, bufferedImage);
        return spectrum;
    }

    public Picture calculateFeaturesInOnePicture(Picture picture) {
        Image spectrum = extractSpectrum(picture.getImage());
        picture.setSpectrum(spectrum);

        LinkedList<Number> features = new LinkedList<>();
        for (String featureName : featureNames) {
            features.add(getFeature(featureName, picture));
        }
        return new Picture(picture.getImage(), picture.getType(), features, picture.getOriginalFileName());
    }

    private Number getFeature(String featureName, Picture picture) {
        return (Number) invokeMethod(CALCULATE_FEATURE_METHOD, featureNameToClassMap.get(featureName),
                new Picture[]{picture}, Picture.class);
    }

    private boolean fileExists(String path) {
        java.io.File f = new java.io.File(path);
        return f.exists() && !f.isDirectory();
    }
}
