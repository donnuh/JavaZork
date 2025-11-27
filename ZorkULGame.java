/* This game is a classic text-based adventure set in a university environment.
   The player starts outside the main entrance and can navigate through different rooms like a 
   lecture theatre, campus pub, computing lab, and admin office using simple text commands (e.g., "go east", "go west").
    The game provides descriptions of each location and lists possible exits.

Key features include:
Room navigation: Moving among interconnected rooms with named exits.
Simple command parser: Recognizes a limited set of commands like "go", "help", and "quit".
Player character: Tracks current location and handles moving between rooms.
Text descriptions: Provides immersive text output describing the player's surroundings and available options.
Help system: Lists valid commands to guide the player.
Overall, it recreates the classic Zork interactive fiction experience with a university-themed setting, 
emphasizing exploration and simple command-driven gameplay
*/

public class ZorkULGame {
    private final Parser parser;
    private Player player;

    public ZorkULGame() {
        createRooms();
        parser = new Parser();
    }

    private void createRooms() {
        Room dorm, corridor, lounge, library, vending;

        // create rooms
        dorm = new Room("in your dorm room. Your desk is covered in papers and a glowing laptop.");
        corridor = new Room("in the dormitory corridor. You can hear muffled video gamenoises from other rooms.");
        lounge = new Room("in the study lounge. Its cold quiet and smells like coffee.");
        library = new Room("in the campus library. Rows of books stretch out.");
        vending = new Room("at the vending machines. You see snacks and drinks.");

        // add items to rooms
        ColdCoffee coffeeMug = new ColdCoffee("CoffeeMug", "A mug with stale coffee residue.");
        ColdCoffee energyDrink = new ColdCoffee("EnergyDrink", "A sugary energy drink that might kill you, but you need it.");
        
        dorm.addItem(new Laptop("Laptop", "Your trusty (and slightly battered) research machine."));

        dorm.addItem(coffeeMug);
        vending.addItem(energyDrink);
        
        // initialise room exits
        dorm.setExit("east", corridor);
        corridor.setExit("west", dorm);
        corridor.setExit("south", vending);
        corridor.setExit("north", lounge);

        lounge.setExit("south", corridor);
        lounge.setExit("east", library);
        library.setExit("west", lounge);
        vending.setExit("north", corridor);

        // create the player character and start in dorm room
        player = new Player("player", dorm); //changed to player class
    }

    public void play() {
        printWelcome();

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing. Goodbye.");
    }

    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the ALL NIGHTER!");
        System.out.println("You must finish your 4000 word paper before 8:00 AM. Good luck.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(player.getCurrentRoom().getLongDescription());
        System.out.println(String.format("Current Status: Sleep Level: %d | Word Count: %d/4000", player.getSleepLevel(), player.getWordCount()));
    }

    private boolean processCommand(Command command) {
        String commandWord = command.getCommandWord();

        if (commandWord == null) {
            System.out.println("I don't understand your command...");
            return false;
        }

        switch (commandWord) {
            case "take" -> takeItem(command);
            case "drop" -> dropItem(command);
            case "consume" ->  consumeItem(command);// New command logic
            case "write" ->  writePaper();// New command logic
            case "status" -> System.out.println(String.format("Status: Sleep Level: %d | Word Count: %d/4000", player.getSleepLevel(), player.getWordCount()));
            case "help" -> printHelp();
            case "look" -> lookAround();
            case "go" -> goRoom(command);
            case "quit" -> {
                if (command.hasSecondWord()) {
                    System.out.println("Quit what?");
                    return false;
                } else {
                    return true;
                }
            }
            default -> System.out.println("I don't know what you mean...");
        }
        if(player.getWordCount() >= 4000) {
            System.out.println("\n Hooray! You finished the paper! Now SUBMIT!");
            // This is where the submission logic would be added
        }
        
        return false;
    }
    private void lookAround() {
        System.out.println(player.getCurrentRoom().getLongDescription());
        player.showInventory();
    }
    
    // NEW METHOD: Handles the WRITE command with logic checks
    private void writePaper() {
        // Check if the player has the required item (Laptop) in their inventory
        if (player.getItem("Laptop") != null) {
            player.write();
        } else {
            System.out.println("You can't write without your Laptop!");
        }
    }

// --- New Command Handler: CONSUME ---
    private void consumeItem(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Consume what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item item = player.getItem(itemName);

        if (item == null) {
            System.out.println("You don't have that item.");
        } else if (item instanceof Consumable consumable) {
            // Use the IConsumable interface method
            consumable.consume(player); 
        } else {
            System.out.println("You can't consume the " + itemName + ".");
        }
    }
    private void printHelp() {
        System.out.println("You are lost. You are alone. You wander around the university.");
        System.out.print("Your command words are: ");
        parser.showCommands();
    }
//left here
    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Go where?");
            return;
        }
        String direction = command.getSecondWord();
        Room nextRoom = player.getCurrentRoom().getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        } else {
            player.setCurrentRoom(nextRoom);
            System.out.println(player.getCurrentRoom().getLongDescription());
        }
    }
    private void takeItem(Command command) {
    if (!command.hasSecondWord()) {
        System.out.println("Take what?");
        return;
    }

    String itemName = command.getSecondWord();
    Item item = player.getCurrentRoom().takeItem(itemName);

    if (item == null) {
        System.out.println("There is no such item here.");
    } else {
        player.addItem(item);
        System.out.println("You picked up the " + itemName + ".");
    }
}

    private void dropItem(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Drop what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item item = player.getItem(itemName);

        if (item == null) {
            System.out.println("You don't have that item.");
        } else {
            player.removeItem(item);
            player.getCurrentRoom().addItem(item);
            System.out.println("You dropped the " + itemName + ".");
        }
    }

    public static void main(String[] args) {
        ZorkULGame game = new ZorkULGame();
        game.play();
    }
}
class Laptop extends Item {

    public Laptop(String name, String description) {
        super(name, description);
    }

    /**
     * Defines the interaction when the player uses or looks at the laptop.
     */
    @Override
    public void interact(Player player) {
        System.out.println("The " + getName() + " is currently open to your half-finished paper.");
        System.out.println("You should probably start writing...");
    }
}

/**public class Box<T>[public class Box<T> {
    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
} 
{
    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
} */