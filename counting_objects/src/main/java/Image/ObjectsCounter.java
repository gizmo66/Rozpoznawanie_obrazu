package Image;

import Core.Pair;
import Image.utils.GaussianFilter;

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
    private static Settings SETTINGS;

    public static ObjectsCount countObjects(BufferedImage baseImage, String originalFileName) {

        SETTINGS = Settings.getSettings(originalFileName);

        if (SETTINGS.isUseGaussianFilter()) {
            GaussianFilter gaussianFilter = new GaussianFilter(6);
            gaussianFilter.filter(baseImage, baseImage);
        }

        IMAGE_WIDTH = baseImage.getWidth();
        IMAGE_HEIGHT = baseImage.getHeight();
        IMAGE_SIZE = IMAGE_WIDTH * IMAGE_HEIGHT;

        BufferedImage imageWithContours = toBufferedImage(bufferedImageToMat(baseImage));

        BufferedImage tempContours = getContours(baseImage);
        save(tempContours, originalFileName + "_A_tempContours", "bmp");

        for (int y = 2; y < IMAGE_HEIGHT - 2; y++) {
            for (int x = 2; x < IMAGE_WIDTH - 2; x++) {
                if (tempContours.getRGB(x, y) == white) {
                    imageWithContours.setRGB(x, y, CONTOURS_COLOR);
                    tempContours.setRGB(x, y, CONTOURS_COLOR);
                }
            }
        }

        save(imageWithContours, originalFileName + "_A1_imageWithContours", "bmp");
        BufferedImage background = getBackground(imageWithContours);

        BufferedImage tempBackground = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                tempBackground.setRGB(x, y, background.getRGB(x, y));
            }
        }

        erosion(tempBackground, background);

        save(tempBackground, originalFileName + "_B0_tempBackground", "bmp");
        save(background, originalFileName + "_B_background", "bmp");

        BufferedImage contours = getContours(background);
        save(contours, originalFileName + "_C_contours", "bmp");

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
        save(objects, originalFileName + "_D_objects", "bmp");
        save(imageWithMarkedBackgroundAndContours, originalFileName + "_E_imageWithMarkedBackgroundAndContours",
                "bmp");

        return countLightAndDarkObjects(baseImage, objects, imageWithMarkedBackgroundAndContours,
                originalFileName);
    }

    private static void erosion(BufferedImage tempBackground, BufferedImage background) {
        int count;
        int xSub1, xAdd1, ySub1, yAdd1;
        int n1Color, n2Color, n3Color, n4Color, n5Color, n6Color, n7Color, n8Color, n9Color, n10Color, n11Color,
                n12Color;
        int n1ColorB, n2ColorB, n3ColorB, n4ColorB, n5ColorB, n6ColorB, n7ColorB, n8ColorB;
        int iterations = SETTINGS.getErosionIterations();
        for (int i = 0; i < iterations; i++) {
            for (int y = 2; y < IMAGE_HEIGHT - 2; y++) {
                for (int x = 2; x < IMAGE_WIDTH - 2; x++) {

                    count = 0;

                    xSub1 = x - 1;
                    xAdd1 = x + 1;
                    ySub1 = y - 1;
                    yAdd1 = y + 1;

                    n1Color = background.getRGB(xSub1, yAdd1);
                    n2Color = background.getRGB(x, yAdd1);
                    n3Color = background.getRGB(xAdd1, yAdd1);
                    n4Color = background.getRGB(xAdd1, y);
                    n5Color = background.getRGB(xAdd1, ySub1);
                    n6Color = background.getRGB(x, ySub1);
                    n7Color = background.getRGB(xSub1, ySub1);
                    n8Color = background.getRGB(xSub1, y);

                    n9Color = background.getRGB(x, y + 2);
                    n10Color = background.getRGB(x, y - 2);
                    n11Color = background.getRGB(x + 2, y);
                    n12Color = background.getRGB(x - 2, y);

                    n1ColorB = background.getRGB(x - 1, y + 2);
                    n2ColorB = background.getRGB(x + 1, y + 2);
                    n3ColorB = background.getRGB(x + 2, y + 1);
                    n4ColorB = background.getRGB(x + 2, y - 1);
                    n5ColorB = background.getRGB(x - 1, y - 2);
                    n6ColorB = background.getRGB(x + 1, y - 2);
                    n7ColorB = background.getRGB(x - 2, y + 1);
                    n8ColorB = background.getRGB(x - 2, y - 1);

                    if (n1Color == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n2Color == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n3Color == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n4Color == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n5Color == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n6Color == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n7Color == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n8Color == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n9Color == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n10Color == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n11Color == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n12Color == BACKGROUND_COLOR) {
                        count++;
                    }

                    if (n1ColorB == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n2ColorB == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n3ColorB == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n4ColorB == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n5ColorB == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n6ColorB == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n7ColorB == BACKGROUND_COLOR) {
                        count++;
                    }
                    if (n8ColorB == BACKGROUND_COLOR) {
                        count++;
                    }

                    if (background.getRGB(x, y) == black) {
                        if (count > 0 && count < 20) {
                            tempBackground.setRGB(x, y, BACKGROUND_COLOR);
                        } else {
                            tempBackground.setRGB(x, y, Color.yellow.getRGB());
                        }
                    }
                }
            }
            for (int y = 1; y < IMAGE_HEIGHT - 1; y++) {
                for (int x = 1; x < IMAGE_WIDTH - 1; x++) {
                    if (tempBackground.getRGB(x, y) == BACKGROUND_COLOR) {
                        if (i == 0) {
                            tempBackground.setRGB(x, y, CONTOURS_COLOR);
                        }
                        background.setRGB(x, y, BACKGROUND_COLOR);
                    }
                }
            }
            //save(baseImage, i + "_B0_baseImage", "bmp");
        }
    }

    private static ObjectsCount countLightAndDarkObjects(BufferedImage baseImage, BufferedImage objects,
                                                         BufferedImage imageWithMarkedBackgroundAndContours,
                                                         String originalFileName) {

        Pair<Integer> lightDarkColorsPair = getLightAndDarkColor(objects);
        int lightColor = lightDarkColorsPair.x;
        int darkColor = lightDarkColorsPair.y;

        int x, y, regionPixelsCount, tempColor, currentColor, ldColor, darkQuantity = 0, lightQuantity = 0;
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

                if (regionPixelsCount > SETTINGS.getMinRegionSize()) {

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
        save(baseImage, originalFileName + "_F_result", "bmp");

        return new ObjectsCount(lightQuantity, darkQuantity, SETTINGS.getLightQuantity(), SETTINGS.getDarkQuantity());
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
        floodFill(object[0], object[1], currentColor, SETTINGS.getMaxDistance(), false);
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
                        lightColors.add(currentColor);
                    } else {
                        darkColors.add(currentColor);
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

    private static BufferedImage getContours(BufferedImage baseImage) {

        int[][] sobelTemplate;
        int[][] sobelMatrixX = {{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1}};
        int[][] sobelMatrixY = {{-1, -1, -1}, {0, 0, 0}, {1, 1, 1}};

        int currentColor, r, g, b, avg;
        BufferedImage contours = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        sobelTemplate = new int[IMAGE_WIDTH][IMAGE_HEIGHT];

        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                currentColor = baseImage.getRGB(x, y);
                r = (currentColor >> 16) & 0xFF;
                g = (currentColor >> 8) & 0xFF;
                b = (currentColor) & 0xFF;

                avg = (r + g + b) / 3;

                sobelTemplate[x][y] = avg;
                currentColor = (avg << 24) | (avg << 16) | (avg << 8) | avg;
                contours.setRGB(x, y, currentColor);
            }
        }

        int px, py, pixel, rgb;
        for (int y = 1; y < IMAGE_HEIGHT - 1; y++) {
            for (int x = 1; x < IMAGE_WIDTH - 1; x++) {

                px = (sobelMatrixX[0][0] * sobelTemplate[x - 1][y - 1]) + (sobelMatrixX[0][1] * sobelTemplate[x][y - 1]) +
                        (sobelMatrixX[0][2] * sobelTemplate[x + 1][y - 1]) + (sobelMatrixX[1][0] * sobelTemplate[x - 1][y]) +
                        (sobelMatrixX[1][1] * sobelTemplate[x][y]) + (sobelMatrixX[1][2] * sobelTemplate[x + 1][y]) +
                        (sobelMatrixX[2][0] * sobelTemplate[x - 1][y + 1]) + (sobelMatrixX[2][1] * sobelTemplate[x][y + 1]) +
                        (sobelMatrixX[2][2] * sobelTemplate[x + 1][y + 1]);

                py = (sobelMatrixY[0][0] * sobelTemplate[x - 1][y - 1]) + (sobelMatrixY[0][1] * sobelTemplate[x][y - 1]) +
                        (sobelMatrixY[0][2] * sobelTemplate[x + 1][y - 1]) + (sobelMatrixY[1][0] * sobelTemplate[x - 1][y]) +
                        (sobelMatrixY[1][1] * sobelTemplate[x][y]) + (sobelMatrixY[1][2] * sobelTemplate[x + 1][y]) +
                        (sobelMatrixY[2][0] * sobelTemplate[x - 1][y + 1]) + (sobelMatrixY[2][1] * sobelTemplate[x][y + 1]) +
                        (sobelMatrixY[2][2] * sobelTemplate[x + 1][y + 1]);

                pixel = (int) Math.sqrt((px * px) + (py * py));

                if (pixel > 255) {
                    pixel = 255;
                } else if (pixel < 0) {
                    pixel = 0;
                }

                rgb = (pixel << 8) | (pixel << 16) | (pixel << 8) | pixel;

                if (ColorHelper.getDistance(rgb, white) < 110000) {
                    rgb = white;
                } else {
                    rgb = black;
                }

                contours.setRGB(x, y, rgb);
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
