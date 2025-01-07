import javafx.application.Application;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BackgroundPosition;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGame extends Application {

    private Pane root;
    private Random random = new Random();
    private Player player = null;
    private List<Meteor> meteors = new ArrayList<>(); // List to store active meteors

    /**
     * The start method. Required by Application
     *
     * @param stage
     */
    public void start(Stage stage) {

        // Set up background image.
        Background background = null;
        try {
            FileInputStream input = new FileInputStream("backgroundImg.png");
            Image image = new Image(input);
            // Set the background image to stretch to fill the window
            BackgroundImage backgroundImage = new BackgroundImage(image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, false));

            background = new Background(backgroundImage);

        } catch (Exception e) {
            System.out.println(e);

        }
        root = new Pane();
        Scene scene = new Scene(root, 960, 540);

        stage.setTitle("Space Trash");
        stage.setScene(scene);
        root.setBackground(background);
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
     * Spawns a new meteor at a random x position at the top of the screen.
     */
    private void spawnPlayer(double x, double y, double size) {
        player = new Player(size / 4, size);

        player.leftPaddle.setLayoutX(x);
        player.leftPaddle.setLayoutY(y);
        player.rightPaddle.setLayoutX(x + player.distance);
        player.rightPaddle.setLayoutY(y);

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
        Color color = Color.rgb(255, 0, 0, 1);
        int velocity = 2 + random.nextInt(4);

        Meteor meteor = new Meteor(velocity, color);

        meteor.shape.setLayoutX(random.nextDouble() * sceneWidth);
        meteor.shape.setLayoutY(-meteor.shape.getBoundsInLocal().getHeight());
        root.getChildren().add(meteor.shape);

        player.leftPaddle.toFront();
        player.rightPaddle.toFront();

        meteors.add(meteor); // Add meteor to the list

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
            if (meteor.shape.getBoundsInParent().intersects(player.leftPaddle.getBoundsInParent())
                    || meteor.shape.getBoundsInParent().intersects(player.rightPaddle.getBoundsInParent())) {
                meteor.deflect(); // Deflect the meteor upon collision
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
