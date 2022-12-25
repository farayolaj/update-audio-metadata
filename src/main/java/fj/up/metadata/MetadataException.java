package fj.up.metadata;

import java.nio.file.Path;

public class MetadataException extends Exception {
    public MetadataException(Path path, Throwable cause) {
        super("An error occurred while updating the metadata of " + path.toString(), cause);
    }
}
