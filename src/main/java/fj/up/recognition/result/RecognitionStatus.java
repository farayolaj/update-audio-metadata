package fj.up.recognition.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecognitionStatus(@JsonProperty("msg") String message, int code, String version) {
}
