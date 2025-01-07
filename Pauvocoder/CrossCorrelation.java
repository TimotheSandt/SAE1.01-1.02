public class CrossCorrelation {
    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static void print(double[] arr) {
        if (arr == null) {
            System.out.println("{ }");
            return;
        }
        String m = "{ ";
        for (int i = 0; i < arr.length; i++) {
            m += round(arr[i], 3) + ", ";
        }
        m = m.substring(0, m.length() - 2) + " }";
        
        System.out.println(m);
    }


    public static void main(String[] args) {
        double[] sig1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] sig2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        print(sig1);
        print(sig2);
        double[] corr1 = CrossCorrelation1.CrossCorrelation(sig1, sig2);
        System.out.println("CrossCorrelation1: ");
        print(corr1);

        double[] corr2 = CrossCorrelation2.CrossCorrelation(sig1, sig2);
        System.out.println("CrossCorrelation2: ");
        print(corr2);
    }
}
