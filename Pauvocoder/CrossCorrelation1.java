public class CrossCorrelation1 {

    public static double[] correlationBof(double[] sig1, double[] sig2) {
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

    public static double[] correlation(double[] sig1, double[] sig2) {
        if (sig1.length != sig2.length) {
            return null;
        }
        int len = sig1.length;
        double[] corr = new double[len * 2 - 1];
        for (int i = -len + 1; i < len; i++) {
            double sum = 0;
            int count = 0;
            for (int j = 0; j < len; j++) {
                if (i + j >= 0 && i + j < len) {
                    sum += sig1[j] * sig2[i + j];
                    count++;
                }
            }
            corr[i + len - 1] = sum / count;
        }
        return corr;
    }

    public static void main(String[] args) {
        double[] sig1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] sig2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] corr = correlation(sig1, sig2);

        String m = "{ ";
        for (int i = 0; i < corr.length; i++) {
            m += corr[i] + ", ";
        }
        m = m.substring(0, m.length() - 2) + " }";
        
        System.out.println(m);

    }

}
