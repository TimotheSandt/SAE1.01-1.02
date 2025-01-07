public class CrossCorrelation1 {

    public static double[] correlation(double[] sig1, double[] sig2) {
        return correlation(sig1, sig2, false);
    }

    public static double[] correlation(double[] sig1, double[] sig2, boolean HasNeg) {
        if (sig1.length != sig2.length) {
            return null;
        }
        int len = sig1.length;
        int ind = (HasNeg) ? len - 1 : 0;
        double[] corr = new double[len + ind];
        for (int i = -ind; i < len; i++) {
            double sum = 0;

            for (int j = 0; j < len; j++) {
                if (i + j >= 0 && i + j < len) {
                    sum += sig1[j] * sig2[i + j];
                }
            }
            corr[i + ind] = sum;
        }
        return corr;
    }

    public static void print(double[] arr) {
        String m = "{ ";
        for (int i = 0; i < arr.length; i++) {
            m += arr[i] + ", ";
        }
        m = m.substring(0, m.length() - 2) + " }";
        
        System.out.println(m);
    }

    public static void main(String[] args) {
        System.out.println(-3 % 5);
        double[] sig1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] sig2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        print(sig1);
        print(sig2);
        double[] corr = correlation(sig1, sig2);
        print(corr);        

    }

}
