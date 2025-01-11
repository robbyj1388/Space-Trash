import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class Meteor{
    int x = 0;
    int y = 0;
    int velocity; // Stores the speed at which the meteor falls.
    Shape shape = null;
    Double maxSize = 50.0;
    Double minSize = 30.0;
    Random rand = null;
    Timeline timeline; // Animation to simulate gravity.

    /**
     * Constructor for picking a random shape for the meteor.
     * 
     * @param velocity The speed of the meteor's fall.
     * @param color The color of the meteor.
     */
    public Meteor(int velocity, Paint color) {
        this.velocity = velocity;
        rand = new Random(); // make a new random value for each meteor.
        // Makes a double withing the range of minSize to maxSize
        Double size = minSize + (maxSize - minSize) * rand.nextDouble();

        // Pick 1 of 3 random shapes for meteor.
        switch (rand.nextInt(3)) {
            case 0:
                Circle circle = new Circle(size, color);
                shape = circle;
                break;
            case 1:
                Rectangle rectangle = new Rectangle(
                    size, 
                    size);
                rectangle.setFill(color);
                shape = rectangle;
                break;
            case 2:
                Polygon polygon = new Polygon(
                    0.0, 0.0, 
                    size, 0.0, 
                    size, size);
                polygon.setFill(color);
                shape = polygon;
                break;
            default:
                System.out.println("404 shape not found.");
                break;
        }

        // Initialize the falling animation
        startFalling();
    }

    /**
     * Starts the falling animation, simulating gravity.
     */
    private void startFalling() {
        timeline = new Timeline(new KeyFrame(Duration.millis(16), 
        e -> moveDown()));

        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat after 16ms;
        timeline.play();
    }

    /**
     * Moves the meteor shape down by the velocity amount.
     */
    private void moveDown() {
        y += velocity; // Increment the y-coordinate by the velocity.

        // Update the shape's position
        shape.setLayoutY(y);
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
        velocity *= -2; // Invert and double the velocity
    }
    
    /**
     * Get x coordinate.
     * 
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Set x coordinate and update shape position.
     * 
     * @param x New x coordinate
     */
    public void setX(int x) {
        this.x = x;
        shape.setLayoutX(x);
    }

    /**
     * Get y coordinate.
     * 
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Set y coordinate and update shape position.
     * 
     * @param y New y coordinate
     */
    public void setY(int y) {
        this.y = y;
        shape.setLayoutY(y);
    }

    /**
     * Get velocity of meteor.
     * 
     * @return velocity
     */
    public int getVelocity() {
        return velocity;
    }

    /**
     * Set velocity of meteor.
     * 
     * @param velocity New velocity
     */
    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    /**
     * Get shape of meteor.
     * 
     * @return shape
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Set shape of meteor.
     * 
     * @param shape New shape
     */
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    /**
     * Get maximum size of meteor.
     * 
     * @return maxSize
     */
    public Double getMaxSize() {
        return maxSize;
    }

    /**
     * Set maximum size of meteor.
     * 
     * @param maxSize New maximum size
     */
    public void setMaxSize(Double maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Get minimum size of meteor.
     * 
     * @return minSize
     */
    public Double getMinSize() {
        return minSize;
    }

    /**
     * Set minimum size of meteor.
     * 
     * @param minSize New minimum size
     */
    public void setMinSize(Double minSize) {
        this.minSize = minSize;
    }

    /**
     * Get timeline for meteor's animation.
     * 
     * @return timeline
     */
    public Timeline getTimeline() {
        return timeline;
    }

    /**
     * Set timeline for meteor's animation.
     * 
     * @param timeline New timeline
     */
    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }
}
