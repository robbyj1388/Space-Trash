package com.example;



import com.example.MainGame.gameState;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


/**
 * AreaButton
 * ----------
 * Designates an area to be a button that increments/decrements based on how long the paddles are at
 * This only works to change the **game states**
 */
public class AreaButton { //TODO: BUG: AreaButton doesnt actually get removed from room
    private double size = 0;
    private Text text;
    private double timer = 0;
    private double end_timer = 60; //How long to hover on the button
    Rectangle shape;
    Rectangle inner_shape;
    gameState toState; //Desired state when button used

    
    public AreaButton(double x, double y, double size, String text, gameState toState)
    {
        this.size = size;
        this.toState = toState; //What the gamestate gets changed to
        Rectangle rect = new Rectangle( x , y , size, size);
        Rectangle inner_rect = new Rectangle( x, y, size, size); // The overlay to showcase how close to be done
        rect.setStroke(Color.WHITE);
        rect.setFill(Color.TRANSPARENT);
        inner_rect.setFill(Color.WHITE);
        rect.setArcHeight(size/4);
        rect.setArcWidth(size/4);
        inner_rect.setArcHeight(size/4);
        inner_rect.setArcWidth(size/4);

        //Text
        Text t = new Text(x+(size/2), y+(size/2), ""); //TODO: Get text aligned correctly
        t.setFill(Color.WHITE);
        t.setTextAlignment(TextAlignment.CENTER);
        this.shape = rect;
        this.inner_shape = inner_rect;
        this.text = t;
        setPos(x, y);
        
    }
    /**
     * Gets the buttons shape
     * @return Shape
     */
    public Shape getShape()
    {
        //return Shape.union(inner_shape, shape);
        return shape; 
    }
    public Shape getInnerShape() {
        return inner_shape;
    }
    public Text getText() {
        return text;
    }

    /**
     * Increments the buttons timer
     */
    public void increment()
    {
        timer++;
        double timer_complete = ( timer / end_timer ); //How complete the timer is
        changeInnerSize( timer_complete );
    }
    /**
     * Decrements the buttons timer
     */
    public void decrement()
    {
        if(timer > 0){
           timer--;
        }
        double timer_complete = ( timer / end_timer ); //How complete the timer is
        changeInnerSize( timer_complete );
    }
    
    /**
     * Changes the size of the inner shape
     * @return
     */
    public void changeInnerSize(double size) {
        inner_shape.setScaleX(size);
        inner_shape.setScaleY(size);
    }
    /**
     * Gets the buttons target state
     * @return gameState
     */
    public gameState getState()
    {
        return toState;
    }
    /**
     * Gets the buttons timer
     * @return double
     */
    public double getTimer()
    {
        return timer;
    }

    public void setPos(double x, double y) {
        x -= size/2;
        y -= size/2;
        shape.relocate(x, y);
        inner_shape.relocate(x, y);
        text.relocate((x+size/2), y+size/2);
    }



}
