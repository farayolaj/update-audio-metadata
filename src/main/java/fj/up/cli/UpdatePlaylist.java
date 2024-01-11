package fj.up.cli;

import fj.up.exceptions.FileNotFoundException;
import fj.up.exceptions.UnsupportedFileFormatException;
import fj.up.metadata.MetadataException;
import fj.up.metadata.MetadataService;
import fj.up.recognition.RecognitionService;
import fj.up.recognition.exceptions.RecognitionErrorCode;
import fj.up.recognition.exceptions.RecognitionFailureException;
import fj.up.recognition.result.RecognitionMetadata;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

@Slf4j
@Command(name = "update-playlist", mixinStandardHelpOptions = true, version = "0.0.1",
        description = "Tool to update your music playlist's metadata.")
public class UpdatePlaylist implements Callable<Integer> {
    private static final Set<String> SUPPORTED_FORMATS = Set.of("mp3");

    private final Scanner scanner;
    private final ExecutorService threadPool;

    @Parameters(index = "0", description = "The directory containing the music files or a single music file.",
            prompt = "File | Directory")
    private final Path inputPath = Path.of(".");
    @CommandLine.Option(names = {"-o", "--output"}, description = "The output directory. Defaults to \"./updated\"")
    private final Path outDir = Path.of("updated");
    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Prints more information.")
    private boolean verbose = false;

    public UpdatePlaylist(Scanner scanner, ExecutorService threadPool) {
        this.scanner = scanner;
        this.threadPool = threadPool;
    }

    @Override
    public Integer call() throws FileNotFoundException, UnsupportedFileFormatException,
            RecognitionFailureException, IOException {
        printVerbose("Input Path: %s".formatted(inputPath.toAbsolutePath()));
        if (!inputPath.toFile().exists()) throw new FileNotFoundException(inputPath);

        if (Files.isDirectory(inputPath)) handleDirectory(inputPath);
        else handleFile(inputPath);

        return 0;
    }

    private void printVerbose(String message) {
        if (verbose) System.out.println(message);
    }

    private void handleFile(Path filePath) throws UnsupportedFileFormatException, RecognitionFailureException {
        if (!isSupportedFormat(filePath)) throw new UnsupportedFileFormatException(filePath, SUPPORTED_FORMATS);

        RecognitionMetadata metadata;

        try {
            RecognitionService recognitionService = RecognitionService.getAcr();
            metadata = recognitionService.identifyAudio(filePath).metadata();
        } catch (RecognitionFailureException e) {
            if (e.errorCode() == RecognitionErrorCode.NO_RECOGNITION) {
                System.out.println(
                        CommandLine.Help.Ansi.AUTO.string(
                                "@|red Recognition failed for audio file: %s|@%n"
                                        .formatted(filePath.getFileName().toString())));
                return;
            } else {
                throw e;
            }
        }

        if (metadata == null || metadata.music().isEmpty()) {
            System.out.println(CommandLine.Help.Ansi.AUTO.string(
                    "@|red Uh-oh... I couldn't find any metadata for %s|@%n".formatted(filePath)));
            return;
        }


        printVerbose("Found %s result(s) for %s".formatted(metadata.music().size(), filePath));

        System.out.printf(
                CommandLine.Help.Ansi.AUTO.string(
                        "@|blue Found some results for %s. Which do you think is right?|@%n"),
                filePath.getFileName());

        for (int i = 0; i < metadata.music().size(); i++) {
            var music = metadata.music().get(i);
            System.out.printf(CommandLine.Help.Ansi.AUTO.string("@|cyan Option %d:|@%n%s%n".formatted(i + 1, music)));
        }

        System.out.printf(
                CommandLine.Help.Ansi.AUTO.string("@|yellow Enter your choice (1-%d): |@"),
                metadata.music().size());
        int chosenOption = scanner.nextInt();

        while (chosenOption < 1 || chosenOption > metadata.music().size()) {
            System.out.printf(
                    CommandLine.Help.Ansi.AUTO.string(
                            "@|red" +
                                    " Invalid option.|@ @|yellow Enter a number between 1 and %d: |@"),
                    metadata.music().size());
            chosenOption = scanner.nextInt();
        }

        System.out.println();
        var chosenMusic = metadata.music().get(chosenOption - 1);

        threadPool.execute(() -> {
            try {
                MetadataService.updateMetadata(filePath, chosenMusic, outDir);
            } catch (MetadataException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleDirectory(Path dirPath) throws UnsupportedFileFormatException,
            RecognitionFailureException, IOException {
        List<Path> filePaths;

        try (var stream = Files.list(dirPath)) {
            filePaths = stream
                    .filter(Files::isRegularFile)
                    .filter(this::isSupportedFormat)
                    .toList();
        }

        for (var filePath : filePaths) {
            handleFile(filePath);
        }
    }

    /**
     * Checks if {@code path} used is supported in update playlist.
     * At this moment, only mp3 files are supported.
     *
     * @param path {@link Path} object representing an existing file.
     * @return boolean indicating whether file represented by {@code path} is supported.
     */
    private boolean isSupportedFormat(Path path) {
        String ext = getExtension(path);
        return SUPPORTED_FORMATS.contains(ext);
    }

    /**
     * Gets the extension of the file represented by the {@code path}.
     * If the file has no extension (also applies to directories), an empty string is returned.
     *
     * @param path Object representing a file.
     * @return extension of the file or an empty string.
     */
    private String getExtension(Path path) {
        if (Files.isDirectory(path)) return "";
        String[] splitPath = path.getFileName().toString().split("\\.");
        return splitPath[splitPath.length - 1].toLowerCase();
    }
}
