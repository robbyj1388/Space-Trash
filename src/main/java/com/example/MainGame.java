package com.example;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * MainGame is the primary class for Space Trash.
 * It sets up the game window, paddles, meteors, handles input, 
 * and updates the game state every frame.
 */
public class MainGame extends Application {
    private HandServer server = new HandServer(5555); // Server for hand tracking
    private final boolean disableHands = false; //Disables hand tracking #TODO Push this to an arg
    private Pane root; // Root pane for the scene
    private Random random = new Random();
    private Paddle leftPaddle = new Paddle(10, 10, 50, 10, Color.WHITE);
    private Paddle rightPaddle = new Paddle(10, 10, 50, 10, Color.WHITE);
    private int distanceBetweenPaddles = 300; // Distance between paddles on spawn
    private List<Meteor> meteors = new ArrayList<>(); // List of active meteors
    private boolean spawnMeteors = false;
    private Set<KeyCode> pressedKeys = new HashSet<>(); // Tracks keys currently pressed

    private List<AreaButton> areaButtons = new ArrayList<>(); // List of active buttons

    // The length of each game
    private final double gameLength = 60;

    private double gameDuration = gameLength; // How long until the game is over
    private double gameTime = 0; // Tracks how long the game has been running in seconds

    public static int score = 0;
    private Text scoreText; // Displays the current score
    private Text titleText; // Displays the title
    private Text durationText; // Displays the duration of the current game

    
    
    private Text interText; //The text saying what to hit and what to avoid
    public double interTimer = 3; //How many seconds for the intermission
    
    private Text endScoreText;
    private double endTimer = 3;

    private AreaButton startButton; // The start button
    
    public double starSpeed = .0;
    private List<Circle> starArray = new ArrayList<>();

    public Logger logger = new Logger();

    public int meteorsIDs = 0; // The last meteor made 
    

    enum gameState {
        menu, game, end, intermission
    }
    gameState state = gameState.menu;

    private Stage stage;


    

    /**
     * Starts the JavaFX application and initializes all game elements.
     * @param stage the main stage for the game
     */
    @Override
    public void start(Stage stage) {
        
        if(!disableHands) {
            server.start(); // Start the hand tracking server
        }
        root = new Pane();
        Scene scene = new Scene(root, 960, 540, Color.BLACK);
        
        stage.setTitle("Space Trash");
        stage.setScene(scene);
        stage.show();
        this.stage = stage; //Look into a better way to save stage

        // Focus handling
        root.requestFocus();
        scene.setOnMouseClicked(e -> root.requestFocus());

        // Score display setup
        scoreText = new Text("Score: 0");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(20));
        scoreText.setX(10);
        scoreText.setY(30);
        //root.getChildren().add(scoreText);

        durationText = new Text("TIME LEFT: 60");
        durationText.setFill(Color.WHITE);
        durationText.setFont(Font.font(20));
        durationText.setX(10);
        durationText.setY(60);
        //root.getChildren().add(durationText);

        // Title
        titleText = new Text("SPACE TRASH");
        titleText.setFill(Color.WHITE);
        titleText.setFont(Font.font(40));
        titleText.setTextOrigin(VPos.BOTTOM);
        titleText.layoutXProperty().bind(scene.widthProperty().subtract(titleText.prefWidth(-1)).divide(2));
        titleText.layoutYProperty().bind(scene.heightProperty().subtract(titleText.prefHeight(-1)).divide(3));


        // Intermission text
        //TODO: replace these w/ randomly selected shapes and their symbols NOT names
        interText = new Text("HIT THESE\nCircle, Square\n\nAVOID THESE\nTriangle, Rectangle");
        interText.setFill(Color.WHITE);
        interText.setTextAlignment(TextAlignment.CENTER);
        interText.setFont(Font.font(40));
        interText.setTextOrigin(VPos.CENTER);
        interText.layoutXProperty().bind(scene.widthProperty().subtract(titleText.prefWidth(-1)).divide(2));
        interText.layoutYProperty().bind(scene.heightProperty().subtract(titleText.prefHeight(-1)).divide(2));
        

        // End Score Text
        endScoreText = new Text("YOUR SCORE\n0");
        endScoreText.setFill(Color.WHITE);
        endScoreText.setTextAlignment(TextAlignment.CENTER);
        endScoreText.setFont(Font.font(40));
        endScoreText.setTextOrigin(VPos.CENTER);
        endScoreText.layoutXProperty().bind(scene.widthProperty().subtract(endScoreText.prefWidth(-1)).divide(2));
        endScoreText.layoutYProperty().bind(scene.heightProperty().subtract(endScoreText.prefHeight(-1)).divide(2));


        root.getChildren().add(titleText);

        startButton = new AreaButton((stage.getWidth()/2)-50, stage.getHeight()/2, 100.0, "START", gameState.intermission); //Creates the start button
        root.getChildren().add(startButton.getShape());
        root.getChildren().add(startButton.getInnerShape());
        root.getChildren().add(startButton.getText());
        areaButtons.add(startButton);
        
        // Spawn player paddles
        spawnPlayer(scene.getWidth() * 0.3, scene.getHeight() * 0.75, 50.0);

        // Keyboard input handling
        scene.setOnKeyPressed(event -> pressedKeys.add(event.getCode()));
        scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));

        // Creating stars
        
        for(int i = 0; i < 500; i++) {
            double x = scene.widthProperty().subtract(random.nextInt(scene.widthProperty().intValue())).doubleValue();
            double y = scene.heightProperty().subtract(random.nextInt(scene.heightProperty().intValue())).doubleValue();
            Circle star = new Circle(x, y, random.nextInt(2));

            //star.layoutXProperty().bind(scene.widthProperty().subtract(random.nextInt(scene.widthProperty().intValue())));
            //star.layoutYProperty().bind(scene.heightProperty().subtract(random.nextInt(scene.heightProperty().intValue())));
            star.setFill(Color.WHITE);
            root.getChildren().add(star);
            starArray.add(star);
        }

        // Game loop using AnimationTimer
        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                // Convert nanoseconds to seconds and increment game time
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                if (lastTime > 0) {
                    gameTime += deltaTime;
                }
                lastTime = now;

                updateLeftPaddle(leftPaddle);
                updateRightPaddle(rightPaddle);
                //TODO: This code is a bit messy
                switch(getState()) {
                    case intermission:
                        interTimer -= 1*deltaTime;
                        if(interTimer <= 0) {
                            setState(gameState.game);
                        }
                        starSpeed = lerp(starSpeed, 1, 0.05);
                    break;
                    case end:
                        endTimer -= 1*deltaTime;
                        if(endTimer <= 0) {
                            setState(gameState.menu);
                        }
                    break;
                    case game:
                        gameDuration -= 1*deltaTime;
                        durationText.setText("TIME LEFT: " + (int)Math.clamp(gameDuration, 0, gameLength));
                        if(gameDuration <= 0) {
                            setState(gameState.end);
                        }       
                    break;
                }

                // Manual keyboard movement for paddles
                if (pressedKeys.contains(KeyCode.W)) leftPaddle.moveUp();
                if (pressedKeys.contains(KeyCode.S)) leftPaddle.moveDown(scene.getHeight());
                if (pressedKeys.contains(KeyCode.A)) leftPaddle.moveLeft();
                if (pressedKeys.contains(KeyCode.D)) leftPaddle.moveRight(scene.getWidth());

                if (pressedKeys.contains(KeyCode.UP)) rightPaddle.moveUp();
                if (pressedKeys.contains(KeyCode.DOWN)) rightPaddle.moveDown(scene.getHeight());
                if (pressedKeys.contains(KeyCode.LEFT)) rightPaddle.moveLeft();
                if (pressedKeys.contains(KeyCode.RIGHT)) rightPaddle.moveRight(scene.getWidth());
                windowResizeUI();
                
            }
        };
        gameLoop.start();
        this.gameTime = 0;
        logger.logEntry(getGameTime(), "Game started.");

        // The timeline that runs the hand logger, lower value if want more "precision"
        Timeline handLogger = new Timeline( new KeyFrame(
            Duration.seconds(0.05), e -> logPositions()));
        handLogger.setCycleCount(Timeline.INDEFINITE);
        handLogger.play();
        // Spawn meteors periodically
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

    public void stop() {
        System.out.println("PROGRAM STOPPING");
        logger.logEntry(getGameTime(), "End 0");
        logger.close();
    }
    /**
     * Updates the left paddle's position based on hand tracking input.
     * Uses linear interpolation for smooth movement.
     * @param paddle the left paddle to update
     */
    private void updateLeftPaddle(Paddle paddle) {
        if(server.lx == 0.0) //TODO: Test if this works with hand tracking
        {
            return;
        }
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
        if(server.rx == 0.0) //TODO: Test if this works with hand tracking
        {
            return;
        }
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
     * Meteor speed increases gradually as game time increases.
     * @param sceneWidth the width of the scene to constrain meteor spawn
     */
    private void spawnMeteor(double sceneWidth) {
        
        if(!spawnMeteors)
        {
            return;
        }
        Color color = Color.RED;

        // Base velocity + additional velocity based on elapsed game time
        int baseVelocity = 2 + random.nextInt(4);
        int speedIncrease = (int) (gameTime / 10); // Speed increases by 1 every 10 seconds
        int velocity = baseVelocity + speedIncrease;

        Meteor meteor = new Meteor(velocity, 0, color, meteorsIDs);
        meteorsIDs++; //just incrementing by one ensures no duplicate IDs

        double randomX = random.nextDouble() * Math.max(0, sceneWidth - 20);
        meteor.setPosition(randomX, -meteor.getShape().getBoundsInLocal().getHeight());

        root.getChildren().add(meteor.getShape());
        meteors.add(meteor);
        //                                          X position      Velocity        ID
        logger.logEntry(getGameTime(), "MeSpawn " + randomX + " " + velocity + " " + meteor.getID() + " " + meteor.getShapeName()); //Maybe give them an ID so the log watcher can track
        // Remove meteor if it goes off screen
        meteor.getShape().translateYProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > root.getHeight() + 200 || newValue.doubleValue() < -400) {
                root.getChildren().remove(meteor.getShape());
                meteors.remove(meteor);
                logger.logEntry(Double.toString(gameTime), "MeRemove");
            }
        });
    }

    /**
     * Logs the paddle positions every few frames
     */
    public void logPositions() {
        logger.logEntry(getGameTime(), "LeftPaddle " + Double.toString(leftPaddle.getX()) + " " + Double.toString(leftPaddle.getY()));
        logger.logEntry(getGameTime(), "RightPaddle " + Double.toString(rightPaddle.getX()) + " " + Double.toString(rightPaddle.getY()));
    }
    public String getGameTime() {
        return Double.toString(((double)Math.round(gameTime*1000))/1000);
    }
    /**
     * Goes through every UI element that needs realigned based on window resize
     */
    public void windowResizeUI() {
        startButton.setPos(root.getWidth()/2, root.getHeight()/2);
    }

    /**
     * Checks for collisions between meteors and paddles.
     * Increases score if a meteor collides and deflects the meteor.
     */
    private void checkCollisions() {
        for (Meteor meteor : new ArrayList<>(meteors)) { // Meteor collisions
            if (meteor.getShape().getBoundsInParent().intersects(leftPaddle.getBoundsInParent())
                    || meteor.getShape().getBoundsInParent().intersects(rightPaddle.getBoundsInParent())) {

                if(!meteor.getCollided())
                {
                    meteor.setCollided(true);
                    meteor.deflect();
                    //Please leave logged entries short and one word, makes it easier for multiple reasons
                    logger.logEntry(getGameTime(), "MeDeflect " + meteor.getID()); //add ID 
                    // Update score if meteor is circle or square
                    String shapeName = meteor.getShapeName();
                    if ("circle".equalsIgnoreCase(shapeName) || "square".equalsIgnoreCase(shapeName)) {
                        score++;
                        logger.logEntry(getGameTime(), "Score " + score);
                    }
                    scoreText.setText("Score: " + score);
                }
            }
            //Check if outside of game range to delete from meteor list
            if(meteor.getY() > root.getHeight()+5) {
                meteors.remove(meteor);
            }
            if((meteor.getCollided()) && (meteor.getY() < 0)) {
                meteors.remove(meteor);
            }
        }
        for(AreaButton button : new ArrayList<>(areaButtons)) //Area buttons
        {
            if (button.getShape().getBoundsInParent().intersects(leftPaddle.getBoundsInParent())
                    || button.getShape().getBoundsInParent().intersects(rightPaddle.getBoundsInParent())) {
                        button.increment();
                }
                else
                {
                    button.decrement();
                }
                if(button.getTimer() > 60)
                {
                    button.reset();
                    setState( button.getState() );
                    
                }
        }
    }



    /**
     * Gets the game state
     * @return gameState
     */
    public gameState getState() {
        return this.state;
    }

    /**
     * Sets the game state and runs logic regarding it
     * @param gameState the state to set
     */
    public void setState(gameState x)
    {
        boolean comp = true; //If we are completing the state transition (There is a better way to handle this)
        //Removing elements
        if(x != gameState.menu) //menu elements
        {
            if(root.getChildren().contains(titleText)) { //Removing all menu elements if title exist
                root.getChildren().remove(titleText);
                root.getChildren().remove(startButton.getShape()); 
                root.getChildren().remove(startButton.getInnerShape());   
                root.getChildren().remove(startButton.getText()); 
                areaButtons.remove(startButton);
                
            }        
        }
        if(x != gameState.intermission) {
            if(root.getChildren().contains(interText)) {
                root.getChildren().remove(interText);
            }
        }
        
        //Adding elements
        if( (x == gameState.intermission) && (getState() != x) ) {
            root.getChildren().add(interText);
        }
        
        //Entering MENU state
        if( x == gameState.menu ) {

                root.getChildren().add(titleText);
                root.getChildren().add(startButton.getShape()); 
                root.getChildren().add(startButton.getInnerShape());   
                root.getChildren().add(startButton.getText());   
                areaButtons.add(startButton);
                //Deleting objects
                root.getChildren().remove(endScoreText);
        }
        //Entering END state
        if( x == gameState.end ) {
            spawnMeteors = false; 
            comp = false;
            if(meteors.size() == 0) { //setState(gameState.end) will repeatably run each game frame until there are no meteors left
                comp = true;
                endScoreText.setText("YOUR SCORE\n" + score);
                root.getChildren().add(endScoreText);
                endTimer = 3; //END TIMER TIME
                //Removing UI
                root.getChildren().remove(scoreText);
                root.getChildren().remove(durationText);
                for (Meteor meteor : new ArrayList<>(meteors)) {
                    root.getChildren().remove(meteor.getShape());
                }
            }
        }


        //Entering GAME state
        if( (x == gameState.game) && (getState() != x)) {
            meteorsIDs = 0;
            spawnMeteors = true;
            gameDuration = gameLength; 
            score = 0;
            gameTime = 0;
            logger.newLog(); //Starts up new logs
            logger.logEntry("-1", "Resolution " + Double.toString(stage.getWidth()) + " " + Double.toString(stage.getHeight()));
            //Adding UI
            if(!root.getChildren().contains(scoreText)) { //Edge case just in case
                root.getChildren().add(scoreText);
                root.getChildren().add(durationText);
            }
        }
        if(comp) {
            this.state = x;
        }
    }
    /**
     * Linear interpolation
     * @param start
     * @param end
     * @param percentage
     * @return a->b by f%
     */
    public double lerp(double a, double b, double f) {
        return a * (1.0 - f) + (b * f);
    }

    /**
     * Launches the JavaFX application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println(args);
        launch(args);
    }
}
