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
    private Player player = null;
    private Meteor activeMeteor; // To store the last meteor created

    /**
     * The start method. Required by Application
     *
     * @param stage
     */
    public void start(Stage stage) {
        root = new Pane();
        Scene scene = new Scene(root, 960, 540);

        stage.setTitle("Space Trash");
        stage.setScene(scene);
        stage.show();


        // Spawn player paddles and set their initial position relative to window size
        spawnPlayer(scene.getWidth() * 0.3, scene.getHeight() * 0.75, 50.0);

        // Set up a Timeline to spawn meteors at intervals
        Timeline meteorSpawner = new Timeline(new KeyFrame(
                Duration.seconds(1), e -> spawnMeteor(scene.getWidth())));
        meteorSpawner.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
        meteorSpawner.play();

        // Create a "Deflect!" button for testing
        Button btn = new Button("Deflect!");
        btn.layoutXProperty().bind(scene.widthProperty().divide(2).subtract(30));
        btn.layoutYProperty().bind(scene.heightProperty().subtract(50));
        // Set button action to deflect the meteor when clicked
        btn.setOnAction(e -> deflectMeteor());
        root.getChildren().add(btn);

        // Listen for window resize events and reposition player paddles
        scene.widthProperty().addListener((obs, oldVal, newVal) -> updatePlayerPosition(scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updatePlayerPosition(scene));
    }

    /**
     * Spawns a new meteor at a random x position at the top of the screen.
     */
    private void spawnPlayer(double x, double y, double size) {
        // Create a Player.
        player = new Player(size / 4, size);

        // Set paddle positions relative to provided x and y
        player.leftPaddle.setLayoutX(x);
        player.leftPaddle.setLayoutY(y);
        player.rightPaddle.setLayoutX(x + player.distance);
        player.rightPaddle.setLayoutY(y);

        // Add the player paddles to the scene.
        root.getChildren().addAll(player.leftPaddle, player.rightPaddle);
    }

    /**
     * Updates player paddles' position based on scene size to make it responsive.
     */
    private void updatePlayerPosition(Scene scene) {
        double x = scene.getWidth() * 0.3;
        double y = scene.getHeight() * 0.75;

        player.leftPaddle.setLayoutX(x);
        player.leftPaddle.setLayoutY(y);
        player.rightPaddle.setLayoutX(x + player.distance);
        player.rightPaddle.setLayoutY(y);
    }

    /**
     * Spawns a new meteor at a random x position at the top of the screen.
     */
    private void spawnMeteor(double sceneWidth) {
        // Generate a random color for the meteor
        Color color = Color.rgb(255, 0, 0, 1); // Red
        // Random speed between 2 and 5
        int velocity = 2 + random.nextInt(4);

        // Create a new meteor
        Meteor meteor = new Meteor(velocity, color);

        // Set initial position at a random x within the scene width, just above the top
        // of the screen
        meteor.shape.setLayoutX(random.nextDouble() * sceneWidth);
        meteor.shape.setLayoutY(-meteor.shape.getBoundsInLocal().getHeight());
        // Add the meteor's shape to the scene
        root.getChildren().add(meteor.shape);

        // Bring paddles to the front
        player.leftPaddle.toFront();
        player.rightPaddle.toFront();

        // Save the active meteor so we can interact with it later
        activeMeteor = meteor;

        // Set up a Timeline to remove the meteor when it moves out of view
        meteor.shape.layoutYProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // If meteor moves out of the scene at the bottom
                    if (newValue.doubleValue() > root.getHeight()) {
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
     * main( ) serves only as fallback in case the application is launched
     * as a regular Java application, e.g., in IDEs with limited FX
     * support.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

