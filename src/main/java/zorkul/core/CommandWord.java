package zorkul.core;

import java.util.HashMap;
import java.util.Map;

public enum CommandWord {
    GO, QUIT, HELP, LOOK, USE, TAKE, DROP, WRITE, STATUS, REPAIR,
    SWIPE, TALK, CLOSE, MUTE, SUBMIT, CHEAT, SAVE, UNKNOWN;

    private static final Map<String, CommandWord> lookup = new HashMap<>();

    static {
        for (CommandWord cw : CommandWord.values()) {
            lookup.put(cw.name().toLowerCase(), cw);
        }
    }

    public static CommandWord fromString(String command) {
        if (command == null) return UNKNOWN;
        return lookup.getOrDefault(command.toLowerCase(), UNKNOWN);
    }
}
