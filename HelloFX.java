/* 
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {
        Button button = new Button("Click me!");
        button.setOnAction(e -> System.out.println("Button clicked!"));

        StackPane root = new StackPane(button);
        Scene scene = new Scene(root, 300, 200);

        stage.setTitle("My First JavaFX App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // This starts the JavaFX app
    }
}
*/