package com.liunoble.pacman;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Carson on 12/4/2015.
 * Class to package all important functionality of objects on-screen that act like buttons.
 */
public class Button
{
    private Texture buttonTexture;
    private int xCoord;
    private int yCoord;
    private int width;
    private int height;

    public Button(Texture t, int x, int y)
    {
        buttonTexture = t;
        xCoord = x;
        yCoord = y;
        width = t.getWidth();
        height = t.getHeight();
    }

    /**
     * Constructor for centered images on screen
     * @param t Texture file provided for button
     * @param y Y coordinate of button
     */
    public Button(Texture t, int y)
    {
        buttonTexture = t;
        xCoord = (1200-t.getWidth())/2;
        yCoord = y;
        width = t.getWidth();
        height = t.getHeight();
    }

    /**
     * Determines if a screen touch is inside bounds of button
     * @param screenX X coordinate of touch.
     * @param screenY Y coordinate of touch.
     */
    public boolean contains(int screenX, int screenY)
    {
        return (xCoord < screenX && xCoord+width > screenX && yCoord <(1824-screenY) && yCoord+height > (1824-screenY));
    }

    /**
     * @return texture of button
     */
    public Texture getTexture()
    {
        return this.buttonTexture;
    }

    /**
     * @return X coordinate of button origin
     */
    public int getX()
    {
        return this.xCoord;
    }

    /**
     * @return Y coordinate of button origin
     */
    public int getY()
    {
        return this.yCoord;
    }
}
