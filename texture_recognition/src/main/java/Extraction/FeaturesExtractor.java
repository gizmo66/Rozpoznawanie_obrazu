package Extraction;

import Core.ImageUtils;
import Core.Picture;
import Core.ThinnerImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class FeaturesExtractor {

    public static FeaturesVector extractFeaturesVector(LinkedList<Picture> pictures) {
        Map<String, Map<String, LinkedList<Number>>> imageClassToFeaturesValuesMap = new LinkedHashMap<>();
        for (Picture picture : pictures) {
            LinkedHashMap<StarFeaturesEnum, Number> starFeaturesValues = new LinkedHashMap<>();

            Map<String, Integer> minutiaesMap = getMinutiaes(picture);
            starFeaturesValues.put(StarFeaturesEnum.EDGES_TO_SURFACE, minutiaesMap.get(MinutiaesEnum.EDGES_LENGTH
                        .getName()) / getSurfaceSize(picture) * 50);

            LinkedHashMap<FeaturesEnum, Number> allFeaturesValues = new LinkedHashMap<>();
            allFeaturesValues.put(FeaturesEnum.LINE_ENDS, minutiaesMap.get(MinutiaesEnum.ENDING_POINT.getName()));
            allFeaturesValues.put(FeaturesEnum.CROSSING_POINTS, minutiaesMap.get(MinutiaesEnum.CROSSING_POINT.getName()));

            if (imageClassToFeaturesValuesMap.get(picture.getType()) != null) {
                for (StarFeaturesEnum feature : Arrays.asList(StarFeaturesEnum.values())) {
                    imageClassToFeaturesValuesMap.get(picture.getType()).get(feature.getName())
                            .add(starFeaturesValues.get(feature));
                }

                for (FeaturesEnum feature : Arrays.asList(FeaturesEnum.values())) {
                    imageClassToFeaturesValuesMap.get(picture.getType()).get(feature.getName())
                            .add(allFeaturesValues.get(feature));
                }
            } else {
                LinkedHashMap<String, LinkedList<Number>> featureNameToValuesMap = new LinkedHashMap<>();

                for (StarFeaturesEnum starFeature : Arrays.asList(StarFeaturesEnum.values())) {
                    LinkedList<Number> valuesList = new LinkedList<>();
                    valuesList.add(starFeaturesValues.get(starFeature));
                    featureNameToValuesMap.put(starFeature.getName(), valuesList);
                }

                for (FeaturesEnum feature : Arrays.asList(FeaturesEnum.values())) {
                    LinkedList<Number> valuesList = new LinkedList<>();
                    valuesList.add(allFeaturesValues.get(feature));
                    featureNameToValuesMap.put(feature.getName(), valuesList);
                }
                imageClassToFeaturesValuesMap.put(picture.getType(), featureNameToValuesMap);
            }
        }
        return new FeaturesVector(imageClassToFeaturesValuesMap);
    }

    private static Float getQuarterSize(Picture picture, int hStart, int hEnd, int wStart, int wEnd) {
        int numberOfBlackPixels = 0;
        BufferedImage image = ImageUtils.toBufferedImage(picture.getImage());
        for (int i = hStart; i < hEnd; i++) {
            for (int j = wStart; j < wEnd; j++) {
                if (image.getRGB(i, j) == Color.BLACK.getRGB()) {
                    numberOfBlackPixels++;
                }
            }
        }
        return (float) numberOfBlackPixels;
    }

    public static Picture calculateFeatureInOnePicture(Picture picture) {
        LinkedList<Number> features = new LinkedList<>();

        Map<String, Integer> minutiaesMap = getMinutiaes(picture);
        float edgesToSurface = minutiaesMap.get(MinutiaesEnum.EDGES_LENGTH.getName()) / getSurfaceSize(picture) * 50;
        features.add(edgesToSurface);

        int lineEnds = minutiaesMap.get(MinutiaesEnum.ENDING_POINT.getName());
        int crossingPoints = minutiaesMap.get(MinutiaesEnum.CROSSING_POINT.getName());

        features.add(lineEnds);
        features.add(crossingPoints);

        return new Picture(picture.getImage(), picture.getType(), features);
    }

    public static float getSurfaceSize(Picture picture) {
        return calculateNumberSurface(picture);
    }

    private static Map<String, Integer> getMinutiaes(Picture picture) {
        BufferedImage image = (BufferedImage) picture.getImage();
        BufferedImage tempImage = ImageUtils.toBufferedImage(picture.getImage());

        int height = image.getHeight();
        int width = image.getWidth();

        for (int h = 1; h < height - 1; h++) {
            for (int w = 1; w < width - 1; w++) {
                if (getBinaryValueOfTheColor(image, w, h) == 1) {
                    double CN = getCrossingNumber(image, h, w);
                    if (CN == 1) {
                        tempImage.setRGB(w, h, Color.RED.getRGB());
                    } else if (CN >= 3) {
                        tempImage.setRGB(w, h, Color.GREEN.getRGB());
                    }
                }
            }
        }

        int edgesLength = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (tempImage.getRGB(w, h) == Color.RED.getRGB()) {
                    edgesLength++;
                }
            }
        }

        image = (BufferedImage) ThinnerImage.Start(picture);

        int lineEndsQuantity = 0;
        int crossingPointsQuantity = 0;
        for (int h = 1; h < height - 1; h++) {
            for (int w = 1; w < width - 1; w++) {
                if (getBinaryValueOfTheColor(image, w, h) == 1) {
                    double CN = getCrossingNumber(image, h, w);
                    if (CN == 1) {
                        lineEndsQuantity++;
                    } else if (CN >= 3) {
                        crossingPointsQuantity++;
                    }
                }
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put(MinutiaesEnum.CROSSING_POINT.getName(), crossingPointsQuantity);
        result.put(MinutiaesEnum.ENDING_POINT.getName(), lineEndsQuantity);
        result.put(MinutiaesEnum.EDGES_LENGTH.getName(), edgesLength);

        return result;
    }

    private static double getCrossingNumber(BufferedImage image, int h, int w) {
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
        return CN;
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
        return (float) getNumberOfBlackPixels(ImageUtils.toBufferedImage(picture.getImage()));
    }

    private static int getNumberOfBlackPixels(BufferedImage image) {
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
