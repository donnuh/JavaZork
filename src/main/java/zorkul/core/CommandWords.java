package zorkul.core;
import java.util.HashMap;
import java.util.Map;

public class CommandWords {
    public final Map<String, String> validCommands;

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
        validCommands.put("repair", "Attempt to repair the coffee machine");
        validCommands.put("swipe", "Swipe your ID card");
        validCommands.put("talk", "Talk to someone");
        validCommands.put("close", "Close a distraction");
        validCommands.put("mute", "Mute a distraction");
        validCommands.put("submit", "Submit your paper");
        validCommands.put("cheat", "Cheat on your paper"); 
        validCommands.put("save", "Save your game");
        validCommands.put("load", "Load a saved game");
        
    }

    public boolean isCommand(String commandWord) {
        return validCommands.containsKey(commandWord);
    }

    public void showAll() {
        for (String command : validCommands.keySet()) {
            if (!command.equals("cheat")) {
                System.out.print(command + " ");
            }
        }
        System.out.print(""); 
    }
}
