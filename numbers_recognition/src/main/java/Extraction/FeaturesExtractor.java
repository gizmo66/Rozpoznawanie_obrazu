package Extraction;

import Core.ImageUtils;
import Core.Picture;
import View.Window;
import View.WindowTestRecognizer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.FeatureDescriptor;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class FeaturesExtractor {


    public static FeaturesVector extractFeaturesVector(List<Picture> pictures) {
        //TODO: wyciagnąć cechy z zestawu zdjęć treningowych do wektora cech
        return new FeaturesVector();
    }

    public static Map<String,Float> getMidSurfaceOfNumber(List<Picture> pictures)
    {
        return calculateNumberSurface(pictures);
    }

    private static Map<String,Float> calculateNumberSurface(List<Picture> pictures)
    {
        Map<String,Float> tempMap = new HashMap<>();
        for(Picture picture:pictures)
        {
            if(tempMap.get(picture.getType()) != null)
            {
                float tempValue = (float)getNumberWhitePixels(ImageUtils.toBufferedImage(picture.getImage())) + tempMap.get(picture.getType());
                tempMap.put(picture.getType(), tempValue);
            }
            else
                tempMap.put(picture.getType(), (float)getNumberWhitePixels(ImageUtils.toBufferedImage(picture.getImage())));
        }
        //mid surface calculate
        float numberOccurrencesDigit = pictures.size() / 10;
        for(String key:tempMap.keySet())
        {
            tempMap.put(key,tempMap.get(key)/numberOccurrencesDigit);
        }
        return tempMap;
    }

    private static int getNumberWhitePixels(BufferedImage image)
    {
        int returnValue = 0;
        for(int i = 0; i < image.getHeight(); i++)
        {
            for (int j = 0 ; j < image.getWidth(); j++)
            {
                //bufferedImage.getRaster().getDataBuffer()).getData()
                if(image.getRGB(i,j) == Color.WHITE.getRGB())
                    returnValue ++;
            }
        }

        return returnValue;
    }
}
