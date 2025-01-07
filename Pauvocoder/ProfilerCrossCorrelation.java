import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.PrintWriter;



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

public static void SaveResultToFile(String filename, String nameFunction[], long[][] result, int p, int N, int repetitions) {
    try (PrintWriter writer = new PrintWriter(new File(filename))) {
        writer.println("Parameters:");
        writer.println("p = " + p);
        writer.println("N = " + N);
        writer.println("(input size : { p^0, p^1, ..., p^(N-2), p^(N-1) })");
        writer.println("repetitions = " + repetitions);
        writer.println();
        writer.println("Result Data:");
        for (int i = 0; i < result.length; i++) {
            writer.print(nameFunction[i] + ": ");
            for (int j = 0; j < result[i].length; j++) {
                writer.print(result[i][j] + (j == result[i].length - 1 ? "" : ", "));
            }
            writer.println();
        }
    } catch (IOException e) {
        System.out.println("Can't save result to file: " + e.getMessage());
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
        int repetitions = args.length > 2 ? Integer.parseInt(args[2]) : 10000;
        result = new long[2][N];

        for (int i = 0; i < N; i++) {
            double[] sig1 = GetRandomSignal((int)Math.pow(p, i));
            double[] sig2 = GetRandomSignal((int)Math.pow(p, i));
            CrossCorrelation(sig1, sig2, repetitions, i);
            
        }

        long max = 0;
        for (int i = 0; i < N; i++) {
            if (result[0][i] > max) max = result[0][i];
            if (result[1][i] > max) max = result[1][i];
        }

        long r[][] = new long[2][N + 2];
        r[0][0] = 0;
        r[1][0] = max;
        System.arraycopy(result[0], 0, r[0], 2, N);
        System.arraycopy(result[1], 0, r[1], 2, N);
        String FileName = "CrossCorrelation_" + p + "_" + N + "_" + repetitions;
        SaveAsImage(FileName + ".png", r, max);

        String nameFunction[] = { "CrossCorrelation1", "CrossCorrelation2" };
        SaveResultToFile(FileName + ".txt", nameFunction, result, p, N, repetitions);
    }


}