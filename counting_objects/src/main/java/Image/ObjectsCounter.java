package Image;

import Core.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import static Image.ImageUtils.*;

public class ObjectsCounter {

    private static int[][] TARGET_PIXELS;
    private static int[][] SOURCE_PIXELS;

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
        save(background, originalFileName + "_background", "bmp");

        BufferedImage contours;
        if (isImageWithGrapes) {
            contours = getContours(baseImage, true, 50000, 500, 10000);
        } else {
            contours = getContours(baseImage, true, 15000, 500, 7000);
        }
        save(contours, originalFileName + "_contours", "bmp");

        BufferedImage objects = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        BufferedImage imageWithMarkedBackgroundAndContours = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT,
                BufferedImage.TYPE_INT_RGB);

        SOURCE_PIXELS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        int x, y, colorFromImageWithBackground, colorFromImageWithContours;
        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                colorFromImageWithBackground = background.getRGB(x, y);
                colorFromImageWithContours = contours.getRGB(x, y);
                if (colorFromImageWithBackground == black && colorFromImageWithContours == black) {
                    objects.setRGB(x, y, baseImage.getRGB(x, y));
                    imageWithMarkedBackgroundAndContours.setRGB(x, y, objects.getRGB(x, y));
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
        save(objects, originalFileName + "_objects", "bmp");
        save(imageWithMarkedBackgroundAndContours, originalFileName + "_imageWithMarkedBackgroundAndContours",
                "bmp");

        ObjectsCount result = countLightAndDarkObjects(baseImage, objects, imageWithMarkedBackgroundAndContours);

        int X, Y, width, height;
        for (Map.Entry<Pair<Integer>, BufferedImage> region : REGIONS.entrySet()) {
            BufferedImage textImage = region.getValue();
            Pair<Integer> pixel = region.getKey();
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

        save(baseImage, originalFileName + "_result", "bmp");
        return result;
    }

    private static ObjectsCount countLightAndDarkObjects(BufferedImage baseImage, BufferedImage objects,
                                                         BufferedImage imageWithMarkedBackgroundAndContours) {

        Pair<Integer> lightDarkColorsPair = getLightAndDarkColor(objects);
        int lightColor = lightDarkColorsPair.x;
        int darkColor = lightDarkColorsPair.y;

        int x, y;
        int R, G, B, r, g, b, regionPixelsCount;
        int minRegionSize = isImageWithGrapes ? 700 : 500;
        int darkQuantity = 0;
        int lightQuantity = 0;
        boolean isDark;
        int currentColor, averageColorInRegion;
        int[] colorsFromRegion;
        int ldColor;

        for (int y1 = 0; y1 < IMAGE_HEIGHT; y1++) {
            for (int x1 = 0; x1 < IMAGE_WIDTH; x1++) {
                currentColor = imageWithMarkedBackgroundAndContours.getRGB(x1, y1);
                if (currentColor != NO_OBJECTS_COLOR && currentColor != BACKGROUND_COLOR
                        && currentColor != CONTOURS_COLOR) {

                    TARGET_PIXELS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
                    floodFill(x1, y1, currentColor, 20000, false);

                    colorsFromRegion = new int[IMAGE_SIZE];
                    regionPixelsCount = 0;
                    for (y = 0; y < IMAGE_HEIGHT; y++) {
                        for (x = 0; x < IMAGE_WIDTH; x++) {
                            if (TARGET_PIXELS[x][y] == -1) {
                                colorsFromRegion[regionPixelsCount] = imageWithMarkedBackgroundAndContours.getRGB(x, y);
                                regionPixelsCount++;
                                imageWithMarkedBackgroundAndContours.setRGB(x, y, NO_OBJECTS_COLOR);
                            }
                            SOURCE_PIXELS[x][y] = imageWithMarkedBackgroundAndContours.getRGB(x, y);
                        }
                    }

                    if (regionPixelsCount > minRegionSize) {

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

                        if (isDark) {
                            ldColor = darkColor;
                            darkQuantity++;
                        } else {
                            ldColor = lightColor;
                            lightQuantity++;
                        }

                        int middleX, middleY, minX = IMAGE_WIDTH, minY = IMAGE_HEIGHT, maxX = 0, maxY = 0;
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

                        String regionId = isDark ? "d." + String.valueOf(darkQuantity) : "l." + String.valueOf
                                (lightQuantity);
                        REGIONS.put(new Pair<>(middleX, middleY),
                                ImageUtils.textToImage(regionId, REGION_ID_TEXT_SIZE));

                    }
                }
            }
        }
        return new ObjectsCount(lightQuantity, darkQuantity);
    }

    private static Pair<Integer> getLightAndDarkColor(BufferedImage objects) {
        java.util.List<Integer> lightColors = new ArrayList<>();
        java.util.List<Integer> darkColors = new ArrayList<>();

        int x, y;
        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                int currentColor = objects.getRGB(x, y);
                if (currentColor != NO_OBJECTS_COLOR && currentColor != BACKGROUND_COLOR && currentColor != CONTOURS_COLOR) {
                    int distanceToBlack = ColorHelper.getDistance(currentColor, black);
                    int distanceToWhite = ColorHelper.getDistance(currentColor, white);
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

        int R = 0, G = 0, B = 0;
        int r, g, b;
        for (int lColor : lightColors) {
            r = (lColor >> 16) & 0xFF;
            g = (lColor >> 8) & 0xFF;
            b = (lColor) & 0xFF;
            R += r;
            G += g;
            B += b;
        }
        int lightColorsSize = lightColors.size();
        int lightColor = (((R / lightColorsSize) & 0xFF) << 16) |
                (((G / lightColorsSize) & 0xFF) << 8) |
                (((B / lightColorsSize) & 0xFF));

        R = 0;
        G = 0;
        B = 0;
        for (int dColor : darkColors) {
            r = (dColor >> 16) & 0xFF;
            g = (dColor >> 8) & 0xFF;
            b = (dColor) & 0xFF;
            R += r;
            G += g;
            B += b;
        }
        int darkColorsSize = darkColors.size();
        int darkColor = (((R / darkColorsSize) & 0xFF) << 16) |
                (((G / darkColorsSize) & 0xFF) << 8) |
                (((B / darkColorsSize) & 0xFF));

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

        Set<Integer> backgroundColors = new HashSet<>();
        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                if (TARGET_PIXELS[x][y] == -1) {
                    backgroundColors.add(imageWithContours.getRGB(x, y));
                    background.setRGB(x, y, BACKGROUND_COLOR);
                }
            }
        }

        int R = 0, G = 0, B = 0;

        int r;
        int g;
        int b;
        for (int color : backgroundColors) {
            r = (color >> 16) & 0xFF;
            g = (color >> 8) & 0xFF;
            b = (color) & 0xFF;

            R += r;
            G += g;
            B += b;
        }

        int size = backgroundColors.size();
        R = R / size;
        G = G / size;
        B = B / size;

        Color averageBackgroundColor = new Color(R, G, B);

        TARGET_PIXELS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        int color;
        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                color = imageWithContours.getRGB(x, y);
                if (ColorHelper.getDistance(color, averageBackgroundColor.getRGB()) < 300) {
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

    private static void floodFill(int x, int y, int selectedColor, int maxDistance, boolean markBorders) {

        if (x < 0 || x > IMAGE_WIDTH - 1 || y < 0 || y > IMAGE_HEIGHT - 1) {
            return;
        }

        if (TARGET_PIXELS[x][y] == -1
                || ColorHelper.getDistance(SOURCE_PIXELS[x][y], selectedColor) > maxDistance) {
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
