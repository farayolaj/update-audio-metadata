package fj.up.recognition.exceptions;

import java.util.HashMap;
import java.util.Map;

public enum RecognitionErrorCode {
    NO_RECOGNITION(1001, "No recognition result"),
    INIT_FAILED(2001, "Initialisation failed or request timeout"),
    TIMEOUT(2005, "Recognition timeout"),
    SERVICE_ERROR(3000, "Recognition service error"),
    LIMIT_EXCEEDED(3003, "Limit exceeded, upgrade your ACR account"),
    INVALID_ARGUMENTS(3006, "Invalid arguments to ACR"),
    INVALID_SIGNATURE(3014, "Invalid ACR signature"),
    QPS_LIMIT_EXCEEDED(3015, "QpS limit exceeded, upgrade your ACR account"),
    UNKNOWN_ERROR(9999, "Unknown recognition error");

    private static final Map<Integer, RecognitionErrorCode> CODE = new HashMap<>();

    static {
        for (RecognitionErrorCode e: values()) {
            CODE.put(e.code, e);
        }
    }

    public final int code;
    public final String message;

    RecognitionErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static RecognitionErrorCode valueOfCode(int code) {
        return CODE.getOrDefault(code, RecognitionErrorCode.UNKNOWN_ERROR);
    }
}
