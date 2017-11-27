package Math;

import org.apache.commons.math3.complex.Complex;

public class DiscreteFourierTransform {

    public Complex[] calculate(Complex[] input) {
        // Ak = sum(An * WN ^ -k*n)
        // WN = e ^ (2*PI / N)

        // Ak = sum(An * ((e ^ (2*PI / N)) ^ -k*n))
        // (e ^ (2*PI / N)) ^ -k*n -> R: cos((2*PI / N)) ^ k*n), I: -sin((2*PI / N)) ^ k*n)

        int N = input.length;
        double twoPIOnN = 2 * Math.PI / N;
        double twoPIKOnN;
        double twoPIKnOnN;

        Complex[] output = new Complex[N];
        Complex sum, temp;
        for (int k = 0; k < N; k++) {
            twoPIKOnN = twoPIOnN * k;
            sum = new Complex(0, 0);
            for (int n = 0; n < N; n++) {
                twoPIKnOnN = twoPIKOnN * n;
                temp = (new Complex(Math.cos(twoPIKnOnN), -Math.sin(twoPIKnOnN)));
                sum = sum.add(temp.multiply(input[n]));
            }
            output[k] = sum;
        }
        return output;
    }
}
