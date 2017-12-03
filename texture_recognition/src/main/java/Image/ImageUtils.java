package Image;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.complex.Complex;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ImageUtils {

    private static final String LIB_NAME = "opencv_java320";

    public static BufferedImage binarizeImage(BufferedImage bfImage) {
        final int THRESHOLD = 120;
        int height = bfImage.getHeight();
        int width = bfImage.getWidth();
        BufferedImage returnImage = bfImage;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color c = new Color(returnImage.getRGB(i, j));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                if (red < THRESHOLD && green < THRESHOLD && blue < THRESHOLD) {
                    returnImage.setRGB(i, j, Color.BLACK.getRGB());
                } else {
                    returnImage.setRGB(i, j, Color.WHITE.getRGB());
                }
            }
        }

        Mat src = bufferedImageToMat(returnImage);
        Image result = toBufferedImage(src);

        for (int y = 0; y < height - 7; y += 5) {
            for (int x = 0; x < width - 7; x += 5) {
                int count = 0;
                for (int h = y; h < y + 7; h++) {
                    for (int w = x; w < x + 7; w++) {
                        if (returnImage.getRGB(w, h) == Color.WHITE.getRGB()) {
                            count++;
                        }
                    }
                }

                if (count > 45) {
                    for (int h = y; h < y + 7; h++) {
                        for (int w = x; w < x + 7; w++) {
                            ((BufferedImage) result).setRGB(w, h, Color.YELLOW.getRGB());
                        }
                    }
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (((BufferedImage) result).getRGB(i, j) == Color.YELLOW.getRGB()) {
                    ((BufferedImage) result).setRGB(i, j, Color.WHITE.getRGB());
                } else if (((BufferedImage) result).getRGB(i, j) == Color.WHITE.getRGB() && i < width - 7 && j <
                        height - 7) {
                    ((BufferedImage) result).setRGB(i, j, Color.BLACK.getRGB());
                }
            }
        }

        return (BufferedImage) result;
    }

    private static Mat bufferedImageToMat(BufferedImage bi) {
        String opencvpath = "C:\\OpenCV\\opencv\\build\\java\\x64\\";
        System.load(opencvpath + LIB_NAME + ".dll");

        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

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

    static BufferedImage toBufferImageFrom2DArray(int[][] array, int width, int height) {
        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (array[i][j] == 1)
                    bimage.setRGB(j, i, Color.BLACK.getRGB());
                else
                    bimage.setRGB(j, i, Color.WHITE.getRGB());
            }
        }

        return bimage;
    }

    public static BufferedImage toBufferedImage(Image img) {
        return toBufferedImage(img, BufferedImage.TYPE_INT_ARGB);
    }

    public static BufferedImage upscaleImage(BufferedImage img, double scale) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage upscaledImg = new BufferedImage((int) (w * scale), (int) (h * scale), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        upscaledImg = scaleOp.filter(img, upscaledImg);
        return upscaledImg;
    }

    public static void save(Image image, String fileName, String extension) {
        File file = new File(fileName + "." + extension);
        try {
            ImageIO.write(toBufferedImage(image, BufferedImage.TYPE_INT_RGB), extension, file);
        } catch(IOException e) {
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

    public static Complex[][] imageToComplex(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        WritableRaster raster = image.getRaster();

        Complex[][] result = new Complex[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                result[x][y] = new Complex(raster.getSample(x, y, 0), 0);
            }
        }
        return result;
    }

    public static Image complexToImage(Complex[][] input, BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster rgb = result.getRaster();

        LinkedHashMap<Point2D, Double> pixelCoordinatesToModules = new LinkedHashMap<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double modules = Math.sqrt(Math.pow(input[x][y].getReal() - input[x][y].getImaginary(), 2));
                pixelCoordinatesToModules.put(new Point(x,y), modules);
            }
        }

        Collection<Double> modulesList = pixelCoordinatesToModules.values();
        java.util.List<Double> sortedModules = new ArrayList<>();
        sortedModules.addAll(modulesList.stream().sorted().collect(Collectors.toList()));

        double bottomBorder = sortedModules.get((int) (0.1 * sortedModules.size()));
        double topBorder = sortedModules.get((int) (0.9 * sortedModules.size()));

        double maxModules = topBorder + Math.abs(bottomBorder);

        for (Map.Entry pixelToModules : pixelCoordinatesToModules.entrySet()) {
            double modulesClampValue = (double)pixelToModules.getValue() > topBorder ? topBorder : (double)
                    pixelToModules.getValue();
            modulesClampValue = modulesClampValue < bottomBorder ? bottomBorder : modulesClampValue;
            Color color = ColorHelper.numberToColor((modulesClampValue * 100.0) / maxModules);
            Point pixel = (Point) pixelToModules.getKey();
            int x = (int) pixel.getX();
            int y = (int) pixel.getY();
            rgb.setSample(x, y, 0, color.getRed());
            rgb.setSample(x, y, 1, color.getGreen());
            rgb.setSample(x, y, 2, color.getBlue());
        }
        return result;
    }
}
