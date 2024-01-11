package fj.up.exceptions;

import java.nio.file.Path;
import java.util.Collection;

public class UnsupportedFileFormatException extends Exception {
    public UnsupportedFileFormatException(Path filePath) {
        super(filePath + " has an unsupported format.");
    }

    public UnsupportedFileFormatException(Path filePath, Collection<String> supportedFormats) {
        super(filePath + " has an unsupported format. Supported formats: " +
            String.join(", ", supportedFormats));
    }
}
