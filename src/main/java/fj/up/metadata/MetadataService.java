package fj.up.metadata;

import com.mpatric.mp3agic.*;
import fj.up.recognition.result.RecognitionArtist;
import fj.up.recognition.result.RecognitionMusic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class MetadataService {

    public static ID3v2 getMetadata(Path path) throws MetadataException {
        try {
            Mp3File mp3File = new Mp3File(path);
            return getMetadata(mp3File);
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new MetadataException(path, e);
        }
    }

    private static ID3v2 getMetadata(Mp3File mp3File) {
        ID3v2 tag;

        if (Objects.isNull(mp3File.getId3v2Tag())) {
            tag = new ID3v24Tag();
            mp3File.setId3v2Tag(tag);
        } else tag = mp3File.getId3v2Tag();

        return tag;
    }

    public static void updateMetadata(Path path, RecognitionMusic metadata, Path outDir) throws MetadataException {
        try {
            Mp3File mp3File = new Mp3File(path);
            ID3v2 tag = getMetadata(mp3File);
            tag.setTitle(metadata.title());

            if (metadata.artists() != null) {
                String artist = String.join(", ",
                        metadata.artists().stream().map(RecognitionArtist::name).toList());
                tag.setAlbumArtist(artist);
                tag.setArtist(artist);
            }

            if (metadata.genres() != null) {
                String genre = metadata.genres().isEmpty() ? "" : metadata.genres().get(0).name();
                if (ID3v1Genres.matchGenreDescription(genre) != -1)
                    tag.setGenreDescription(genre);
            }

            if (metadata.album() != null)
                tag.setAlbum(metadata.album().name());

            if (metadata.releaseDate() != null) {
                String localDate = metadata.releaseDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
                String year = metadata.releaseDate().format(DateTimeFormatter.ofPattern("yyyy"));
                tag.setDate(localDate);
                tag.setYear(year);
            }


            Path newPath = getNewFilePath(metadata.title(), outDir);
            mp3File.save(newPath.toAbsolutePath().toString());
        } catch (NotSupportedException | UnsupportedTagException | InvalidDataException | IOException e) {
            throw new MetadataException(path, e);
        }
    }

    /**
     * Returns a {@code Path} object representing the location of the new file with updated metadata.
     * By default, the new file is located in the `updated` directory of the current working directory.
     * If an file with the same title already exists in the output directory, it is replaced.
     *
     * @param title Title of the music file
     * @return Path of the new file
     * @throws IOException If an error is encountered while creating the required paths.
     */
    private static Path getNewFilePath(String title, Path outDir) throws IOException {
        Path newPath = outDir.resolve(title + ".mp3");

        if (!Files.exists(outDir)) Files.createDirectory(outDir);

        Files.deleteIfExists(newPath);
        return newPath;
    }
}

// Todo: Move acr account details into a config file
