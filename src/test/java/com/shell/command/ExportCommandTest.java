package com.shell.command;

import com.shell.ExecutionContext;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExportCommandTest {

    @Test
    void testExport() {
        ExportCommand export = new ExportCommand();
        ExecutionContext context = new ExecutionContext();
        export.execute(List.of("export", "MY_VAR=hello"), context);
        assertEquals("hello", context.getEnvironmentVariable("MY_VAR"));
    }
}
