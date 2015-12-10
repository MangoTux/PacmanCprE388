package com.liunoble.pacman.Gameplay;

import com.badlogic.gdx.math.Vector2;

/**
 * Player-controlled entity. Uses swipes to change movement and picks up dots.
 */
public class Player extends Entity
{

    private int score; //10 for dot, 50 for big pill, 200 for spooped ghost
    private int lives;
    private boolean isDying;
    public int chompCount;

    public boolean isMoving;
    public Player(int gridX, int gridY)
    {
        this.grid = new Vector2(gridX, gridY);
        this.current = GridToPixel(this.grid);
        //Conventional direction
        this.direction = Direction.Left;
        this.nextDirection = Direction.Left;
        this.isMoving = false;

        this.lives = 3;
        this.score = 0; //Score starts at zero!
        this.chompCount = 0;
    }

    public void updateScore(int i)
    {
        this.score += i;
    }

    public int getScore()
    {
        return this.score;
    }

    public int getLives()
    {
        return this.lives;
    }

    // It seems like I can handle these a whole lot better.
    public int loseLife() { this.isDying = true; return --this.lives; }

    // For use on new levels and death - resets all info
    public void reset()
    {
        // End dying animation/behaviors, if ongoing
        this.isDying = false;
        // Set starting direction as formality
        this.direction = Direction.Left;
        this.nextDirection = Direction.Left;

        // Reset player position
        resetPosition(14, 26);
        // As a formality
        this.isMoving = false;
    }

    public boolean isDying()
    {
        return this.isDying;
    }
}
