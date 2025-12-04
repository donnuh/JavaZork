package zorkul.items;
//edited to become the abstract base class that defines all objects in the world

import java.io.Serializable;

import zorkul.world.Player;

public abstract class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void interact(Player player);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

