//made interface to define the resource mechanics

public interface Consumable {
    //amount of sleep level restored
    int getSleepBoostAmount();

    //logic to modify users status when consumed
    void consume(Player player);
}
