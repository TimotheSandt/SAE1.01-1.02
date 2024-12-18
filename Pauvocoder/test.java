public class test {
    public static void main(String[] args) {
        System.out.println("test");

        double freq = 440.0;
        double time = 1;
        String outPutFile = "sinoide" + freq + "_" + time + "s";

        double[] sinoide = sinoide(freq, time);
        StdAudio.save(outPutFile + ".wav", sinoide);

        double freqScale = args.length > 0 ? Double.parseDouble(args[0]) : 1.2;
        
        double[] newPitchWav = Pauvocoder.resample(sinoide, freqScale);
        StdAudio.save(outPutFile + "_" + freqScale + "_Resampled.wav", newPitchWav);

        double[] vocodeSimple = Pauvocoder.vocodeSimple(newPitchWav, 1/freqScale);
        StdAudio.save(outPutFile + "_" + freqScale + "_Simple.wav", vocodeSimple);

        double[] vocodeSimpleOver = Pauvocoder.vocodeSimpleOver(newPitchWav, 1/freqScale);
        StdAudio.save(outPutFile + "_" + freqScale + "_SimpleOver.wav", vocodeSimpleOver);

        double[] vocodeSimpleOverCross = Pauvocoder.vocodeSimpleOverCross(newPitchWav, 1/freqScale);
        StdAudio.save(outPutFile + "_" + freqScale + "_SimpleOverCross.wav", vocodeSimpleOverCross);

    }    


    public static double[] sinoide(double freq, double time) {
        int taille = (int)(StdAudio.SAMPLE_RATE * time);
        double[] sin = new double[taille];
        for (int i = 0; i < taille; i++) {
            sin[i] = Math.sin(2 * Math.PI * freq * i / StdAudio.SAMPLE_RATE);
        }
        return sin;
    }
}