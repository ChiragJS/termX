package com.shell.command;

import com.shell.ExecutionContext;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PwdCommandTest {

    @Test
    void testPwd() throws Exception {
        PwdCommand pwd = new PwdCommand();
        ExecutionContext context = new ExecutionContext();
        Process process = pwd.execute(List.of("pwd"), context);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            assertEquals(System.getProperty("user.dir"), reader.readLine());
        }
    }
}
