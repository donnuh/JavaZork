package zorkul.world;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import zorkul.items.Item;


public class Player implements Serializable { //renamed from Character
    private static final long serialVersionUID = 1L;

    private Room currentRoom;
    private final Map<String, Item> inventory;
    private int sleepLevel;
    private int wordCount; 


    public Player(String name, Room startRoom) {
        this.currentRoom = startRoom;
        this.inventory = new HashMap<>();
        this.sleepLevel = 100; 
        this.wordCount = 0;
    }
     public void write() {
        int words = 100; 
        this.wordCount += words;
        this.sleepLevel -= 5; 
        System.out.println("You wrote " + words + " words. Total: " + wordCount + "/4000. (-5 sleep)");
    }

    public void addItem(Item item) {
        inventory.put(item.getName(), item);
    }

    public void removeItem(Item item) {
        inventory.remove(item.getName());
    }

    public Item getItem(String name) {
        return inventory.get(name);
    }

    public String showInventoryAndReturnMessage() {
        if (inventory.isEmpty()) {
            return "Inventory: empty.";
        }
        StringBuilder sb = new StringBuilder("Inventory:");
        for (String itemName : inventory.keySet()) {
            sb.append(" ").append(itemName);
        }
        return sb.toString();
    }

    //Accessors/Mutators
    
    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public int getSleepLevel() {
        return sleepLevel;
    }

    public void setSleepLevel(int sleepLevel) {
        this.sleepLevel = Math.min(sleepLevel, 100); // Max cap at 100
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }
}