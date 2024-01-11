package fj.up.recognition.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import fj.up.recognition.exceptions.RecognitionFailureException;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecognitionResult(RecognitionStatus status, RecognitionMetadata metadata) {
    public static RecognitionResult fromJson(String json) throws RecognitionFailureException {
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

