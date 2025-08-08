package com.shell.parser;

import com.shell.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputParserTest {

    private InputParser parser;
    private ExecutionContext context;

    @BeforeEach
    void setUp() {
        parser = new InputParser();
        context = new ExecutionContext();
    }

    @Test
    void testTokenizeSimple() {
        List<String> tokens = parser.tokenize("echo hello world");
        assertEquals(List.of("echo", "hello", "world"), tokens);
    }

    @Test
    void testTokenizeWithQuotes() {
        List<String> tokens = parser.tokenize("echo \"hello world\"");
        assertEquals(List.of("echo", "hello world"), tokens);
    }

    @Test
    void testTokenizeWithSingleQuotes() {
        List<String> tokens = parser.tokenize("echo 'hello world'");
        assertEquals(List.of("echo", "hello world"), tokens);
    }

    @Test
    void testTokenizeWithMixedQuotes() {
        List<String> tokens = parser.tokenize("echo \"'hello' world\"");
        assertEquals(List.of("echo", "'hello' world"), tokens);
    }

    @Test
    void testTokenizeWithEmptyString() {
        List<String> tokens = parser.tokenize("");
        assertEquals(List.of(), tokens);
    }

    @Test
    void testExpandVariable() {
        context.setEnvironmentVariable("MY_VAR", "world");
        List<String> tokens = List.of("echo", "hello", "$MY_VAR");
        List<String> expanded = parser.expandVariables(tokens, context);
        assertEquals(List.of("echo", "hello", "world"), expanded);
    }

    @Test
    void testExpandVariableInQuotes() {
        context.setEnvironmentVariable("MY_VAR", "world");
        List<String> tokens = List.of("echo", "\"hello $MY_VAR\"");
        List<String> expanded = parser.expandVariables(tokens, context);
        assertEquals(List.of("echo", "\"hello world\""), expanded);
    }

    @Test
    void testNoExpandVariableInSingleQuotes() {
        context.setEnvironmentVariable("MY_VAR", "world");
        List<String> tokens = List.of("echo", "'hello $MY_VAR'");
        List<String> expanded = parser.expandVariables(tokens, context);
        assertEquals(List.of("echo", "'hello $MY_VAR'"), expanded);
    }
}
