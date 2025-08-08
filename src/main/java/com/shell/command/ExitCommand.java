package com.shell.command;

import com.shell.ExecutionContext;
import java.util.List;

public class ExitCommand implements Command {
    @Override
    public Process execute(List<String> args, ExecutionContext context) {
        throw new ExitShellException();
    }
}

