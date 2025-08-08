package com.shell.command;

import com.shell.ExecutionContext;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EchoCommandTest {

    @Test
    void testEcho() throws Exception {
        EchoCommand echo = new EchoCommand();
        ExecutionContext context = new ExecutionContext();
        Process process = echo.execute(List.of("echo", "hello", "world"), context);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            assertEquals("hello world", reader.readLine());
        }
    }
}
