package Core;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

@Slf4j
public class ImageUtils {

    private static final String WELCOME_MESSAGE = "Welcome to OpenCV ver. {} ";
    private static final String LIB_NAME = "opencv_java320";

    static Image bytesToImage(byte[] source) {
        int type = BufferedImage.TYPE_BYTE_GRAY;

        //FIXME akolodziejek: change const image size
        BufferedImage image = new BufferedImage(28, 28, type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(source, 0, targetPixels, 0, source.length);
        return image;
    }

    public static Image fileToImage(File file) {
        System.loadLibrary(LIB_NAME);
        log.info(WELCOME_MESSAGE, Core.VERSION);

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

    public static BufferedImage upscaleImage(BufferedImage img, double scale) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage upscaledImg = new BufferedImage((int) (w * scale), (int) (h * scale), BufferedImage.TYPE_3BYTE_BGR);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        upscaledImg = scaleOp.filter(img, upscaledImg);
        return upscaledImg;
    }
}
