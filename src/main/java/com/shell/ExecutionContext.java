package com.shell;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {
    private Path currentDirectory;
    private final PrintStream originalOut;
    private final PrintStream originalErr;
    private final InputStream originalIn;
    private PrintStream out;
    private PrintStream err;
    private InputStream in;
    private Path inputFile;
    private final Map<String, String> environmentVariables;

    public ExecutionContext() {
        this.currentDirectory = Paths.get(System.getProperty("user.dir"));
        this.originalOut = System.out;
        this.originalErr = System.err;
        this.originalIn = System.in;
        this.out = originalOut;
        this.err = originalErr;
        this.in = originalIn;
        this.environmentVariables = new HashMap<>(System.getenv());
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariable(String key, String value) {
        environmentVariables.put(key, value);
    }

    public String getEnvironmentVariable(String key) {
        return environmentVariables.get(key);
    }


    public Path getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String path) {
        Path newPath;
        if (path.startsWith("~")) {
            newPath = Paths.get(System.getProperty("user.home") + path.substring(1));
        } else {
            newPath = currentDirectory.resolve(path).normalize();
        }

        if (Files.isDirectory(newPath)) {
            this.currentDirectory = newPath;
            System.setProperty("user.dir", newPath.toString());
        } else {
            err.printf("cd: %s: No such file or directory\n", path);
        }
    }

    public PrintStream getOut() {
        return out;
    }

    public void setOut(PrintStream out) {
        this.out = out;
        System.setOut(out);
    }

    public void resetOut() {
        System.setOut(originalOut);
        this.out = originalOut;
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
        System.setIn(in);
    }

    public void resetIn() {
        System.setIn(originalIn);
        this.in = originalIn;
    }

    public Path getInputFile() {
        return inputFile;
    }

    public void setInputFile(Path inputFile) {
        this.inputFile = inputFile;
    }
}
