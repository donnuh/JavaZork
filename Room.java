import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Room {
    private final String description;
    private final Map<String, Room> exits; // Map direction to neighboring Room
    private final ArrayList<Item> items; // Items in the room

    public Room(String description) {
        this.description = description;
        exits = new HashMap<>();
        items = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    public Room getExit(String direction) {
        return exits.get(direction);
    }

    public String getExitString() {
        StringBuilder sb = new StringBuilder();
        for (String direction : exits.keySet()) {
            sb.append(direction).append(" ");
        }
        return sb.toString().trim();
    }

    
    public void addItem(Item item) {
        items.add(item);
    }

    public String getItemString() {
        if (items.isEmpty()) {
            return "There are no items here.";
        }
        StringBuilder itemList = new StringBuilder("Items in the room: ");
        for (Item item : items) {
            
                itemList.append(item.getName())
                        .append(" - ")
                        .append(item.getDescription())
                        .append(". ");
            }
        
        return itemList.toString();
    }


    public String getLongDescription() {
        return "You are " + description + ".\nExits: " + getExitString() + "\n" + getItemString();
    }
    public Item takeItem(String itemName) {
    for (Item i : items) {
        if (i.getName().equalsIgnoreCase(itemName)) {
            items.remove(i);
            return i;
        }
    }
    return null;
}
}