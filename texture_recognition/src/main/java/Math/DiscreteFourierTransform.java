package Math;

import org.apache.commons.math3.complex.Complex;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class DiscreteFourierTransform {

    public static Complex[][] ft(Complex[][] input) {
        //e ^ (-i * x) -> Real: cos(x); Imaginary: -sin(x)
        //e ^ (-j * ((2PI / M) * p * m))) -> Real: cos((2PI / M) * p * m); Imaginary: -sin((2PI / M) * p * m)

        int M = input.length;
        int N = input[0].length;
        double twoPI = (2 * Math.PI);

        Complex[][] output = new Complex[M][N];
        for (int p = 0; p < M; p++) {
            for (int q = 0; q < N; q++) {
                Complex sum = new Complex(0, 0);
                for (int m = 0; m < M; m++) {
                    for (int n = 0; n < N; n++) {
                        double twoPIpmOnM = (twoPI / M) * p * m;
                        double twoPIqnOnN = (twoPI / N) * q * n;
                        Complex tempMN = new Complex(Math.cos(twoPIpmOnM + twoPIqnOnN), -Math.sin(twoPIpmOnM +
                                twoPIqnOnN));
                        sum = sum.add(input[m][n].multiply(tempMN));
                    }
                }
                output[p][q] = sum;
            }
        }
        return output;
    }

    public static Complex[][] fft(BufferedImage bufferedImage) {
        int h = bufferedImage.getHeight();
        int w = bufferedImage.getWidth();
        WritableRaster raster = bufferedImage.getRaster();

        int count = 0;
        int[] pixels = new int[w * h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                pixels[count] = raster.getSample(x, y, 0);
                count++;
            }
        }

        TwoDArray input = new TwoDArray(pixels, w, h);
        TwoDArray temp = new TwoDArray(pixels, w, h);
        TwoDArray output = new TwoDArray(pixels, w, h);
        for (int i = 0; i < input.size; ++i) {
            temp.putColumn(i, oneDfft(input.getColumn(i)));
        }
        for (int i = 0; i < temp.size; ++i) {
            output.putRow(i, oneDfft(temp.getRow(i)));
        }
        return output.values;
    }

    public static Complex[] oneDfft(Complex[] x) {
        int n = x.length;

        if (n == 1) {
            return new Complex[]{x[0]};
        }

        if (n % 2 != 0) {
            throw new IllegalArgumentException("n nie jest potega dwojki!");
        }

        Complex[] even = new Complex[n / 2];
        for (int k = 0; k < n / 2; k++) {
            even[k] = x[2 * k];
        }
        Complex[] q = oneDfft(even);

        //żeby nie tworzyć nowej tablicy
        Complex[] odd = even;

        for (int k = 0; k < n / 2; k++) {
            odd[k] = x[2 * k + 1];
        }
        Complex[] r = oneDfft(odd);

        Complex[] y = new Complex[n];
        for (int k = 0; k < n / 2; k++) {
            double kth = -2 * k * Math.PI / n;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k] = q[k].add(wk.multiply(r[k]));
            y[k + n / 2] = q[k].subtract(wk.multiply(r[k]));
        }
        return y;
    }
}
