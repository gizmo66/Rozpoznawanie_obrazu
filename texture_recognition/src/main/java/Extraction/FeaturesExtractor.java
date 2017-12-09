package Extraction;

import Image.ImageUtils;
import Math.DiscreteFourierTransform;
import View.Utils.ImageTypeEnum;
import lombok.Getter;
import org.apache.commons.math3.complex.Complex;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static Core.ReflectionUtils.getSubTypesOf;
import static Core.ReflectionUtils.invokeMethod;
import static Extraction.Feature.CALCULATE_FEATURE_METHOD;
import static Extraction.Feature.GET_FEATURE_NAME_METHOD;

public class FeaturesExtractor {

    private static final String SPECTRA_DIRECTORY_PATH = "./spectrum/";
    private static final String DOT = ".";
    private Map<String, Class<? extends Feature>> featureNameToClassMap = new HashMap<>();
    private List<String> featureNames = new ArrayList<>();

    @Getter
    private List<String> featureIds = new ArrayList<>();

    public FeaturesExtractor() {
        for (Class<? extends Feature> featureImpl : getSubTypesOf(getPackageName(), Feature.class)) {
            String simpleName = featureImpl.getSimpleName();
            Optional<FeatureEnum> featureEnum = Arrays.stream(FeatureEnum.values()).filter(f -> f.getSimpleName()
                    .equals(simpleName)).findFirst();
            if (featureEnum.isPresent()) {
                FeatureEnum featureEnum1 = featureEnum.get();
                boolean isActive = featureEnum1.isActive();
                if (isActive) {
                    String featureName = (String) invokeMethod(GET_FEATURE_NAME_METHOD, featureImpl);
                    this.featureNameToClassMap.put(featureName, featureImpl);
                    this.featureNames.add(featureName);
                    this.featureIds.add(featureEnum1.name());
                }
            } else {
                throw new IllegalStateException("Feature " + simpleName + " is not present in FeatureEnum!");
            }
        }
    }

    private String getPackageName() {
        Package pack = this.getClass().getPackage();
        return pack.getName();
    }

    public FeaturesVector extractFeaturesVector(LinkedList<Picture> pictures) {
        Map<String, Map<String, LinkedList<Number>>> imageClassToFeaturesValuesMap = new LinkedHashMap<>();
        for (Picture picture : pictures) {
            String fileName = SPECTRA_DIRECTORY_PATH + picture.getOriginalFileName();
            String extension = ImageTypeEnum.BMP.getExtensions().get(0);
            extractSpectra(picture, fileName, extension);
            extractFeatures(imageClassToFeaturesValuesMap, picture);
        }
        return new FeaturesVector(imageClassToFeaturesValuesMap);
    }

    private void extractFeatures(Map<String, Map<String, LinkedList<Number>>> imageClassToFeaturesValuesMap,
                                 Picture picture) {
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

    private void extractSpectra(Picture picture, String fileName, String extension) {
        if (!fileExists(fileName + DOT + extension)) {
            Image spectrum = extractSpectrum(picture.getImage());
            picture.setSpectrum(spectrum);
            ImageUtils.save(spectrum, fileName, extension);
        } else {
            picture.setSpectrum(ImageUtils.fileToImage(new File(fileName + "." + extension)));
        }
    }

    private Image extractSpectrum(Image image) {
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        //Complex[][] input = ImageUtils.imageToComplex(bufferedImage);
        //Complex[][] spectrum = DiscreteFourierTransform.ft(input);
        Complex[][] spectrum = DiscreteFourierTransform.fft(bufferedImage);
        return ImageUtils.complexToImage(spectrum, bufferedImage);
    }

    public Picture calculateFeaturesInOnePicture(Picture picture) {
        Image spectrum = extractSpectrum(picture.getImage());
        picture.setSpectrum(spectrum);

        LinkedList<Number> features = new LinkedList<>();
        for (String featureName : featureNames) {
            features.add(getFeature(featureName, picture));
        }
        picture.setFeatures(features);
        return picture;
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
