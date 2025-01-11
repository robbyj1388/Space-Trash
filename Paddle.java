import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Paddle extends Rectangle {
    private double speed = 5.0;
    

    public Paddle(double x, double y, double width, double height, Color color) {
        super(width, height);
        setX(x);
        setY(y);
        setFill(color);
    }

    public void moveUp() {
        if (getY() > 0) {
            setY(getY() - speed);
        }
    }

    public void moveDown(double sceneHeight) {
        if (getY() + getHeight() < sceneHeight) {
            setY(getY() + speed);
        }
    }
}
