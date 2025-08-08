package com.shell.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ShellTestHelper {

    public static class ShellResult {
        public final String stdout;
        public final String stderr;
        public final int exitCode;

        public ShellResult(String stdout, String stderr, int exitCode) {
            this.stdout = stdout;
            this.stderr = stderr;
            this.exitCode = exitCode;
        }
    }

    public static ShellResult runCommands(List<String> commands) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "target/codecrafters-shell.jar");
        Process process = pb.start();

        // Write commands to the shell's stdin
        try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream())) {
            for (String command : commands) {
                writer.write(command + "\n");
                writer.flush();
            }
        }

        // Asynchronously read stdout
        CompletableFuture<String> stdoutFuture = CompletableFuture.supplyAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                return "Error reading stdout: " + e.getMessage();
            }
        });

        // Asynchronously read stderr
        CompletableFuture<String> stderrFuture = CompletableFuture.supplyAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                return reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                return "Error reading stderr: " + e.getMessage();
            }
        });

        // Wait for the process to exit and for the streams to be fully read
        int exitCode = process.waitFor();
        String stdout = stdoutFuture.join();
        String stderr = stderrFuture.join();

        return new ShellResult(stdout, stderr, exitCode);
    }
}
