package Extraction;

import Core.ImageUtils;
import Core.Picture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum LINE_TYP
{
    horizontal,
    vertical
}

public class FeaturesExtractor {

    public static FeaturesVector extractFeaturesVector(List<Picture> pictures) {

        Map<String, Map<String, List<Number>>> imageClassToFeaturesValuesMap = new HashMap<>();

        for(Picture picture : pictures) {
            Float surfaceSize = getSurfaceSize(picture);
            Float lengthOfHLine = getLenghtOfHLine(picture);
            Float lengthOfVLine = getLenghtOfVLine(picture);
            Integer numbersOfEnded = getNumberOfEnded(picture);
            /*Boolean hasHLine = getHLine(picture);
            Boolean hasVLine = getVLine(picture);*/

            if(imageClassToFeaturesValuesMap.get(picture.getType()) != null) {
                imageClassToFeaturesValuesMap.get(picture.getType()).get("SURFACE").add(surfaceSize);
                imageClassToFeaturesValuesMap.get(picture.getType()).get("VERTICAL_LINES").add(lengthOfHLine);
                imageClassToFeaturesValuesMap.get(picture.getType()).get("HORIZONTAL_LINES").add(lengthOfVLine);
                imageClassToFeaturesValuesMap.get(picture.getType()).get("ENDED_NUMBER").add(numbersOfEnded);
                /*imageClassToFeaturesValuesMap.get(picture.getType()).get("VERTICAL_LINE").add(hasHLine ? 1 : 0);
                imageClassToFeaturesValuesMap.get(picture.getType()).get("HORIZONTAL_LINE").add(hasVLine ? 1 : 0);*/
            } else {
                Map<String, List<Number>> featureNameToValuesMap = new HashMap<>();

                List<Number> surfaceSizeList = new ArrayList<>();
                surfaceSizeList.add(surfaceSize);
                featureNameToValuesMap.put("SURFACE", surfaceSizeList);

                List<Number> lengthOfHLineList = new ArrayList<>();
                lengthOfHLineList.add(lengthOfHLine);
                featureNameToValuesMap.put("VERTICAL_LINES", lengthOfHLineList);

                List<Number> lengthOfVLineList = new ArrayList<>();
                lengthOfVLineList.add(lengthOfVLine);
                featureNameToValuesMap.put("HORIZONTAL_LINES", lengthOfVLineList);

                List<Number> numbersOfEndedList = new ArrayList<>();
                numbersOfEndedList.add(numbersOfEnded);
                featureNameToValuesMap.put("ENDED_NUMBER", numbersOfEndedList);

                /*List<Number> hasHLineList = new ArrayList<>();
                hasHLineList.add(hasHLine ? 1 : 0);
                featureNameToValuesMap.put("VERTICAL_LINE", hasHLineList);

                List<Number> hasVLineList = new ArrayList<>();
                hasVLineList.add(hasVLine ? 1 : 0);
                featureNameToValuesMap.put("HORIZONTAL_LINE", hasVLineList);*/

                imageClassToFeaturesValuesMap.put(picture.getType(), featureNameToValuesMap);
            }
        }
        return new FeaturesVector(imageClassToFeaturesValuesMap);
    }

    public static Picture calculateFeatureInOnePicture(Picture picture) {
        float surfaceSize = getSurfaceSize(picture);
        float hLenght = getLenghtOfHLine(picture);
        float vLenght = getLenghtOfVLine(picture);
        int numberOfEnded = getNumberOfEnded(picture);

        List<Number> features = new ArrayList<>();
        features.add(surfaceSize);
        features.add(hLenght);
        features.add(vLenght);
        features.add(numberOfEnded);

        return new Picture(picture.getImage(), picture.getType(), features);
    }

    //cecha 2 dlugosc lini pionowych
    public static float getLenghtOfVLine(Picture picture) {
        return determinatedLineLenght(picture, LINE_TYP.vertical);
    }

    //cecha 3 dlugosc lini pionowych
    public static float getLenghtOfHLine(Picture picture) {
        return determinatedLineLenght(picture, LINE_TYP.horizontal);
    }

    //1 cecha
    public static float getSurfaceSize(Picture picture) {
        return calculateNumberSurface(picture);
    }

    //2 cecha
    public static boolean getVLine(Picture picture) {
        return determinatedLine(picture, LINE_TYP.vertical);
    }

    //3 cecha
    public static boolean getHLine(Picture picture) {
        return determinatedLine(picture, LINE_TYP.horizontal);
    }

    //4 cecha
    public static int getNumberOfEnded(Picture picture) {
        return getNumberOfEndedLines(picture);
    }

    private static float determinatedLineLenght(Picture picture, LINE_TYP lineType) {
        BufferedImage tempPicture = ImageUtils.toBufferedImage(picture.getImage());
        for(int i = 0; i < tempPicture.getWidth(); i++)
        {
            for(int j = 0; j < tempPicture.getWidth(); j++)
            {
                if(tempPicture.getRGB(i,j) == Color.BLACK.getRGB())
                {
                    if(lineType.equals(LINE_TYP.vertical))
                    {
                        return countVerticalLine(i,j,tempPicture,4);
                    }
                    if(lineType.equals(LINE_TYP.horizontal))
                    {
                        return countHorizontalLines(i,j,tempPicture,4);
                    }
                }
            }
        }
        return 0;
    }

    private static boolean determinatedLine(Picture picture, LINE_TYP lineType)
    {
        BufferedImage tempPicture = ImageUtils.toBufferedImage(picture.getImage());
        for(int i = 0; i < tempPicture.getWidth(); i++)
        {
            for(int j = 0; j < tempPicture.getWidth(); j++)
            {
                if(tempPicture.getRGB(i,j) == Color.BLACK.getRGB())
                {
                    if(lineType.equals(LINE_TYP.vertical))
                    {
                        if(hasVerticalLine(i,j,tempPicture,5))
                        {
                            return true;
                        }
                    }
                    if(lineType.equals(LINE_TYP.horizontal))
                    {
                        if(hasHorizontalLine(i,j,tempPicture,5))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static Integer getNumberOfEndedLines(Picture picture) {
        BufferedImage tempImage = ImageUtils.toBufferedImage(picture.getImage());
        int numberOfEndedLines = 0;
        for (int i = 1; i < tempImage.getWidth(); i += 3) {
            for (int j = 1; j < tempImage.getHeight(); j += 3) {
                if (tempImage.getRGB(i, j) == Color.BLACK.getRGB()) {
                    if (getLineEndsQuantity(i, j, tempImage) <= 2) {
                        numberOfEndedLines++;
                    }
                }
            }
        }
        return numberOfEndedLines;
    }

    private static int getLineEndsQuantity(int w, int h, BufferedImage image) {
        int lineEndsQuantity = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (image.getHeight() > h + j && 0 <= h + j && w + i >= 0 && w + i < image.getWidth()) {
                    if (image.getRGB(w + i, h + j) == Color.BLACK.getRGB()) {
                        lineEndsQuantity++;
                    }
                }
            }
        }
        return lineEndsQuantity;
    }

    private static float countVerticalLine(int w, int h, BufferedImage image, int rateLenght)
    {
        int lineLenght = 0;
        for(int i = h; i < image.getHeight();i++)
        {
            if(h+i >= 0 && h+i < image.getHeight())
            {
                if(image.getRGB(w,h+i) == Color.BLACK.getRGB())
                {
                    lineLenght++;
                }
                else
                    break;
            }
            else
                break;
        }

        if(lineLenght >= rateLenght)
            return  lineLenght;
        else
            return 0;
    }

    private static boolean hasVerticalLine(int w, int h, BufferedImage image, int minimumLeght)
    {
        int lineLenght = 0;
        for(int i = h; i < image.getHeight();i++)
        {
            if(h+i >= 0 && h+i < image.getHeight())
            {
                if(image.getRGB(w,h+i) == Color.BLACK.getRGB())
                {
                    lineLenght++;
                }
                else
                {
                    if(lineLenght < minimumLeght)
                        lineLenght = 0;
                }
            }
            else
            {
                break;
            }
        }
        if(lineLenght >= minimumLeght)
            return  true;
        else
            return false;
    }

    private static float countHorizontalLines(int w, int h, BufferedImage image, int rateLenght)
    {
        int lineLenght = 0;
        for(int i = w; i < image.getWidth();i++)
        {
            if(w+i >= 0 && w+i < image.getWidth())
            {
                if(image.getRGB(w+i,h) == Color.BLACK.getRGB())
                {
                    lineLenght++;
                }
                else
                    break;
            }
            else
                break;
        }
        if(lineLenght >= rateLenght)
            return  lineLenght;
        else
            return 0;
    }

    private static boolean hasHorizontalLine(int w, int h, BufferedImage image, int minimumLeght)
    {
        int lineLenght = 0;
        for(int i = w; i < image.getWidth();i++)
        {
            if(w+i >= 0 && w+i < image.getWidth())
            {
                if(image.getRGB(w+i,h) == Color.BLACK.getRGB())
                {
                    lineLenght++;
                }
                else
                {
                    if(lineLenght < minimumLeght)
                        lineLenght = 0;
                }
            }
            else
            {
                break;
            }
        }
        if(lineLenght >= minimumLeght)
            return  true;
        else
            return false;
    }

    private static float calculateNumberSurface(Picture picture) {
        return (float) getNumberWhitePixels(ImageUtils.toBufferedImage(picture.getImage()));
    }

    private static int getNumberWhitePixels(BufferedImage image)
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
