package Core;

import lombok.extern.slf4j.Slf4j;
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

    private static final String LIB_NAME = "opencv_java320";

    public static BufferedImage binarizeImage(BufferedImage bfImage, boolean isMnist){
        if (!isMnist) {

            final int THRESHOLD = 110;
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
                        returnImage.setRGB(i,j,Color.BLACK.getRGB());
                    }else{
                        returnImage.setRGB(i,j,Color.WHITE.getRGB());
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
                            if (((BufferedImage) result).getRGB(w, h) == Color.WHITE.getRGB()
                                    || ((BufferedImage) result).getRGB(w, h) == Color.YELLOW.getRGB()) {
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

            for(int i=0; i<width; i++){
                for(int j=0; j<height; j++){
                    if (((BufferedImage) result).getRGB(i, j) == Color.YELLOW.getRGB()) {
                        ((BufferedImage) result).setRGB(i,j,Color.WHITE.getRGB());
                    } else if (((BufferedImage) result).getRGB(i, j) == Color.WHITE.getRGB() && i < width - 7 && j <
                            height - 7) {
                        ((BufferedImage) result).setRGB(i,j,Color.BLACK.getRGB());
                    }
                }
            }

            return (BufferedImage) result;
        } else {
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
    }

    public static Mat bufferedImageToMat(BufferedImage bi) {
        String opencvpath = "C:\\OpenCV\\opencv\\build\\java\\x64\\";
        System.load(opencvpath + LIB_NAME + ".dll");

        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    static Image bytesToImage(byte[] source, int width, int height) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        BufferedImage image = new BufferedImage(width, height, type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(source, 0, targetPixels, 0, source.length);
        return image;
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

    public static BufferedImage toBufferImageFrom2DArray(int[][] array, int width, int height)
    {
        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for(int i =0 ; i < width;i ++)
        {
            for(int j = 0; j < height; j++)
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
        BufferedImage upscaledImg = new BufferedImage((int) (w * scale), (int) (h * scale), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        upscaledImg = scaleOp.filter(img, upscaledImg);
        return upscaledImg;
    }
}
