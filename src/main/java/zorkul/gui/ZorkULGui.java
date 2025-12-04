package zorkul.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import zorkul.core.ZorkULGame;
import zorkul.core.GamePersistence;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public class ZorkULGui extends Application {

    private static final String PROMPT = "\n> ";

    private ZorkULGame game;
    private TextArea outputArea;
    private TextField inputField;
    private ImageView backgroundImageView;
    private Label messageLabel;

    private final Map<String, String> roomImageMap = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        initializeRoomImages();

        outputArea = createOutputArea();
        inputField = createInputField();
        messageLabel = createMessageLabel();

        initializeGameAndPrintStatus();

        // 1. Root Container
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #222222;"); // Dark background for the app frame

        // 2. Game Viewport (Image + Terminal Overlay)
        StackPane gameViewport = createGameViewport();
        
        // 3. Controls (Buttons and Input)
        VBox controlsBox = createControlsBox();

        root.setCenter(gameViewport);
        root.setBottom(controlsBox);

        // Standard scene size 
        Scene scene = new Scene(root, 1000, 700); 
        
        // Bind image to scene size
        backgroundImageView.fitWidthProperty().bind(scene.widthProperty());
        backgroundImageView.fitHeightProperty().bind(gameViewport.heightProperty()); // Bind to its parent StackPane height

        primaryStage.setTitle("Uni All Nighter");
        primaryStage.setScene(scene);
        primaryStage.show();

        updateBackgroundImage();
        appendTextAndScroll(PROMPT);
        inputField.requestFocus();
    }

    private void initializeRoomImages() {
        // NOTE: These keys MUST perfectly match the lowercase, trimmed output of game.getCurrentRoomName().
        // I have cleaned up the keys slightly to remove trailing whitespace/newline issues.
        roomImageMap.put("in the dormitory corridor. you can hear muffled video gamenoises from other rooms.", "images/default.jpeg");
        roomImageMap.put("in your dorm room. your desk is covered in papers and a glowing laptop.", "images/dorm.jpeg");
        roomImageMap.put("in max's gamer room. the walls glow with rgb light and the sound of mechanical keyboards fills the air.", "images/gamerRoom.jpeg");
        roomImageMap.put("in the deep, dusty research stacks of the library. it is quiet here, perfect for focus.", "images/researchStacks.jpeg");
        roomImageMap.put("in a dusty maintenance closet. it smells faintly of bleach and mildew.", "images/maintenanceCloset.jpeg");
        roomImageMap.put("in the study lounge. a cold, uncomfortable room filled with uncomfortable chairs and stale air. there is a broken coffee machine on a counter.", "images/lounge.jpeg");
        roomImageMap.put("in the campus library. rows of books stretch out.", "images/library.jpeg"); 
        roomImageMap.put("fallback", "images/default.jpeg"); // Default/error image 
    }

    private TextArea createOutputArea() {
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setWrapText(true);
        
        // User-requested size (450x450)
        area.setPrefSize(650, 450); 
        area.setMaxSize(650, 450); 
        
        area.setStyle(
            // Use 'transparent' for the inner control background
            "-fx-control-inner-background: transparent;" + 
            // Set the text color to the user's request
            "-fx-text-fill:rgb(80, 79, 79);" + 
            "-fx-font-family: 'Consolas', 'Monospaced';" +
            "-fx-font-size: 14pt;" +
            // Ensure the border of the TextArea control itself is also transparent
            "-fx-background-color: transparent;" +
            "-fx-background-radius: 5px;"
        );
        
        return area;
    }

    private TextField createInputField() {
        TextField field = new TextField();
        field.setPromptText("Type your command...");
        field.setOnAction(e -> processCommand());
        field.setStyle(
            "-fx-control-inner-background: #333333;" + 
            "-fx-text-fill: #FFFFFF;" + 
            "-fx-font-family: 'Consolas', 'Monospaced';" +
            "-fx-font-size: 14pt;" +
            "-fx-prompt-text-fill: #888888;" +
            "-fx-background-radius: 5px;"
        );
        return field;
    }
    
    private Label createMessageLabel() {
        Label label = new Label();
        label.setStyle("-fx-text-fill: white; -fx-font-size: 12pt;");
        return label;
    }

    private StackPane createGameViewport() {
        backgroundImageView = new ImageView();
        backgroundImageView.setFitWidth(1000);
        backgroundImageView.setPreserveRatio(false); // Let the image stretch to fill the area
        backgroundImageView.setSmooth(true);

        // Container for the output area, centered over the image
        StackPane terminalOverlay = new StackPane(outputArea);
        terminalOverlay.setPadding(new Insets(20));
        
        StackPane gamePane = new StackPane();
        gamePane.getChildren().addAll(backgroundImageView, terminalOverlay);
        
        return gamePane;
    }

    private VBox createControlsBox() {
        HBox directions = createDirectionalButtons();

        // Style the action buttons
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveGameAction());

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> quitGameAction());
        
        // Standard button style
        String buttonStyle = "-fx-background-color: #555555; -fx-text-fill: white; -fx-padding: 8 16; -fx-font-weight: bold; -fx-background-radius: 5px;";
        saveButton.setStyle(buttonStyle);
        quitButton.setStyle(buttonStyle);
        
        HBox misc = new HBox(20, saveButton, quitButton, messageLabel);
        misc.setAlignment(Pos.CENTER);

        VBox box = new VBox(10, directions, inputField, misc);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 10, 20, 10)); // Extra padding at the bottom
        return box;
    }

    private HBox createDirectionalButtons() {
        Button north = new Button("North");
        Button south = new Button("South");
        Button east = new Button("East");
        Button west = new Button("West");
        
        String goButtonStyle = "-fx-background-color: #008CBA; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold; -fx-background-radius: 5px;";

        north.setStyle(goButtonStyle);
        south.setStyle(goButtonStyle);
        east.setStyle(goButtonStyle);
        west.setStyle(goButtonStyle);

        north.setOnAction(e -> sendDirection("go north"));
        south.setOnAction(e -> sendDirection("go south"));
        east.setOnAction(e -> sendDirection("go east"));
        west.setOnAction(e -> sendDirection("go west"));

        HBox box = new HBox(20, north, south, east, west);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private void initializeGameAndPrintStatus() {
        game = GamePersistence.loadGame();
        if (game == null) {
            game = new ZorkULGame();
            appendTextAndScroll("New game started.\n");
        } else {
            appendTextAndScroll("Game loaded successfully.\n");
        }
        
        // Use captureGameOutput to redirect printWelcome
        captureGameOutput(() -> game.printWelcome()); 
    }

    private void processCommand() {
        String command = inputField.getText().trim();
        if (command.isEmpty()) return;

        appendTextAndScroll(command + "\n");

        // The game logic will print output directly to System.out, which is captured.
        captureGameOutput(() -> {
            boolean finished = game.processCommandString(command);
            if (finished) {
                // Handle game finish state
                appendTextAndScroll("\n--- Game Over ---");
                inputField.setEditable(false);
            }
        });

        // Ensure updateBackgroundImage is called AFTER the command processing which updates the room state
        updateBackgroundImage();

        appendTextAndScroll(PROMPT);
        inputField.clear();
        inputField.requestFocus();
    }

    private void sendDirection(String cmd) {
        // This is a direct command execution, not just setting the field text
        inputField.setText(cmd); // Set the text so the user sees it in the input history if needed
        processCommand(); // Execute the command
    }

    private void captureGameOutput(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try {
            action.run();
        } catch (Exception e) {
            // CRITICAL: If game logic fails, capture the error and restore original System.out
            System.setOut(original);
            System.err.println("\n[FATAL GAME ERROR] An unhandled exception occurred during command processing:");
            e.printStackTrace(System.err);
            appendTextAndScroll("[FATAL GAME ERROR] Check console for details.");
            return;
        } finally {
            // IMPORTANT: Always restore the original System.out after capturing output
            System.setOut(original); 
        }

        appendTextAndScroll(baos.toString());
    }

    private void updateBackgroundImage() {
        // Get the room description from the game
        String roomDescriptionRaw = game.getCurrentRoomName();
        
        // Convert the raw description to lowercase and trim it before lookup
        String key = roomDescriptionRaw.toLowerCase().trim();
        
        // --- CRITICAL DEBUGGING STEP: Check your console for this output! ---
        System.out.println("--- Image Debug ---");
        System.out.println("Raw Room Description: '" + roomDescriptionRaw + "'");
        System.out.println("Lookup Key:           '" + key + "'");
        // -------------------------------------------------------------------

        // Find the image path using the normalized key
        String imagePath = roomImageMap.getOrDefault(key, roomImageMap.get("fallback"));
        
        System.out.println("Resolved Image Path:  '" + imagePath + "'");

        try {
            // Use getResourceAsStream for resource loading, which is required for packaged applications
            Image roomImage = new Image(getClass().getResourceAsStream("/" + imagePath));
            
            if (roomImage.isError()) {
                throw new Exception("Image failed to load: " + imagePath);
            }
            
            backgroundImageView.setImage(roomImage);
            
        } catch (Exception e) {
            System.err.println("[IMAGE ERROR] Could not load image: " + imagePath + ". Error: " + e.getMessage());
            // Fallback to a plain dark background if image fails
            backgroundImageView.setImage(null);
            backgroundImageView.setStyle("-fx-background-color: #333333;"); 
        }
    }

    private void saveGameAction() {
        GamePersistence.saveGame(game);
        showMessage("Game saved!", true);
    }

    private void quitGameAction() {
        Optional<ButtonType> result = showConfirmation("Quit", "Are you sure you want to quit?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    private void showMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setStyle(success ? "-fx-text-fill: #00FF00; -fx-font-weight: bold;" : "-fx-text-fill: red; -fx-font-weight: bold;");
    }

    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

    private void appendTextAndScroll(String text) {
        outputArea.appendText(text);
        outputArea.setScrollTop(Double.MAX_VALUE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}