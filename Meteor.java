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
    Double maxSize = 250.0;
    Double minSize = 100.0;
    Random rand = null;
    Timeline timeline; // Animation to simulate gravity.

    /**
     * Constructor for picking a random shape for the meteor.
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
        System.out.println("Meteor deflected!");
    }
}
