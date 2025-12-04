package zorkul.core;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;

import zorkul.items.Consumable;
import zorkul.items.Item;
import zorkul.world.Player;
import zorkul.world.Room;

import java.util.HashMap;

public final class ZorkULGame implements Serializable {
    public static final long serialVersionUID = 1L;
    
    // Fields marked as 'transient' are ignored during serialization.
    public transient Parser parser;
    
    public Player player;
    public final Map<String, Room> roomMap; 

    // Game state flags/variables (will be serialized)
    public boolean isCoffeeMachineFixed = false; 
    public boolean isSnackTaken = false;
    public int gameTimeHours = 22;
    public int gameTimeMinutes = 0;
    public boolean isLibraryUnlocked = false;
    public boolean isMaxPlacated = false;
    public boolean isLibrarianGone = false; 
    public boolean isDistracted = false; 
    public boolean isHyperfocused = false;
    public int hyperfocusRemainingTurns = 0; 
    public String requiredCommand = null;

    /**
     * Constructor for a NEW game. 
     */
    public ZorkULGame() {
        // Initialize fields that are transient or needed for setup
        this.roomMap = new HashMap<>();
        this.parser = new Parser();
        createRooms();
    }
    
    // --- Custom Deserialization Logic ---
    private void readObject(java.io.ObjectInputStream in) 
        throws java.io.IOException, ClassNotFoundException {

    in.defaultReadObject(); // Restore saved fields

    // Recreate transient parser after load
    this.parser = new Parser();
}

    
    // --- Room Creation ---

    /**
     * Creates all rooms and items for a NEW game.
     */
    public void createRooms() {
        Room dorm, corridor, lounge, library, maintenance, gamerRoom, researchStacks;
       
        dorm = new Room("in your dorm room. Your desk is covered in papers and a glowing laptop.");
        corridor = new Room("in the dormitory corridor. You can hear muffled video gamenoises from other rooms.");
        lounge = new Room("in the Study Lounge. A cold, uncomfortable room filled with uncomfortable chairs and stale air. There is a broken coffee machine on a counter.");
        library = new Room("in the campus library. Rows of books stretch out.");
        maintenance = new Room("in a dusty Maintenance Closet. It smells faintly of bleach and mildew.");
        gamerRoom = new Room("in Max's Gamer Room. The walls glow with RGB light and the sound of mechanical keyboards fills the air.");
        researchStacks = new Room("in the deep, dusty Research Stacks of the library. It is quiet here, perfect for focus.");

        // Map rooms by description
        roomMap.put(dorm.getDescription(), dorm);
        roomMap.put(corridor.getDescription(), corridor);
        roomMap.put(lounge.getDescription(), lounge);
        roomMap.put(library.getDescription(), library);
        roomMap.put(maintenance.getDescription(), maintenance);
        roomMap.put(gamerRoom.getDescription(), gamerRoom);
        roomMap.put(researchStacks.getDescription(), researchStacks);
        
        // Items for a new game (using nested classes defined elsewhere)
        ColdCoffee coffeeMug = new ColdCoffee("CoffeeMug", "A mug with stale coffee residue.");
        SimpleItem replacementFilter = new SimpleItem("Filter", "A brand new, clean coffee filter. Seems to be the right size for the machine.");
        SimpleItem studentID = new SimpleItem("StudentID", "Your university ID card. You need this to get into locked campus buildings.");

        maintenance.addItem(replacementFilter); 
        dorm.addItem(new Laptop("Laptop", "Your trusty (and slightly battered) research machine."));
        dorm.addItem(coffeeMug);
        dorm.addItem(studentID); 
   
        // Set fixed exits
        dorm.setExit("east", corridor);
        corridor.setExit("west", dorm);
        corridor.setExit("up", lounge); 
        corridor.setExit("north", library);
        corridor.setExit("south", gamerRoom);
        lounge.setExit("down", corridor); 
        lounge.setExit("south", maintenance); 
        library.setExit("south", corridor);
        library.setExit("east", researchStacks);
        maintenance.setExit("north", lounge);
        gamerRoom.setExit("north", corridor);
        researchStacks.setExit("west", library);

        player = new Player("player", dorm);
    }


    public void play() {
        printWelcome();

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            
            // Check for save/load commands first
            if (command.getCommandWord() != null) {
                if (command.getCommandWord().equals("save")) {
                    GamePersistence.saveGame(this);
                    continue; // Do not advance time for saving
                }
            }
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing. Goodbye.");
    }

    public  void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the ALL NIGHTER!");
        System.out.println("You must finish your 4000 word paper before 8:00 AM. Good luck.");
        System.out.println("Type 'help' for commands, or 'save' to save your progress.");
        System.out.println();
        System.out.println(player.getCurrentRoom().getLongDescription()); 
        System.out.println(String.format("Current Time: %s | Sleep Level: %d | Word Count: %d/4000", formatTime(), player.getSleepLevel(), player.getWordCount()));
    }
    public boolean processCommandString(String commandLine) {
        // 1. Use the existing parser to turn the raw String into a Command object
        Command command = parser.getCommand(commandLine);
        
        // 2. Pass the resulting Command object to the internal logic
        return processCommand(command);
    }

    public String formatTime() {
        int displayHour = gameTimeHours % 12;
        if (displayHour == 0) displayHour = 12;
        String amPm = (gameTimeHours >= 12 && gameTimeHours < 24) ? "PM" : "AM";
        return String.format("%d:%02d %s", displayHour, gameTimeMinutes, amPm);
    }

    public void advanceTime(int minutes) {
        int oldHour = gameTimeHours;
        
        int newTotalMinutes = gameTimeMinutes + minutes;
        gameTimeMinutes = newTotalMinutes % 60;
        
        int hoursToAdd = newTotalMinutes / 60;
        gameTimeHours = (gameTimeHours + hoursToAdd) % 24;
        
        System.out.println("\n[It is now " + formatTime() + "]");

        if (gameTimeHours == 1 && oldHour != 1 && !isLibrarianGone) {
            isLibrarianGone = true;
            System.out.println("\n*** The clock strikes 1:00 AM. The Librarian silently packs up and leaves! ***");
            System.out.println("*** The Research Stacks are now open. ***");
        }
        
        boolean isAllNighterActive = (gameTimeHours >= 22 || gameTimeHours <= 7);
        if (!isDistracted && !isHyperfocused && isAllNighterActive) {
            if (Math.random() < 0.15) { 
                isDistracted = true;
                
                if (Math.random() < 0.5) {
                     requiredCommand = "CLOSE";
                     System.out.println("\n*** SOCIAL MEDIA DISTRACTION ALERT! ***");
                     System.out.println("Your laptop tab popped up. You need to quickly type 'CLOSE'!");
                } else {
                     requiredCommand = "MUTE";
                     System.out.println("\n*** SOCIAL MEDIA DISTRACTION ALERT! ***");
                     System.out.println("Your phone is buzzing. You need to quickly type 'MUTE'!");
                }
                System.out.println("Failure to act or using the wrong command will result in a time penalty!");
            }
        }
        
        if (gameTimeHours >= 8 && gameTimeHours < 22 && player.getWordCount() < 4000) {
            System.out.println("\n*** 8:00 AM has arrived! Your paper is not finished. You failed! ***");
            System.exit(0);
        }
    }

    public boolean processCommand(Command command) {
        String commandWord = command.getCommandWord();
        boolean commandHandled = false;

        if (commandWord == null) {
            System.out.println("I don't understand your command...");
            return false;
        }

        // --- Distraction Resolution Check ---
        // We check this BEFORE advancing time or executing game logic
        if (isDistracted) {
            if (commandWord.equalsIgnoreCase("close") || commandWord.equalsIgnoreCase("mute")) {
                // If the player successfully attempts to resolve the distraction, 
                // we handle the resolution here and prevent the time penalty/time advance.
                resolveDistraction(commandWord);
                commandHandled = true; // Command was a distraction resolution attempt
            } else {
                // The player executed a different command while distracted. Apply penalty.
                System.out.println("\n[DISTRACTION FAILURE] You ignored the alert and wasted 15 minutes!");
                advanceTime(15); 
                isDistracted = false;
                requiredCommand = null;
            }
        }
        
        // --- Advance Time (Only if not a status command or a distraction resolution) ---
        if (!commandHandled && !commandWord.equals("status") && !commandWord.equals("help") && !commandWord.equals("look") && !commandWord.equals("save")) {
            advanceTime(10); // 10 minutes per action
        }


        switch (commandWord) {
            case "take" -> takeItem(command);
            case "drop" -> dropItem(command);
            case "use" -> interactItem(command); 
            case "write" -> writePaper(); 
            case "status" -> System.out.println(String.format("Status: Time: %s | Sleep Level: %d | Word Count: %d/4000", formatTime(), player.getSleepLevel(), player.getWordCount()));
            case "help" -> printHelp();
            case "go" -> goRoom(command);
            case "look" -> lookAround(); 
            case "repair" -> repairCoffeeMachine(); 
            case "talk" -> talkMax(command);
            case "swipe" -> swipeCard();
            case "submit" -> { return submitPaper(); } 
            case "cheat" -> { return cheat(); } 
            case "save" -> { /* Handled in play() loop */ }
            case "close", "mute" -> { 
                // If it wasn't handled by the distraction check above, it's just a normal command
                if (!commandHandled) { 
                    System.out.println("Nothing needs closing or muting right now.");
                }
            }
            case "quit" -> {
                if (command.hasSecondWord()) {
                    System.out.println("Quit what?");
                    return false;
                } else {
                    return true;
                }
            }
            default -> {
                if (!commandHandled) {
                    System.out.println("I don't know what you mean...");
                }
            }
        }
        
        if (player.getSleepLevel() <= 0) {
            System.out.println("\n*** You fell asleep on your keyboard from exhaustion! Game Over. ***");
            return true;
        }
        
        return false; 
    }

    public boolean submitPaper() {
        if (player.getWordCount() < 4000) {
            System.out.println("\n[SUBMISSION FAILED] You are not finished! Word Count is only " + player.getWordCount() + "/4000.");
            return false;
        }
        System.out.println("\n||              SUBMITTED PAPER! VICTORY!             ||");
        System.out.println("\nYou click 'Submit' just as the sun begins to rise at " + formatTime() + ".");
        return true; 
    }
    
    public boolean cheat() {
        player.setWordCount(4000);
        System.out.println("\n*** CHEAT CODE ACTIVATED: OVERRIDE SUBMIT ***");
        System.out.println("The professor won't know what hit them. VICTORY!");
        return true; 
    }

    public void resolveDistraction(String inputCommand) {
        // This is only called if isDistracted is true and inputCommand is 'close' or 'mute'
        if (requiredCommand != null && inputCommand.equalsIgnoreCase(requiredCommand)) {
            System.out.println("\n[DISTRACTION RESOLVED] That was fast! You enter **Hyperfocus**!");
            isDistracted = false;
            isHyperfocused = true;
            hyperfocusRemainingTurns = 5; 
            requiredCommand = null;
        } else {
            System.out.println("\n[DISTRACTION FAILURE] Wrong command! You wasted 15 minutes.");
            advanceTime(15); 
            isDistracted = false;
            requiredCommand = null;
        }
    }

    public void talkMax(Command command) {
        if (player.getCurrentRoom().getDescription().contains("dormitory corridor") && command.getSecondWord() != null && command.getSecondWord().equalsIgnoreCase("max")) {
            if (isMaxPlacated) {
                System.out.println("Max grunts from his room. He's still busy.");
                return;
            }
            Item snack = player.getItem("Snack");
            if (snack != null) {
                System.out.println("You offer the " + snack.getName() + " to Max.");
                player.removeItem(snack);
                isMaxPlacated = true;
                System.out.println("Max clears the path to the Gamer Room.");
            } else {
                System.out.println("Max is blocking your way. He looks hungry.");
            }
        } else {
            System.out.println("You talk to yourself awkwardly.");
        }
    }

    public void swipeCard() {
        if (player.getCurrentRoom().getDescription().contains("dormitory corridor") || player.getCurrentRoom().getDescription().contains("campus library")) {
            if (isLibraryUnlocked) {
                System.out.println("The lock is already green.");
                return;
            }
            Item studentID = player.getItem("StudentID");
            if (studentID != null) {
                isLibraryUnlocked = true;
                System.out.println("You swipe your StudentID card. The library door is now unlocked!");
            } else {
                System.out.println("You try swiping your hand, but that doesn't work.");
            }
        } else {
            System.out.println("There is nothing here to swipe.");
        }
    }

    public void repairCoffeeMachine() {
        if (player.getCurrentRoom().getDescription().contains("Study Lounge")) {
            if (isCoffeeMachineFixed) {
                System.out.println("The coffee machine is already fixed.");
            } else if (player.getItem("Filter") != null) {
                isCoffeeMachineFixed = true;
                player.removeItem(player.getItem("Filter"));
                // Add the snack dynamically after repair
                player.getCurrentRoom().addItem(new Snack("Snack", "A sugary energy bar, slightly crushed.", 10));
                System.out.println("Success! The coffee machine is operational. A *Snack* has appeared!");
            } else {
                System.out.println("You can't fix the machine without a replacement filter.");
            }
        } else {
            System.out.println("There's nothing here that needs repairing.");
        }
    }

    public void interactItem(Command command) {
        if (!command.hasSecondWord()) {
            if (player.getCurrentRoom().getDescription().contains("Study Lounge") && isCoffeeMachineFixed) {
                player.setSleepLevel(player.getSleepLevel() + 50); 
                System.out.println("You pour yourself a cup of fresh, hot coffee. (+50 Sleep)");
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
        
        // Note: Runtime check for Consumable is necessary here
        if (item instanceof Consumable consumable) {
            consumable.consume(player); 
        } else {
            item.interact(player);
        }
    }

    public void lookAround() {
        System.out.println(player.getCurrentRoom().getLongDescription());
    }
    
    public void writePaper() {
        if (player.getItem("Laptop") != null) {
            if (isHyperfocused) {
                int newWords = 200; // Bonus words for hyperfocus
                player.setWordCount(player.getWordCount() + newWords);
                System.out.println("\n*** HYPERFOCUS BONUS! +200 words added! ***");
                hyperfocusRemainingTurns--;
                if (hyperfocusRemainingTurns <= 0) {
                    isHyperfocused = false;
                    System.out.println("Hyperfocus faded.");
                } else {
                    System.out.println("Hyperfocus remaining: " + hyperfocusRemainingTurns + " turns.");
                }
            }
            player.write();
        } else {
            System.out.println("You can't write without your Laptop!");
        }
    }

    public void printHelp() {
        System.out.println("Type 'save' to save the game state.");
        parser.showCommands();
        System.out.println();
    }

    public void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Go where?");
            return;
        }
        String direction = command.getSecondWord();
        Room nextRoom = player.getCurrentRoom().getExit(direction);
        

        if (nextRoom == null) {
            System.out.println("There is no door!");
            return;
        }

        String nextRoomName = nextRoom.getDescription(); 

        if (nextRoomName.contains("campus library") && !isLibraryUnlocked) {
            System.out.println("The library door is locked. You need to swipe your StudentID to enter.");
            return;
        }
        if (nextRoomName.contains("Max's Gamer Room") && !isMaxPlacated) {
            System.out.println("Max is blocking the entrance. Maybe you can 'talk max'?");
            return;
        }
        if (nextRoomName.contains("Research Stacks") && !isLibrarianGone) { 
             System.out.println("The Librarian glares at you. You can't go there until 1:00 AM.");
             return;
        }

        player.setCurrentRoom(nextRoom);
        System.out.println(player.getCurrentRoom().getLongDescription());
    }

    public void takeItem(Command command) {
        if (!command.hasSecondWord()) { 
            System.out.println("Take what?"); 
            return; 
        }
        
        String inputItemName = command.getSecondWord();
        Item itemToTake = null;
        String officialItemName = null;

        // --- FIX: Case-Insensitive Search (Requires getItems() in Room.java) ---
        // We iterate through all items in the room to find a case-insensitive match.
        Map<String, Item> roomItems = player.getCurrentRoom().getItems();
        
        for (Map.Entry<String, Item> entry : roomItems.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(inputItemName)) {
                officialItemName = entry.getKey(); // Get the correct, cased name for lookup
                itemToTake = entry.getValue();
                break;
            }
        }

       // 2. Handle dynamic snack taking (using the case-insensitive official name)
        if (officialItemName != null && officialItemName.equalsIgnoreCase("Snack") && player.getCurrentRoom().getDescription().contains("Study Lounge")) {
            if (!isCoffeeMachineFixed) {
                System.out.println("Nothing to take here.");
                return;
            }
            if (isSnackTaken) {
                System.out.println("You already took the snack.");
                return;
            }
            
            Item item = player.getCurrentRoom().takeItem(officialItemName);
            if (item != null) {
                player.addItem(item); 
                isSnackTaken = true;
                System.out.println("You grabbed the " + officialItemName + " from the hidden compartment!");
                return;
            }
        }
        
        // 3. Normal item taking logic
        if (itemToTake != null) { 
            // We use the officially cased name (officialItemName) to remove it from the room
            Item item = player.getCurrentRoom().takeItem(officialItemName); 
            if (item != null) {
                 player.addItem(item); 
                 System.out.println("You picked up the " + item.getName() + "."); 
            } else {
                 System.out.println("Error taking the item from the room.");
            }
        } 
        else { 
            System.out.println("There is no such item here."); 
        }
    }

    public void dropItem(Command command) {
        if (!command.hasSecondWord()) { System.out.println("Drop what?"); return; }
        String itemName = command.getSecondWord();
        Item item = player.getItem(itemName);
        if (item == null) { System.out.println("You don't have that item."); } 
        else { player.removeItem(item); player.getCurrentRoom().addItem(item); System.out.println("You dropped the " + itemName + "."); }
    }
    public int getWordCount() {
    return player.getWordCount();
    }

    public int getSleepLevel() {
        return player.getSleepLevel();
    } 
    public String currentRoomString() {
        return player.getCurrentRoom().getLongDescription();
}
    public String getCurrentRoomName() {
        return player.getCurrentRoom().getDescription().toLowerCase();
    }
    public static class CommandResult {
    public final String output;
    public final boolean finished;
    public CommandResult(String output, boolean finished) {
        this.output = output;
        this.finished = finished;
    }
}

/**
 * Run a textual command through the game's parser and logic while capturing
 * all System.out output produced during the call. Returns printed output
 * and the boolean result (true == game finished).
 *
 * Use this from a GUI so printed lines are visible in the UI.
 */
public CommandResult processCommandAndCapture(String commandLine) {
    // Temporarily capture System.out
    PrintStream originalOut = System.out;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream capturing = new PrintStream(baos);
    System.setOut(capturing);

    boolean finished = false;
    try {
        finished = processCommandString(commandLine);
    } catch (Exception e) {
        // make sure exceptions are visible in GUI
        e.printStackTrace(capturing);
        finished = true; // treat exception as fatal to avoid further calls
    } finally {
        // restore standard out
        System.setOut(originalOut);
        try { capturing.flush(); } catch (Exception ignored) {}
    }

    String captured = baos.toString();
    return new CommandResult(captured, finished);
}


    public static void main(String[] args) {
        // Attempt to load the game first
        ZorkULGame game = GamePersistence.loadGame();
        
        // If loading failed, start a new game
        if (game == null) {
            System.out.println("\n--- Starting New Game ---");
            game = new ZorkULGame();
        }

        game.play();
    }  
    
    // --- Placeholder Classes for Compilation ---
    // These must be defined for ZorkULGame to compile, 
    // but the full definitions are provided in ItemSubclasses.java
    
    static class SimpleItem extends Item implements Serializable {
        public static final long serialVersionUID = 1L;
        public SimpleItem(String name, String description) { super(name, description); }
        @Override public void interact(Player player) { System.out.println("You examine the " + getName() + "."); }
        
        /** Serialization Factory Method */
        public static SimpleItem fromSaveableMap(Map<String, Object> map) {
            String name = (String) map.get("name");
            String description = (String) map.get("description");
            return new SimpleItem(name, description);
        }
    }
    
    static class Laptop extends Item implements Serializable {
        public static final long serialVersionUID = 1L;
        public Laptop(String name, String description) { super(name, description); }
        @Override public void interact(Player player) { System.out.println("The " + getName() + " is currently open to your unfinished paper."); }

        /** Serialization Factory Method */
        public static Laptop fromSaveableMap(Map<String, Object> map) {
            String name = (String) map.get("name");
            String description = (String) map.get("description");
            return new Laptop(name, description);
        }
    }
    
    static class Snack extends Item implements Consumable, Serializable {
        public static final long serialVersionUID = 1L;
        public final int sleepEffect = 10; 
        public Snack(String name, String description, int sleepEffect) { super(name, description); }
        @Override public int getSleepBoostAmount() { return sleepEffect; }
        @Override public void consume(Player player) { player.setSleepLevel(player.getSleepLevel() + getSleepBoostAmount()); System.out.println("You gulp down the stale coffee residue. (+" + getSleepBoostAmount() + " Sleep)"); player.removeItem(this); }
        @Override public void interact(Player player) { System.out.println("This " + getName() + " is a sugary treat. Use it to eat it."); }

        /** Serialization Factory Method */
        public static Snack fromSaveableMap(Map<String, Object> map) {
            String name = (String) map.get("name");
            String description = (String) map.get("description");
            // Since sleepEffect is hardcoded to 10 in the class, we pass that value to the constructor
            return new Snack(name, description, 10);
        }
    }
    static class ColdCoffee extends Item implements Consumable, Serializable {
        public static final long serialVersionUID = 1L;
        private final int sleepEffect = 10; 
        public ColdCoffee(String name, String description) { super(name, description); }
        @Override public int getSleepBoostAmount() { return sleepEffect; }
        @Override public void consume(Player player) { player.setSleepLevel(player.getSleepLevel() + getSleepBoostAmount()); System.out.println("You gulp down the stale coffee residue. (+" + getSleepBoostAmount() + " Sleep)"); player.removeItem(this); }
        @Override public void interact(Player player) { System.out.println("This " + getName() + " is old and cold. Use it to drink it."); }

        /** Serialization Factory Method */
        public static ColdCoffee fromSaveableMap(Map<String, Object> map) {
            String name = (String) map.get("name");
            String description = (String) map.get("description");
            // The ColdCoffee constructor only takes two arguments
            return new ColdCoffee(name, description);
        }
    }
}