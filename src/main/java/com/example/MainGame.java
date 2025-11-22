package com.example;

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * MainGame is the primary class for the "Space Trash" game.
 * It sets up the game window, paddles, meteors, handles input, 
 * and updates the game state every frame.
 */
public class MainGame extends Application {
    private HandServer server = new HandServer(5555); // Server for hand tracking
    private Pane root; // Root pane for the scene
    private Random random = new Random();
    private Paddle leftPaddle = new Paddle(10, 10, 50, 10, Color.WHITE);
    private Paddle rightPaddle = new Paddle(10, 10, 50, 10, Color.WHITE);
    private int distanceBetweenPaddles = 300; // Distance between paddles on spawn
    private List<Meteor> meteors = new ArrayList<>(); // List of active meteors
    private Set<KeyCode> pressedKeys = new HashSet<>(); // Tracks keys currently pressed

    public static int score = 0;
    private Text scoreText; // Displays the current score

    /**
     * Starts the JavaFX application and initializes all game elements.
     * @param stage the main stage for the game
     */
    @Override
    public void start(Stage stage) {
        server.start(); // Start the hand tracking server

        root = new Pane();
        Scene scene = new Scene(root, 960, 540, Color.BLACK);

        stage.setTitle("Space Trash");
        stage.setScene(scene);
        stage.show();

        // Focus handling
        root.requestFocus();
        scene.setOnMouseClicked(e -> root.requestFocus());

        // Score display setup
        scoreText = new Text("Score: 0");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(20));
        scoreText.setX(10);
        scoreText.setY(30);
        root.getChildren().add(scoreText);

        // Spawn player paddles
        spawnPlayer(scene.getWidth() * 0.3, scene.getHeight() * 0.75, 50.0);

        // Keyboard input handling
        scene.setOnKeyPressed(event -> pressedKeys.add(event.getCode()));
        scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));

        // Game loop using AnimationTimer
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateLeftPaddle(leftPaddle);
                updateRightPaddle(rightPaddle);

                // Manual keyboard movement for paddles
                if (pressedKeys.contains(KeyCode.W)) leftPaddle.moveUp();
                if (pressedKeys.contains(KeyCode.S)) leftPaddle.moveDown(scene.getHeight());
                if (pressedKeys.contains(KeyCode.A)) leftPaddle.moveLeft();
                if (pressedKeys.contains(KeyCode.D)) leftPaddle.moveRight(scene.getWidth());

                if (pressedKeys.contains(KeyCode.UP)) rightPaddle.moveUp();
                if (pressedKeys.contains(KeyCode.DOWN)) rightPaddle.moveDown(scene.getHeight());
                if (pressedKeys.contains(KeyCode.LEFT)) rightPaddle.moveLeft();
                if (pressedKeys.contains(KeyCode.RIGHT)) rightPaddle.moveRight(scene.getWidth());
            }
        };
        gameLoop.start();

        // Spawn meteors every second
        Timeline meteorSpawner = new Timeline(new KeyFrame(
                Duration.seconds(1), e -> spawnMeteor(scene.getWidth())));
        meteorSpawner.setCycleCount(Timeline.INDEFINITE);
        meteorSpawner.play();

        // Check collisions frequently
        Timeline collisionChecker = new Timeline(new KeyFrame(
                Duration.millis(20), e -> checkCollisions()));
        collisionChecker.setCycleCount(Timeline.INDEFINITE);
        collisionChecker.play();

        // Update paddles on window resize
        scene.widthProperty().addListener((obs, oldVal, newVal) -> updatePlayerPosition(scene));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updatePlayerPosition(scene));
    }

    /**
     * Updates the left paddle's position based on hand tracking input.
     * Uses linear interpolation for smooth movement.
     * @param paddle the left paddle to update
     */
    private void updateLeftPaddle(Paddle paddle) {
        double handX = server.lx;
        double handY = server.ly;

        double targetX = handX * root.getWidth() - paddle.getWidth() / 2;
        double targetY = handY * root.getHeight() - paddle.getHeight() / 2;

        // Clamp values to scene bounds
        targetX = Math.max(0, Math.min(targetX, root.getWidth() - paddle.getWidth()));
        targetY = Math.max(0, Math.min(targetY, root.getHeight() - paddle.getHeight()));

        double lerpFactor = 0.2;
        paddle.setX(paddle.getX() + (targetX - paddle.getX()) * lerpFactor);
        paddle.setY(paddle.getY() + (targetY - paddle.getY()) * lerpFactor);
    }

    /**
     * Updates the right paddle's position based on hand tracking input.
     * Uses linear interpolation for smooth movement.
     * @param paddle the right paddle to update
     */
    private void updateRightPaddle(Paddle paddle) {
        double handX = server.rx;
        double handY = server.ry;

        double targetX = handX * root.getWidth() - paddle.getWidth() / 2;
        double targetY = handY * root.getHeight() - paddle.getHeight() / 2;

        // Clamp values to scene bounds
        targetX = Math.max(0, Math.min(targetX, root.getWidth() - paddle.getWidth()));
        targetY = Math.max(0, Math.min(targetY, root.getHeight() - paddle.getHeight()));

        double lerpFactor = 0.2;
        paddle.setX(paddle.getX() + (targetX - paddle.getX()) * lerpFactor);
        paddle.setY(paddle.getY() + (targetY - paddle.getY()) * lerpFactor);
    }

    /**
     * Spawns the player paddles at the specified position.
     * @param x initial x-coordinate for the left paddle
     * @param y initial y-coordinate for the left paddle
     * @param size size of the paddle (currently unused)
     */
    private void spawnPlayer(double x, double y, double size) {
        leftPaddle.setX(x);
        leftPaddle.setY(y);
        rightPaddle.setX(x + distanceBetweenPaddles);
        rightPaddle.setY(y);

        if (!root.getChildren().contains(leftPaddle)) root.getChildren().add(leftPaddle);
        if (!root.getChildren().contains(rightPaddle)) root.getChildren().add(rightPaddle);
    }

    /**
     * Updates player paddle positions when the scene size changes.
     * @param scene the scene to reference for width/height
     */
    private void updatePlayerPosition(Scene scene) {
        double x = scene.getWidth() * 0.3;
        double y = scene.getHeight() * 0.75;

        leftPaddle.setX(x);
        leftPaddle.setY(y);
        rightPaddle.setX(x + distanceBetweenPaddles);
        rightPaddle.setY(y);
    }

    /**
     * Spawns a new meteor at a random horizontal position at the top of the scene.
     * @param sceneWidth the width of the scene to constrain meteor spawn
     */
    private void spawnMeteor(double sceneWidth) {
        Color color = Color.RED;
        int velocity = 2 + random.nextInt(4);

        Meteor meteor = new Meteor(velocity, 0, color);

        double randomX = random.nextDouble() * Math.max(0, sceneWidth - 20);
        meteor.setPosition(randomX, -meteor.getShape().getBoundsInLocal().getHeight());

        root.getChildren().add(meteor.getShape());
        meteors.add(meteor);

        // Remove meteor if it goes off screen
        meteor.getShape().translateYProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > root.getHeight() + 200 || newValue.doubleValue() < -400) {
                root.getChildren().remove(meteor.getShape());
                meteors.remove(meteor);
            }
        });
    }

    /**
     * Checks for collisions between meteors and paddles.
     * Increases score if a meteor collides and deflects the meteor.
     */
    private void checkCollisions() {
        for (Meteor meteor : new ArrayList<>(meteors)) {
            if (meteor.getShape().getBoundsInParent().intersects(leftPaddle.getBoundsInParent())
                    || meteor.getShape().getBoundsInParent().intersects(rightPaddle.getBoundsInParent())) {

                meteor.deflect();

                // Update score if meteor is circle or square
                String shapeName = meteor.getShapeName();
                if ("circle".equalsIgnoreCase(shapeName) || "square".equalsIgnoreCase(shapeName)) {
                    score++;
                }
                scoreText.setText("Score: " + score);
            }
        }
    }

    /**
     * Launches the JavaFX application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
