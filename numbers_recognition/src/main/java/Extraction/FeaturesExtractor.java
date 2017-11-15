package Extraction;

import Core.ImageUtils;
import Core.Picture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class FeaturesExtractor {

    public static FeaturesVector extractFeaturesVector(LinkedList<Picture> pictures, boolean isMnist) {

        Map<String, Map<String, LinkedList<Number>>> imageClassToFeaturesValuesMap = new LinkedHashMap<>();

        for(Picture picture : pictures) {
            Float surfaceSize = null;
            Float quarterSize1 = null;
            Float quarterSize2 = null;
            Float quarterSize3 = null;
            Float quarterSize4 = null;
            if (isMnist) {
                surfaceSize = getSurfaceSize(picture) / 10;
                quarterSize1 = getQuarterSize(picture, 1) / 2.5f;
                quarterSize2 = getQuarterSize(picture, 2) / 2.5f;
                quarterSize3 = getQuarterSize(picture, 3) / 2.5f;
                quarterSize4 = getQuarterSize(picture, 4) / 2.5f;
            }
            Map<String, Integer> minutiaesMap = getMinuatiaesMap(picture);
            Integer numbersOfEnded = minutiaesMap.get("ENDING_POINT");
            Integer crossingPointsQuantity = minutiaesMap.get("CROSSING_POINT");

            if(imageClassToFeaturesValuesMap.get(picture.getType()) != null) {
                if (isMnist) {
                    imageClassToFeaturesValuesMap.get(picture.getType()).get("SURFACE").add(surfaceSize);
                    imageClassToFeaturesValuesMap.get(picture.getType()).get("QUARTER_SIZE_1").add(quarterSize1);
                    imageClassToFeaturesValuesMap.get(picture.getType()).get("QUARTER_SIZE_2").add(quarterSize2);
                    imageClassToFeaturesValuesMap.get(picture.getType()).get("QUARTER_SIZE_3").add(quarterSize3);
                    imageClassToFeaturesValuesMap.get(picture.getType()).get("QUARTER_SIZE_4").add(quarterSize4);
                }
                imageClassToFeaturesValuesMap.get(picture.getType()).get("LINE_ENDS").add(numbersOfEnded);
                imageClassToFeaturesValuesMap.get(picture.getType()).get("CROSSING_POINTS").add(crossingPointsQuantity);
            } else {
                Map<String, LinkedList<Number>> featureNameToValuesMap = new LinkedHashMap<>();

                if (isMnist) {
                    LinkedList<Number> surfaceSizeLinkedList = new LinkedList<>();
                    surfaceSizeLinkedList.add(surfaceSize);
                    featureNameToValuesMap.put("SURFACE", surfaceSizeLinkedList);

                    LinkedList<Number> quarterSize1LinkedList = new LinkedList<>();
                    quarterSize1LinkedList.add(quarterSize1);
                    featureNameToValuesMap.put("QUARTER_SIZE_1", quarterSize1LinkedList);

                    LinkedList<Number> quarterSize2LinkedList = new LinkedList<>();
                    quarterSize2LinkedList.add(quarterSize1);
                    featureNameToValuesMap.put("QUARTER_SIZE_2", quarterSize2LinkedList);

                    LinkedList<Number> quarterSize3LinkedList = new LinkedList<>();
                    quarterSize3LinkedList.add(quarterSize1);
                    featureNameToValuesMap.put("QUARTER_SIZE_3", quarterSize3LinkedList);

                    LinkedList<Number> quarterSize4LinkedList = new LinkedList<>();
                    quarterSize4LinkedList.add(quarterSize1);
                    featureNameToValuesMap.put("QUARTER_SIZE_4", quarterSize4LinkedList);
                }

                LinkedList<Number> numbersOfEndedLinkedList = new LinkedList<>();
                numbersOfEndedLinkedList.add(numbersOfEnded);
                featureNameToValuesMap.put("LINE_ENDS", numbersOfEndedLinkedList);

                LinkedList<Number> crossingPointsQuantityLinkedList = new LinkedList<>();
                crossingPointsQuantityLinkedList.add(crossingPointsQuantity);
                featureNameToValuesMap.put("CROSSING_POINTS", crossingPointsQuantityLinkedList);

                imageClassToFeaturesValuesMap.put(picture.getType(), featureNameToValuesMap);
            }
        }
        return new FeaturesVector(imageClassToFeaturesValuesMap);
    }

    private static Float getQuarterSize(Picture picture, int quarterNumber) {
        if (quarterNumber == 1) {
            int numberOfBlackPixels = 0;
            BufferedImage image = ImageUtils.toBufferedImage(picture.getImage());
            for (int i = 0; i < image.getHeight() / 2; i++) {
                for (int j = 0; j < image.getWidth() / 2; j++) {
                    if (image.getRGB(i, j) == Color.BLACK.getRGB()) {
                        numberOfBlackPixels++;
                    }
                }
            }
            return (float) numberOfBlackPixels;
        } else if (quarterNumber == 2) {
            int numberOfBlackPixels = 0;
            BufferedImage image = ImageUtils.toBufferedImage(picture.getImage());
            for (int i = image.getHeight() / 2; i < image.getHeight(); i++) {
                for (int j = 0; j < image.getWidth() / 2; j++) {
                    if (image.getRGB(i, j) == Color.BLACK.getRGB()) {
                        numberOfBlackPixels++;
                    }
                }
            }
            return (float) numberOfBlackPixels;
        } else if (quarterNumber == 3) {
            int numberOfBlackPixels = 0;
            BufferedImage image = ImageUtils.toBufferedImage(picture.getImage());
            for (int i = image.getHeight() / 2; i < image.getHeight(); i++) {
                for (int j = 0; j < image.getWidth() / 2; j++) {
                    if (image.getRGB(i, j) == Color.BLACK.getRGB()) {
                        numberOfBlackPixels++;
                    }
                }
            }
            return (float) numberOfBlackPixels;
        } else {
            int numberOfBlackPixels = 0;
            BufferedImage image = ImageUtils.toBufferedImage(picture.getImage());
            for (int i = image.getHeight() / 2; i < image.getHeight(); i++) {
                for (int j = image.getWidth() / 2; j < image.getWidth(); j++) {
                    if (image.getRGB(i, j) == Color.BLACK.getRGB()) {
                        numberOfBlackPixels++;
                    }
                }
            }
            return (float) numberOfBlackPixels;
        }
    }

    public static Picture calculateFeatureInOnePicture(Picture picture, boolean isMnist) {
        float surfaceSize = 0;
        float quarterSize1 = 0;
        float quarterSize2 = 0;
        float quarterSize3 = 0;
        float quarterSize4 = 0;
        if (isMnist) {
            surfaceSize = getSurfaceSize(picture) / 10;
            quarterSize1 = getQuarterSize(picture, 1) / 2.5f;
            quarterSize2 = getQuarterSize(picture, 2) / 2.5f;
            quarterSize3 = getQuarterSize(picture, 3) / 2.5f;
            quarterSize4 = getQuarterSize(picture, 4) / 2.5f;
        }
        Map<String, Integer> minutiaesMap = getMinuatiaesMap(picture);
        int lineEnds = minutiaesMap.get("ENDING_POINT");
        int crossingPoints = minutiaesMap.get("CROSSING_POINT");

        LinkedList<Number> features = new LinkedList<>();
        if (isMnist) {
            features.add(surfaceSize);
            features.add(quarterSize1);
            features.add(quarterSize2);
            features.add(quarterSize3);
            features.add(quarterSize4);
        }
        features.add(lineEnds);
        features.add(crossingPoints);

        return new Picture(picture.getImage(), picture.getType(), features);
    }

    public static float getSurfaceSize(Picture picture) {
        return calculateNumberSurface(picture);
    }

    private static Map<String, Integer> getMinuatiaesMap(Picture picture) {
        BufferedImage tempImage = ImageUtils.toBufferedImage(picture.getImage());
        picture.setImage(tempImage);
        return getMinutiaes(tempImage);
    }

    private static Map<String, Integer> getMinutiaes(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        int lineEndsQuantity = 0;
        int crossingPointsQuantity = 0;
        for (int h = 1; h < height - 1; h++) {
            for (int w = 1; w < width - 1; w++) {

                if (getBinaryValueOfTheColor(image, w, h) == 1) {
                    double CN = 0;

                    int[] P = new int[10];

                    P[1] = getBinaryValueOfTheColor(image, w + 1, h);
                    P[2] = getBinaryValueOfTheColor(image, w + 1, h - 1);
                    P[3] = getBinaryValueOfTheColor(image, w, h - 1);
                    P[4] = getBinaryValueOfTheColor(image, w - 1, h - 1);
                    P[5] = getBinaryValueOfTheColor(image, w - 1, h);
                    P[6] = getBinaryValueOfTheColor(image, w - 1, h + 1);
                    P[7] = getBinaryValueOfTheColor(image, w, h + 1);
                    P[8] = getBinaryValueOfTheColor(image, w + 1, h + 1);
                    P[9] = P[1];

                    for (int i = 1; i <= 8; i++) {
                        CN += Math.abs(P[i] - P[i + 1]);
                    }
                    CN = CN * 0.5;

                    if (CN == 1) {
                        lineEndsQuantity++;
                        image.setRGB(w, h, Color.RED.getRGB());
                    } else if (CN >= 3) {
                        crossingPointsQuantity++;
                        image.setRGB(w, h, Color.GREEN.getRGB());
                    }
                }
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("CROSSING_POINT", crossingPointsQuantity);
        result.put("ENDING_POINT", lineEndsQuantity);

        return result;
    }

    private static int getBinaryValueOfTheColor(Image image, int w, int h) {
        Color p = new Color(((BufferedImage) image).getRGB(w, h));
        if (p.getRGB() == Color.WHITE.getRGB()) {
            return 0;
        } else {
            return 1;
        }
    }

    private static float calculateNumberSurface(Picture picture) {
        return (float) getNumberOfWhitePixels(ImageUtils.toBufferedImage(picture.getImage()));
    }

    private static int getNumberOfWhitePixels(BufferedImage image)
    {
        int returnValue = 0;
        for(int i = 0; i < image.getHeight(); i++)
        {
            for (int j = 0 ; j < image.getWidth(); j++)
            {
                //bufferedImage.getRaster().getDataBuffer()).getData()
                if(image.getRGB(i,j) == Color.BLACK.getRGB())
                    returnValue ++;
            }
        }

        return returnValue;
    }
}
