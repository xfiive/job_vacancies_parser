package org.parser.parsermail.bot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Command {
    START("/start"),
    SHOW_LIKED("/showliked"),
    RESET("/reset"),
    STOP("/stop"),
    UNKNOWN("");

    private final List<String> commandTexts = new ArrayList<>();

    Command(@NotNull String... commandTexts) {
        this.commandTexts.addAll(Arrays.asList(commandTexts));
    }

    public static Command fromString(String text) {
        for (Command command : Command.values()) {
            for (String commandText : command.getCommandTexts()) {
                if (commandText.equalsIgnoreCase(text)) {
                    return command;
                }
            }
        }
        return UNKNOWN;
    }

    public List<String> getCommandTexts() {
        return commandTexts;
    }

    public void addAlternateCommandText(String alternateCommandText) {
        this.commandTexts.add(alternateCommandText);
    }
}
