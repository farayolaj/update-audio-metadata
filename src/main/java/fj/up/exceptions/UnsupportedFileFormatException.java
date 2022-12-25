package fj.up.exceptions;

import java.nio.file.Path;

public class UnsupportedFileFormatException extends Exception {
    public UnsupportedFileFormatException(Path filePath) {
        super(filePath + " has an unsupported format.");
    }
}
