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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainGame extends Application {
    private HandServer server = new HandServer(5555);
    private Pane root;
    private Random random = new Random();
    private Paddle leftPaddle = new Paddle(10, 10, 50, 10, Color.WHITE);
    private Paddle rightPaddle = new Paddle(10, 10, 50, 10, Color.WHITE);
    private int distanceBetweenPaddles = 300; // For spawning
    private List<Meteor> meteors = new ArrayList<>(); // List to store active meteors
    private Set<KeyCode> pressedKeys = new HashSet<>(); // Track player pressed keys

    public static int score = 0; // Keep score for each meteor hit
    private Text scoreText; // Display the score

    /**
     * The start method. Required by Application.
     *
     * Contains main player movement.
     *
     * @param stage the primary stage for this application
     */
    public void start(Stage stage) {
        // start server for hand tracking
        server.start();

        root = new Pane();
        setupBackground(root);

        Scene scene = new Scene(root, 960, 540);
        stage.setTitle("Space Trash");
        stage.setScene(scene);
        stage.show();

        // Add score display
        scoreText = new Text("Score: 0");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(20));
        scoreText.setX(10);
        scoreText.setY(30);
        root.getChildren().add(scoreText);

        // Spawn player paddles and set their position relative to window size
        spawnPlayer(scene.getWidth() * 0.3, scene.getHeight() * 0.75, 50.0);

        // Track key presses
        scene.setOnKeyPressed(event -> pressedKeys.add(event.getCode()));
        scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));

        // Game loop for smooth movement
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Hand tracking 
                updateLeftPaddle(leftPaddle);
                updateRightPaddle(rightPaddle);
                
                // keyboard tracking
                // Left paddle movement
                if (pressedKeys.contains(KeyCode.W)) {
                    leftPaddle.moveUp();
                }
                if (pressedKeys.contains(KeyCode.S)) {
                    leftPaddle.moveDown(scene.getHeight());
                }
                if (pressedKeys.contains(KeyCode.A)) {
                    leftPaddle.moveLeft();
                }
                if (pressedKeys.contains(KeyCode.D)) {
                    leftPaddle.moveRight(scene.getWidth());
                }
                // Right paddle movement
                if (pressedKeys.contains(KeyCode.UP)) {
                    rightPaddle.moveUp();
                }
                if (pressedKeys.contains(KeyCode.DOWN)) {
                    rightPaddle.moveDown(scene.getHeight());
                }
                if (pressedKeys.contains(KeyCode.LEFT)) {
                    rightPaddle.moveLeft();
                }
                if (pressedKeys.contains(KeyCode.RIGHT)) {
                    rightPaddle.moveRight(scene.getWidth());
                }
            }
        };
        gameLoop.start();

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
     * LEFT HAND
     * Maps the hand’s normalized coordinates (0.0–1.0) to the scene’s pixel coordinates
     * and moves the paddle accordingly.
     */
    private void updateLeftPaddle(Paddle paddle) {
        double handX = server.lx; // normalized 0–1
        double handY = server.ly; // normalized 0–1

        // Map normalized coordinates (from Python) to actual screen pixels
        double targetX = handX * root.getWidth() - paddle.getWidth() / 2;
        double targetY = handY * root.getHeight() - paddle.getHeight() / 2;

        // Keep the paddle within scene bounds
        targetX = Math.max(0, Math.min(targetX, root.getWidth() - paddle.getWidth()));
        targetY = Math.max(0, Math.min(targetY, root.getHeight() - paddle.getHeight()));

        // Smooth movement (optional)
        double lerpFactor = 0.2;
        double newX = paddle.getX() + (targetX - paddle.getX()) * lerpFactor;
        double newY = paddle.getY() + (targetY - paddle.getY()) * lerpFactor;
        
        if (newX > -9 && newX < 9){
          return;
        }

        System.out.println(newX);
        System.out.println(newY);
         
        paddle.setX(newX);
        paddle.setY(newY);
    }

    /**
     * RIGHT HAND
     * Maps the hand’s normalized coordinates (0.0–1.0) to the scene’s pixel coordinates
     * and moves the paddle accordingly.
     */
    private void updateRightPaddle(Paddle paddle) {
        double handX = server.rx; // normalized 0–1
        double handY = server.ry; // normalized 0–1

        // Map normalized coordinates (from Python) to actual screen pixels
        double targetX = handX * root.getWidth() - paddle.getWidth() / 2;
        double targetY = handY * root.getHeight() - paddle.getHeight() / 2;

        // Keep the paddle within scene bounds
        targetX = Math.max(0, Math.min(targetX, root.getWidth() - paddle.getWidth()));
        targetY = Math.max(0, Math.min(targetY, root.getHeight() - paddle.getHeight()));

        // Smooth movement (optional)
        double lerpFactor = 0.2;
        double newX = paddle.getX() + (targetX - paddle.getX()) * lerpFactor;
        double newY = paddle.getY() + (targetY - paddle.getY()) * lerpFactor;
        

        System.out.println(newX);
        System.out.println(newY);

        paddle.setX(newX);
        paddle.setY(newY);
    }

    /**
     * Sets up the background image and makes it responsive to window resizing.
     *
     * @param root the main Pane of the application
     */
    private void setupBackground(Pane root) {
        try {
            // Load image from resources (src/main/resources/backgroundImg.png)
            Image image = new Image(getClass().getResourceAsStream("/backgroundImg.png"));
            ImageView backgroundView = new ImageView(image);

            // Stretch to fill pane without preserving ratio
            backgroundView.setPreserveRatio(false);

            // Bind size to pane size
            backgroundView.fitWidthProperty().bind(root.widthProperty());
            backgroundView.fitHeightProperty().bind(root.heightProperty());

            // Add background as first child (bottom)
            root.getChildren().add(0, backgroundView);
        } catch (Exception e) {
            e.printStackTrace();
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
        leftPaddle.setX(x);
        leftPaddle.setY(y);
        rightPaddle.setX(x + distanceBetweenPaddles);
        rightPaddle.setY(y);
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
        Color color = Color.rgb(255, 0, 0, 1); // Set meteor color
        int velocity = 2 + random.nextInt(4); // Randomize meteor speed

        Meteor meteor = new Meteor(velocity, 0, color); // Create new meteor

        // Set a random x position within the screen width and y position at the top
        double randomX = random.nextDouble() * sceneWidth;
        meteor.setPosition(randomX, -meteor.getShape().getBoundsInLocal().getHeight());

        // Add the meteor to the scene
        root.getChildren().add(meteor.getShape());
        meteors.add(meteor);

        // Remove the meteor when it goes out of bounds
        meteor.getShape().layoutYProperty().addListener((observable, oldValue, newValue) -> {
            if ((newValue.doubleValue() < -100) || (newValue.doubleValue() > root.getHeight())) {
                root.getChildren().remove(meteor.getShape());
                meteors.remove(meteor);
            }
        });
    }

    /**
     * Checks for collisions between meteors and player paddles.
     */
    private void checkCollisions() {
        for (Meteor meteor : new ArrayList<>(meteors)) { // Avoid ConcurrentModificationException
            if (meteor.shape.getBoundsInParent().intersects(leftPaddle.getBoundsInParent())
                    || meteor.shape.getBoundsInParent().intersects(rightPaddle.getBoundsInParent())) {
                        meteor.deflect(); // Deflect the meteor upon collision
                        // if meteor is a circle  or squarethen up the score.
                        if (meteor.shapeName.equals("circle") || meteor.shapeName.equals("square")){ 
                            score++; 
                        }
                scoreText.setText("Score: " + score);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
