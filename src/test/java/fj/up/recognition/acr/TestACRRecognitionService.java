package fj.up.recognition.acr;

import fj.up.recognition.exceptions.RecognitionFailureException;
import fj.up.recognition.result.RecognitionResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

class TestACRRecognitionService {
    @Test
    void audioFileRecognisedWithCorrectTitle() {
        try {
            URL fileUrl = getClass().getClassLoader().getResource("FILE347.mp3");
            Assertions.assertNotNull(fileUrl);
            Path filePath = Paths.get(fileUrl.getPath()).toAbsolutePath();
            RecognitionResult result = new ACRRecognitionService().identifyAudio(filePath);

            Assertions.assertEquals(0, result.status().code());

            Assertions.assertNotNull(result.metadata());
            Assertions.assertTrue(result.metadata().music().stream()
                    .anyMatch(music -> music.title().contains("Wide As The Sky")));
        } catch (RecognitionFailureException e) {
            Assertions.fail(e);
        }
    }
}
