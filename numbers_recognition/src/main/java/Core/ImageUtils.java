package Core;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.opencv.core.CvType;
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

    public static BufferedImage binarizeImage(BufferedImage bfImage){
        final int THRESHOLD = 160;
        int height = bfImage.getHeight();
        int width = bfImage.getWidth();
        BufferedImage returnImage = bfImage;

        for(int i=0; i<width; i++){
            for(int j=0; j<height; j++){
                Color c = new Color(returnImage.getRGB(i,j));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                if(red<THRESHOLD && green<THRESHOLD && blue<THRESHOLD){
                    returnImage.setRGB(i,j,Color.WHITE.getRGB());
                }else{
                    returnImage.setRGB(i,j,Color.BLACK.getRGB());
                }
            }
        }
        return returnImage;
    }

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

    public static BufferedImage toBufferImageFrom2DArray(int[][] array)
    {
        BufferedImage bimage = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
        for(int i =0 ; i < 28;i ++)
        {
            for(int j = 0; j < 28; j++)
            {
                if(array[i][j] == 1)
                    bimage.setRGB(j,i,Color.BLACK.getRGB());
                else
                    bimage.setRGB(j,i,Color.WHITE.getRGB());
            }
        }

        return bimage;
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
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
