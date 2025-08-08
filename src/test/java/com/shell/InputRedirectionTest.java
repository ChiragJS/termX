package com.shell;

import com.shell.parser.InputParser;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputRedirectionTest {

    @Test
    void testInputRedirection() throws IOException {
        // Create a temporary file with some content
        File tempFile = File.createTempFile("test-input", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("hello from file\n");
        }

        // Prepare shell components
        ExecutionContext context = new ExecutionContext();
        InputParser parser = new InputParser();

        // The command to test
        String commandString = "grep hello < " + tempFile.getAbsolutePath();

        // Capture standard output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        context.setOut(ps);

        // Manually parse and execute
        String commandWithoutRedirection = parser.handleRedirection(commandString, context);
        List<List<String>> pipeline = parser.parsePipeline(commandWithoutRedirection);
        
        // This is a simplified execution path for testing purposes
        // In a real scenario, we would use the Shell's executePipeline method
        ProcessBuilder pb = new ProcessBuilder(pipeline.get(0));
        if (context.getInputFile() != null) {
            pb.redirectInput(context.getInputFile().toFile());
        }
        Process process = pb.start();
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                context.getOut().println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Flush the stream and verify the output
        ps.flush();
        assertEquals("hello from file", baos.toString().trim());

        // Clean up
        tempFile.delete();
    }
}