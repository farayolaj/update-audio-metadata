package fj.up.recognition.shazam;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class RawAudioSampler {
    private final int SAMPLE_RATE;
    private final int SAMPLE_SIZE_IN_BITS;
    private final int CHANNELS;
    private final AudioFormat audioFormat;

    public RawAudioSampler(int sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian) {
        SAMPLE_RATE = sampleRate;
        SAMPLE_SIZE_IN_BITS = sampleSizeInBits;
        CHANNELS = channels;
        this.audioFormat = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, signed, bigEndian);
    }

    private byte[] formatAudioToWav(final byte[] audioFileContent, final AudioFormat audioFormat) throws
            IOException, UnsupportedAudioFileException {

        try (
                final AudioInputStream originalAudioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(audioFileContent));
                final AudioInputStream formattedAudioStream = AudioSystem.getAudioInputStream(audioFormat, originalAudioStream);
                final AudioInputStream lengthAddedAudioStream = new AudioInputStream(formattedAudioStream, audioFormat, audioFileContent.length);
                final ByteArrayOutputStream convertedOutputStream = new ByteArrayOutputStream()
        ) {
            AudioSystem.write(lengthAddedAudioStream, AudioFileFormat.Type.WAVE, convertedOutputStream);
            return convertedOutputStream.toByteArray();
        }
    }

    private byte[] formatWavToRaw(final byte[] audioFileContent) {
        return Arrays.copyOfRange(audioFileContent, 44, 44 + 441000);
    }

    public byte[] getRawSample(Path audioPath, int lengthsInSeconds) throws IOException, UnsupportedAudioFileException {
        return formatWavToRaw(formatAudioToWav(Files.readAllBytes(audioPath), audioFormat));
    }
}
