import javafx.scene.shape.Rectangle;

public class Player {
    int x = 0;
    int y = 0;
    double distance = 200.0; // between paddles
    int velocity;
    Rectangle leftPaddle = null;
    Rectangle rightPaddle = null;
    Double height;
    Double width;

    /**
     * Constructor for making player paddles
     * 
     * @param height The height of the paddle.
     * @param width  The width of the paddle.
     */
    public Player(Double height, Double width) {
        this.height = height;
        this.width = width;

        leftPaddle = new Rectangle(x, y, this.width, this.height);
        rightPaddle = new Rectangle(x + distance, y, this.width, this.height);

    }

    /**
     * Get x coord
     * 
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Set x coord
     * 
     * @param x New x value
     */
    public void setX(int x) {
        this.x = x;
        leftPaddle.setX(x);
        rightPaddle.setX(x + distance);
    }

    /**
     * Get y coord
     * 
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Set y coord
     * 
     * @param y New y value
     */
    public void setY(int y) {
        this.y = y;
        leftPaddle.setY(y);
        rightPaddle.setY(y + distance);
    }

    /**
     * Get distance between paddles
     * 
     * @return distance
     */
    public Double getDistance() {
        return distance;
    }

    /**
     * Set distance between paddles
     * 
     * @param distance New distance value
     */
    public void setDistance(Double distance) {
        this.distance = distance;
        rightPaddle.setX(x + distance);
        rightPaddle.setY(y);
    }

    /**
     * Get height of paddles
     * 
     * @return height
     */
    public Double getHeight() {
        return height;
    }

    /**
     * Set height of paddles
     * 
     * @param height New height value
     */
    public void setHeight(Double height) {
        this.height = height;
        leftPaddle.setHeight(height);
        rightPaddle.setHeight(height);
    }

    /**
     * Get width of paddles
     * 
     * @return width
     */
    public Double getWidth() {
        return width;
    }

    /**
     * Set width of paddles
     * 
     * @param width New width value
     */
    public void setWidth(Double width) {
        this.width = width;
        leftPaddle.setWidth(width);
        rightPaddle.setWidth(width);
    }
}
