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
    private static final Color DARK_COLOR = new Color(60, 30, 20);
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

        markContours((BufferedImage) result, height, width, true, 50000, Color.BLACK.getRGB(),
                200, 500);

        IMAGE = result;
        IMAGE_WIDTH = width;
        IMAGE_HEIGHT = height;

        COLORS = new int[width][height];

        markBackground((BufferedImage) temp, height, width);

        markContours((BufferedImage) temp, height, width, true, 50000, Color.red.getRGB(),
                500, 10000);

        COLORS = null;
        IMAGE = null;

        countObjects(temp, lightQuantity, darkQuantity);

        return temp;
    }

    private static void countObjects(Image image, AtomicInteger lightQuantity, AtomicInteger darkQuantity) {
        List<Integer> lightColors = new ArrayList<>();
        List<Integer> darkColors = new ArrayList<>();
        int height = ((BufferedImage) image).getHeight();
        int width = ((BufferedImage) image).getWidth();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int currentColor = ((BufferedImage) image).getRGB(w, h);
                if (currentColor != Color.black.getRGB() && currentColor != Color.blue.getRGB() &&
                        currentColor != Color.red.getRGB()) {
                    if (ColorHelper.getDistance(currentColor, Color.white.getRGB()) > ColorHelper.getDistance(currentColor, Color
                            .black.getRGB())) {
                        lightColors.add(currentColor);
                    } else {
                        darkColors.add(currentColor);
                    }
                }
            }
        }

        int lR = 0;
        int lG = 0;
        int lB = 0;
        for (int lColor : lightColors) {
            int r = (lColor >> 16) & 0xFF;
            int g = (lColor >> 8) & 0xFF;
            int b = (lColor) & 0xFF;
            lR += r;
            lG += g;
            lB += b;
        }
        Color lightColor = new Color(lR / lightColors.size(), lG / lightColors.size(), lB / lightColors.size());

        int dR = 0;
        int dG = 0;
        int dB = 0;
        for (int dColor : darkColors) {
            int r = (dColor >> 16) & 0xFF;
            int g = (dColor >> 8) & 0xFF;
            int b = (dColor) & 0xFF;
            dR += r;
            dG += g;
            dB += b;
        }
        Color darkColor = new Color(dR / darkColors.size(), dG / darkColors.size(), dB / darkColors.size());

        if (ColorHelper.getDistance(lightColor, Color.white) > ColorHelper.getDistance(darkColor, Color.white)) {
            Color temp = lightColor;
            lightColor = darkColor;
            darkColor = temp;
        }

        //TODO
        //optymalizacja, zeby mozna było się doczekać całego obrazka
        //poprawić liczenie i sprawdzić dlaczego tyle razy dodaje się count a nie powinien

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int currentColor = ((BufferedImage) image).getRGB(w, h);
                if (currentColor != Color.black.getRGB() && currentColor != Color.blue.getRGB() &&
                        currentColor != Color.red.getRGB() && currentColor != LIGHT_COLOR.getRGB()
                        && currentColor != DARK_COLOR.getRGB() && currentColor != Color.green.getRGB()) {

                    COLORS = new int[width][height];
                    floodFill(w, h, ((BufferedImage) image).getRGB(w, h), Color.green.getRGB(), 200);

                    List<Integer> colorsFromRegion = new ArrayList<>();
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            if (COLORS[x][y] == Color.green.getRGB()) {
                                colorsFromRegion.add(((BufferedImage) image).getRGB(x, y));
                            }
                        }
                    }

                    int R = 0;
                    int G = 0;
                    int B = 0;
                    for (int c : colorsFromRegion) {
                        int r = (c >> 16) & 0xFF;
                        int g = (c >> 8) & 0xFF;
                        int b = (c) & 0xFF;
                        R += r;
                        G += g;
                        B += b;
                    }
                    Color colorFromRegion = new Color(R / colorsFromRegion.size(), G / colorsFromRegion.size(),
                            B / colorsFromRegion.size());

                    boolean isDark = true;
                    if (ColorHelper.getDistance(colorFromRegion.getRGB(), lightColor.getRGB())
                            < ColorHelper.getDistance(colorFromRegion.getRGB(), darkColor.getRGB())) {
                        isDark = false;
                    }

                    Color ldColor = DARK_COLOR;
                    if (!isDark) {
                        ldColor = LIGHT_COLOR;
                        lightQuantity.incrementAndGet();
                    } else {
                        darkQuantity.incrementAndGet();
                    }

                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            if (COLORS[x][y] == Color.green.getRGB()) {
                                ((BufferedImage) image).setRGB(x, y, ldColor.getRGB());
                            }
                        }
                    }

                    COLORS = null;

                }
            }
        }
    }

    private static void markBackground(BufferedImage temp, int height, int width) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                COLORS[x][y] = ((BufferedImage) IMAGE).getRGB(x, y);
            }
        }

        floodFill(10, 20, ((BufferedImage) IMAGE).getRGB(10, 20), Color.blue.getRGB(), 15000);

        List<Integer> backgroundColors = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (COLORS[x][y] == Color.blue.getRGB()) {
                    backgroundColors.add(temp.getRGB(x, y));
                    temp.setRGB(x, y, COLORS[x][y]);
                }
            }
        }

        int R = 0, G = 0, B = 0;

        for (int color : backgroundColors) {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = (color) & 0xFF;

            R += r;
            G += g;
            B += b;
        }

        int size = backgroundColors.size();
        int aR = R / size;
        int aG = G / size;
        int aB = B / size;

        Color averageBackgroundColor = new Color(aR, aG, aB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = temp.getRGB(x, y);
                if (ColorHelper.getDistance(color, averageBackgroundColor.getRGB()) < 300) {
                    floodFill(x, y, color, Color.blue.getRGB(), 15000);
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (COLORS[x][y] == Color.blue.getRGB()) {
                    temp.setRGB(x, y, COLORS[x][y]);
                }
            }
        }
    }

    private static void markContours(BufferedImage result, int height, int width, boolean markExtraNeighbors, int
            distance, int color, int lDistance, int dDistance) {
        int d;
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int currentColor = result.getRGB(x, y);

                if (ColorHelper.isSimilar(currentColor, white, distance)) {
                    d = lDistance;
                } else {
                    d = dDistance;
                }

                int x1 = x - 1;
                int y1 = y + 1;

                int y2 = y + 1;

                int x3 = x + 1;
                int y3 = y + 1;

                int x4 = x + 1;

                int x5 = x + 1;
                int y5 = y - 1;

                int y6 = y - 1;

                int x7 = x - 1;
                int y7 = y - 1;

                int x8 = x - 1;

                int n1Color = result.getRGB(x1, y1);
                int n2Color = result.getRGB(x, y2);
                int n3Color = result.getRGB(x3, y3);
                int n4Color = result.getRGB(x4, y);
                int n5Color = result.getRGB(x5, y5);
                int n6Color = result.getRGB(x, y6);
                int n7Color = result.getRGB(x7, y7);
                int n8Color = result.getRGB(x8, y);

                if (ColorHelper.getDistance(n1Color, currentColor) > d) {
                    buffer.add(new Pair<>(x1, y1));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x, y2));
                        buffer.add(new Pair<>(x8, y));
                    }
                }
                if (ColorHelper.getDistance(n2Color, currentColor) > d) {
                    buffer.add(new Pair<>(x, y2));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x1, y1));
                        buffer.add(new Pair<>(x3, y3));
                        buffer.add(new Pair<>(x4, y));
                        buffer.add(new Pair<>(x8, y));
                    }
                }
                if (ColorHelper.getDistance(n3Color, currentColor) > d) {
                    buffer.add(new Pair<>(x3, y3));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x, y2));
                        buffer.add(new Pair<>(x4, y));
                    }
                }
                if (ColorHelper.getDistance(n4Color, currentColor) > d) {
                    buffer.add(new Pair<>(x4, y));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x, y2));
                        buffer.add(new Pair<>(x3, y3));
                        buffer.add(new Pair<>(x5, y5));
                        buffer.add(new Pair<>(x, y6));
                    }
                }
                if (ColorHelper.getDistance(n5Color, currentColor) > d) {
                    buffer.add(new Pair<>(x5, y5));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x4, y));
                        buffer.add(new Pair<>(x, y6));
                    }
                }
                if (ColorHelper.getDistance(n6Color, currentColor) > d) {
                    buffer.add(new Pair<>(x, y6));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x4, y));
                        buffer.add(new Pair<>(x5, y5));
                        buffer.add(new Pair<>(x7, y7));
                        buffer.add(new Pair<>(x8, y));
                    }
                }
                if (ColorHelper.getDistance(n7Color, currentColor) > d) {
                    buffer.add(new Pair<>(x7, y7));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x, y6));
                        buffer.add(new Pair<>(x8, y));
                    }
                }
                if (ColorHelper.getDistance(n8Color, currentColor) > d) {
                    buffer.add(new Pair<>(x8, y));
                    buffer.add(new Pair<>(x, y));

                    if (markExtraNeighbors) {
                        buffer.add(new Pair<>(x, y6));
                        buffer.add(new Pair<>(x7, y7));
                        buffer.add(new Pair<>(x1, y1));
                        buffer.add(new Pair<>(x, y2));
                    }
                }
            }
        }

        for (Pair pair : buffer) {
            result.setRGB((Integer) pair.getP1(), (Integer) pair.getP2(), color);
        }
        buffer.clear();
    }

    private static void floodFill(int x, int y, int selectedColor, int newColor, int maxDistance) {
        if (x < 0 || x > IMAGE_WIDTH - 1 || y < 0 || y > IMAGE_HEIGHT - 1) return;

        if (ColorHelper.getDistance(COLORS[x][y], selectedColor) > maxDistance) {
            COLORS[x][y] = newColor;
            return;
        }

        COLORS[x][y] = newColor;
        floodFill(x, y - 1, selectedColor, newColor, maxDistance);
        floodFill(x + 1, y, selectedColor, newColor, maxDistance);
        floodFill(x, y + 1, selectedColor, newColor, maxDistance);
        floodFill(x - 1, y, selectedColor, newColor, maxDistance);
    }

    public static BufferedImage toBufferImageFrom2DArray(int[][] array, int width, int height) {
        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (array[i][j] == 1)
                    bimage.setRGB(j, i, Color.BLACK.getRGB());
                else
                    bimage.setRGB(j, i, Color.WHITE.getRGB());
            }
        }

        return bimage;
    }
}
