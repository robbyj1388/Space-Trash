import javafx.application.Application;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGame extends Application {
    private Pane root;
    private Random random = new Random();
    private Paddle leftPaddle = new Paddle(10, 10, 50, 10, Color.WHITE);
    private Paddle rightPaddle = new Paddle(10, 10, 50, 10, Color.WHITE);
    private int distanceBetweenPaddles = 300; // For spawning,
    private List<Meteor> meteors = new ArrayList<>(); // List to store active meteors,
    public static int score = 0; // Keep score for each meteor hit.

    /**
     * The start method. Required by Application.
     *
     * @param stage the primary stage for this application
     */
    public void start(Stage stage) {
        root = new Pane();
        setupBackground(root);

        Scene scene = new Scene(root, 960, 540);
        stage.setTitle("Space Trash");
        stage.setScene(scene);
        stage.show();

        // Spawn player paddles and set their position relative to window size
        spawnPlayer(scene.getWidth() * 0.3, scene.getHeight() * 0.75, 50.0);

        // Set up a Timeline to spawn meteors at intervals
        Timeline meteorSpawner = new Timeline(new KeyFrame(
                Duration.seconds(1), e -> spawnMeteor(scene.getWidth())));
        meteorSpawner.setCycleCount(Timeline.INDEFINITE);
        meteorSpawner.play();

        // Continuously check for collisions between meteors and paddles
        Timeline collisionChecker = new Timeline(new KeyFrame(
                Duration.millis(20), e -> checkCollisions()));
        collisionChecker.setCycleCount(Timeline.INDEFINITE);
        collisionChecker.play();
        
        // Listen for window resize events and reposition player paddles
        scene.widthProperty().addListener((obs, oldVal, newVal) -> updatePlayerPosition(scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updatePlayerPosition(scene));
    }

    /**
     * Sets up the background image and makes it responsive to window resizing.
     *
     * @param root the main Pane of the application
     */
    private void setupBackground(Pane root) {
        try {
            FileInputStream input = new FileInputStream("backgroundImg.png");
            Image image = new Image(input);
            ImageView backgroundView = new ImageView(image);

            // Make the background image cover the window
            backgroundView.setPreserveRatio(false);
            backgroundView.setFitWidth(root.getWidth());
            backgroundView.setFitHeight(root.getHeight());

            // Update the image dimensions on window resize
            root.widthProperty().addListener((obs, oldVal, newVal) -> backgroundView.setFitWidth(newVal.doubleValue()));
            root.heightProperty().addListener((obs, oldVal, newVal) -> backgroundView.setFitHeight(newVal.doubleValue()));

            root.getChildren().add(0, backgroundView); // Add background to the root as the first child
        } catch (Exception e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }
    }

    /**
     * Spawns a new player with paddles at the specified position.
     *
     * @param x    the x-coordinate for the player
     * @param y    the y-coordinate for the player
     * @param size the size of the paddles
     */
    private void spawnPlayer(double x, double y, double size) {
        leftPaddle.setLayoutX(x);
        leftPaddle.setLayoutY(y);
        rightPaddle.setLayoutX(x + distanceBetweenPaddles);
        rightPaddle.setLayoutY(y);

        root.getChildren().addAll(leftPaddle, rightPaddle);
    }

    /**
     * Updates player paddles' position based on scene size to make it responsive.
     *
     * @param scene the Scene containing the game
     */
    private void updatePlayerPosition(Scene scene) {
        double x = scene.getWidth() * 0.3;
        double y = scene.getHeight() * 0.75;

        leftPaddle.setLayoutX(x);
        leftPaddle.setLayoutY(y);
        rightPaddle.setLayoutX(x + distanceBetweenPaddles);
        rightPaddle.setLayoutY(y);
    }

    /**
     * Spawns a new meteor at a random x position at the top of the screen.
     *
     * @param sceneWidth the width of the scene
     */
    private void spawnMeteor(double sceneWidth) {
        Color color = Color.rgb(255, 0, 0, 1);
        int velocity = 2 + random.nextInt(4);

        Meteor meteor = new Meteor(velocity, color);

        meteor.shape.setLayoutX(random.nextDouble() * sceneWidth);
        meteor.shape.setLayoutY(-meteor.shape.getBoundsInLocal().getHeight());
        root.getChildren().add(meteor.shape);

        leftPaddle.toFront();
        rightPaddle.toFront();

        meteors.add(meteor); // Add meteor to th
        meteor.shape.layoutYProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() > root.getHeight()) {
                        root.getChildren().remove(meteor.shape);
                        meteors.remove(meteor); // Remove meteor from the list when out of view
                    }
                });
    }

    /**
     * Checks for collisions between meteors and player paddles.
     */
    private void checkCollisions() {
        for (Meteor meteor : meteors) {
            if (meteor.shape.getBoundsInParent().intersects(leftPaddle.getBoundsInParent())
                    || meteor.shape.getBoundsInParent().intersects(rightPaddle.getBoundsInParent())) {
                meteor.deflect(); // Deflect the meteor upon collision
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
