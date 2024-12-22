// MIGUET Maxime & SANDT Timothé
// S1C2
// 
// Pauvocoder file



import static java.lang.System.exit;
import java.io.File;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Pauvocoder {

    // Processing SEQUENCE size (100 msec with 44100Hz samplerate)
    final static int SEQUENCE = StdAudio.SAMPLE_RATE/10;

    // Overlapping size (20 msec)
    final static int OVERLAP = SEQUENCE/5 ;
    // Best OVERLAP offset seeking window (15 msec)
    final static int SEEK_WINDOW = 3*OVERLAP/4;

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("test")) {
            test();
            exit(0);
        }

        if (args.length < 2)
        {
            System.out.println("usage: pauvocoder <input.wav> <freqScale>");
            System.out.println("       pauvocoder test\n");
            exit(1);
        }



        String wavInFile = args[0];
        double freqScale = Double.valueOf(args[1]);
        String outPutFile= wavInFile.split("\\.")[0] + "_" + freqScale +"_";

        // Open input .wev file
        System.out.println("Opening " + wavInFile);
        double[] inputWav = StdAudio.read(wavInFile);


        // Resample test
        System.out.println("Resampling");
        double[] newPitchWav = resample(inputWav, freqScale);
        StdAudio.save(outPutFile+"Resampled.wav", newPitchWav);

        // Simple dilatation
        System.out.println("Simple dilatation");
        double[] outputWav   = vocodeSimple(newPitchWav, 1.0/freqScale);
        StdAudio.save(outPutFile+"Simple.wav", outputWav);

        // Simple dilatation with overlaping
        System.out.println("Simple dilatation with overlaping");
        outputWav = vocodeSimpleOver(newPitchWav, 1.0/freqScale);
        StdAudio.save(outPutFile+"SimpleOver.wav", outputWav);

        // Simple dilatation with overlaping and maximum cross correlation search
        System.out.println("Simple dilatation with overlaping and maximum cross correlation search");
        outputWav = vocodeSimpleOverCross(newPitchWav, 1.0/freqScale);
        StdAudio.save(outPutFile+"SimpleOverCross.wav", outputWav);

        // joue(outputWav); // Is called by displayWaveform

        // Some echo above all
        outputWav = echo(outputWav, 100, 0.7);
        StdAudio.save(outPutFile+"SimpleOverCrossEcho.wav", outputWav);

        // Display waveform
        displayWaveform(outputWav);
    }


    /**
     * Resample inputWav with freqScale
     * @param inputWav
     * @param freqScale
     * @return resampled wav
     */
    public static double[] resample(double[] inputWav, double freqScale) {
        double raison;
        double outputWav[];
        int taille = inputWav.length;
        int n = 0;
        if (freqScale > 1) {
            raison = (freqScale-1.0)/freqScale;
            taille = (int)(inputWav.length * (1 - raison) + 1);
        }
        else if (freqScale < 1) {
            raison = (1.0-freqScale)/freqScale;
            taille = (int)(inputWav.length * (raison + 1) + 1);
        }

        outputWav = new double[taille];
        for (double i = 0; i < inputWav.length; i += freqScale) {
            outputWav[n++] = inputWav[(int)i];
        } 
        
        System.out.println("###########################");
        System.out.println("resample");
        System.out.println("freqScale = " + freqScale);
        System.out.println("old taille = " + inputWav.length);
        System.out.println("new taille = " + taille);
        System.out.println("n = " + n);

        return outputWav;
    }


    /**
     * Simple dilatation, without any overlapping
     * @param inputWav
     * @param dilatation factor
     * @return dilated wav
     */
    public static double[] vocodeSimple(double[] inputWav, double dilatation) {
        double saut = (SEQUENCE * dilatation);
        int n = 0;
        double outputWav[];
        int taille = (int)(inputWav.length / dilatation);

        outputWav = new double[taille];

        for (double i = 0; i < inputWav.length; i += saut) {
            int ind = (int)i;
            if ((int)(i+SEQUENCE) >= inputWav.length) {
                ind = inputWav.length - SEQUENCE;
            };
            for (int j = 0; j < SEQUENCE; j++) {
                if (n >= taille) { // to avoid index out of bounds
                    break;
                }
                outputWav[n++] = inputWav[(int)(ind+j)];
            }
        }

        System.out.println("###########################");
        System.out.println("vocodeSimple");
        System.out.println("dilatation = " + dilatation);
        System.out.println("saut = " + saut);
        System.out.println("SEQUENCE = " + SEQUENCE);
        System.out.println("old taille = " + inputWav.length);
        System.out.println("new taille = " + taille);
        System.out.println("n = " + n);

        return outputWav;
    }


    /**
     * Applies overlapping and mixing to the outputWav from the inputWav
     * @param inputWav
     * @param outputWav
     * @param i the starting index in the input wave
     * @param seq
     * @param n the starting index in the output wave
     * @param offset
     * @return the new value of n
     */
    public static int applyOverlapAndMix(double[] inputWav, double[] outputWav, int i, int seq, int n, int offset) {
        n -= OVERLAP; // to mix the overlapping
        for (int j = 0; j < seq ; j++) {
            int index = i+j + offset;
            int len = inputWav.length;
            if (index >= len || n >= len)
                break;

            if (j < OVERLAP) {
                // increase the coefficient
                double coefficient = ((double)(j) / (double)OVERLAP);
                outputWav[n++] += inputWav[index] * coefficient;
            }
            else if (j >= OVERLAP && j < seq - OVERLAP) {
                outputWav[n++] += inputWav[index];
            }
            else {
                // decrease the coefficient
                double coefficient = ((double)(seq - j) / (double)OVERLAP);
                outputWav[n++] += inputWav[index] * coefficient;
            }
        }
        return n;
    }


    /**
     * Simple dilatation, with overlapping
     * @param inputWav
     * @param dilatation factor
     * @return dilated wav
     */
    public static double[] vocodeSimpleOver(double[] inputWav, double dilatation) {
        int seq = SEQUENCE + OVERLAP;
        int saut = (int) (SEQUENCE * dilatation);
        int n = OVERLAP;
        double outputWav[];
        int taille = (int)(inputWav.length / dilatation);
        
        outputWav = new double[taille];


        int offset = 0;
        for (int i = 0; i < inputWav.length; i += saut) {
            if (i+seq >= inputWav.length) { // adapt offset if necessary to avoid index out of bounds
                offset = inputWav.length - (i+seq);
            };
            n = applyOverlapAndMix(inputWav, outputWav, i, seq, n, offset);
        }

        System.out.println("###########################");
        System.out.println("vocodeSimpleOver");
        System.out.println("dilatation = " + dilatation);
        System.out.println("saut = " + saut);
        System.out.println("SEQUENCE = " + SEQUENCE);
        System.out.println("seq = " + seq);
        System.out.println("OVERLAP = " + OVERLAP);
        System.out.println("old taille = " + inputWav.length);
        System.out.println("new taille = " + outputWav.length);
        System.out.println("n = " + n);

        return outputWav;
    }


    /**
     * Computes the cross-correlation between two segments of the input waveform.
     * 
     * @param inputWav
     * @param decStart the starting index for the decreasing segment
     * @param incStop the starting index for the increasing segment
     * @return the cross-correlation sum
     */
    public static double correlation(double[] inputWav, int decStart, int incStop) {
        double sum = 0;
        int len = inputWav.length;
        for (int i = 0; i < OVERLAP; i++) {
            if (decStart + i >= len || (incStop - i >= len || incStop - i < 0))
                break;
            sum += inputWav[decStart + i] * inputWav[incStop - i];
        }
        return sum;
    }


    /**
     * Computes the mean difference between two segments of the input waveform.
     * 
     * @param inputWav
     * @param decStart the starting index for the decreasing segment
     * @param incStart the starting index for the increasing segment
     * @return the mean difference
     */
    public static double meanDifferences(double[] inputWav, int decStart, int incStart) {
        double sum = 0;
        int i;
        int len = inputWav.length;
        for (i = 0; i < OVERLAP; i++) {
            if (decStart + i >= len || incStart + i >= len)
                break;
            sum += Math.abs(inputWav[decStart + i] - inputWav[incStart + i]);
        }
        return sum/(i+1);
    }


    /**
     * Calcul the offset 
     * @param inputWav
     * @param decStart the starting index for the decreasing segmenavect
     * @param incStop the starting index for the increasing segment
     * @return the offset
     */
    public static int calculOffset(double[] inputWav, int decStart, int incStop) {  
        double similarity = meanDifferences(inputWav, decStart, incStop);
        int offset = 0;
        for (int i = 1; i < SEEK_WINDOW; i++) {
            double sim = meanDifferences(inputWav, decStart, incStop + i);
            if (sim < similarity) { // (sim > similarity) if use of correlation()
                similarity = sim;
                offset = i;
            }
        }
        return offset;
    }


    /**
     * Simple dilatation, with overlapping and maximum cross correlation search
     * @param inputWav
     * @param dilatation factor
     * @return dilated wav
     */
    public static double[] vocodeSimpleOverCross(double[] inputWav, double dilatation) {
        int seq = SEQUENCE + OVERLAP;
        int saut = (int) (SEQUENCE * dilatation);
        int n = OVERLAP;
        double outputWav[];
        int taille = (int)(inputWav.length / dilatation);
        
        outputWav = new double[taille];


        int offset = 0;
        for (int i = 0; i < inputWav.length; i += saut) {
            n = applyOverlapAndMix(inputWav, outputWav, i, seq, n, offset);

            // offset search
            int decStart = i+seq+offset-OVERLAP;
            int incStop = i+saut; //+OVERLAP; if use of correlation()
            
            if ((int)(incStop+SEQUENCE+SEEK_WINDOW) >= inputWav.length) { // adapt offset if necessary to avoid index out of bounds
                int incStopCorr = inputWav.length - SEQUENCE - SEEK_WINDOW;
                int offsetCorr = incStopCorr - incStop;
                incStop = incStopCorr;
                offset = calculOffset(inputWav, decStart, incStop)+ offsetCorr;
            } else {
                offset = calculOffset(inputWav, decStart, incStop);
            }
        }

        System.out.println("###########################");
        System.out.println("vocodeSimpleOverCross");
        System.out.println("dilatation = " + dilatation);
        System.out.println("saut = " + saut);
        System.out.println("SEQUENCE = " + SEQUENCE);
        System.out.println("seq = " + seq);
        System.out.println("OVERLAP = " + OVERLAP);
        System.out.println("SEEK_WINDOW = " + SEEK_WINDOW);
        System.out.println("old taille = " + inputWav.length);
        System.out.println("new taille = " + outputWav.length);
        System.out.println("n = " + n);

        return outputWav;
    }
    

    /**
     * Play the wav
     * @param wav
     */
    public static void joue(double[] wav) {
        StdAudio.play(wav);
    }


    /**
     * Add an echo to the wav
     * @param wav
     * @param delayMs in msec
     * @param attn
     * @return wav with echo
     */
    public static double[] echo(double[] wav, double delayMs, double attn) {
        for(int index = 0; index < wav.length; index++) {
            int temp = (int)(delayMs * StdAudio.SAMPLE_RATE / 1000);
            int new_index = index - temp;
            if (new_index >= 0) {
                wav[index] += wav[new_index] * attn;
                if (wav[index] > 1.0) {wav[index] = 1.0;}
                if (wav[index] < -1.0) {wav[index] = -1.0;}
            }
        }
        return wav;
    }



    /**
     * Display the waveform of a double array of audio samples.
     * The waveform is saved to a file named "wave.png".
     * @param wav the double array of audio samples
     */
    public static void DrawWaveForm(double[] wav) {
        StdDraw.clear();
        StdDraw.text(0.5, 0.05, "Pauvocoder waveform");
        StdDraw.text(0.5, -0.05, "Calculating waveform...");
        StdDraw.show(30);
        

        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(1.0/wav.length);
        // Draw the waveform
        for (int i = 0; i < wav.length; i++) {
            double x = (double)i / wav.length;
            double y = wav[i];
            StdDraw.line(x, y, x, 0);
        }
        StdDraw.show();
        StdDraw.save("wave.png");
    }


    /**
     * Display the waveform and play it simultaneously
     * @param wav
     */
    public static void displayWaveform(double[] wav) {
        int WIDTH_WINDOW = 1500;
        int HEIGHT_WINDOW = 500;
        StdDraw.setCanvasSize(WIDTH_WINDOW, HEIGHT_WINDOW);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(-1.0, 1.0);
        StdDraw.enableDoubleBuffering();
        StdDraw.show(100);

        DrawWaveForm(wav);

        // Draw the waveform on another thread
        Thread drawingThread = new Thread(() -> {
            long start = System.currentTimeMillis();
            double x = 0.0;
            while (x < 1.0) {
                long crt = System.currentTimeMillis();
                long duree = crt - start;
                int indexDuree = (int) (duree * StdAudio.SAMPLE_RATE / 1000);
                x = (double)indexDuree / wav.length;
                Draw(wav, Math.min(1.0, x));
            }
        });
        drawingThread.start();
        joue(wav);


        StdDraw.pause(1000);
        StdDraw.close();

        File f = new File("wave.png");
        f.delete();
    }


    
    /**
     * Display the waveform and the vertical red line at the time played (x)
     * @param wav the double array of audio samples
     * @param x the time between 0 and 1
     */
    public static void Draw(double[] wav, double x) {
        StdDraw.clear();
        
        // Draw the waveform from the wave.png file
        StdDraw.picture(0.5, 0, "wave.png", 1, 2.0);
        
        // Draw the vertical line at the time played
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(0.01);
        StdDraw.line(x, -1, x, 1);

        StdDraw.show(50);
    }





    /**
     * Returns a waveform representing a sine wave with the given frequency 
     * and duration.
     *
     * @param freq the frequency of the sine wave in Hz
     * @param time the duration of the sine wave in seconds
     * @return the waveform as an array of doubles
     */
    public static double[] sinoide(double freq, double time) {
        int taille = (int)(StdAudio.SAMPLE_RATE * time);
        double[] sin = new double[taille];
        for (int i = 0; i < taille; i++) {
            sin[i] = Math.sin(2 * Math.PI * freq * i / StdAudio.SAMPLE_RATE);
        }
        return sin;
    }

    /**
     * Compares two waveforms by calculating the average absolute difference 
     * between their corresponding samples.
     *
     * @param wav the first waveform as an array of doubles
     * @param wav2 the second waveform as an array of doubles
     * @return the average absolute difference between the two waveforms
    */
    public static double testComparasion(double[] wav, double[] wav2) {
        double sum = 0;
        for (int i = 0; i < Math.min(wav.length, wav2.length); i++) {
            sum += Math.abs(wav[i] - wav2[i]);
        }
        return sum / wav.length;
        
    }

    /**
     * Compares a sine wave with a transformed sine wave
     * with resampled and vocodeSimpleOverCross
     * The two sine waves are then compared with the comparasion function
     * @param freq the frequency of the original sine wave
     * @param time the duration of the two sine waves
     * @param freqScale the resampling factor
     * @return the similarity between the two sine waves
     */
    public static double testComparasionFreqScale(double freq, double time, double freqScale) {

        double[] sin = sinoide(freq, time);
        double[] sinResampled = resample(sin, freqScale);
        double[] sinTransformed = vocodeSimpleOverCross(sinResampled, 1/freqScale);
        double[] sinAttendu = sinoide(freq * freqScale, time);
        
        return testComparasion(sinTransformed, sinAttendu);
    }

    /**
     * Compare two sine waves with different frequencies
     * 
     * @param freq1 frequency of the first sine wave
     * @param freq2 frequency of the second sine wave
     * @param time duration of the two sine waves
     * @return the mean difference between the two sine waves
     */
    public static double testComparasionFreq(double freq1, double freq2, double time) {
        double[] sin1 = sinoide(freq1, time);
        double[] sin2 = sinoide(freq2, time);
        return testComparasion(sin1, sin2);
    }


    /**
     * Tests vocodeSimpleOverCross() and resample() by comparing a sine wave
     * with the result of applying these methods to the same sine wave 
     * with a different frequency.
     */
    public static void test() {

        // disable stdout
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Tests for vocodeSimpleOverCross
        double testResult440_3s_1point2 = testComparasionFreqScale(440, 3, 1.2);
        double testResult440_3s_0point8 = testComparasionFreqScale(440, 3, 0.8);
        double testResult440_3s_1 = testComparasionFreqScale(440, 3, 1);
        double testResult440_10s_1point2 = testComparasionFreqScale(440, 10, 1.2);
        double testResult440_10s_0point8 = testComparasionFreqScale(440, 10, 0.8);
        double testResult440_10s_1 = testComparasionFreqScale(440, 10, 1);

        // Comparison of different frequencies
        double test400_3s_vs_480_3s = testComparasionFreq(400, 480, 3);
        double test400_10s_vs_480_10s = testComparasionFreq(400, 480, 10);
        double test800_3s_vs_4400_3s = testComparasionFreq(800, 4400, 3);
        double test800_10s_vs_4400_10s = testComparasionFreq(800, 4400, 10);

        // enable stdout
        System.setOut(originalOut);
        
        System.out.println("###################");
        System.out.println("Test");
        System.out.println("Low results indicate a high similarity between the two audio");
        System.out.println("High results indicate a low similarity between the two audio");
        System.out.println("All tests should have a result between 0 and 1");
        System.out.println("###################");
        System.out.println();
        System.out.println();

        System.out.println("Comparison of different frequencies");
        System.out.println("(Large results expected)");
        System.out.printf("testResult time:3s - freq:400 vs 480 = %.4f%n", test400_3s_vs_480_3s);
        System.out.printf("testResult time:10s - freq:400 vs 480 = %.4f%n", test400_10s_vs_480_10s);
        System.out.println();  
        System.out.printf("testResult time:3s - freq:800 vs 4400 = %.4f%n", test800_3s_vs_4400_3s);
        System.out.printf("testResult time:10s - freq:800 vs 4400 = %.4f%n", test800_10s_vs_4400_10s);
        System.out.println();
        System.out.println();

        System.out.println("Comparison of supposedly identical frequencies transformed with resample() then with vocodeSimpleOverCross()");
        System.out.println("(Low results expected)");
        System.out.printf("testResult freq:440 time:3s freqScale:1.2 = %.4f%n", testResult440_3s_1point2);
        System.out.printf("testResult freq:440 time:3s freqScale:0.8 = %.4f%n", testResult440_3s_0point8);
        System.out.printf("testResult freq:440 time:3s freqScale:1 = %.4f%n", testResult440_3s_1);
        System.out.println();
        System.out.printf("testResult freq:440 time:10s freqScale:1.2 = %.4f%n", testResult440_10s_1point2);
        System.out.printf("testResult freq:440 time:10s freqScale:0.8 = %.4f%n", testResult440_10s_0point8);
        System.out.printf("testResult freq:440 time:10s freqScale:1 = %.4f%n", testResult440_10s_1);
        System.out.println();

        System.out.println();
    }


}



