public class ZorkULGame {
    private final Parser parser;
    private Player player;
    // New state flag for the coffee machine quest
    private boolean isCoffeeMachineFixed = false; 

    public ZorkULGame() {
        createRooms();
        parser = new Parser();
    }

    private void createRooms() {
        Room dorm, corridor, lounge, library, maintenance;

       
        dorm = new Room("in your dorm room. Your desk is covered in papers and a glowing laptop.");
        corridor = new Room("in the dormitory corridor. You can hear muffled video gamenoises from other rooms.");
        // UPDATED LOUNGE DESCRIPTION and Exits
        lounge = new Room("in the Study Lounge. A cold, uncomfortable room filled with uncomfortable chairs and stale air. There is a broken coffee machine on a counter.");
        library = new Room("in the campus library. Rows of books stretch out.");
        // NEW ROOM: Maintenance, replaces the old vending room logic
        maintenance = new Room("in a dusty Maintenance Closet. It smells faintly of bleach and mildew.");


        // add items to rooms
        ColdCoffee coffeeMug = new ColdCoffee("CoffeeMug", "A mug with stale coffee residue.");
        
        // Using the concrete SimpleItem class. This now works because SimpleItem is defined
        // within the ZorkULGame class structure and is accessible.
        SimpleItem replacementFilter = new SimpleItem("Filter", "A brand new, clean coffee filter. Seems to be the right size for the machine.");
        maintenance.addItem(replacementFilter); 
        
        // Items needed for the main quest
        dorm.addItem(new Laptop("Laptop", "Your trusty (and slightly battered) research machine."));
        dorm.addItem(coffeeMug);
        
        // initialise room exits
        dorm.setExit("east", corridor);
        
        corridor.setExit("west", dorm);
        corridor.setExit("up", lounge); // Changed exit to Lounge
        corridor.setExit("north", library);

        // Lounge Exits
        lounge.setExit("down", corridor); 
        lounge.setExit("south", maintenance); // The Maintenance Closet is off the lounge

        // Library Exits
        library.setExit("south", corridor);
        
        // Maintenance Exits
        maintenance.setExit("north", lounge);


        // create the player character and start in dorm room
        player = new Player("player", dorm);
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
            case "use" -> interactItem(command); 
            case "write" -> writePaper(); 
            case "status" -> System.out.println(String.format("Status: Sleep Level: %d | Word Count: %d/4000", player.getSleepLevel(), player.getWordCount()));
            case "help" -> printHelp();
            case "go" -> goRoom(command);
            case "look" -> lookAround(); 
            case "repair" -> repairCoffeeMachine(); 
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
        
        // Win condition check
        if(player.getWordCount() >= 4000) {
            System.out.println("\n Hooray! You finished the paper! Now SUBMIT!");
        }
        
        return false;
    }

    // NEW METHOD: Handles the Repair command
    private void repairCoffeeMachine() {
        // Use current room's description to check location
        if (player.getCurrentRoom().getDescription().contains("Study Lounge")) { 
            if (isCoffeeMachineFixed) {
                System.out.println("The coffee machine is already fixed. You can 'use' it now!");
            } else if (player.getItem("Filter") != null) {
                // Fix the machine
                isCoffeeMachineFixed = true;
                player.removeItem(player.getItem("Filter"));
                
                // Change room description to reflect fix
                Room lounge = player.getCurrentRoom();
                Room newLounge = new Room(lounge.getDescription().replace("broken coffee machine", "fully operational coffee machine, brewing a fresh pot!"));
                newLounge.setExit("down", lounge.getExit("down")); 
                newLounge.setExit("south", lounge.getExit("south"));

                player.setCurrentRoom(newLounge);
                
                System.out.println("You carefully insert the new filter and press the 'brew' button. Success! Fresh coffee is brewing.");
                System.out.println("The Sleep Level penalty for writing will now be reduced!");
            } else {
                System.out.println("You can't fix the machine without a replacement filter. It looks like it needs a clean one.");
            }
        } else {
            System.out.println("There's nothing here that needs repairing.");
        }
    }


    // NEW METHOD: Unified item interaction handler, replaces consumeItem
    private void interactItem(Command command) {
        if (!command.hasSecondWord()) {
            // Check if player is in the lounge and wants to use the machine
            if (player.getCurrentRoom().getDescription().contains("Study Lounge") && isCoffeeMachineFixed) {
                // Assume 'use' without a second word means use the fixed coffee machine
                player.setSleepLevel(player.getSleepLevel() + 50); // Big boost for fresh coffee
                System.out.println("You grab a mug and pour yourself a cup of fresh, hot coffee. That hits the spot!");
                System.out.println("Sleep Level increased by 50 to " + player.getSleepLevel() + ".");
                return;
            }
            System.out.println("Use what? (e.g., use CoffeeMug)");
            return;
        }

        String itemName = command.getSecondWord();
        Item item = player.getItem(itemName);

        if (item == null) {
            System.out.println("You don't have that item.");
            return;
        } 
        
        // 1. Check if Consumable (like coffee mug)
        if (item instanceof Consumable consumable) {
            consumable.consume(player); 
        } 
        // 2. Otherwise, use the general Item interact method
        else {
            item.interact(player);
        }
    }


    private void lookAround() {
        System.out.println(player.getCurrentRoom().getLongDescription());
        player.showInventory();
    }
    
    private void writePaper() {
        // Check if the player has the required item (Laptop) in their inventory
        if (player.getItem("Laptop") != null) {
            player.write();
        } else {
            System.out.println("You can't write without your Laptop! You should 'take Laptop' first.");
        }
    }

    private void printHelp() {
        System.out.println("You are lost. You are alone. You wander around the university.");
        System.out.print("Your command words are: ");
        parser.showCommands();
    }

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
    
    // --- Nested Item Class Definitions ---
    
    /**
     * SimpleItem.java (Integrated Class Definition)
     * A concrete class for simple, non-consumable items that just need to be picked up
     * (like the Filter), and don't need a custom 'interact' behavior beyond the default 
     * message inherited from Item.
     */
    static class SimpleItem extends Item {
        public SimpleItem(String name, String description) {
            super(name, description);
        }
        @Override
        public void interact(Player player) {
            System.out.println("You examine the " + getName() + ". It looks like a replacement part for a machine.");
        }
    }


    /**
     * Laptop.java (Integrated Class Definition)
     * The essential, non-consumable item required for the player to write the paper.
     * It extends the abstract Item class.
     */
    static class Laptop extends Item {

        public Laptop(String name, String description) {
            super(name, description);
        }

        /**
         * Defines the interaction when the player uses or looks at the laptop.
         */
        @Override
        public void interact(Player player) {
            System.out.println("The " + getName() + " is currently open to your half-finished paper.");
            System.out.println("It seems to be working, but you should probably use the 'write' command.");
        }
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