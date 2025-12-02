import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Room {
    private final String description;
    private final Map<String, Room> exits; //map direction to neighboring Room
    private final Map<String, Item> items;

    public Room(String description) {
        this.description = description;
        this.exits = new HashMap<>();
        this.items = new HashMap<>();
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
    public String getShortDescription() {
        return description;
    }

    public String getExitString() {
        StringBuilder returnString = new StringBuilder();
        Set<String> keys = exits.keySet();
        for (String exit : keys) {
            returnString.append(" ").append(exit);
        }
        return returnString.toString();
    }
    public void addItem(Item item) {
        items.put(item.getName().toLowerCase(), item);
    }
    

    private String getItemString() {
        if (items.isEmpty()) {
            return "The room contains no items.";
        }
        StringBuilder returnString = new StringBuilder("Items here:");
        for (Item item : items.values()) {
            returnString.append(" ").append(item.getName()).append(" (").append(item.getDescription()).append(")");
        }
        return returnString.toString();
    }


    public String getLongDescription() {
        return "You are " + description + ".\nExits:" + getExitString() + "\n" + getItemString();
    }
   public Item takeItem(String itemName) {
        return items.remove(itemName.toLowerCase());
    }
}