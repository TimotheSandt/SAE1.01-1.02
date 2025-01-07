import java.util.Arrays;

public class CrossCorrelation2 {

    

    public static double[] correlation(double[] sig1, double[] sig2) {
        int input_size = sig1.length;
        int output_size = input_size * 2 - 1;

        // First power of 2 above output_size
        int power_of_2 = 1;
        while (power_of_2 < output_size) {
            power_of_2 *= 2;
        }
        output_size = power_of_2;

        // zero padding
        double[] sig1_padded = new double[output_size];
        double[] sig2_padded = new double[output_size];
        for (int i = 0; i < input_size; i++) {
            sig1_padded[i] = sig1[i];
            sig2_padded[i] = sig2[i];
        }

        // FFT
        Complex[] sig1_fft = FFT.fft(sig1_padded);
        Complex[] sig2_fft = FFT.fft(sig2_padded);

        // Correlation
        Complex[] corr = new Complex[output_size];
        for (int i = 0; i < output_size; i++) {
            corr[i] = sig1_fft[i].times(sig2_fft[i].conjugate());
        }

        // IFFT
        corr = FFT.ifft(corr);

        // Extract real part
        double[] corr_real = new double[sig1.length];
        for (int i = 0; i < sig1.length; i++) {
            corr_real[i] = corr[i].re();
        }

        return corr_real;
    }

    public static void main(String[] args) {
        double[] sig1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        double[] sig2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

        System.out.println("Signal 1: " + Arrays.toString(sig1));
        System.out.println("Signal 2: " + Arrays.toString(sig2));

        double[] correlation = correlation(sig1, sig2);

        System.out.println("Correlation: " + Arrays.toString(correlation));
    }
}
