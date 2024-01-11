package fj.up.recognition.acr;

import com.acrcloud.utils.ACRCloudRecognizer;
import fj.up.recognition.RecognitionService;
import fj.up.recognition.config.AcrConfig;
import fj.up.recognition.exceptions.RecognitionFailureException;
import fj.up.recognition.result.RecognitionResult;
import java.nio.file.Path;

public class ACRRecognitionService implements RecognitionService {
    private static final ACRCloudRecognizer recognizer;

    static {
        AcrConfig config = AcrConfig.loadConfig();
        recognizer = new ACRCloudRecognizer(config);
    }

    public RecognitionResult identifyAudio(Path path) throws RecognitionFailureException {
        String json = recognizer.recognizeAudioByFile(path.toAbsolutePath().toString(), 5, 5);
        return RecognitionResult.fromJson(json);
    }
}
