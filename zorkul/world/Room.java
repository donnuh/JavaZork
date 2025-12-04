package zorkul.world;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import zorkul.items.Item;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String description;
    private final Map<String, Room> exits;
    private final Map<String, Item> items;

    public Room(String description) {
        this.description = description;
        this.exits = new HashMap<>();
        this.items = new HashMap<>();
    }
    
    // NOTE: Manual I/O methods (fromSaveableMap, toSaveableMap) have been removed.

    // --- Accessors and Mutators ---

    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    public Room getExit(String direction) {
        return exits.get(direction);
    }

    public String getDescription() {
        return description;
    }

    public String getLongDescription() {
        return "You are " + description + ".\n" + getExitString() + "\n" + getItemString();
    }

    private String getExitString() {
        StringBuilder returnString = new StringBuilder("Exits:");
        for (String exit : exits.keySet()) {
            returnString.append(" ").append(exit);
        }
        return returnString.toString();
    }

    public void addItem(Item item) {
        items.put(item.getName(), item);
    }

    public Item takeItem(String itemName) {
        return items.remove(itemName);
    }
    
    public Item getItem(String itemName) {
        return items.get(itemName);
    }

    private String getItemString() {
        if (items.isEmpty()) {
            return "There are no items here.";
        }
        StringBuilder returnString = new StringBuilder("Items:");
        for (String itemName : items.keySet()) {
            returnString.append(" ").append(itemName);
        }
        return returnString.toString();
    }
    public Map<String, Item> getItems() {
    return items; // Assumes 'items' is the name of your item map field
}
}