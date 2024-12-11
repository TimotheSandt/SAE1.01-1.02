import static java.lang.System.exit;

public class Pauvocoder {

    // Processing SEQUENCE size (100 msec with 44100Hz samplerate)
    final static int SEQUENCE = StdAudio.SAMPLE_RATE/10;

    // Overlapping size (20 msec)
    final static int OVERLAP = SEQUENCE/5 ;
    // Best OVERLAP offset seeking window (15 msec)
    final static int SEEK_WINDOW = 3*OVERLAP/4;

    public static void main(String[] args) {
        if (args.length < 2)
        {
            System.out.println("usage: pauvocoder <input.wav> <freqScale>\n");
            exit(1);
        }


        String wavInFile = args[0];
        double freqScale = Double.valueOf(args[1]);
        String outPutFile= wavInFile.split("\\.")[0] + "_" + freqScale +"_";

        // Open input .wev file
        double[] inputWav = StdAudio.read(wavInFile);

        // Resample test
        double[] newPitchWav = resample(inputWav, freqScale);
        StdAudio.save(outPutFile+"Resampled.wav", newPitchWav);

        // Simple dilatation
        double[] outputWav   = vocodeSimple(newPitchWav, 1.0/freqScale);
        StdAudio.save(outPutFile+"Simple.wav", outputWav);

        // Simple dilatation with overlaping
        outputWav = vocodeSimpleOver(newPitchWav, 1.0/freqScale);
        StdAudio.save(outPutFile+"SimpleOver.wav", outputWav);

        // Simple dilatation with overlaping and maximum cross correlation search
        // outputWav = vocodeSimpleOverCross(newPitchWav, 1.0/freqScale);
        // StdAudio.save(outPutFile+"SimpleOverCross.wav", outputWav);

        // joue(outputWav);

        // Some echo above all
        // outputWav = echo(outputWav, 100, 0.7);
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

        System.out.println("###########################");
        System.out.println("resample");
        System.out.println("freqScale = " + freqScale);
        System.out.println("old taille = " + inputWav.length);
        System.out.println("new taille = " + taille);

        outputWav = new double[taille];
        for (double i = 0; i < inputWav.length; i += freqScale) {
            outputWav[n++] = inputWav[(int)i];
        }
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
        int taille = (int)(inputWav.length / dilatation) + 1;

        outputWav = new double[taille];

        System.out.println("###########################");
        System.out.println("vocodeSimple");
        System.out.println("dilatation = " + dilatation);
        System.out.println("saut = " + saut);
        System.out.println("SEQUENCE = " + SEQUENCE);
        System.out.println("old taille = " + inputWav.length);
        System.out.println("new taille = " + taille);

        for (double i = 0; i < inputWav.length; i += saut) {
            for (int j = 0; j < SEQUENCE; j++) {
                if ((int)(i+j) >= inputWav.length || n >= taille) {
                    break;
                }
                outputWav[n++] = inputWav[(int)(i+j)];
            }
        }
        System.out.println("n = " + n);
        return outputWav;
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
        int taille = (int)(inputWav.length / dilatation) + 1;
        
        outputWav = new double[taille];

        System.out.println("###########################");
        System.out.println("vocodeSimpleOver");
        System.out.println("dilatation = " + dilatation);
        System.out.println("saut = " + saut);
        System.out.println("SEQUENCE = " + SEQUENCE);
        System.out.println("seq = " + seq);
        System.out.println("OVERLAP = " + OVERLAP);
        System.out.println("old taille = " + inputWav.length);
        System.out.println("new taille = " + outputWav.length);


        for (int i = 0; i < inputWav.length; i += saut) {
            n -= OVERLAP;
            for (int j = 0; j < seq ; j++) {
                if (i+j >= inputWav.length || n >= outputWav.length)
                    break;

                if (j < OVERLAP) {
                    outputWav[n++] += inputWav[i+j] * ((double)j / (double)OVERLAP);
                }
                else if (j >= OVERLAP && j < seq - OVERLAP) {
                    outputWav[n++] = inputWav[i+j];
                }
                else {
                    outputWav[n++] = inputWav[i+j] * ((double)(seq - j) / (double)OVERLAP);
                }
            }
        }
        System.out.println("n = " + n);
        return outputWav;
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
        int taille = (int)(inputWav.length / dilatation) + 1;
        
        outputWav = new double[taille];

        System.out.println("###########################");
        System.out.println("vocodeSimpleOver");
        System.out.println("dilatation = " + dilatation);
        System.out.println("saut = " + saut);
        System.out.println("SEQUENCE = " + SEQUENCE);
        System.out.println("seq = " + seq);
        System.out.println("OVERLAP = " + OVERLAP);
        System.out.println("old taille = " + inputWav.length);
        System.out.println("new taille = " + outputWav.length);


        for (int i = 0; i < inputWav.length; i += saut) {
            n -= OVERLAP;
            for (int j = 0; j < seq ; j++) {
                if (i+j >= inputWav.length || n >= outputWav.length)
                    break;

                if (j < OVERLAP) {
                    outputWav[n++] += inputWav[i+j] * ((double)j / (double)OVERLAP);
                }
                else if (j >= OVERLAP && j < seq - OVERLAP) {
                    outputWav[n++] = inputWav[i+j];
                }
                else {
                    outputWav[n++] = inputWav[i+j] * ((double)(seq - j) / (double)OVERLAP);
                }
            }
        }
        System.out.println("n = " + n);
        return outputWav;
    }
    

    /**
     * Play the wav
     * @param wav
     */
    public static void joue(double[] wav) {
        throw new UnsupportedOperationException("Not implemented yet");
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
            int new_index = index - (int)(delayMs * StdAudio.SAMPLE_RATE / 1000);
            if (new_index >= 0) {
                wav[index] += wav[new_index] * attn;
                if (wav[index] > 1.0) {wav[index] = 1.0;}
                if (wav[index] < -1.0) {wav[index] = -1.0;}
            }
        }
        return wav;
    }

    /**
     * Display the waveform
     * @param wav
     */
    public static void displayWaveform(double[] wav) {
        throw new UnsupportedOperationException("Not implemented yet");
    }


}
