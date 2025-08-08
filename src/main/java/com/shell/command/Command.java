package com.shell.command;

import com.shell.ExecutionContext;
import java.util.List;

public interface Command {
    Process execute(List<String> args, ExecutionContext context);
}

