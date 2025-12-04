package zorkul.items;
// ColdCoffee.java

import zorkul.world.Player;

public class ColdCoffee extends Item implements Consumable {

    private final int CAFFEINE_VALUE = 20;

    public ColdCoffee(String name, String description) {
        super(name, description);
    }
    
    @Override
    public void interact(Player player) { 
        System.out.println("You look at the " + getName() + ". It looks cold and vaguely depressing.");
    }

    @Override
    public int getSleepBoostAmount() {
        return CAFFEINE_VALUE;
    }

    @Override
    public void consume(Player player) { 
        player.setSleepLevel(player.getSleepLevel() + CAFFEINE_VALUE);
        System.out.println("You drink the cold coffee. Your sleep level is now " + player.getSleepLevel() + ".");
        player.removeItem(this);
        }
    }
