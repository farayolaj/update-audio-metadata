package fj.up.recognition.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecognitionResult(RecognitionStatus status, RecognitionMetadata metadata) {
}

