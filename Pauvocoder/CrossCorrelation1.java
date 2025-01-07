import java.util.Arrays;

public class CrossCorrelation1 {

    public static double correlation(double[] sig1, double[] sig2, int n, int i) {
        double sum = 0;

        for (int j = 0; j < n; j++) {
            if (i + j < 0) {
                j = -i;
            }
            else if (i + j >= n) {
                break;
            } 
            sum += sig1[i + j] * sig2[j];
        }

        return sum;
    }

    public static double[] CrossCorrelation(double[] sig1, double[] sig2) {
        if (sig1.length != sig2.length) {
            return null;
        }
        
        int len = sig1.length;
        double[] corr = new double[len + len - 1];
        for (int i = - len + 1; i < len; i++) {
            corr[i + len - 1] = correlation(sig1, sig2, len, i);
        }
        return corr;
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
