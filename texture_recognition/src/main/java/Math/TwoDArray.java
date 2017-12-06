package Math;

import org.apache.commons.math3.complex.Complex;

public class TwoDArray {

    public int width;
    public int height;
    public int size;
    public Complex[][] values;

    public TwoDArray(int[] p, int w, int h) {
        width = w;
        height = h;

        int n = 0;
        while (Math.pow(2, n) < Math.max(w, h)) {
            ++n;
        }

        size = (int) Math.pow(2, n);
        values = new Complex[size][size];
        for (int j = 0; j < size; ++j) {
            for (int i = 0; i < size; ++i) {
                values[i][j] = new Complex(0, 0);
            }
        }

        for (int j = 0; j < h; ++j) {
            for (int i = 0; i < w; ++i) {
                values[i][j] = new Complex(p[i + (j * w)], 0.0);
            }
        }
    }

    public Complex[] getColumn(int n) {
        Complex[] c = new Complex[size];
        for (int i = 0; i < size; ++i) {
            c[i] = new Complex(values[n][i].getReal(), values[n][i].getImaginary());
        }
        return c;
    }

    public void putColumn(int n, Complex[] c) {
        for (int i = 0; i < size; ++i) {
            values[n][i] = new Complex(c[i].getReal(), c[i].getImaginary());
        }
    }

    public void putRow(int n, Complex[] c) {
        for (int i = 0; i < size; ++i) {
            values[i][n] = new Complex(c[i].getReal(), c[i].getImaginary());
        }
    }

    public Complex[] getRow(int n) {
        Complex[] r = new Complex[size];
        for (int i = 0; i < size; ++i) {
            r[i] = new Complex(values[i][n].getReal(), values[i][n].getImaginary());
        }
        return r;
    }
}