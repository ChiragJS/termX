package com.shell;

import com.shell.command.Command;
import com.shell.command.CommandFactory;
import com.shell.parser.InputParser;
import com.shell.command.ExitShellException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.History;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Shell {
    private final CommandFactory commandFactory;
    private final InputParser inputParser;
    private final ExecutionContext context;

    public Shell() {
        this.commandFactory = new CommandFactory();
        this.inputParser = new InputParser();
        this.context = new ExecutionContext();
    }

    public void start() {
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .history(new DefaultHistory())
                    .build();

            while (true) {
                try {
                    String input = lineReader.readLine("$ ");
                    if (input == null) { // This case handles Ctrl+D
                        break;
                    }

                    try {
                        input = inputParser.handleRedirection(input, context);
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                        continue;
                    }

                    List<List<String>> pipelineTokens = inputParser.parsePipeline(input);
                    if (pipelineTokens.isEmpty() || pipelineTokens.get(0).isEmpty()) {
                        continue;
                    }

                    // Expand variables for each command in the pipeline
                    List<List<String>> expandedPipeline = new ArrayList<>();
                    for (List<String> tokens : pipelineTokens) {
                        expandedPipeline.add(inputParser.expandVariables(tokens, context));
                    }

                    executePipeline(expandedPipeline);

                    context.resetOut();
                    context.resetIn();
                    context.setInputFile(null); // Reset input file after each command
                    lineReader.getHistory().add(input);

                } catch (org.jline.reader.EndOfFileException e) {
                    // This is expected when the input stream is closed (e.g., in tests or scripts)
                    break;
                } catch (ExitShellException e) {
                    // This is thrown by the ExitCommand to signal a clean shutdown
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executePipeline(List<List<String>> pipeline) {
        boolean allExternal = pipeline.stream()
                .allMatch(tokens -> !commandFactory.isBuiltIn(tokens.get(0)));

        if (allExternal) {
            executeExternalPipeline(pipeline);
        } else {
            executeMixedPipeline(pipeline);
        }
    }

    private void executeExternalPipeline(List<List<String>> pipeline) {
        List<ProcessBuilder> builders = new ArrayList<>();
        for (int i = 0; i < pipeline.size(); i++) {
            List<String> command = pipeline.get(i);
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(context.getCurrentDirectory().toFile());
            pb.environment().putAll(context.getEnvironmentVariables());

            if (i == 0 && context.getInputFile() != null) {
                pb.redirectInput(context.getInputFile().toFile());
            }
            builders.add(pb);
        }

        try {
            List<Process> processes = ProcessBuilder.startPipeline(builders);
            Process lastProcess = processes.get(processes.size() - 1);
            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(lastProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    context.getOut().println(line);
                }
            }
            for (Process process : processes) {
                process.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void executeMixedPipeline(List<List<String>> pipeline) {
        List<Process> processes = new ArrayList<>();
        for (List<String> commandTokens : pipeline) { // Use the already expanded tokens
            Command command = commandFactory.getCommand(commandTokens.get(0));
            Process process = command.execute(commandTokens, context);
            if (process != null) {
                processes.add(process);
            } else {
                return; // Command failed, abort pipeline
            }
        }

        for (int i = 0; i < processes.size() - 1; i++) {
            Process source = processes.get(i);
            Process dest = processes.get(i + 1);
            new Thread(() -> {
                try (var out = dest.getOutputStream()) {
                    source.getInputStream().transferTo(out);
                } catch (IOException e) {
                    // Pipe broken, ignore
                }
            }).start();
        }

        Process lastProcess = processes.get(processes.size() - 1);
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(lastProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                context.getOut().println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Process process : processes) {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}
