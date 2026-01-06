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
 * This only works to change the game states
 */
public class AreaButton {
    private double x = 0;
    private double y = 0;
    private double size = 0;
    private Text text;
    private double timer = 0;
    Shape shape;
    gameState toState; //Desired state when button used

    
    public AreaButton(double x, double y, double size, String text, gameState toState)
    {
        this.x = x;
        this.y = y;
        this.size = size;
        this.text = new Text(text);
        this.toState = toState;

        Rectangle rect = new Rectangle( x, y, size, size);
        rect.setStroke(Color.WHITE);
        this.shape = rect;
    }

    public Shape getShape()
    {
        return shape;
    }
    public void increment()
    {
        this.timer++;
    }
    public void decrement()
    {
        this.timer--;
    }

    public gameState getState()
    {
        return toState;
    }
    public double getTimer()
    {
        return this.timer;
    }



}
