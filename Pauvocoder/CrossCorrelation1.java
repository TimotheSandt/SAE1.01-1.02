public class CrossCorrelation1 {

    public static double[] correlation(double[] sig1, double[] sig2) {
        if (sig1.length != sig2.length) {
            return null;
        }
        int len = sig1.length;
        double[] corr = new double[len];
        for (int i = 0; i < len; i++) {
            corr[i] = sig1[i] * sig2[len - i - 1];
        }
        return corr;
    }

    public static double[] correlation(int[] sig1, int[] sig2) {
        if (sig1.length != sig2.length) {
            return null;
        }
        int len = sig1.length;
        double[] corr = new double[len];
        for (int i = 0; i < len; i++) {
            corr[i] = sig1[i] * sig2[len - i - 1];
        }
        return corr;
    }

}
