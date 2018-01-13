package Image;

import Core.Pair;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ImageUtils {

    private static final String LIB_NAME = "opencv_java320";
    private static final Color LIGHT_COLOR = Color.yellow;
    private static final Color DARK_COLOR = Color.magenta;
    private static int[][] COLORS;
    private static Image IMAGE;
    private static Image TEMP;
    private static int IMAGE_WIDTH;
    private static int IMAGE_HEIGHT;
    private static int white = Color.white.getRGB();
    private static List<Pair<Integer>> buffer = new ArrayList<>();

    public static Image fileToImage(File file) {
        String opencvpath = "C:\\OpenCV\\opencv\\build\\java\\x64\\";
        System.load(opencvpath + LIB_NAME + ".dll");

        Mat src = Imgcodecs.imread(file.getAbsolutePath().replaceAll("\\\\", "/"));
        return toBufferedImage(src);
    }

    private static Image toBufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b);
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    public static BufferedImage toBufferedImage(Image img) {
        return toBufferedImage(img, BufferedImage.TYPE_INT_ARGB);
    }

    public static BufferedImage scaleImage(BufferedImage img, double scaleX, double scaleY) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage scaledImg = new BufferedImage((int) (w * scaleX), (int) (h * scaleY), BufferedImage
                .TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scaleX, scaleY);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        scaledImg = scaleOp.filter(img, scaledImg);
        return scaledImg;
    }

    public static void save(Image image, String fileName, String extension) {
        File file = new File(fileName + "." + extension);
        try {
            ImageIO.write(toBufferedImage(image, BufferedImage.TYPE_INT_RGB), extension, file);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    private static BufferedImage toBufferedImage(Image img, int type) {
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), type);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    public static Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    public static Image f(Image image, AtomicInteger lightQuantity, AtomicInteger darkQuantity) {
        Mat src = bufferedImageToMat((BufferedImage) image);
        Image result = toBufferedImage(src);
        Image temp = toBufferedImage(src);
        int height = ((BufferedImage) result).getHeight();
        int width = ((BufferedImage) result).getWidth();

        IMAGE = result;
        IMAGE_WIDTH = width;
        IMAGE_HEIGHT = height;

        markContours((BufferedImage) result, true, 50000, Color.BLACK.getRGB(),
                200, 500);

        COLORS = new int[width][height];

        markBackground((BufferedImage) temp);

        markContours((BufferedImage) temp, true, 50000, Color.red.getRGB(),
                500, 10000);

        COLORS = null;
        IMAGE = null;

        //countObjects(temp, lightQuantity, darkQuantity);

        return temp;
    }

    private static void countObjects(Image image, AtomicInteger lightQuantity, AtomicInteger darkQuantity) {
        List<Integer> lightColors = new ArrayList<>();
        List<Integer> darkColors = new ArrayList<>();
        int black = Color.black.getRGB();
        int blue = Color.blue.getRGB();
        int red = Color.red.getRGB();
        int white = Color.white.getRGB();
        int green = Color.green.getRGB();
        for (int h = 0; h < IMAGE_HEIGHT; h++) {
            for (int w = 0; w < IMAGE_WIDTH; w++) {
                int currentColor = ((BufferedImage) image).getRGB(w, h);
                if (currentColor != black && currentColor != blue && currentColor != red) {
                    if (ColorHelper.getDistance(currentColor, white) > ColorHelper.getDistance(currentColor, black)) {
                        lightColors.add(currentColor);
                    } else {
                        darkColors.add(currentColor);
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
        Color lightColor = new Color(R / lightColors.size(), G / lightColors.size(), B / lightColors.size());

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
        Color darkColor = new Color(R / darkColors.size(), G / darkColors.size(), B / darkColors.size());

        if (ColorHelper.getDistance(lightColor, Color.white) > ColorHelper.getDistance(darkColor, Color.white)) {
            Color temp = lightColor;
            lightColor = darkColor;
            darkColor = temp;
        }

        List<Integer> colorsFromRegion = new ArrayList<>();
        Color colorFromRegion, ldColor;
        int currentColor;
        boolean isDark;
        for (int h = 0; h < IMAGE_HEIGHT; h++) {
            for (int w = 0; w < IMAGE_WIDTH; w++) {
                currentColor = ((BufferedImage) image).getRGB(w, h);
                if (currentColor != black && currentColor != blue && currentColor != red
                        && currentColor != LIGHT_COLOR.getRGB()
                        && currentColor != DARK_COLOR.getRGB() && currentColor != green) {

                    COLORS = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
                    floodFill(w, h, currentColor, green, 5000, false);

                    colorsFromRegion.clear();
                    for (int y = 0; y < IMAGE_HEIGHT; y++) {
                        for (int x = 0; x < IMAGE_WIDTH; x++) {
                            if (COLORS[x][y] == green) {
                                colorsFromRegion.add(((BufferedImage) image).getRGB(x, y));
                            }
                        }
                    }

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

                    int colorsFromRegionSize = colorsFromRegion.size();
                    if (colorsFromRegionSize > 0) {
                        colorFromRegion = new Color(R / colorsFromRegionSize, G / colorsFromRegionSize,
                                B / colorsFromRegionSize);

                        isDark = ColorHelper.getDistance(colorFromRegion.getRGB(), lightColor.getRGB())
                                > ColorHelper.getDistance(colorFromRegion.getRGB(), darkColor.getRGB());

                        ldColor = DARK_COLOR;
                        if (!isDark) {
                            ldColor = LIGHT_COLOR;
                            lightQuantity.incrementAndGet();
                        } else {
                            darkQuantity.incrementAndGet();
                        }

                        for (int y = 0; y < IMAGE_HEIGHT; y++) {
                            for (int x = 0; x < IMAGE_WIDTH; x++) {
                                if (COLORS[x][y] == green) {
                                    ((BufferedImage) image).setRGB(x, y, ldColor.getRGB());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void markBackground(BufferedImage temp) {
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                COLORS[x][y] = ((BufferedImage) IMAGE).getRGB(x, y);
            }
        }

        floodFill(10, 20, ((BufferedImage) IMAGE).getRGB(10, 20), Color.blue.getRGB(), 15000, true);

        List<Integer> backgroundColors = new ArrayList<>();
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                if (COLORS[x][y] == Color.blue.getRGB()) {
                    backgroundColors.add(temp.getRGB(x, y));
                    temp.setRGB(x, y, COLORS[x][y]);
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

        int color;
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                color = temp.getRGB(x, y);
                if (ColorHelper.getDistance(color, averageBackgroundColor.getRGB()) < 300) {
                    floodFill(x, y, color, Color.blue.getRGB(), 15000, true);
                }
            }
        }

        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                if (COLORS[x][y] == Color.blue.getRGB()) {
                    temp.setRGB(x, y, COLORS[x][y]);
                }
            }
        }
    }

    private static void markContours(BufferedImage result, boolean markExtraNeighbors, int
            distance, int color, int lDistance, int dDistance) {

        int d, currentColor;
        int n1Color, n2Color, n3Color, n4Color, n5Color, n6Color, n7Color, n8Color;
        int xSub1, xAdd1, ySub1, yAdd1;

        for (int y = 1; y < IMAGE_HEIGHT - 1; y++) {
            for (int x = 1; x < IMAGE_WIDTH - 1; x++) {
                currentColor = result.getRGB(x, y);

                if (ColorHelper.isSimilar(currentColor, white, distance)) {
                    d = lDistance;
                } else {
                    d = dDistance;
                }

                xSub1 = x - 1;
                xAdd1 = x + 1;
                ySub1 = y - 1;
                yAdd1 = y + 1;

                n1Color = result.getRGB(xSub1, yAdd1);
                n2Color = result.getRGB(x, yAdd1);
                n3Color = result.getRGB(xAdd1, yAdd1);
                n4Color = result.getRGB(xAdd1, y);
                n5Color = result.getRGB(xAdd1, ySub1);
                n6Color = result.getRGB(x, ySub1);
                n7Color = result.getRGB(xSub1, ySub1);
                n8Color = result.getRGB(xSub1, y);

                if (ColorHelper.getDistance(n1Color, currentColor) > d) {
                    buffer.add(new Pair<>(xSub1, yAdd1));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x, yAdd1));
                        buffer.add(new Pair<>(xSub1, y));
                    }
                }
                if (ColorHelper.getDistance(n2Color, currentColor) > d) {
                    buffer.add(new Pair<>(x, yAdd1));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(xSub1, yAdd1));
                        buffer.add(new Pair<>(xAdd1, yAdd1));
                        buffer.add(new Pair<>(xAdd1, y));
                        buffer.add(new Pair<>(xSub1, y));
                    }
                }
                if (ColorHelper.getDistance(n3Color, currentColor) > d) {
                    buffer.add(new Pair<>(xAdd1, yAdd1));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x, yAdd1));
                        buffer.add(new Pair<>(xAdd1, y));
                    }
                }
                if (ColorHelper.getDistance(n4Color, currentColor) > d) {
                    buffer.add(new Pair<>(xAdd1, y));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x, yAdd1));
                        buffer.add(new Pair<>(xAdd1, yAdd1));
                        buffer.add(new Pair<>(xAdd1, ySub1));
                        buffer.add(new Pair<>(x, ySub1));
                    }
                }
                if (ColorHelper.getDistance(n5Color, currentColor) > d) {
                    buffer.add(new Pair<>(xAdd1, ySub1));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(xAdd1, y));
                        buffer.add(new Pair<>(x, ySub1));
                    }
                }
                if (ColorHelper.getDistance(n6Color, currentColor) > d) {
                    buffer.add(new Pair<>(x, ySub1));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(xAdd1, y));
                        buffer.add(new Pair<>(xAdd1, ySub1));
                        buffer.add(new Pair<>(xSub1, ySub1));
                        buffer.add(new Pair<>(xSub1, y));
                    }
                }
                if (ColorHelper.getDistance(n7Color, currentColor) > d) {
                    buffer.add(new Pair<>(xSub1, ySub1));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x, ySub1));
                        buffer.add(new Pair<>(xSub1, y));
                    }
                }
                if (ColorHelper.getDistance(n8Color, currentColor) > d) {
                    buffer.add(new Pair<>(xSub1, y));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x, ySub1));
                        buffer.add(new Pair<>(xSub1, ySub1));
                        buffer.add(new Pair<>(xSub1, yAdd1));
                        buffer.add(new Pair<>(x, yAdd1));
                    }
                }
            }
        }

        for (Pair pair : buffer) {
            result.setRGB((Integer) pair.getP1(), (Integer) pair.getP2(), color);
        }
        buffer.clear();
    }

    private static void floodFill(int x, int y, int selectedColor, int newColor, int maxDistance, boolean markBorders) {
        if (x < 0 || x > IMAGE_WIDTH - 1 || y < 0 || y > IMAGE_HEIGHT - 1) return;

        if (Color.red.getRGB() == COLORS[x][y] || ColorHelper.getDistance(COLORS[x][y], selectedColor) > maxDistance) {
            if (markBorders) {
                COLORS[x][y] = newColor;
            }
            return;
        }

        COLORS[x][y] = newColor;
        floodFill(x, y - 1, selectedColor, newColor, maxDistance, markBorders);
        floodFill(x + 1, y, selectedColor, newColor, maxDistance, markBorders);
        floodFill(x, y + 1, selectedColor, newColor, maxDistance, markBorders);
        floodFill(x - 1, y, selectedColor, newColor, maxDistance, markBorders);
    }

    public static BufferedImage toBufferImageFrom2DArray(int[][] array, int width, int height) {
        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int black = Color.BLACK.getRGB();
        int white = Color.WHITE.getRGB();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (array[i][j] == 1)
                    bimage.setRGB(j, i, black);
                else
                    bimage.setRGB(j, i, white);
            }
        }

        return bimage;
    }


}
