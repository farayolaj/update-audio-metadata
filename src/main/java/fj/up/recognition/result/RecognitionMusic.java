package fj.up.recognition.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecognitionMusic(
        @JsonProperty("release_date") @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate releaseDate, String label, String title, int score,
        List<RecognitionArtist> artists, List<RecognitionGenre> genres,
        RecognitionAlbum album) {

    @Override
    public String toString() {
        return """
                Title: %s
                Album: %s
                Artist: %s
                Label: %s
                Release Date: %s
                Genres: %s
                Score: %d
                """
                .formatted(title, album.name() == null ? "Unknown" : album.name(),
                        String.join(", ", artists == null ? List.of() :
                                artists.stream().map(RecognitionArtist::name).toList()),
                        label == null ? "Unknown" : label,
                        releaseDate == null ? "" :
                                releaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        String.join(", ", genres == null ? List.of() :
                                genres.stream().map(RecognitionGenre::name).toList()), score);
    }
}
