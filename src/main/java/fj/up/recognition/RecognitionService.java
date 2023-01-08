package fj.up.recognition;

import fj.up.recognition.acr.ACRRecognitionService;
import fj.up.recognition.exceptions.RecognitionFailureException;
import fj.up.recognition.result.RecognitionResult;

import java.nio.file.Path;

public interface RecognitionService {
    RecognitionResult identifyAudio(Path path) throws RecognitionFailureException;

    static RecognitionService create() {
        return new ACRRecognitionService();
    }
}
