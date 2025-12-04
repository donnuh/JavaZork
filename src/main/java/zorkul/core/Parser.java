package zorkul.core;
import java.io.InputStream;
import java.util.Scanner;

public class Parser {
    public final CommandWords commands;
    public final Scanner reader;

    public Parser() {
        commands = new CommandWords();
        reader = new Scanner(System.in);
    }
    public Parser(InputStream inputStream) {
        this.commands = new CommandWords();
        this.reader = new Scanner(inputStream);
    }

    public Command getCommand() {
        System.out.print("> ");
        String inputLine = reader.nextLine().toLowerCase();

        String word1 = null;
        String word2 = null;

        try (Scanner tokenizer = new Scanner(inputLine)) {
            if (tokenizer.hasNext()) {
                word1 = tokenizer.next();
                if (tokenizer.hasNext()) {
                    word2 = tokenizer.next();
                }
            }
        }

        if (commands.isCommand(word1)) {
            return new Command(word1, word2);
        } else {
            return new Command(null, word2);
        }
    }
    public Command getCommand(String commandLine) {
        String inputLine = commandLine.toLowerCase();
        
        String word1 = null;
        String word2 = null;

        // Use a new Scanner (tokenizer) to break the input line into words
        try (Scanner tokenizer = new Scanner(inputLine)) {
            if (tokenizer.hasNext()) {
                // Get first word (command)
                word1 = tokenizer.next();
                if (tokenizer.hasNext()) {
                    // Get second word (modifier)
                    word2 = tokenizer.next(); 
                }
            }
        }
        
        // Check if the command word is known and create the Command object.
        if (commands.isCommand(word1)) {
            return new Command(word1, word2);
        } else {
            return new Command(null, word2); // Unknown command
        }
    }

    public void showCommands() {
        commands.showAll();
    }
}
