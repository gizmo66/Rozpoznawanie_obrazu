package Math;

import org.apache.commons.math3.complex.Complex;

public class DiscreteFourierTransform {

    public static Complex[][] calculate(Complex[][] input) {
        //e ^ (-i * x) -> Real: cos(x); Imaginary: -sin(x)
        //e ^ (-j * ((2PI / M) * p * m))) -> Real: cos((2PI / M) * p * m); Imaginary: -sin((2PI / M) * p * m)

        int M = input.length;
        int N = input[0].length;
        double twoPI = (2 * Math.PI);

        Complex[][] output = new Complex[M][N];
        for (int p = 0; p < M; p++) {
            for (int q = 0; q < N; q++) {
                Complex sum = new Complex(0,0);
                for (int m = 0; m < M; m++) {
                    for(int n = 0; n < N; n++) {
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
}
