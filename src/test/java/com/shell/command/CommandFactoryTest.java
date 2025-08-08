package com.shell.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class CommandFactoryTest {

    private final CommandFactory factory = new CommandFactory();

    @Test
    void testGetBuiltInCommand() {
        assertInstanceOf(EchoCommand.class, factory.getCommand("echo"));
        assertInstanceOf(CdCommand.class, factory.getCommand("cd"));
    }

    @Test
    void testGetExternalCommand() {
        // Assuming 'ls' is an external command available in PATH
        assertInstanceOf(ExternalCommand.class, factory.getCommand("ls"));
    }

    @Test
    void testGetNotFoundCommand() {
        assertInstanceOf(NotFoundCommand.class, factory.getCommand("nonexistentcommand"));
    }
}
