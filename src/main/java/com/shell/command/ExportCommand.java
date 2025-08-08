package com.shell.command;

import com.shell.ExecutionContext;
import java.util.List;

public class ExportCommand implements Command {
    @Override
    public Process execute(List<String> args, ExecutionContext context) {
        if (args.size() > 1) {
            for (int i = 1; i < args.size(); i++) {
                String arg = args.get(i);
                String[] parts = arg.split("=", 2);
                if (parts.length == 2) {
                    context.setEnvironmentVariable(parts[0], parts[1]);
                }
            }
        }
        // This command produces no output, so return a dummy process
        return new Process() {
            @Override
            public java.io.InputStream getInputStream() { return new java.io.ByteArrayInputStream(new byte[0]); }
            @Override
            public java.io.InputStream getErrorStream() { return new java.io.ByteArrayInputStream(new byte[0]); }
            @Override
            public int waitFor() { return 0; }
            @Override
            public int exitValue() { return 0; }
            @Override
            public void destroy() {}
            @Override
            public java.io.OutputStream getOutputStream() { return null; }
        };
    }
}
