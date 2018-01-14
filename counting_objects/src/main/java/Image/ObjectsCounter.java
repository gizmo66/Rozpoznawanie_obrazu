package Image;

import Core.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

import static Image.ImageUtils.*;

public class ObjectsCounter {

    private static BufferedImage IMAGE;
    private static BufferedImage IMAGE_MARKED_BACKGROUND_AND_CONTOURS;
    private static int[][] TARGET_PIXELS;
    private static int[][] SOURCE_PIXELS;

    private static int IMAGE_WIDTH;
    private static int IMAGE_HEIGHT;
    private static int IMAGE_SIZE;

    private static int black = Color.black.getRGB();
    private static int blue = Color.blue.getRGB();
    private static int red = Color.red.getRGB();
    private static int white = Color.white.getRGB();

    private static Map<Pair<Integer>, BufferedImage> REGIONS = new HashMap<>();
    private static final int REGION_ID_TEXT_SIZE = 14;

    public static ObjectsCount countObjects(BufferedImage image, String originalFileName) {

        IMAGE = toBufferedImage(bufferedImageToMat(image));
        IMAGE_WIDTH = IMAGE.getWidth();
        IMAGE_HEIGHT = IMAGE.getHeight();
        IMAGE_SIZE = IMAGE_WIDTH * IMAGE_HEIGHT;

        Set<Pair<Integer>> background;
        if (originalFileName.contains("grapes")) {
            background = getBackground(getContours(true, 50000, 200, 500));
        } else {
            background = getBackground(getContours(true, 50000, 200, 500));
        }

        Set<Pair<Integer>> imageNoBackground = new HashSet<>();
        for (int x = 0; x < IMAGE_WIDTH; x++) {
            for (int y = 0; y < IMAGE_HEIGHT; y++) {
                Pair<Integer> p = new Pair<>(x, y);
                if (!background.contains(p)) {
                    imageNoBackground.add(p);
                }
            }
        }

        BufferedImage temp = toBufferedImage(bufferedImageToMat(image));
        Set<Pair<Integer>> contours;
        if (originalFileName.contains("grapes")) {
            contours = getContours(true, 50000, 500, 10000);
        } else {
            contours = getContours(true, 50000, 3000, 7000);
        }

        for (Pair<Integer> pixel : contours) {
            temp.setRGB(pixel.x, pixel.y, red);
        }

        for (Pair<Integer> pixel : background) {
            temp.setRGB(pixel.x, pixel.y, blue);
        }
        IMAGE_MARKED_BACKGROUND_AND_CONTOURS = temp;

        ImageUtils.save(temp, originalFileName + "_contours_no_background", "bmp");
        ObjectsCount result = countLightAndDarkObjects(imageNoBackground, contours, originalFileName);

        for (int x = 0; x < IMAGE_WIDTH; x++) {
            for (int y = 0; y < IMAGE_HEIGHT; y++) {
                image.setRGB(x, y, IMAGE_MARKED_BACKGROUND_AND_CONTOURS.getRGB(x, y));
            }
        }

        int X, Y;
        for (Map.Entry<Pair<Integer>, BufferedImage> region : REGIONS.entrySet()) {
            BufferedImage textImage = region.getValue();
            Pair<Integer> pixel = region.getKey();
            int width = textImage.getWidth();
            int height = textImage.getHeight();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    X = pixel.x + x;
                    Y = pixel.y + y;
                    if (X < IMAGE_WIDTH && Y < IMAGE_HEIGHT) {
                        image.setRGB(pixel.x + x, pixel.y + y, textImage.getRGB(x, y));
                    }
                }
            }
        }

        save(image, originalFileName + "_result", "bmp");
        return result;
    }

    private static ObjectsCount countLightAndDarkObjects(Set<Pair<Integer>> imageNoBackground, Set<Pair<Integer>> contours, String originalFileName) {

        Set<Pair<Integer>> objectsPixels = imageNoBackground.stream().filter(p -> !contours.contains(p))
                .collect(Collectors.toSet());

        Pair<Integer> lightDarkColorsPair = getLightAndDarkColor(objectsPixels);
        int lightColor = lightDarkColorsPair.x;
        int darkColor = lightDarkColorsPair.y;

        /*BufferedImage temp = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (Pair<Integer> pixel : objectsPixels) {
            temp.setRGB(pixel.x, pixel.y, IMAGE_MARKED_BACKGROUND_AND_CONTOURS.getRGB(pixel.x, pixel.y));
        }
        save(temp, originalFileName + "_objects", "bmp");*/

        SOURCE_PIXELS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        int x, y;
        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                SOURCE_PIXELS[x][y] = IMAGE_MARKED_BACKGROUND_AND_CONTOURS.getRGB(x, y);
            }
        }

        TARGET_PIXELS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        Pair<Integer> p = new Pair<>(0, 0);
        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                p.x = x;
                p.y = y;
                if (!objectsPixels.contains(p)) {
                    TARGET_PIXELS[x][y] = -1;
                }
            }
        }

        int R, G, B, r, g, b, count, minRegionSize = IMAGE_WIDTH;
        int darkQuantity = 0;
        int lightQuantity = 0;
        boolean isDark;
        int currentColor, colorFromRegion;
        int[] colorsFromRegion;
        int[][] USED_PIXELS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        int ldColor;

        for (Pair<Integer> pixel : objectsPixels) {

            currentColor = IMAGE_MARKED_BACKGROUND_AND_CONTOURS.getRGB(pixel.x, pixel.y);
            if (USED_PIXELS[pixel.x][pixel.y] != 1 && currentColor != red && currentColor != black && currentColor !=
                    lightColor && currentColor != darkColor) {

                TARGET_PIXELS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
                floodFill(pixel.x, pixel.y, currentColor, 30000, false);

                colorsFromRegion = new int[IMAGE_SIZE];
                count = 0;
                for (y = 0; y < IMAGE_HEIGHT; y++) {
                    for (x = 0; x < IMAGE_WIDTH; x++) {
                        if (TARGET_PIXELS[x][y] == -1) {
                            colorsFromRegion[count] = IMAGE_MARKED_BACKGROUND_AND_CONTOURS.getRGB(x, y);
                            USED_PIXELS[x][y] = 1;
                            count++;
                        }
                    }
                }

                if (count > minRegionSize) {

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

                    colorFromRegion = (((R / count) & 0xFF) << 16) |
                            (((G / count) & 0xFF) << 8) |
                            (((B / count) & 0xFF));

                    isDark = ColorHelper.getDistance(colorFromRegion, darkColor)
                            < ColorHelper.getDistance(colorFromRegion, lightColor);

                    if (isDark) {
                        ldColor = darkColor;
                        darkQuantity++;
                    } else {
                        ldColor = lightColor;
                        lightQuantity++;
                    }

                    for (y = 0; y < IMAGE_HEIGHT; y++) {
                        for (x = 0; x < IMAGE_WIDTH; x++) {
                            if (TARGET_PIXELS[x][y] == -1) {
                                IMAGE_MARKED_BACKGROUND_AND_CONTOURS.setRGB(x, y, ldColor);
                            }
                        }
                    }

                    int middleX = 0, middleY = 0, minX = IMAGE_WIDTH, minY = IMAGE_HEIGHT, maxX = 0, maxY = 0;
                    for (y = 0; y < IMAGE_HEIGHT; y++) {
                        for (x = 0; x < IMAGE_WIDTH; x++) {
                            if (TARGET_PIXELS[x][y] == -1) {
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
                    REGIONS.put(new Pair<>(middleX, middleY), ImageUtils.textToImage(regionId, REGION_ID_TEXT_SIZE));
                }
            }
        }
        return new ObjectsCount(lightQuantity, darkQuantity);
    }

    private static Pair<Integer> getLightAndDarkColor(Set<Pair<Integer>> objectsPixels) {
        java.util.List<Integer> lightColors = new ArrayList<>();
        java.util.List<Integer> darkColors = new ArrayList<>();

        for (Pair<Integer> pixel : objectsPixels) {
            int currentColor = IMAGE.getRGB(pixel.x, pixel.y);
            if (ColorHelper.getDistance(currentColor, white) < ColorHelper.getDistance(currentColor, black)) {
                lightColors.add(currentColor);
            } else {
                darkColors.add(currentColor);
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

    private static Set<Pair<Integer>> getContours(boolean markExtraNeighbors, int distance, int lDistance,
                                                  int dDistance) {
        Set<Pair<Integer>> result = new HashSet<>();

        int d, currentColor;
        int n1Color, n2Color, n3Color, n4Color, n5Color, n6Color, n7Color, n8Color;
        int xSub1, xAdd1, ySub1, yAdd1;

        for (int y = 1; y < IMAGE_HEIGHT - 1; y++) {
            for (int x = 1; x < IMAGE_WIDTH - 1; x++) {
                currentColor = IMAGE.getRGB(x, y);

                if (ColorHelper.isSimilar(currentColor, white, distance)) {
                    d = lDistance;
                } else {
                    d = dDistance;
                }

                xSub1 = x - 1;
                xAdd1 = x + 1;
                ySub1 = y - 1;
                yAdd1 = y + 1;

                n1Color = IMAGE.getRGB(xSub1, yAdd1);
                n2Color = IMAGE.getRGB(x, yAdd1);
                n3Color = IMAGE.getRGB(xAdd1, yAdd1);
                n4Color = IMAGE.getRGB(xAdd1, y);
                n5Color = IMAGE.getRGB(xAdd1, ySub1);
                n6Color = IMAGE.getRGB(x, ySub1);
                n7Color = IMAGE.getRGB(xSub1, ySub1);
                n8Color = IMAGE.getRGB(xSub1, y);

                if (ColorHelper.getDistance(n1Color, currentColor) > d) {
                    result.add(new Pair<>(xSub1, yAdd1));
                    result.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        result.add(new Pair<>(x, yAdd1));
                        result.add(new Pair<>(xSub1, y));
                    }
                }
                if (ColorHelper.getDistance(n2Color, currentColor) > d) {
                    result.add(new Pair<>(x, yAdd1));
                    result.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        result.add(new Pair<>(xSub1, yAdd1));
                        result.add(new Pair<>(xAdd1, yAdd1));
                        result.add(new Pair<>(xAdd1, y));
                        result.add(new Pair<>(xSub1, y));
                    }
                }
                if (ColorHelper.getDistance(n3Color, currentColor) > d) {
                    result.add(new Pair<>(xAdd1, yAdd1));
                    result.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        result.add(new Pair<>(x, yAdd1));
                        result.add(new Pair<>(xAdd1, y));
                    }
                }
                if (ColorHelper.getDistance(n4Color, currentColor) > d) {
                    result.add(new Pair<>(xAdd1, y));
                    result.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        result.add(new Pair<>(x, yAdd1));
                        result.add(new Pair<>(xAdd1, yAdd1));
                        result.add(new Pair<>(xAdd1, ySub1));
                        result.add(new Pair<>(x, ySub1));
                    }
                }
                if (ColorHelper.getDistance(n5Color, currentColor) > d) {
                    result.add(new Pair<>(xAdd1, ySub1));
                    result.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        result.add(new Pair<>(xAdd1, y));
                        result.add(new Pair<>(x, ySub1));
                    }
                }
                if (ColorHelper.getDistance(n6Color, currentColor) > d) {
                    result.add(new Pair<>(x, ySub1));
                    result.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        result.add(new Pair<>(xAdd1, y));
                        result.add(new Pair<>(xAdd1, ySub1));
                        result.add(new Pair<>(xSub1, ySub1));
                        result.add(new Pair<>(xSub1, y));
                    }
                }
                if (ColorHelper.getDistance(n7Color, currentColor) > d) {
                    result.add(new Pair<>(xSub1, ySub1));
                    result.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        result.add(new Pair<>(x, ySub1));
                        result.add(new Pair<>(xSub1, y));
                    }
                }
                if (ColorHelper.getDistance(n8Color, currentColor) > d) {
                    result.add(new Pair<>(xSub1, y));
                    result.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        result.add(new Pair<>(x, ySub1));
                        result.add(new Pair<>(xSub1, ySub1));
                        result.add(new Pair<>(xSub1, yAdd1));
                        result.add(new Pair<>(x, yAdd1));
                    }
                }
            }
        }
        return result;
    }

    private static Set<Pair<Integer>> getBackground(Set<Pair<Integer>> contours) {

        BufferedImage imageWithContours = toBufferedImage(bufferedImageToMat(IMAGE));
        for (Pair<Integer> pixel : contours) {
            imageWithContours.setRGB(pixel.x, pixel.y, red);
        }

        Set<Pair<Integer>> result = new HashSet<>();
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

        for (y = 0; y < IMAGE_HEIGHT; y++) {
            for (x = 0; x < IMAGE_WIDTH; x++) {
                if (TARGET_PIXELS[x][y] == -1) {
                    result.add(new Pair<>(x, y));
                }
            }
        }

        Set<Integer> backgroundColors = new HashSet<>();
        for (Pair<Integer> pixel : result) {
            backgroundColors.add(imageWithContours.getRGB(pixel.x, pixel.y));
        }
        result.clear();

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
                    result.add(new Pair<>(x, y));
                }
            }
        }

        return result;
    }

    private static void floodFill(int x, int y, int selectedColor, int maxDistance, boolean markBorders) {

        if (x < 0 || x > IMAGE_WIDTH - 1 || y < 0 || y > IMAGE_HEIGHT - 1) {
            return;
        }

        if (SOURCE_PIXELS[x][y] == red || TARGET_PIXELS[x][y] == -1 || ColorHelper.getDistance(SOURCE_PIXELS[x][y],
                selectedColor) > maxDistance) {
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
