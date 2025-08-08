package com.shell.command;

import com.shell.ExecutionContext;
import java.io.IOException;
import java.util.List;

public class ExternalCommand implements Command {
    @Override
    public Process execute(List<String> args, ExecutionContext context) {
        try {
            ProcessBuilder builder = new ProcessBuilder(args);
            builder.directory(context.getCurrentDirectory().toFile());
            builder.environment().putAll(context.getEnvironmentVariables());
            return builder.start();
        } catch (IOException e) {
            // This is a bit of a hack, but we need to return a process
            // so the pipeline doesn't break.
            String output = String.format("%s: command not found\n", args.get(0));
            return new Process() {
                @Override
                public java.io.InputStream getInputStream() { return new java.io.ByteArrayInputStream(output.getBytes()); }
                @Override
                public java.io.InputStream getErrorStream() { return new java.io.ByteArrayInputStream(new byte[0]); }
                @Override
                public int waitFor() { return 1; }
                @Override
                public int exitValue() { return 1; }
                @Override
                public void destroy() {}
                @Override
                public java.io.OutputStream getOutputStream() { return null; }
            };
        }
    }
}

