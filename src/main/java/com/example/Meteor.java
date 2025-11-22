package com.example;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class Meteor {
    private int x = 0; // Current x-coordinate of the meteor
    private int y = 0; // Current y-coordinate of the meteor
    private double dx = 0.0; // Horizontal velocity for diagonal movement
    private int velocity = 0; // Vertical speed of the meteor
    public Shape shape = null; // The shape representing the meteor
    public String shapeName = null; // Hold shape name, i.e. circle, square
    private final Double maxSize = 15.0; // Maximum size of the meteor
    private final Double minSize = 14.0; // Minimum size of the meteor
    private final Random randShape = new Random(); // Randomizer for shape selection
    //private final Random randSlope = new Random(); // Randomizer for movement
    private Timeline timeline; // Animation timeline to simulate falling

    /**
     * Constructor for creating a meteor with random shape and size.
     * 
     * @param velocity           The speed of the meteor's fall.
     * @param horizontalVelocity The horizontal speed for diagonal movement.
     * @param color              The color of the meteor.
     */
    public Meteor(int velocity, int horizontalVelocity, Paint color) {
        this.velocity = velocity;
        this.dx = horizontalVelocity;

        // Generate a random size within the specified range
        Double size = minSize + (maxSize - minSize) * randShape.nextDouble();

        // Randomly pick one of three shapes for the meteor
        switch (randShape.nextInt(4)) {
            case 0:
                Circle circle = new Circle(size, color);
                shape = circle;
                shapeName = "circle";
                break;
            case 1:
                Rectangle square = new Rectangle(size, size);
                square.setFill(color);
                shape = square;
                shapeName = "square";
                break;
            case 2:
                Polygon polygon = new Polygon(0.0, 0.0, size, 0.0, size, size);
                polygon.setFill(color);
                shape = polygon;
                shapeName = "polygon";
                break;
            case 3:
                Rectangle rectangle = new Rectangle(size+15, size);
                rectangle.setFill(color);
                shape = rectangle;
                shapeName = "rectangle";
                break;
            default:
                System.out.println("Error: Shape not found.");
                break;
        }

        // Initialize the falling animation
        startFalling();
    }

    /**
     * Sets the position of the meteor. 
     * Used for setting the range of the positions they will fall onto the screen at.
     * 
     * @param x New x-coordinate
     * @param y New y-coordinate
     */
    public void setPosition(double x, double y) {
        this.x = (int) x;
        this.y = (int) y;
        shape.setLayoutX(x);
        shape.setLayoutY(y);
    }

    /**
     * Starts the falling animation, simulating gravity.
     */
    private void startFalling() {
        timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> move()));
        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
        timeline.play();
    }

    /**
     * Moves the meteor shape down by the velocity amount and adds horizontal movement.
     */
    private void move() {
        y += velocity; // Increment the y-coordinate by the vertical velocity
       // x += randSlope.nextDouble() * 5; // Increment the x-coordinate by the horizontal velocity IS CAUSEING THE SHAKING EFFECT.

        // Update the position of the meteor
        setPosition(x, y);
    }

    /**
     * Stops the falling animation.
     */
    public void stopFalling() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Handles deflect action to reverse and double the velocity.
     */
    public void deflect() {
        velocity = -velocity * 2; // Reverse the vertical velocity
        dx = -dx; // Reverse the horizontal direction
    }

    // Getters and Setters
    public int getX() {
        return x;
    }

    public void setX(int x) {
        setPosition(x, y);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        setPosition(x, y);
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Double getMaxSize() {
        return maxSize;
    }

    public Double getMinSize() {
        return minSize;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public String getShapeName() {
        return shapeName;
    }
}
