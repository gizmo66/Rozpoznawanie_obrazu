package Image;

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

    public static Image markBadRegions(Image binaryImage) {
        Mat src = bufferedImageToMat((BufferedImage) binaryImage);
        Image result = toBufferedImage(src);
        int height = ((BufferedImage) result).getHeight();
        int width = ((BufferedImage) result).getWidth();
        for (int y = 0; y < height - 5; y += 5) {
            for (int x = 0; x < width - 5; x += 5) {
                int count = 0;
                for (int h = y; h < y + 5; h++) {
                    for (int w = x; w < x + 5; w++) {
                        Color color1 = new Color(((BufferedImage) result).getRGB(w, h));
                        if (ColorHelper.isSimilar(color1, Color.white, 10000)
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

}
