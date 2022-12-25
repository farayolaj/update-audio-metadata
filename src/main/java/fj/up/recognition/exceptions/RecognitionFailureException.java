package fj.up.recognition.exceptions;

import lombok.ToString;

@ToString
public class RecognitionFailureException extends Exception {
    private final RecognitionErrorCode errorCode;

    public RecognitionFailureException(int code) {
        this(RecognitionErrorCode.valueOfCode(code));
    }

    public RecognitionFailureException(RecognitionErrorCode errorCode) {
        super(errorCode.message);
        this.errorCode = errorCode;
    }

    public RecognitionFailureException(String message, Throwable cause) {
        super(message, cause);
        errorCode = null;
    }

    public RecognitionErrorCode errorCode() {
        return errorCode;
    }
}
