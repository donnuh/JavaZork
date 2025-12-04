package zorkul.items;


import zorkul.world.Player;

public interface Consumable {
   
    int getSleepBoostAmount();
    void consume(Player player);
}
