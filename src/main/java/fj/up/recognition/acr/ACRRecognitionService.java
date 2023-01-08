package fj.up.recognition.acr;

import com.acrcloud.utils.ACRCloudRecognizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
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
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        try {
            RecognitionResult result = mapper.readerFor(RecognitionResult.class).readValue(json);

            if (result.status().code() != 0) throw new RecognitionFailureException(result.status().code());

            return result;
        } catch (JsonProcessingException e) {
            throw new RecognitionFailureException("Error processing recognition result", e);
        }
    }
}
