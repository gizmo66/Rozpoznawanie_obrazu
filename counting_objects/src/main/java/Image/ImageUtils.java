package Image;

import Core.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
import java.util.*;
import java.util.List;

import static java.util.Comparator.comparingInt;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

@Slf4j
public class ImageUtils {

    private static final String LIB_NAME = "opencv_java320";
    private static final Color LIGHT_COLOR = Color.yellow;
    private static final Color DARK_COLOR = new Color(60, 30, 20);
    private static final int[][] COLORS = new int[4000][4000];
    private static Image IMAGE;
    private static Image TEMP;
    private static int IMAGE_WIDTH;
    private static int IMAGE_HEIGHT;
    private static int white = Color.white.getRGB();

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

    public static Image f(Image image) {
        Mat src = bufferedImageToMat((BufferedImage) image);
        Image result = toBufferedImage(src);
        Image temp = toBufferedImage(src);
        int height = ((BufferedImage) result).getHeight();
        int width = ((BufferedImage) result).getWidth();

        int d;
        List<Pair<Integer>> buffer = new ArrayList<>();
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int currentColor = ((BufferedImage) result).getRGB(x, y);

                if (ColorHelper.isSimilar(currentColor, white, 50000)) {
                    d = 200;
                } else {
                    d = 500;
                }

                int x1 = x - 1;
                int y1 = y + 1;

                int x2 = x;
                int y2 = y + 1;

                int x3 = x + 1;
                int y3 = y + 1;

                int x4 = x + 1;
                int y4 = y;

                int x5 = x + 1;
                int y5 = y - 1;

                int x6 = x;
                int y6 = y - 1;

                int x7 = x - 1;
                int y7 = y - 1;

                int x8 = x - 1;
                int y8 = y;

                int n1Color = ((BufferedImage) result).getRGB(x1, y1);
                int n2Color = ((BufferedImage) result).getRGB(x2, y2);
                int n3Color = ((BufferedImage) result).getRGB(x3, y3);
                int n4Color = ((BufferedImage) result).getRGB(x4, y4);
                int n5Color = ((BufferedImage) result).getRGB(x5, y5);
                int n6Color = ((BufferedImage) result).getRGB(x6, y6);
                int n7Color = ((BufferedImage) result).getRGB(x7, y7);
                int n8Color = ((BufferedImage) result).getRGB(x8, y8);

                if (ColorHelper.getDistance(n1Color, currentColor) > d) {
                    buffer.add(new Pair<>(x1, y1));
                    buffer.add(new Pair<>(x, y));

                    buffer.add(new Pair<>(x2, y2));
                    buffer.add(new Pair<>(x8, y8));
                }
                if (ColorHelper.getDistance(n2Color, currentColor) > d) {
                    buffer.add(new Pair<>(x2, y2));
                    buffer.add(new Pair<>(x, y));

                    buffer.add(new Pair<>(x1, y1));
                    buffer.add(new Pair<>(x3, y3));
                    buffer.add(new Pair<>(x4, y4));
                    buffer.add(new Pair<>(x8, y8));
                }
                if (ColorHelper.getDistance(n3Color, currentColor) > d) {
                    buffer.add(new Pair<>(x3, y3));
                    buffer.add(new Pair<>(x, y));

                    buffer.add(new Pair<>(x2, y2));
                    buffer.add(new Pair<>(x4, y4));
                }
                if (ColorHelper.getDistance(n4Color, currentColor) > d) {
                    buffer.add(new Pair<>(x4, y4));
                    buffer.add(new Pair<>(x, y));

                    buffer.add(new Pair<>(x2, y2));
                    buffer.add(new Pair<>(x3, y3));
                    buffer.add(new Pair<>(x5, y5));
                    buffer.add(new Pair<>(x6, y6));
                }
                if (ColorHelper.getDistance(n5Color, currentColor) > d) {
                    buffer.add(new Pair<>(x5, y5));
                    buffer.add(new Pair<>(x, y));

                    buffer.add(new Pair<>(x4, y4));
                    buffer.add(new Pair<>(x6, y6));
                }
                if (ColorHelper.getDistance(n6Color, currentColor) > d) {
                    buffer.add(new Pair<>(x6, y6));
                    buffer.add(new Pair<>(x, y));

                    buffer.add(new Pair<>(x4, y4));
                    buffer.add(new Pair<>(x5, y5));
                    buffer.add(new Pair<>(x7, y7));
                    buffer.add(new Pair<>(x8, y8));
                }
                if (ColorHelper.getDistance(n7Color, currentColor) > d) {
                    buffer.add(new Pair<>(x7, y7));
                    buffer.add(new Pair<>(x, y));

                    buffer.add(new Pair<>(x6, y6));
                    buffer.add(new Pair<>(x8, y8));
                }
                if (ColorHelper.getDistance(n8Color, currentColor) > d) {
                    buffer.add(new Pair<>(x8, y8));
                    buffer.add(new Pair<>(x, y));

                    buffer.add(new Pair<>(x6, y6));
                    buffer.add(new Pair<>(x7, y7));
                    buffer.add(new Pair<>(x1, y1));
                    buffer.add(new Pair<>(x2, y2));
                }
            }
        }

        for (Pair pair : buffer) {
            ((BufferedImage) result).setRGB((Integer) pair.getP1(), (Integer) pair.getP2(), Color.BLACK.getRGB());
        }

        /*for (int i = 0; i < 5; i ++) {
            for (int h = 0; h < height; h++) {
                Color previousColor = new Color(((BufferedImage) result).getRGB(0, h));
                for (int w = 1; w < width - 2; w++) {
                    Color currentColor = new Color(((BufferedImage) result).getRGB(w, h));
                    if (ColorHelper.getDistance(previousColor, currentColor) > 300 || currentColor.getRGB() == Color
                            .RED.getRGB() || ((BufferedImage) result).getRGB(w + 1, h) == Color.red.getRGB()) {
                        ((BufferedImage) result).setRGB(w - 1, h, Color.RED.getRGB());
                        ((BufferedImage) result).setRGB(w, h, Color.RED.getRGB());
                        ((BufferedImage) result).setRGB(w + 1, h, Color.RED.getRGB());
                        w += 3;
                    }
                    previousColor = new Color(((BufferedImage) result).getRGB(w - 1, h));
                }
            }
        }*/

        /*(for (int i = 0; i < 5; i ++) {
            for (int h = 0; h < height; h++) {

            }
        }*/

        IMAGE = result;
        IMAGE_WIDTH = width;
        IMAGE_HEIGHT = height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                COLORS[x][y] = ((BufferedImage) IMAGE).getRGB(x, y);
            }
        }

        Color selectedColor = new Color(((BufferedImage) IMAGE).getRGB(5, 20));
        floodFill(0, 0, selectedColor.getRGB(), Color.blue.getRGB());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (COLORS[x][y] == Color.blue.getRGB()) {
                    ((BufferedImage) temp).setRGB(x, y, COLORS[x][y]);
                }
            }
        }

        return temp;
    }

    private static void floodFill(int x, int y, int selectedColor, int newColor) {
        if (x < 0 || x > IMAGE_WIDTH - 1 || y < 0 || y > IMAGE_HEIGHT - 1) return;

        if (ColorHelper.getDistance(COLORS[x][y], selectedColor) > 15000) {
            COLORS[x][y] = newColor;
            return;
        }

        COLORS[x][y] = newColor;
        floodFill(x, y - 1, selectedColor, newColor);
        floodFill(x + 1, y, selectedColor, newColor);
        floodFill(x, y + 1, selectedColor, newColor);
        floodFill(x - 1, y, selectedColor, newColor);
    }

    public static Image g(Image image) {
        Mat src = bufferedImageToMat((BufferedImage) image);
        Image result = toBufferedImage(src);
        int height = ((BufferedImage) result).getHeight();
        int width = ((BufferedImage) result).getWidth();

        int a = 2;
        int b = 3;
        if (width < 1000) {
            a = 5;
            b = 7;
        }

        for (int y = 0; y < height - a; y += a) {
            for (int x = 0; x < width - a; x += a) {
                int count = 0;
                for (int h = y; h < y + a; h++) {
                    for (int w = x; w < x + a; w++) {
                        if (((BufferedImage) result).getRGB(w, h) == Color.RED.getRGB()) {
                            count++;
                        }
                    }
                }

                if (count < 0.05 * (double) a * (double) a) {
                    int hLeftLimit = y - a < 0 ? 0 : y - a;
                    int hRightLimit = y + b > height ? height : y + b;
                    int wLeftLimit = x - a < 0 ? 0 : x - a;
                    int wRightLimit = x + b > width ? width : x + b;

                    for (int h = hLeftLimit; h < hRightLimit; h++) {
                        for (int w = wLeftLimit; w < wRightLimit; w++) {
                            ((BufferedImage) result).setRGB(w, h, Color.BLACK.getRGB());
                        }
                    }
                }
            }
        }
        return result;
    }

    /*public static Image markRegions(Image image) {
        Mat src = bufferedImageToMat((BufferedImage) image);
        Image result = toBufferedImage(src);

        List<Color> colorList = new ArrayList<>();
        Map<Color, Integer> colorsCountMap = new HashMap<>();
        HashMap<Color, List<Color>> colorsMap = new HashMap<>();
        int height = ((BufferedImage) result).getHeight();
        int width = ((BufferedImage) result).getWidth();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int currentColor =((BufferedImage) result).getRGB(w, h));
                if (currentColor.getRGB() != Color.BLACK.getRGB()) {
                    if (CollectionUtils.isNotEmpty(colorList)) {
                        boolean similarFound = false;
                        for (Color color : colorList) {
                            if (ColorHelper.isSimilar(currentColor, color, 15000)) {
                                colorsCountMap.replace(color, colorsCountMap.get(color) + 1);
                                if (MapUtils.isNotEmpty(colorsMap)) {
                                    if (colorsMap.get(color) != null) {
                                        colorsMap.get(color).add(currentColor);
                                    } else {
                                        List<Color> values = new ArrayList<>();
                                        values.add(currentColor);
                                        colorsMap.put(color, values);
                                    }
                                } else {
                                    List<Color> values = new ArrayList<>();
                                    values.add(currentColor);
                                    colorsMap.put(color, values);
                                }
                                similarFound = true;
                            }
                        }
                        if (!similarFound) {
                            colorList.add(currentColor);
                            colorsCountMap.put(currentColor, 1);
                        }
                    } else {
                        colorList.add(currentColor);
                        colorsCountMap.put(currentColor, 1);
                    }
                }
            }
        }

        colorsMap = colorsMap.entrySet().stream()
                .sorted(comparingByValue(comparingInt(value -> -value.size())))
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                            throw new AssertionError();
                        },
                        LinkedHashMap::new
                ));

        int lR = 0;
        int lG = 0;
        int lB = 0;
        List<Color> lightColors = colorsMap.remove(colorsMap.entrySet().iterator().next().getKey());
        for (Color lColor : lightColors) {
            int r = lColor.getRed();
            int g = lColor.getGreen();
            int b = lColor.getBlue();
            lR += r;
            lG += g;
            lB += b;
        }
        Color lightColor = new Color(lR / lightColors.size(), lG / lightColors.size(), lB / lightColors.size());

        int dR = 0;
        int dG = 0;
        int dB = 0;
        List<Color> darkColors = colorsMap.remove(colorsMap.entrySet().iterator().next().getKey());
        for (Color dColor : darkColors) {
            int r = dColor.getRed();
            int g = dColor.getGreen();
            int b = dColor.getBlue();
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

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                Color currentColor = new Color(((BufferedImage) result).getRGB(w, h));
                if (((BufferedImage) result).getRGB(w, h) != Color.BLACK.getRGB()) {
                    boolean isLight = ColorHelper.getDistance(currentColor, lightColor)
                            < ColorHelper.getDistance(currentColor, darkColor);
                    if (isLight) {
                        ((BufferedImage) result).setRGB(w, h, LIGHT_COLOR.getRGB());
                    } else {
                        ((BufferedImage) result).setRGB(w, h, DARK_COLOR.getRGB());
                    }
                }
            }
        }
        return result;
    }*/

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
