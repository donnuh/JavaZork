//edited to become the abstract base class that defines all objects in the world

public abstract class Item { // Now abstract
    protected String name; // Changed to protected
    protected String description; // Changed to protected
    // Removed location, id, isVisible as these are handled by Room/Inventory lists

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // New: Abstract method for general interaction (like examine or use)
    public abstract void interact(Player player); // Required by Item.java
}