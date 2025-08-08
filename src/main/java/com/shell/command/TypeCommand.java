package com.shell.command;

import com.shell.ExecutionContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class TypeCommand implements Command {
    @Override
    public Process execute(List<String> args, ExecutionContext context) {
        if (args.size() < 2) {
            return createProcess("");
        }
        String command = args.get(1);
        String[] validCommands = {"exit", "echo", "type", "pwd", "cd"};
        String[] PATH = System.getenv("PATH").split(":");
        for (String validCommand : validCommands) {
            if (validCommand.equals(command)) {
                return createProcess(String.format("%s is a shell builtin\n", command));
            }
        }
        for (String path : PATH) {
            File[] directory = new File(path).listFiles();
            if (directory != null) {
                for (File file : directory) {
                    if (file.getName().equals(command)) {
                        return createProcess(String.format("%s is %s\n", command, file.getAbsolutePath()));
                    }
                }
            }
        }
        return createProcess(String.format("%s: not found\n", command));
    }

    private Process createProcess(String output) {
        return new Process() {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(output.getBytes());
            }

            @Override
            public InputStream getErrorStream() {
                return new ByteArrayInputStream(new byte[0]);
            }

            @Override
            public int waitFor() {
                return 0;
            }

            @Override
            public int exitValue() {
                return 0;
            }

            @Override
            public void destroy() {
            }

            @Override
            public java.io.OutputStream getOutputStream() {
                return null;
            }
        };
    }
}

