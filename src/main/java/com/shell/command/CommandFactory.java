package com.shell.command;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private final Map<String, Command> builtInCommands = new HashMap<>();

    public CommandFactory() {
        builtInCommands.put("exit", new ExitCommand());
        builtInCommands.put("echo", new EchoCommand());
        builtInCommands.put("pwd", new PwdCommand());
        builtInCommands.put("cd", new CdCommand());
        builtInCommands.put("type", new TypeCommand());
        builtInCommands.put("export", new ExportCommand());
    }

    public boolean isBuiltIn(String commandName) {
        return builtInCommands.containsKey(commandName);
    }

    public Command getCommand(String commandName) {
        if (builtInCommands.containsKey(commandName)) {
            return builtInCommands.get(commandName);
        }

        String[] PATH = System.getenv("PATH").split(":");
        for (String path : PATH) {
            File[] directory = new File(path).listFiles();
            if (directory != null) {
                for (File file : directory) {
                    if (file.getName().equals(commandName)) {
                        return new ExternalCommand();
                    }
                }
            }
        }

        return new NotFoundCommand();
    }
}
