public class ZorkULGame {
    private final Parser parser;
    private Player player;

    private boolean isCoffeeMachineFixed = false; 
    private boolean isSnackTaken = false;
    private int gameTimeHours = 22;
    private int gameTimeMinutes = 0;

    private boolean isLibraryUnlocked = false;
    private boolean isMaxPlacated = false;
    private boolean isLibrarianGone = false; 

    //socmed distraction states
    private boolean isDistracted = false; 
    private boolean isHyperfocused = false;
    private int hyperfocusRemainingTurns = 0; //duration of boost 
    private String requiredCommand = null;

    public ZorkULGame() {
        createRooms();
        parser = new Parser();
    }
    private String formatTime() {
        int displayHour = gameTimeHours % 12;
        if (displayHour == 0) displayHour = 12;
        String amPm = (gameTimeHours >= 12 && gameTimeHours < 24) ? "PM" : "AM";
        return String.format("%d:%02d %s", displayHour, gameTimeMinutes, amPm);
    }
    
    private void advanceTime(int minutes) {
        int oldHour = gameTimeHours;
        
        int newTotalMinutes = gameTimeMinutes + minutes;
        gameTimeMinutes = newTotalMinutes % 60;
        
        int hoursToAdd = newTotalMinutes / 60;
        gameTimeHours = (gameTimeHours + hoursToAdd) % 24;
        
        System.out.println("\n[It is now " + formatTime() + "]");

        //check if the time crossed 1:00 AM (hour 1)
        if (gameTimeHours == 1 && oldHour != 1 && !isLibrarianGone) {
            isLibrarianGone = true;
            System.out.println("\n*** The clock strikes 1:00 AM. The Librarian silently packs up and leaves! ***");
            System.out.println("*** The Research Stacks are now open. ***");
        }
        //random socmed distraction make it 15% chance per action 
        boolean isAllNighterActive = (gameTimeHours >= 22 || gameTimeHours <= 7);
        if (!isDistracted && !isHyperfocused && isAllNighterActive) {
            if (Math.random() < 0.15) { //15% chance
                isDistracted = true;
                
                // Randomly choose the required command
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
        //check for deadline (8:00 AM or 08:00)
        if (gameTimeHours >= 8 && gameTimeHours < 22 && player.getWordCount() < 4000) {
            System.out.println("\n*** 8:00 AM has arrived! Your paper is not finished. You failed! ***");
            System.exit(0);
        }
    }

    private void createRooms() {
        Room dorm, corridor, lounge, library, maintenance, gamerRoom, researchStacks;
       
        dorm = new Room("in your dorm room. Your desk is covered in papers and a glowing laptop.");
        corridor = new Room("in the dormitory corridor. You can hear muffled video gamenoises from other rooms.");
        lounge = new Room("in the Study Lounge. A cold, uncomfortable room filled with uncomfortable chairs and stale air. There is a broken coffee machine on a counter.");
        library = new Room("in the campus library. Rows of books stretch out.");
        maintenance = new Room("in a dusty Maintenance Closet. It smells faintly of bleach and mildew.");
        gamerRoom = new Room("in Max's Gamer Room. The walls glow with RGB light and the sound of mechanical keyboards fills the air.");
        researchStacks = new Room("in the deep, dusty Research Stacks of the library. It is quiet here, perfect for focus.");


        ColdCoffee coffeeMug = new ColdCoffee("CoffeeMug", "A mug with stale coffee residue.");
        SimpleItem replacementFilter = new SimpleItem("Filter", "A brand new, clean coffee filter. Seems to be the right size for the machine.");
        SimpleItem studentID = new SimpleItem("StudentID", "Your university ID card. You need this to get into locked campus buildings.");
        //Snack snack = new Snack("Snack", "A sugary energy bar. Max might like this.", 10); //sleep boost of 10

        maintenance.addItem(replacementFilter); 
        dorm.addItem(new Laptop("Laptop", "Your trusty (and slightly battered) research machine."));
        dorm.addItem(coffeeMug);
        dorm.addItem(studentID); 
        //library.addItem(snack); 
        
   
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

        //advance time before command execution cs most actions take time
        if (!commandWord.equals("status") && !commandWord.equals("help") && !commandWord.equals("look")) {
            advanceTime(10); // 10 minutes per action
            
            //handle socmed distraction check
            if (isDistracted && !commandWord.equalsIgnoreCase("close") && !commandWord.equalsIgnoreCase("mute")) {
                 System.out.println("\n[DISTRACTION FAILURE] You ignored the alert and wasted 15 minutes!");
                 advanceTime(15); 
                 isDistracted = false;
                 requiredCommand = null;
            }
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
            case "talk" -> talkMax(command);
            case "swipe" -> swipeCard();
            case "close" -> resolveDistraction("CLOSE");
            case "mute" -> resolveDistraction("MUTE");
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
        
        //win condition check
        if(player.getWordCount() >= 4000) {
            System.out.println("\n Hooray! You finished the paper! Now SUBMIT!");
            return true;
        }
        //death condition check
        if (player.getSleepLevel() <= 0) {
            System.out.println("\n*** You fell asleep at your keyboard from exhaustion! Game Over. ***");
            return true;
        }
        
        return false; 
    }

    private void resolveDistraction(String inputCommand) {
        if (!isDistracted) {
            System.out.println("You are not currently distracted.");
            return;
        }
        
        if (requiredCommand != null && inputCommand.equalsIgnoreCase(requiredCommand)) {
            //if success
            System.out.println("\n[DISTRACTION RESOLVED] That was fast! You successfully silenced the noise.");
            System.out.println("You enter a state of **Hyperfocus**! Your writing speed is temporarily boosted.");
            
            isDistracted = false;
            isHyperfocused = true;
            hyperfocusRemainingTurns = 5; 
            requiredCommand = null;
            
        } else {
            System.out.println("\n[DISTRACTION FAILURE] Wrong command! You wasted 15 minutes scrolling through memes.");
            advanceTime(15); 
            isDistracted = false;
            requiredCommand = null;
        }
    }
    private void talkMax(Command command) {
        if (player.getCurrentRoom().getDescription().contains("dormitory corridor") && command.getSecondWord() != null && command.getSecondWord().equalsIgnoreCase("max")) {
            if (isMaxPlacated) {
                System.out.println("Max grunts from his room. He's still busy, but he won't block your way.");
                return;
            }
            
            //check for Snack item
            Item snack = player.getItem("Snack");
            if (snack instanceof Snack) {
                System.out.println("You offer the " + snack.getName() + " to Max who is blocking the way");
                System.out.println("Max happily takes the snack and steps aside, allowing you to pass.");
                player.removeItem(snack);
                isMaxPlacated = true;
                System.out.println("Max shoves the snack in his face and slumps back into his chair, clearing the path to the Gamer Room.");
            } else {
                System.out.println("Max is blocking your way. He looks hungry. Maybe if you had something to offer him...");
            }
        } else {
            System.out.println("You talk to yourself awkwardly.");
        }
    }

    private void swipeCard() {
        if (player.getCurrentRoom().getDescription().contains("dormitory corridor") || player.getCurrentRoom().getDescription().contains("campus library")) {
            if (isLibraryUnlocked) {
                System.out.println("The lock is already green. You don't need to swipe again.");
                return;
            }
            
            //check for Student ID item
            Item studentID = player.getItem("StudentID");
            if (studentID != null) {
                isLibraryUnlocked = true;
                System.out.println("You swipe your StudentID card. The lock flashes green and emits a satisfying *BEEP*. The library door is now unlocked!");
            } else {
                System.out.println("You try swiping your hand, but that doesn't work. You need an access card.");
            }
        } else {
            System.out.println("There is nothing here to swipe.");
        }
    }

    //handles repair command
     private void repairCoffeeMachine() {
        if (player.getCurrentRoom().getDescription().contains("Study Lounge")) { 
            if (isCoffeeMachineFixed) {
                System.out.println("The coffee machine is already fixed. You can 'use' it now to get fresh coffee!");
            } else if (player.getItem("Filter") != null) {
                // fix the machine
                isCoffeeMachineFixed = true;
                player.removeItem(player.getItem("Filter"));
                
                // Add the snack to the room's visible items list temporarily for the 'take' command
                player.getCurrentRoom().addItem(new Snack("Snack", "A sugary energy bar, slightly crushed. It was hidden behind the filter compartment.", 10));
                
                System.out.println("Success! The coffee machine is operational. You notice a small, crushed box hidden in the filter compartment. A *Snack* has appeared!");
                System.out.println("You can now 'use' the machine for coffee, and perhaps 'take Snack'.");
            } else {
                System.out.println("You can't fix the machine without a replacement filter. It looks like it needs a clean one.");
            }
        } else {
            System.out.println("There's nothing here that needs repairing.");
        }
    }

    private void interactItem(Command command) {
        if (!command.hasSecondWord()) {
            //check if player is in the lounge and wants to use the machine
            if (player.getCurrentRoom().getDescription().contains("Study Lounge") && isCoffeeMachineFixed) {
                //assume 'use' without a second word means use the fixed coffee machine
                player.setSleepLevel(player.getSleepLevel() + 50); 
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
        
        //1. check if Consumable (like coffee mug)
        if (item instanceof Consumable consumable) {
            consumable.consume(player); 
        } 
        //2. otherwise, use the general Item interact method
        else {
            item.interact(player);
        }
    }

    private void lookAround() {
        System.out.println(player.getCurrentRoom().getLongDescription());
        player.showInventory();
    }
    
    private void writePaper() {
        //Check if the player has the required item (Laptop) in their inventory
        if (player.getItem("Laptop") != null) {
            if (isHyperfocused) {
                int newWords = player.getWordCount() + 200; // +200 words bonus!
                player.setWordCount(newWords);
                
                System.out.println("\n*** HYPERFOCUS BONUS! +200 words added! ***");
                
                // Decrement hyperfocus turns
                hyperfocusRemainingTurns--;
                if (hyperfocusRemainingTurns <= 0) {
                    isHyperfocused = false;
                    System.out.println("Hyperfocus faded. Back to normal efficiency.");
                } else {
                    System.out.println("Hyperfocus remaining: " + hyperfocusRemainingTurns + " turns.");
                }
            }
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
        Room currentRoom = player.getCurrentRoom();
        Room nextRoom = currentRoom.getExit(direction);
        

        if (nextRoom == null) {
            System.out.println("There is no door!");
            return;
        }

        String nextRoomName = nextRoom.getDescription(); //used for checking locks

        //lock 1: library access
        if (nextRoomName.contains("campus library") && !isLibraryUnlocked) {
            System.out.println("The library door is locked. You need to swipe your StudentID to enter.");
            return;
        }
        //lock 2: Max's Gamer Room
        if (nextRoomName.contains("Max's Gamer Room") && !isMaxPlacated) {
            System.out.println("Max is blocking the entrance to his room. Maybe you can 'talk max'?");
            return;
        }
        //lock 3: librarian time barrier
        if (nextRoomName.contains("Research Stacks") && !isLibrarianGone) { 
             System.out.println("The Librarian glares at you from their desk, guarding the research stacks.");
             System.out.println("Librarian: 'No one is allowed in the stacks until I leave at 1:00 AM. Shoo!'");
             System.out.println("(Current time: " + formatTime() + ")");
             return;
        }

        player.setCurrentRoom(nextRoom);
        System.out.println(player.getCurrentRoom().getLongDescription());
    }

    private void takeItem(Command command) {
        if (!command.hasSecondWord()) { System.out.println("Take what?"); return; }
        String itemName = command.getSecondWord();

       //taking the snack in lounge
        if (itemName.equalsIgnoreCase("Snack") && player.getCurrentRoom().getDescription().contains("Study Lounge")) {
            if (!isCoffeeMachineFixed) {
                System.out.println("You look around, but all you see is a broken coffee machine. Nothing to take here.");
                return;
            }
            if (isSnackTaken) {
                System.out.println("You already took the snack from behind the filter compartment.");
                return;
            }
            // If fixed and not taken, proceed to take it normally, then set the flag.
            Item item = player.getCurrentRoom().takeItem(itemName);
            if (item != null) {
                player.addItem(item); 
                isSnackTaken = true;
                System.out.println("You grabbed the " + itemName + " from the hidden compartment!");
                return;
            }
        }
        
        //normal item taking logic
        Item item = player.getCurrentRoom().takeItem(itemName);
        if (item == null) { System.out.println("There is no such item here."); } 
        else { player.addItem(item); System.out.println("You picked up the " + itemName + "."); }
    }

    private void dropItem(Command command) {
        if (!command.hasSecondWord()) { System.out.println("Drop what?"); return; }
        String itemName = command.getSecondWord();
        Item item = player.getItem(itemName);
        if (item == null) { System.out.println("You don't have that item."); } 
        else { player.removeItem(item); player.getCurrentRoom().addItem(item); System.out.println("You dropped the " + itemName + "."); }
    }
        /**if (item == null) {
            System.out.println("There is no such item here.");
        } else {
            player.addItem(item);
            System.out.println("You picked up the " + itemName + ".");
        } **/   

    public static void main(String[] args) {
        ZorkULGame game = new ZorkULGame();
        game.play();
    }  
    
    static class SimpleItem extends Item {
        public SimpleItem(String name, String description) {
            super(name, description);
        }
        @Override
        public void interact(Player player) {
            System.out.println("You examine the " + getName() + ". It looks like a replacement part for a machine.");
        }
    }
    static class Laptop extends Item {

        public Laptop(String name, String description) {
            super(name, description);
        }

        @Override
        public void interact(Player player) {
            System.out.println("The " + getName() + " is currently open to your unfinished paper.");
            System.out.println("It seems to be working, but you should probably use the 'write' command.");
        }
    }
    static class Snack extends Item implements Consumable {
        private final int sleepEffect;
        
        //constructor matches Item n stores the sleep effect
        public Snack(String name, String description, int sleepEffect) {
            super(name, description);
            this.sleepEffect = sleepEffect;
        }
        
        //implement method from Consumable interface
        @Override
        public int getSleepBoostAmount() {
            return sleepEffect;
        }
        
        //implement method from Consumable interface
        @Override
        public void consume(Player player) {
             player.setSleepLevel(player.getSleepLevel() + getSleepBoostAmount());
             System.out.println("You quickly devour the " + getName() + ". It gives you a small sugar rush.");
             System.out.println("Sleep Level increased by " + getSleepBoostAmount() + " to " + player.getSleepLevel() + ".");
             player.removeItem(this); // Now works because Snack is an Item
        }
        
        //implement abstract method from Item class
        @Override
        public void interact(Player player) {
            System.out.println("This " + getName() + " is a sugary treat. You should 'use Snack' to eat it.");
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