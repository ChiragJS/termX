package com.shell.command;

import com.shell.ExecutionContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class CdCommand implements Command {
    @Override
    public Process execute(List<String> args, ExecutionContext context) {
        if (args.size() > 1) {
            context.setCurrentDirectory(args.get(1));
        }
        return createProcess("");
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

