package zorkul.core;
//to handle saving and loading of the game state using java built in serialisation (ObjectOutputStream/ObjectInputStream)
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class GamePersistence {
    public static final String SAVE_FILE = "zorkul_save.dat"; //Changed extension for binary serialization

    public static void saveGame(ZorkULGame game) {
        try (FileOutputStream fileOut = new FileOutputStream(SAVE_FILE);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
             
            out.writeObject(game);

            System.out.println("----------------------------------------");
            System.out.println("Game Saved Successfully using Serialization to " + SAVE_FILE);
            System.out.println("----------------------------------------");
        } catch (IOException i) {
            System.err.println("Error saving game via Serialization: " + i.getMessage());
            
        }
    }

    public static ZorkULGame loadGame() {
        try (FileInputStream fileIn = new FileInputStream(SAVE_FILE);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
             
            ZorkULGame game = (ZorkULGame) in.readObject();
            
            System.out.println("----------------------------------------");
            System.out.println("Game Loaded Successfully using Serialization!");
            System.out.println("----------------------------------------");
            return game;

        } catch (IOException | ClassNotFoundException e) {
            if (e instanceof IOException && e.getMessage().contains("No such file")) {
                 System.out.println("No save file found. Starting new game.");
            } else {
                 System.err.println("Error loading game via Serialization: " + e.getMessage());
                 
            }
            return null;
        }
    }
}