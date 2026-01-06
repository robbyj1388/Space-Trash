package com.example;



import com.example.MainGame.gameState;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;


/**
 * AreaButton
 * ----------
 * Designates an area to be a button that increments/decrements based on how long the paddles are at
 * This only works to change the **game states**
 */
public class AreaButton {
    private double x = 0;
    private double y = 0;
    private double size = 0;
    private Text text;
    private double timer = 0;
    private double end_timer = 60; //How long to hover on the button
    Rectangle shape;
    Rectangle inner_shape;
    gameState toState; //Desired state when button used

    
    public AreaButton(double x, double y, double size, String text, gameState toState)
    {
        this.x = x;
        this.y = y;
        this.size = size;
        this.text = new Text(text);
        this.toState = toState; //What the gamestate gets changed to

        Rectangle rect = new Rectangle( x, y, size, size);
        Rectangle inner_rect = new Rectangle( x, y, 3, 3); // The overlay to showcase how close to be done
        rect.setStroke(Color.WHITE);
        inner_rect.setFill(Color.WHITE);
        this.shape = rect;
        this.inner_shape = inner_rect;
        
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

    /**
     * Increments the buttons timer
     */
    public void increment()
    {
        timer++;
        double timer_complete = ( timer / end_timer ); //How complete the timer is
        inner_shape.setWidth( timer_complete * size );
        inner_shape.setHeight( timer_complete * size );
    }
    /**
     * Decrements the buttons timer
     */
    public void decrement()
    {
        timer--;
        double timer_complete = ( timer / end_timer ); //How complete the timer is
        inner_shape.setWidth( timer_complete * size );
        inner_shape.setHeight( timer_complete * size );
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



}
