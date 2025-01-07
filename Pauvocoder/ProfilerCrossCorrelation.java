import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;


public class ProfilerCrossCorrelation {

    static long[][] result;

    public static long timestamp() {
        return System.nanoTime();
    }

    public static void PrintInfo(String name, long timestamp, int inputSize, int repetitions) {
        System.out.println();
        System.out.println(name + ": ");
        System.out.println("timestamp: " + timestamp / 1e6 + " ms");
        System.out.println("input size: " + inputSize);
        System.out.println("repetitions: " + repetitions);
    }

    public static void CrossCorrelation(double[] sig1, double[] sig2, int n, int i) {
        result[0][i] = CrossCorrelation1(sig1, sig2, n);
        result[1][i] = CrossCorrelation2(sig1, sig2, n);
    }

    public static long CrossCorrelation1(double[] sig1, double[] sig2, int n) {
        long clock0 = timestamp();
        for (int i = 0; i < n; i++) {
            CrossCorrelation1.CrossCorrelation(sig1, sig2);
        }
        long clock1 = timestamp();
        long timestampCrossCorrelation1 = clock1 - clock0;
        PrintInfo("CrossCorrelation1", timestampCrossCorrelation1, sig1.length, n);
        return timestampCrossCorrelation1;
    }

    public static long CrossCorrelation2(double[] sig1, double[] sig2, int n) {
        long clock0 = timestamp();
        for (int i = 0; i < n; i++) {
            CrossCorrelation2.CrossCorrelation(sig1, sig2);
        }
        long clock1 = timestamp();
        long timestampCrossCorrelation2 = clock1 - clock0;
        PrintInfo("CrossCorrelation2", timestampCrossCorrelation2, sig1.length, n);
        return timestampCrossCorrelation2;
    }

    
    
    public static void SaveAsImage(String filename, long[][] result, long max) {
        int N = result.length;
        int M = result[0].length;

        BufferedImage img = new BufferedImage(N, M, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                int value = (int)(255 * Math.log(result[i][j] + 1) / Math.log(max + 1));
                img.setRGB(i, j, (value << 16) | (value << 8) | value);
            }
        }
        try {
            ImageIO.write(img, "png", new File(filename));
        } catch (IOException e) {
            System.out.println("Can't save image: " + e.getMessage());
        }
    }

    public static double[] GetRandomSignal(int length) {
        double[] signal = new double[length];
        for (int i = 0; i < length; i++) {
            signal[i] = Math.random();
        }
        return signal;
    }

    public static void main(String[] args) {
        int p = args.length > 0 ? Integer.parseInt(args[0]) : 2;
        int N = args.length > 0 ? Integer.parseInt(args[1]) : 12;
        result = new long[2][N];

        for (int i = 0; i < N; i++) {
            double[] sig1 = GetRandomSignal((int)Math.pow(p, i));
            double[] sig2 = GetRandomSignal((int)Math.pow(p, i));
            CrossCorrelation(sig1, sig2, 1000, i);
            
        }

        long max = 0;
        for (int i = 0; i < N; i++) {
            if (result[0][i] > max) max = result[0][i];
            if (result[1][i] > max) max = result[1][i];
        }

        SaveAsImage("CrossCorrelation_" + p + "_" + N + ".png", result, max);
    }


}