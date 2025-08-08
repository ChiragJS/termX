package com.shell.command;

import com.shell.ExecutionContext;
import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CdCommandTest {

    @Test
    void testCd() {
        CdCommand cd = new CdCommand();
        ExecutionContext context = new ExecutionContext();
        String homeDir = System.getProperty("user.home");
        cd.execute(List.of("cd", "~"), context);
        assertEquals(Paths.get(homeDir).normalize(), context.getCurrentDirectory());
    }
}
