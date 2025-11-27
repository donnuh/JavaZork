import java.util.ArrayList;

public class Player { // Renamed from Character
    private final String name;
    private Room currentRoom;
    private final ArrayList<Item> inventory; // Item is now the abstract base class
    
    // --- New Game Attributes ---
    private int sleepLevel; // 0-100
    private int wordCount;  // 0-4000

    public Player(String name, Room startingRoom) {
        this.name = name;
        this.currentRoom = startingRoom;
        this.inventory = new ArrayList<>();
        this.sleepLevel = 80; // Start with decent energy
        this.wordCount = 0;
    }
    
    // --- New Game Methods ---
    
    public void write() {
        if (wordCount < 4000) {
            int wordsWritten = (sleepLevel < 10) ? 25 : 50; // Simple sleep penalty
            this.wordCount += wordsWritten;
            if (this.wordCount > 4000) this.wordCount = 4000;
            System.out.println("You wrote " + wordsWritten + " words. Word Count: " + this.wordCount + "/4000");
        } else {
            System.out.println("The paper is complete! Time to submit.");
        }
    }

    // --- Core Action Methods ---
    // (Keep move, addItem, removeItem, getItem, showInventory from original Character.java)
    
    public void move(String direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            currentRoom = nextRoom;
            System.out.println("You moved to: " + currentRoom.getDescription());
        } else {
            System.out.println("You can't go that way!");
        }
    }
    
    public void addItem(Item item) {
        inventory.add(item);
    }

    public void removeItem(Item item) {
        inventory.remove(item);
    }
    
    public Item getItem(String itemName) {
        for (Item i : inventory) {
            if (i.getName().equalsIgnoreCase(itemName)) {
                return i;
            }
        }
        return null;
    }
    
    public void showInventory() {
        if (inventory.isEmpty()) {
            System.out.println("You are not carrying anything.");
        } else {
            System.out.print("You are carrying: ");
            for (Item i : inventory) {
                System.out.print(i.getName() + " ");
            }
            System.out.println();
        }
    }
    

    // --- Getters and Setters ---
    public int getSleepLevel() { 
        return sleepLevel; 
    }
    public void setSleepLevel(int level) { 
        // Ensure level stays between 0 and 100
        this.sleepLevel = Math.max(0, Math.min(100, level)); 
    }
    public int getWordCount() { 
        return wordCount; 
    }
    public void setWordCount(int count) { 
        this.wordCount = count; 
    }
    
    public String getName() { 
        return name; 
    }
    public Room getCurrentRoom() { 
        return currentRoom; 
    }
    public void setCurrentRoom(Room room) { 
        this.currentRoom = room;
    }

}
