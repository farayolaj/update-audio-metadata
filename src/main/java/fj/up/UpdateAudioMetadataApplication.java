package fj.up;

import fj.up.cli.UpdatePlaylist;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.ParseResult;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UpdateAudioMetadataApplication {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        Scanner scanner = new Scanner(System.in);
        int exitCode = new CommandLine(new UpdatePlaylist(scanner, threadPool))
                .setExecutionExceptionHandler((Exception ex, CommandLine cmd, ParseResult parseResult) -> {
                    log.error(ex.getMessage());
                    cmd.getErr().println(cmd.getColorScheme().errorText("Something went wrong: %s".formatted(ex.getMessage())));

                    return cmd.getExitCodeExceptionMapper() != null
                            ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                            : cmd.getCommandSpec().exitCodeOnExecutionException();
                })
                .execute(args);
        try {
            //noinspection ResultOfMethodCallIgnored
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.exit(exitCode);
    }
}

