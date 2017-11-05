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
import java.nio.Buffer;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

enum LINE_TYP
{
    horizontal,
    vertical
}

public class FeaturesExtractor {


    public static FeaturesVector extractFeaturesVector(List<Picture> pictures) {
        //TODO: wyciagnąć cechy z zestawu zdjęć treningowych do wektora cech
        return new FeaturesVector(getMidSurfaceOfNumber(pictures),getVLine(pictures),getHLine(pictures),getNumberOfEnded(pictures));
    }

    //1 cecha
    public static Map<String,Float> getMidSurfaceOfNumber(List<Picture> pictures)
    {
        return calculateNumberSurface(pictures);
    }

    //2 cecha
    public static Map<String,Boolean> getVLine(List<Picture> pictures)
    {
        return determinatedLine(pictures, LINE_TYP.vertical);
    }

    //3 cecha
    public static Map<String,Boolean> getHLine(List<Picture> pictures)
    {
        return determinatedLine(pictures, LINE_TYP.horizontal);
    }

    //4 cecha
    public static Map<String,Integer> getNumberOfEnded(List<Picture> pictures)
    {
        return countNumberOfEnded(pictures);
    }

    private static Map<String,Boolean> determinatedLine(List<Picture> pictures, LINE_TYP lineType)
    {
        Map<String,Boolean> returnMap = new HashMap<>();
        Map<String, List<Boolean>> allValueMap = new HashMap<>();
        for(Picture picture:pictures)
        {
            boolean tempPictureHas =false;
            BufferedImage tempPicture = ImageUtils.toBufferedImage(picture.getImage());
            for(int i = 0; i < tempPicture.getWidth(); i++)
            {
                for(int j = 0; j < tempPicture.getWidth(); j++)
                {
                    if(tempPicture.getRGB(i,j) == Color.WHITE.getRGB())
                    {
                        if(lineType.equals(LINE_TYP.vertical))
                        {
                            if(hasVerticalLine(i,j,tempPicture,5))
                            {
                                tempPictureHas = true;
                                if(returnMap.keySet().contains(picture.getType()))
                                {
                                    allValueMap.get(picture.getType()).add(true);
                                }
                                else
                                {
                                    List<Boolean> tempList = new ArrayList<>();
                                    tempList.add(true);
                                    allValueMap.put(picture.getType(),tempList );
                                }
                                i = tempPicture.getWidth();
                                break;
                            }
                        }
                        if(lineType.equals(LINE_TYP.horizontal))
                        {
                            if(hasHorizontalLine(i,j,tempPicture,5))
                            {
                                tempPictureHas = true;
                                if(returnMap.keySet().contains(picture.getType()))
                                {
                                    allValueMap.get(picture.getType()).add(true);
                                }
                                else
                                {
                                    List<Boolean> tempList = new ArrayList<>();
                                    tempList.add(true);
                                    allValueMap.put(picture.getType(),tempList );
                                }
                                i = tempPicture.getWidth();
                                break;
                            }
                        }
                    }
                }
            }
            if(!tempPictureHas)
            {
                if(returnMap.keySet().contains(picture.getType()))
                {
                    allValueMap.get(picture.getType()).add(false);
                }
                else
                {
                    List<Boolean> tempList = new ArrayList<>();
                    tempList.add(false);
                    allValueMap.put(picture.getType(),tempList );
                }
            }
        }

        for(String key :allValueMap.keySet())
        {
            returnMap.put(key,midBooleanValue(allValueMap.get(key)));
        }
        return returnMap;
    }

    private static boolean midBooleanValue(List<Boolean> value)
    {
        int positiveValue = 0;
        for(int i = 0 ; i < value.size(); i ++)
        {
            if( value.get(i).equals(true))
            {
                positiveValue++;
            }
        }

        if(positiveValue > value.size()/2)
            return true;
        else
            return false;
    }

    private static Map<String, Integer> countNumberOfEnded(List<Picture> pictures)
    {
        Map<String,Integer> returnMap = new HashMap<>();
        for(Picture picture:pictures)
        {
            BufferedImage tempImage = ImageUtils.toBufferedImage(picture.getImage());
            int numberOfEnded = 0;
            for( int i = 1; i < tempImage.getWidth(); i+=3)
            {
                for(int j = 1; j < tempImage.getHeight(); j+=3)
                {
                    if(tempImage.getRGB(i,j) == Color.BLACK.getRGB())
                    {
                        if(lineWithEnd(i,j,tempImage) <= 2)
                            numberOfEnded++;
                    }
                }
            }
            if(!returnMap.keySet().contains(picture.getType()))
                returnMap.put(picture.getType(),numberOfEnded);
            else
                returnMap.put(picture.getType(),returnMap.get(picture.getType()) + numberOfEnded);
        }

        //mid number of ended
        int numberOccurrencesEnded = pictures.size() / 100;
        for(String key:returnMap.keySet())
        {
            returnMap.put(key,returnMap.get(key)/100);
        }
        return  returnMap;
    }

    private static int lineWithEnd (int w, int h, BufferedImage image)
    {
        int numberOfCount = 0;
        for(int i = -1; i < 2;i++)
        {
            for(int j = -1; j < 2;j++)
            {
                if(image.getHeight() > h + j && 0 <= h + j && w + i >= 0 && w + i < image.getWidth())
                {
                    if(image.getRGB(w + i,h + j) == Color.BLACK.getRGB())
                        numberOfCount++;
                }
            }
        }

        return numberOfCount;
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
                if(image.getRGB(i,j) == Color.BLACK.getRGB())
                    returnValue ++;
            }
        }

        return returnValue;
    }
}
