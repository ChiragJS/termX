package com.shell.command;

import com.shell.ExecutionContext;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeCommandTest {

    @Test
    void testTypeBuiltIn() throws Exception {
        TypeCommand type = new TypeCommand();
        ExecutionContext context = new ExecutionContext();
        Process process = type.execute(List.of("type", "echo"), context);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            assertTrue(reader.readLine().contains("is a shell builtin"));
        }
    }

    @Test
    void testTypeExternal() throws Exception {
        TypeCommand type = new TypeCommand();
        ExecutionContext context = new ExecutionContext();
        Process process = type.execute(List.of("type", "ls"), context);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            assertTrue(reader.readLine().contains("is /"));
        }
    }
}
