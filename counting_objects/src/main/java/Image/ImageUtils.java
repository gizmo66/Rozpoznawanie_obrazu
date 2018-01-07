package Image;

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

    public static Image removeBackground(Image image) {
        Mat src = bufferedImageToMat((BufferedImage) image);
        Image result = toBufferedImage(src);
        int height = ((BufferedImage) result).getHeight();
        int width = ((BufferedImage) result).getWidth();
        for (int y = 0; y < height - 5; y += 5) {
            for (int x = 0; x < width - 5; x += 5) {
                int count = 0;
                for (int h = y; h < y + 5; h++) {
                    for (int w = x; w < x + 5; w++) {
                        Color color1 = new Color(((BufferedImage) result).getRGB(w, h));
                        if (ColorHelper.isSimilar(color1, Color.white, 20000)
                                || ((BufferedImage) result).getRGB(w, h) == Color.BLACK.getRGB()) {
                            count++;
                        }
                    }
                }

                if (count > 21) {
                    int hLeftLimit = y - 2 < 0 ? 0 : y - 2;
                    int hRightLimit = y + 7 > height ? height : y + 7;
                    int wLeftLimit = x - 2 < 0 ? 0 : x - 2;
                    int wRightLimit = x + 7 > width ? width : x + 7;

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

    public static Image markRegions(Image image) {
        Mat src = bufferedImageToMat((BufferedImage) image);
        Image result = toBufferedImage(src);

        List<Color> colorList = new ArrayList<>();
        Map<Color, Integer> colorsCountMap = new HashMap<>();
        HashMap<Color, List<Color>> colorsMap = new HashMap<>();
        int height = ((BufferedImage) result).getHeight();
        int width = ((BufferedImage) result).getWidth();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                Color currentColor = new Color(((BufferedImage) result).getRGB(w, h));
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

        Color brown = new Color(60, 30, 20);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                Color currentColor = new Color(((BufferedImage) result).getRGB(w, h));
                if (((BufferedImage) result).getRGB(w, h) != Color.BLACK.getRGB()) {
                    boolean isLight = ColorHelper.getDistance(currentColor, lightColor)
                            < ColorHelper.getDistance(currentColor, darkColor);
                    if (isLight) {
                        ((BufferedImage) result).setRGB(w, h, Color.yellow.getRGB());
                    } else {
                        ((BufferedImage) result).setRGB(w, h, brown.getRGB());
                    }
                }
            }
        }

        return result;
    }


}
