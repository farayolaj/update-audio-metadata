package fj.up.metadata;

import fj.up.recognition.result.RecognitionAlbum;
import fj.up.recognition.result.RecognitionArtist;
import fj.up.recognition.result.RecognitionGenre;
import fj.up.recognition.result.RecognitionMusic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TestMetadataService {

    @AfterAll
    public static void deleteOutputDirectory() throws IOException {
        Path outDir = Path.of("updated");

        if (Files.exists(outDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(outDir)) {
                for (Path path : stream) {
                    Files.deleteIfExists(path);
                }
            }

            Files.deleteIfExists(outDir);
        }
    }

    @Test
    public void newFileIsCreatedWithUpdatedMetadata() throws MetadataException {
        URL fileUrl = getClass().getClassLoader().getResource("FILE347.mp3");
        Assertions.assertNotNull(fileUrl);
        Path inputPath = Paths.get(fileUrl.getPath()).toAbsolutePath();
        Path outDir = Path.of("updated");
        RecognitionMusic music = new RecognitionMusic(
                LocalDate.of(2014, 5, 23),
                "StarRising Label",
                "New Life",
                100,
                List.of(new RecognitionArtist("Joshua Farayola")),
                List.of(new RecognitionGenre("Dance"), new RecognitionGenre("Dance")),
                new RecognitionAlbum("Something New"));
        Path outputPath = Paths.get("updated", music.title() + ".mp3");

        MetadataService.updateMetadata(inputPath, music, outDir);

        var tags = MetadataService.getMetadata(outputPath);

        Assertions.assertTrue(Files.exists(outputPath));
        Assertions.assertEquals(music.title(), tags.getTitle());
        Assertions.assertEquals(music.releaseDate().toString(), tags.getDate());
        Assertions.assertEquals((music.releaseDate().format(DateTimeFormatter.ofPattern("yyyy"))), tags.getYear());
        Assertions.assertEquals(
                music.artists().size(),
                music.artists().stream()
                        .filter(artist -> tags.getArtist().contains(artist.name()))
                        .count());
        Assertions.assertTrue(
                music.genres().stream()
                        .anyMatch(genre -> tags.getGenreDescription().equalsIgnoreCase(genre.name())));
        Assertions.assertEquals(music.album().name(), tags.getAlbum());
    }
}
