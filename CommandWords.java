import java.util.HashMap;
import java.util.Map;

public class CommandWords {
    private final Map<String, String> validCommands;

    public CommandWords() {
        validCommands = new HashMap<>();
        validCommands.put("go", "Move to another room");
        validCommands.put("quit", "End the game");
        validCommands.put("help", "Show help");
        validCommands.put("look", "Look around");
        //validCommands.put("eat", "Eat something"); //put under 'use'
        validCommands.put("use", "Use an object or interact with something");
        validCommands.put("take", "Pick up an item");
        validCommands.put("drop", "Drop an item");
        //validCommands.put("consume", "Consume an item");
        validCommands.put("write", "Write your paper");
        validCommands.put("status", "Check your word count and sleep level");
    }

    public boolean isCommand(String commandWord) {
        return validCommands.containsKey(commandWord);
    }

    public void showAll() {
        System.out.print("Valid commands are: ");
        for (String command : validCommands.keySet()) {
            System.out.print(command + " ");
        }
        System.out.println();
    }
}
