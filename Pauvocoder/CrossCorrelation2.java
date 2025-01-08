// MIGUET Maxime & SANDT Timothé
// S1C2
// Groupe 12
// 
// Second CrossCorrelation file

import java.util.Arrays;

public class CrossCorrelation2 {

    public static int power_of_2(int n) {
        int p = 1;
        while (p < n) {
            p *= 2;
        }
        return p;
    }

    public static Complex[] ZeroPadding(double[] sig, int size_padded) {
        Complex[] sig_padded = new Complex[size_padded];
        for (int i = 0; i < sig.length; i++) {
            sig_padded[i] = new Complex(sig[i], 0);
        }
        for (int i = sig.length; i < size_padded; i++) {
            sig_padded[i] = new Complex(0, 0);
        }
        return sig_padded;
    }

    public static double[] CrossCorrelation(double[] sig1, double[] sig2) {
        if (sig1.length != sig2.length) {
            return null;
        }

        int size = sig1.length;
        int output_size = size * 2 - 1;
        int size_padded = power_of_2(output_size);

        // zero padding
        Complex[] sig1_padded = ZeroPadding(sig1, size_padded);
        Complex[] sig2_padded = ZeroPadding(sig2, size_padded);

        // FFT
        Complex[] sig1_fft = FFT.fft(sig1_padded);
        Complex[] sig2_fft = FFT.fft(sig2_padded);

        // Correlation
        Complex[] corr = new Complex[size_padded];
        for (int i = 0; i < size_padded; i++) {
            corr[i] = sig1_fft[i].times( sig2_fft[i].conjugate() );
        }

        // IFFT
        corr = FFT.ifft(corr);

        // Extract real part
        double[] corr_real = new double[output_size];
        for (int i = 0; i < size; i++) {
            corr_real[i] = corr[i].re();
        }
        for (int i = 1; i < size; i++) {
            corr_real[output_size - i] = corr[size_padded - i].re();
        }

        return corr_real;
    }

    public static void main(String[] args) {
        double[] sig1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] sig2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        System.out.println("Signal 1: " + Arrays.toString(sig1));
        System.out.println("Signal 2: " + Arrays.toString(sig2));

        double[] correlation = CrossCorrelation(sig1, sig2);

        System.out.println("CrossCorrelation: " + Arrays.toString(correlation));
    }
}
