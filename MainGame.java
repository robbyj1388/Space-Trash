import javafx.application.Application;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class MainGame extends Application {

    private Pane root;
    private Random random = new Random();
    private Meteor activeMeteor; // To store the last meteor created

    /**
     * The start method. Required by Application
     * @param stage
     */
    public void start(Stage stage) {
        root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("Space Trash");
        stage.setScene(scene);
        stage.show();

        // Set up a Timeline to spawn meteors at intervals
        Timeline meteorSpawner = new Timeline(new KeyFrame(
            Duration.seconds(1), e -> spawnMeteor()));
        meteorSpawner.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
        meteorSpawner.play();

        // Create a "Deflect!" button
        Button btn = new Button("Deflect!");
        btn.setLayoutX(350); // Position the button at the bottom
        btn.setLayoutY(550);
        // Set button action to deflect the meteor when clicked
        btn.setOnAction(e -> deflectMeteor());
        root.getChildren().add(btn);
    }

    /**
     * Spawns a new meteor at a random x position at the top of the screen.
     */
    private void spawnMeteor() {
        // Generate a random color for the meteor
        Color color = Color.rgb(255, 0, 0, 1); // Red
        // Random speed between 2 and 5
        int velocity = 2 + random.nextInt(4);

        // Create a new meteor
        Meteor meteor = new Meteor(velocity, color);

        // Set initial position at a random x, just above the top of the screen
        meteor.shape.setLayoutX(random.nextInt(800));
        meteor.shape.setLayoutY(-meteor.shape.getBoundsInLocal().getHeight());
        // Add the meteor's shape to the scene
        root.getChildren().add(meteor.shape);

        // Save the active meteor so we can interact with it later
        activeMeteor = meteor;

        // Set up a Timeline to remove the meteor when it moves out of view
        meteor.shape.layoutYProperty().addListener(
            (observable, oldValue, newValue) -> {
                // If meteor moves out of the scene at the bottom
                if (newValue.doubleValue() > 600) { 
                    // Remove it from the scene
                    root.getChildren().remove(meteor.shape);
                }
        });
    }

    /**
     * Deflect the active meteor when the button is pressed.
     */
    private void deflectMeteor() {
        if (activeMeteor != null) {
            // Call the deflect method of the active meteor
            activeMeteor.deflect();
        }
    }

    /**
     * The main( ) method is ignored in JavaFX applications.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

class Meteor {
    int velocity; // Speed at which the meteor falls
    javafx.scene.shape.Shape shape;

    // Constructor to initialize a meteor with velocity and color
    public Meteor(int velocity, javafx.scene.paint.Paint color) {
        this.velocity = velocity;

        // Randomly choose a shape for the meteor (Circle, Rectangle, Polygon)
        int shapeType = new Random().nextInt(3);
        switch (shapeType) {
            case 0: // Circle
                shape = new javafx.scene.shape.Circle(50, color);
                break;
            case 1: // Rectangle
                shape = new javafx.scene.shape.Rectangle(50,
                    50, color);
                break;
            case 2: // Polygon (Triangle)
                shape = new javafx.scene.shape.Polygon(
                    0.0, 0.0, 
                    50.0, 0.0, 
                    25.0, 50.0);
                shape.setFill(color);
                break;
            default:
                shape = new javafx.scene.shape.Circle(50, color); 
                break;
        }

        // Set initial position
        shape.setLayoutX(0);
        shape.setLayoutY(0);
    }

    /**
     * Inverts and doubles the velocity of the meteor (deflects it).
     */
    public void deflect() {
        velocity *= -2; // Invert and double the velocity
        System.out.println("Meteor deflected! New velocity: " + velocity);
    }
}
