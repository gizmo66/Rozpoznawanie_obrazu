package Image;

import Core.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Image.ImageUtils.*;

public class ObjectsCounter {

    private static int[][] TARGET_PIXELS;
    private static int[][] SOURCE_PIXELS;

    private static int[][] OBJECTS;
    private static int OBJECTS_SIZE;

    private static int IMAGE_WIDTH;
    private static int IMAGE_HEIGHT;
    private static int IMAGE_SIZE;

    private static int black = Color.black.getRGB();
    private static int blue = Color.blue.getRGB();
    private static int red = Color.red.getRGB();
    private static int white = Color.white.getRGB();
    private static int green = Color.green.getRGB();

    private static int BACKGROUND_COLOR = blue;
    private static int CONTOURS_COLOR = red;
    private static int NO_OBJECTS_COLOR = green;

    private static Map<Pair<Integer>, BufferedImage> REGIONS = new HashMap<>();
    private static final int REGION_ID_TEXT_SIZE = 14;

    private static boolean isImageWithGrapes;

    public static ObjectsCount countObjects(BufferedImage baseImage, String originalFileName) {

        IMAGE_WIDTH = baseImage.getWidth();
        IMAGE_HEIGHT = baseImage.getHeight();
        IMAGE_SIZE = IMAGE_WIDTH * IMAGE_HEIGHT;

        BufferedImage imageWithContours = toBufferedImage(bufferedImageToMat(baseImage));
        BufferedImage tempContours;
        isImageWithGrapes = originalFileName.contains("grapes");
        if (isImageWithGrapes) {
            tempContours = getContours(baseImage, true, 50000, 200, 500);
        } else {
            tempContours = getContours(baseImage, true, 50000, 200, 500);
        }

        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                if (tempContours.getRGB(x, y) == CONTOURS_COLOR) {
                    imageWithContours.setRGB(x, y, CONTOURS_COLOR);
                }
            }
        }

        BufferedImage background = getBackground(imageWithContours);
        //save(background, originalFileName + "_background", "bmp");

        BufferedImage contours;
        if (isImageWithGrapes) {
            contours = getContours(baseImage, true, 50000, 500, 10000);
        } else {
            contours = getContours(baseImage, true, 15000, 500, 7000);
        }
        //save(contours, originalFileName + "_contours", "bmp");

        BufferedImage objects = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        BufferedImage imageWithMarkedBackgroundAndContours = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT,
                BufferedImage.TYPE_INT_RGB);

        SOURCE_PIXELS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        OBJECTS = new int[IMAGE_SIZE][2];
        OBJECTS_SIZE = 0;
        int x, y, colorFromImageWithBackground, colorFromImageWithContours;
        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                colorFromImageWithBackground = background.getRGB(x, y);
                colorFromImageWithContours = contours.getRGB(x, y);
                if (colorFromImageWithBackground == black && colorFromImageWithContours == black) {
                    objects.setRGB(x, y, baseImage.getRGB(x, y));
                    imageWithMarkedBackgroundAndContours.setRGB(x, y, objects.getRGB(x, y));
                    OBJECTS[OBJECTS_SIZE][0] = x;
                    OBJECTS[OBJECTS_SIZE][1] = y;
                    OBJECTS_SIZE++;
                } else {
                    if (colorFromImageWithBackground == BACKGROUND_COLOR) {
                        imageWithMarkedBackgroundAndContours.setRGB(x, y, BACKGROUND_COLOR);
                        baseImage.setRGB(x, y, BACKGROUND_COLOR);
                    }
                    if (colorFromImageWithContours == CONTOURS_COLOR) {
                        imageWithMarkedBackgroundAndContours.setRGB(x, y, CONTOURS_COLOR);
                        baseImage.setRGB(x, y, CONTOURS_COLOR);
                    }
                    objects.setRGB(x, y, NO_OBJECTS_COLOR);
                }
                SOURCE_PIXELS[x][y] = imageWithMarkedBackgroundAndContours.getRGB(x, y);
            }
        }
        //save(objects, originalFileName + "_objects", "bmp");
        //save(imageWithMarkedBackgroundAndContours, originalFileName + "_imageWithMarkedBackgroundAndContours",
        //        "bmp");

        return countLightAndDarkObjects(baseImage, objects, imageWithMarkedBackgroundAndContours,
                originalFileName);
    }

    private static ObjectsCount countLightAndDarkObjects(BufferedImage baseImage, BufferedImage objects,
                                                         BufferedImage imageWithMarkedBackgroundAndContours,
                                                         String originalFileName) {

        Pair<Integer> lightDarkColorsPair = getLightAndDarkColor(objects);
        int lightColor = lightDarkColorsPair.x;
        int darkColor = lightDarkColorsPair.y;

        int x, y, regionPixelsCount, tempColor, currentColor, ldColor, darkQuantity = 0, lightQuantity = 0;
        int minRegionSize = isImageWithGrapes ? 700 : 500;
        boolean isDark;
        int[] colorsFromRegion;

        for (int o = 0; o < OBJECTS_SIZE; o++) {

            currentColor = imageWithMarkedBackgroundAndContours.getRGB(OBJECTS[o][0], OBJECTS[o][1]);
            if (currentColor != NO_OBJECTS_COLOR && currentColor != BACKGROUND_COLOR
                    && currentColor != CONTOURS_COLOR) {

                fillRegion(currentColor, OBJECTS[o]);

                colorsFromRegion = new int[IMAGE_SIZE];
                regionPixelsCount = 0;
                for (y = 0; y < IMAGE_HEIGHT; y++) {
                    for (x = 0; x < IMAGE_WIDTH; x++) {
                        tempColor = imageWithMarkedBackgroundAndContours.getRGB(x, y);
                        if (TARGET_PIXELS[x][y] == -1) {
                            colorsFromRegion[regionPixelsCount] = tempColor;
                            regionPixelsCount++;
                            imageWithMarkedBackgroundAndContours.setRGB(x, y, NO_OBJECTS_COLOR);
                        }
                        SOURCE_PIXELS[x][y] = tempColor;
                    }
                }

                if (regionPixelsCount > minRegionSize) {

                    isDark = isRegionDark(lightColor, darkColor, regionPixelsCount, colorsFromRegion);
                    if (isDark) {
                        ldColor = darkColor;
                        darkQuantity++;
                    } else {
                        ldColor = lightColor;
                        lightQuantity++;
                    }

                    Pair<Integer> middleOfRegion = markRegionAndGetItMiddle(baseImage, ldColor);
                    String regionId = getRegionId(darkQuantity, lightQuantity, isDark);

                    REGIONS.put(middleOfRegion, ImageUtils.textToImage(regionId, REGION_ID_TEXT_SIZE));

                }
            }
        }

        markRegionsIds(baseImage);
        save(baseImage, originalFileName + "_result", "bmp");

        return new ObjectsCount(lightQuantity, darkQuantity);
    }

    private static Pair<Integer> markRegionAndGetItMiddle(BufferedImage baseImage, int ldColor) {
        int minX = IMAGE_WIDTH, minY = IMAGE_HEIGHT, maxX = 0, maxY = 0, y, x, middleX, middleY;
        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                if (TARGET_PIXELS[x][y] == -1) {
                    baseImage.setRGB(x, y, ldColor);
                    if (x < minX) {
                        minX = x;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }

        middleX = (minX + maxX) / 2;
        middleY = (minY + maxY) / 2;

        return new Pair<>(middleX, middleY);
    }

    private static void fillRegion(int currentColor, int[] object) {
        TARGET_PIXELS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        floodFill(object[0], object[1], currentColor, 20000, false);
    }

    private static String getRegionId(int darkQuantity, int lightQuantity, boolean isDark) {
        return isDark ? "d." + String.valueOf(darkQuantity) : "l." + String.valueOf
                (lightQuantity);
    }

    private static boolean isRegionDark(int lightColor, int darkColor, int regionPixelsCount, int[] colorsFromRegion) {
        int R, G, B, r, g, b, averageColorInRegion;
        boolean isDark;
        R = 0;
        G = 0;
        B = 0;
        for (int c : colorsFromRegion) {
            r = (c >> 16) & 0xFF;
            g = (c >> 8) & 0xFF;
            b = (c) & 0xFF;
            R += r;
            G += g;
            B += b;
        }

        averageColorInRegion = (((R / regionPixelsCount) & 0xFF) << 16) |
                (((G / regionPixelsCount) & 0xFF) << 8) |
                (((B / regionPixelsCount) & 0xFF));

        isDark = ColorHelper.getDistance(averageColorInRegion, darkColor)
                < ColorHelper.getDistance(averageColorInRegion, lightColor);
        return isDark;
    }

    private static void markRegionsIds(BufferedImage baseImage) {
        int x, y, X, Y, width, height;
        BufferedImage textImage;
        Pair<Integer> pixel;
        for (Map.Entry<Pair<Integer>, BufferedImage> region : REGIONS.entrySet()) {
            textImage = region.getValue();
            pixel = region.getKey();
            width = textImage.getWidth();
            height = textImage.getHeight();
            for (x = 0; x < width; x++) {
                for (y = 0; y < height; y++) {
                    X = pixel.x + x;
                    Y = pixel.y + y;
                    if (X < IMAGE_WIDTH && Y < IMAGE_HEIGHT) {
                        baseImage.setRGB(pixel.x + x, pixel.y + y,
                                textImage.getRGB(x, y));
                    }
                }
            }
        }
    }

    private static Pair<Integer> getLightAndDarkColor(BufferedImage objects) {
        java.util.List<Integer> lightColors = new ArrayList<>();
        java.util.List<Integer> darkColors = new ArrayList<>();

        int x, y, currentColor, distanceToBlack, distanceToWhite;
        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                currentColor = objects.getRGB(x, y);
                if (currentColor != NO_OBJECTS_COLOR && currentColor != BACKGROUND_COLOR
                        && currentColor != CONTOURS_COLOR) {
                    distanceToBlack = ColorHelper.getDistance(currentColor, black);
                    distanceToWhite = ColorHelper.getDistance(currentColor, white);
                    if (distanceToWhite < distanceToBlack) {
                        if (distanceToWhite < 60000) {
                            lightColors.add(currentColor);
                        }
                    } else {
                        if (distanceToBlack < 7000) {
                            darkColors.add(currentColor);
                        }
                    }
                }
            }
        }

        int lightColor = getAverageColor(lightColors);
        int darkColor = getAverageColor(darkColors);

        if (ColorHelper.getDistance(lightColor, white) > ColorHelper.getDistance(darkColor, white)) {
            int tempColor = lightColor;
            lightColor = darkColor;
            darkColor = tempColor;
        }

        return new Pair<>(lightColor, darkColor);
    }

    private static BufferedImage getContours(BufferedImage baseImage, boolean markExtraNeighbors, int distance,
                                             int lDistance, int dDistance) {

        int d, currentColor;
        int n1Color, n2Color, n3Color, n4Color, n5Color, n6Color, n7Color, n8Color;
        int xSub1, xAdd1, ySub1, yAdd1;

        BufferedImage contours = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 1; y < IMAGE_HEIGHT - 1; y++) {
            for (int x = 1; x < IMAGE_WIDTH - 1; x++) {
                currentColor = baseImage.getRGB(x, y);

                if (ColorHelper.isSimilar(currentColor, white, distance)) {
                    d = lDistance;
                } else {
                    d = dDistance;
                }

                xSub1 = x - 1;
                xAdd1 = x + 1;
                ySub1 = y - 1;
                yAdd1 = y + 1;

                n1Color = baseImage.getRGB(xSub1, yAdd1);
                n2Color = baseImage.getRGB(x, yAdd1);
                n3Color = baseImage.getRGB(xAdd1, yAdd1);
                n4Color = baseImage.getRGB(xAdd1, y);
                n5Color = baseImage.getRGB(xAdd1, ySub1);
                n6Color = baseImage.getRGB(x, ySub1);
                n7Color = baseImage.getRGB(xSub1, ySub1);
                n8Color = baseImage.getRGB(xSub1, y);

                if (ColorHelper.getDistance(n1Color, currentColor) > d) {
                    contours.setRGB(xSub1, yAdd1, CONTOURS_COLOR);
                    contours.setRGB(x, y, CONTOURS_COLOR);

                    if (markExtraNeighbors) {
                        contours.setRGB(x, yAdd1, CONTOURS_COLOR);
                        contours.setRGB(xSub1, y, CONTOURS_COLOR);
                    }
                }
                if (ColorHelper.getDistance(n2Color, currentColor) > d) {
                    contours.setRGB(x, yAdd1, CONTOURS_COLOR);
                    contours.setRGB(x, y, CONTOURS_COLOR);

                    if (markExtraNeighbors) {
                        contours.setRGB(xSub1, yAdd1, CONTOURS_COLOR);
                        contours.setRGB(xAdd1, yAdd1, CONTOURS_COLOR);
                        contours.setRGB(xAdd1, y, CONTOURS_COLOR);
                        contours.setRGB(xSub1, y, CONTOURS_COLOR);
                    }
                }
                if (ColorHelper.getDistance(n3Color, currentColor) > d) {
                    contours.setRGB(xAdd1, yAdd1, CONTOURS_COLOR);
                    contours.setRGB(x, y, CONTOURS_COLOR);

                    if (markExtraNeighbors) {
                        contours.setRGB(x, yAdd1, CONTOURS_COLOR);
                        contours.setRGB(xAdd1, y, CONTOURS_COLOR);
                    }
                }
                if (ColorHelper.getDistance(n4Color, currentColor) > d) {
                    contours.setRGB(xAdd1, y, CONTOURS_COLOR);
                    contours.setRGB(x, y, CONTOURS_COLOR);

                    if (markExtraNeighbors) {
                        contours.setRGB(x, yAdd1, CONTOURS_COLOR);
                        contours.setRGB(xAdd1, yAdd1, CONTOURS_COLOR);
                        contours.setRGB(xAdd1, ySub1, CONTOURS_COLOR);
                        contours.setRGB(x, ySub1, CONTOURS_COLOR);
                    }
                }
                if (ColorHelper.getDistance(n5Color, currentColor) > d) {
                    contours.setRGB(xAdd1, ySub1, CONTOURS_COLOR);
                    contours.setRGB(x, y, CONTOURS_COLOR);

                    if (markExtraNeighbors) {
                        contours.setRGB(xAdd1, y, CONTOURS_COLOR);
                        contours.setRGB(x, ySub1, CONTOURS_COLOR);
                    }
                }
                if (ColorHelper.getDistance(n6Color, currentColor) > d) {
                    contours.setRGB(x, ySub1, CONTOURS_COLOR);
                    contours.setRGB(x, y, CONTOURS_COLOR);

                    if (markExtraNeighbors) {
                        contours.setRGB(xAdd1, y, CONTOURS_COLOR);
                        contours.setRGB(xAdd1, ySub1, CONTOURS_COLOR);
                        contours.setRGB(xSub1, ySub1, CONTOURS_COLOR);
                        contours.setRGB(xSub1, y, CONTOURS_COLOR);
                    }
                }
                if (ColorHelper.getDistance(n7Color, currentColor) > d) {
                    contours.setRGB(xSub1, ySub1, CONTOURS_COLOR);
                    contours.setRGB(x, y, CONTOURS_COLOR);

                    if (markExtraNeighbors) {
                        contours.setRGB(x, ySub1, CONTOURS_COLOR);
                        contours.setRGB(xSub1, y, CONTOURS_COLOR);
                    }
                }
                if (ColorHelper.getDistance(n8Color, currentColor) > d) {
                    contours.setRGB(xSub1, y, CONTOURS_COLOR);
                    contours.setRGB(x, y, CONTOURS_COLOR);

                    if (markExtraNeighbors) {
                        contours.setRGB(x, ySub1, CONTOURS_COLOR);
                        contours.setRGB(xSub1, ySub1, CONTOURS_COLOR);
                        contours.setRGB(xSub1, yAdd1, CONTOURS_COLOR);
                        contours.setRGB(x, yAdd1, CONTOURS_COLOR);
                    }
                }
            }
        }
        return contours;
    }

    private static BufferedImage getBackground(BufferedImage imageWithContours) {

        BufferedImage background = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

        TARGET_PIXELS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        SOURCE_PIXELS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];

        int x, y;
        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                SOURCE_PIXELS[x][y] = imageWithContours.getRGB(x, y);
            }
        }

        int initialBackgroundColor = imageWithContours.getRGB(10, 20);
        floodFill(10, 20, initialBackgroundColor, 15000, true);

        List<Integer> backgroundColors = new ArrayList<>();
        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                if (TARGET_PIXELS[x][y] == -1) {
                    backgroundColors.add(SOURCE_PIXELS[x][y]);
                    background.setRGB(x, y, BACKGROUND_COLOR);
                }
            }
        }

        int averageBackgroundColor = getAverageColor(backgroundColors);

        TARGET_PIXELS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        int color;
        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                color = SOURCE_PIXELS[x][y];
                if (ColorHelper.getDistance(color, averageBackgroundColor) < 300) {
                    floodFill(x, y, color, 15000, true);
                }
            }
        }

        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                if (TARGET_PIXELS[x][y] == -1) {
                    background.setRGB(x, y, BACKGROUND_COLOR);
                }
            }
        }

        return background;
    }

    private static int getAverageColor(List<Integer> colors) {
        int R = 0, G = 0, B = 0, r, g, b;
        for (int color : colors) {
            r = (color >> 16) & 0xFF;
            g = (color >> 8) & 0xFF;
            b = (color) & 0xFF;

            R += r;
            G += g;
            B += b;
        }

        int colorsSize = colors.size();
        return (((R / colorsSize) & 0xFF) << 16) |
                (((G / colorsSize) & 0xFF) << 8) |
                (((B / colorsSize) & 0xFF));
    }

    private static void floodFill(int x, int y, int selectedColor, int maxDistance, boolean markBorders) {

        if (x < 0 || x > IMAGE_WIDTH - 1 || y < 0 || y > IMAGE_HEIGHT - 1) {
            return;
        }

        if (TARGET_PIXELS[x][y] == -1) {
            return;
        }

        if (ColorHelper.getDistance(SOURCE_PIXELS[x][y], selectedColor) > maxDistance) {
            if (markBorders) {
                TARGET_PIXELS[x][y] = -1;
            }
            return;
        }

        TARGET_PIXELS[x][y] = -1;
        floodFill(x, y - 1, selectedColor, maxDistance, markBorders);
        floodFill(x + 1, y, selectedColor, maxDistance, markBorders);
        floodFill(x, y + 1, selectedColor, maxDistance, markBorders);
        floodFill(x - 1, y, selectedColor, maxDistance, markBorders);
    }

}
