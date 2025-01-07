public class CrossCorrelation {
    public static void print(double[] arr) {
        String m = "{ ";
        for (int i = 0; i < arr.length; i++) {
            m += arr[i] + ", ";
        }
        m = m.substring(0, m.length() - 2) + " }";
        
        System.out.println(m);
    }

    public static void main(String[] args) {
        double[] sig1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        double[] sig2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

        print(sig1);
        print(sig2);

        double[] correlation1 = CrossCorrelation1.correlation(sig1, sig2);
        double[] correlation2 = CrossCorrelation2.correlation(sig1, sig2);

        System.out.println("Correlation 1: ");
        print(correlation1);
        System.out.println("Correlation 2: ");
        print(correlation2);
    }
}
