package zorkul.core;
//to handle saving and loading of the game state using java built in serialisation (ObjectOutputStream/ObjectInputStream)
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * Utility class for saving and loading the entire ZorkULGame state.
 * This version utilizes standard Java Serialization to handle complex
 * game objects, replacing the manual (and simulated) JSON/Map approach.
 * * NOTE: For this to work, ZorkULGame, Player, Room, Item, and all 
 * Item subclasses MUST implement the 'java.io.Serializable' interface.
 */
public class GamePersistence {
    private static final String SAVE_FILE = "zorkul_save.dat"; // Changed extension for binary serialization

    // --- Actual Persistence Logic using Serialization ---

    /**
     * Saves the current game state to the disk using Java Serialization.
     * The entire object graph, starting from the ZorkULGame instance, is saved.
     * @param game The ZorkULGame instance to save (MUST be Serializable).
     */
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

    /**
     * Loads the game state from the disk using Java Serialization.
     * @return A new ZorkULGame instance loaded from the save file, or null if loading fails/file is missing.
     */
    public static ZorkULGame loadGame() {
        try (FileInputStream fileIn = new FileInputStream(SAVE_FILE);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
             
            ZorkULGame game = (ZorkULGame) in.readObject();
            
            System.out.println("----------------------------------------");
            System.out.println("Game Loaded Successfully using Serialization!");
            System.out.println("----------------------------------------");
            return game;

        } catch (IOException | ClassNotFoundException e) {
            // File not found (IOException) or Class definition changed (ClassNotFoundException)
            if (e instanceof IOException && e.getMessage().contains("No such file")) {
                 System.out.println("No save file found. Starting new game.");
            } else {
                 System.err.println("Error loading game via Serialization: " + e.getMessage());
                 
            }
            return null;
        }
    }
    
    // --- Manual I/O methods removed as they are no longer needed for Serialization ---
    // The previous JSON utility and file reading helpers are removed.

}