package fj.up.recognition.shazam;

import fj.up.recognition.RecognitionService;
import fj.up.recognition.exceptions.RecognitionFailureException;
import fj.up.recognition.result.RecognitionResult;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Base64;

public class ShazamRecognitionService implements RecognitionService {
    private static final String SHAZAM_API_URL = "https://shazam.p.rapidapi.com/songs/v2/detect";
    private final RawAudioSampler rawAudioSampler = new RawAudioSampler(44100, 16, 2,
            true, false);;

    public RecognitionResult identifyAudio(Path path) throws RecognitionFailureException {
        try {
            byte[] base64RawAudio = Base64.getEncoder().encode(rawAudioSampler.getRawSample(path, 5));
            var client = HttpClient.newHttpClient();
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(SHAZAM_API_URL))
                    .header("content-type", "text/plain")
                    .header("X-RapidAPI-Key", "d3884da9acmshcf2dd956ca2a7dap10e576jsneb5b7ec88791")
                    .header("X-RapidAPI-Host", "shazam.p.rapidapi.com")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(base64RawAudio))
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return RecognitionResult.fromJson(response.body());
        } catch (IOException e) {
            throw new RecognitionFailureException("Error processing audio file", e);
        } catch (UnsupportedAudioFileException e) {
            throw new RecognitionFailureException("Unsupported audio file", e);
        } catch (InterruptedException e) {
            throw new RecognitionFailureException("Error recognizing file", e);
        }
    }
}
