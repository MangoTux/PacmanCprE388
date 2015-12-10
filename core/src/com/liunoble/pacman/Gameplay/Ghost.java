package com.liunoble.pacman.Gameplay;

import com.badlogic.gdx.math.Vector2;

/**
 * Ghosts all have a predetermined path:
 * Scatter (4 corners) for 7, chase 20, scatter 7, chase 20, scatter 5, chase forever
 * Current state is paused when large pill is picked up.
 * State change forces a direction change.
 */
public class Ghost extends com.liunoble.pacman.Gameplay.Entity
{
    // Scatter - Target is corner
    // Chase - Based on Ghost
    final int INKY=0, PINKY=1, BLINKY=2, CLYDE=3;
    public enum Mode {
        Scatter, Spooked, Chase, Hide
    }
    public Mode mode;
    public int ID; // 0:Inky; 1:Pinky; 2: Blinky; 3: Clyde
    public Vector2 target; // Target grid coord
    public int speed; // The speed at which the ghost travels - TODO?

    public Ghost(int id, int gridX, int gridY)
    {
        this.grid = new Vector2(gridX, gridY);
        this.mode = Mode.Scatter;
        this.ID = id;
        this.current = GridToPixel(this.grid);

        this.direction = Entity.Direction.Left; //Convention after ghosthouse
        this.nextDirection = Entity.Direction.Down;
    }
    //Selects target
    public void getTarget(Player p)
    {
        if (mode == Mode.Scatter || mode == Mode.Spooked)
        {
            switch(ID)
            {
                case INKY: target.x = 27; target.y = 35; break;    //Bottom right
                case PINKY: target.x = 0; target.y = 0; break;  //Top left
                case BLINKY: target.x = 27; target.y = 0; break; //Top right
                case CLYDE: target.x = 0; target.y = 35; break; //Bottom left
            }
        }
        else if (mode == Mode.Chase)
        {
            switch(ID)
            {
                case INKY: break;
                case PINKY: break;
                case BLINKY: target.x = p.getGridX(); target.y = p.getGridY(); break;
                case CLYDE: break;
            }
        }
        else if (mode == Mode.Hide)
        {
            //TODO Target x, y are ghost house
        }
    };
}
