package src;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Paddle extends Rectangle {
    private double speed = 10.0;

    public Paddle(double x, double y, double width, double height, Color color) {
        super(width, height);
        setX(x);
        setY(y);
        setFill(color);
    }

    /**
     * Move paddles y coord up by subtracting the speed.
     */
    public void moveUp() {
        if (getBoundsInParent().getMaxY() > 0) {
            setY(getY() - speed);
        }
    }

    /**
     * Move paddles y coord up by add the speed.
     * 
     * @param sceneHeight the height of the scene.
     */
    public void moveDown(double sceneHeight) {
        if (getBoundsInParent().getMaxY() + getHeight() < sceneHeight) {
            setY(getY() + speed);
        }
    }

    /**
     * Move paddles x coord up by subtracting the speed.
     * 
     * @param sceneWidth the width of the scene.
     */
    public void moveLeft() {
        if (getBoundsInParent().getMaxX() - getWidth() > 0) {
            setX(getX() - speed);
        }
    }

    /** 
     * Mov e paddles x coord up by add the sp eed.
     * 
     * @param sceneWidth the width of the scene.
     */
    public void moveRight(double sceneWidth){
        if(getBoundsInParent().getMaxX() < sceneWidth){
            setX(getX() + speed);
        }
    }
}
