package com.shell.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EndToEndIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void testExportEchoAndRedirect() throws IOException, InterruptedException {
        // Define the output file path within the temporary directory
        Path outputFile = tempDir.resolve("output.txt");
        
        // Commands to be executed by the shell
        List<String> commands = List.of(
            "export GREETING=hello",
            "export SUBJECT=world",
            "echo $GREETING $SUBJECT > " + outputFile.toAbsolutePath(),
            "exit"
        );

        // Run the shell with the commands
        ShellTestHelper.ShellResult result = ShellTestHelper.runCommands(commands);

        // Assertions
        // 1. Check that the shell exited cleanly
        assertEquals(0, result.exitCode, "Shell should exit with code 0");

        // 2. Verify the content of the output file
        assertTrue(Files.exists(outputFile), "Output file should be created");
        String fileContent = Files.readString(outputFile).trim();
        assertEquals("hello world", fileContent, "File content should match the expanded echo command");
    }
}
