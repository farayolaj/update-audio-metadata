package fj.up.recognition.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecognitionMetadata(List<RecognitionMusic> music) {
}
