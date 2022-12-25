package fj.up.exceptions;

import java.nio.file.Path;

public class FileNotFoundException extends Exception {
    public FileNotFoundException(Path filePath) {
        super(filePath.toAbsolutePath() + " was not found.");
    }
}
