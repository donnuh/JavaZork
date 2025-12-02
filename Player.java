import java.util.HashMap;
import java.util.Map;

public class Player { //renamed from Character
    private Room currentRoom;
    private final Map<String, Item> inventory;
    private int sleepLevel;
    private int wordCount; 

    public Player(String name, Room startingRoom) {
        this.currentRoom = startingRoom;
        this.inventory = new HashMap<>();
        this.sleepLevel = 100; 
        this.wordCount = 0;
    }
    
    public void write() {
        int wordsWritten = 200; //dfault in dorm/lounge
        int sleepCost = 15;     
        String locationName;

        //check if ur in research stacks, highest efficiency
        if (currentRoom.getDescription().contains("Research Stacks")) {
            wordsWritten = 500; 
            sleepCost = 10;     
            locationName = "the quiet Stacks";
        } 
        //check if ur in max's gamer room
        else if (currentRoom.getDescription().contains("Gamer Room")) {
            wordsWritten = 300; 
            sleepCost = 13;     
            locationName = "Max's noisy room";
        } else {
            locationName = "your current noisy location";
        }

        wordCount += wordsWritten;
        sleepLevel -= sleepCost;
        
        System.out.println("You write feverishly in " + locationName + " for a while. Paper now at " + wordCount + " words.");
        System.out.println("You lost " + sleepCost + " sleep points. Current Sleep Level: " + sleepLevel);
    }
    
    
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
        inventory.put(item.getName().toLowerCase(), item);
    }

    public void removeItem(Item item) {
        inventory.remove(item.getName().toLowerCase());
    }
    
    public Item getItem(String itemName) {
        return inventory.get(itemName.toLowerCase());
    }
    
    public void showInventory() {
        if (inventory.isEmpty()) {
            System.out.println("Inventory: (Empty)");
            return;
        }
        StringBuilder sb = new StringBuilder("Inventory:");
        for (Item item : inventory.values()) {
            sb.append(" ").append(item.getName());
        }
        System.out.println(sb.toString());
        }

        public Room getCurrentRoom() {
            return currentRoom;
        }
         
        public Room setCurrentRoom(Room room) {
            return currentRoom = room;
        }

        public int getSleepLevel() {
            return sleepLevel;
        }

        public void setSleepLevel(int level) {
            this.sleepLevel = Math.max(0, Math.min(100, level)); // Keep between 0 and 100
        }
        public int getWordCount() {
        return wordCount;
    }
    public void setWordCount(int count) {
        this.wordCount = count;
    }
}





