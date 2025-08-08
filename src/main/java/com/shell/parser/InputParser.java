package com.shell.parser;

import com.shell.ExecutionContext;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputParser {

    public List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean escaping = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (escaping) {
                current.append(c);
                escaping = false;
                continue;
            }
            if (c == '\\') {
                if (inSingleQuote) {
                    current.append(c);
                } else if (inDoubleQuote) {
                    if (i + 1 < input.length()) {
                        char next = input.charAt(i + 1);
                        if (next == '\\' || next == '"' || next == '$' || next == '\n') {
                            i++;
                            current.append(input.charAt(i));
                        } else {
                            current.append(c);
                        }
                    } else {
                        current.append(c);
                    }
                } else {
                    escaping = true;
                }
            } else if (c == '\'') {
                if (inDoubleQuote) {
                    current.append(c);
                } else {
                    inSingleQuote = !inSingleQuote;
                }
            } else if (c == '"') {
                if (inSingleQuote) {
                    current.append(c);
                } else {
                    inDoubleQuote = !inDoubleQuote;
                }
            } else if (Character.isWhitespace(c)) {
                if (inSingleQuote || inDoubleQuote) {
                    current.append(c);
                } else {
                    if (!current.isEmpty()) {
                        tokens.add(current.toString());
                        current.setLength(0);
                    }
                }
            } else {
                current.append(c);
            }
        }
        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }
        return tokens;
    }

    public List<List<String>> parsePipeline(String input) {
        List<List<String>> pipeline = new ArrayList<>();
        String[] commands = input.split("\\|");
        for (String command : commands) {
            pipeline.add(tokenize(command.trim()));
        }
        return pipeline;
    }

    public List<String> expandVariables(List<String> tokens, ExecutionContext context) {
        List<String> expandedTokens = new ArrayList<>();
        for (String token : tokens) {
            expandedTokens.add(expandToken(token, context));
        }
        return expandedTokens;
    }

    private String expandToken(String token, ExecutionContext context) {
        if (token.startsWith("'") && token.endsWith("'")) {
            return token; // No expansion in single quotes
        }

        // Handle double-quoted strings
        String content = token;
        boolean isDoubleQuoted = token.startsWith("\"") && token.endsWith("\"");
        if (isDoubleQuoted) {
            content = token.substring(1, token.length() - 1);
        }

        Pattern pattern = Pattern.compile("\\$([a-zA-Z_][a-zA-Z0-9_]*)");
        Matcher matcher = pattern.matcher(content);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String varName = matcher.group(1);
            String varValue = context.getEnvironmentVariable(varName);
            if (varValue == null) {
                varValue = ""; // Replace with empty string if not found
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(varValue));
        }
        matcher.appendTail(sb);

        if (isDoubleQuoted) {
            return "\"" + sb.toString() + "\"";
        } else {
            return sb.toString();
        }
    }

    public String handleRedirection(String input, ExecutionContext context) throws IOException {
        // Pattern to find <, >, >> and the filename that follows.
        Pattern pattern = Pattern.compile("(<|>>|>) *([^ ]+)");
        Matcher matcher = pattern.matcher(input);
        StringBuffer command = new StringBuffer();

        while (matcher.find()) {
            String operator = matcher.group(1);
            String file = matcher.group(2);
            Path path = context.getCurrentDirectory().resolve(file);

            switch (operator) {
                case "<":
                    if (!Files.exists(path)) {
                        throw new IOException("No such file or directory: " + file);
                    }
                    context.setInputFile(path);
                    break;
                case ">":
                    context.setOut(new PrintStream(Files.newOutputStream(path)));
                    break;
                case ">>":
                    context.setOut(new PrintStream(Files.newOutputStream(path, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND)));
                    break;
            }
            matcher.appendReplacement(command, "");
        }
        matcher.appendTail(command);
        return command.toString().trim();
    }
}
