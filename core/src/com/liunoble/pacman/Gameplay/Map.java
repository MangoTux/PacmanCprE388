package com.liunoble.pacman.Gameplay;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by cmnoble on 11/17/2015.
 */
public class Map
{
    private final char W = 'w';
    private final char D = '.';
    private final char L = 'o';
    private final char E = ' ';
    private final int SIZEX = 28;
    private final int SIZEY = 36;

    private int numDots = 244;
    /*
    The overall map
     */
    public char grid[][] =
            {{E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E},
                    {E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E},
                    {E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E},
                    {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
                    {W,D,D,D,D,D,D,D,D,D,D,D,D,W,W,D,D,D,D,D,D,D,D,D,D,D,D,W},
                    {W,D,W,W,W,W,D,W,W,W,W,W,D,W,W,D,W,W,W,W,W,D,W,W,W,W,D,W},
                    {W,L,W,W,W,W,D,W,W,W,W,W,D,W,W,D,W,W,W,W,W,D,W,W,W,W,L,W},
                    {W,D,W,W,W,W,D,W,W,W,W,W,D,W,W,D,W,W,W,W,W,D,W,W,W,W,D,W},
                    {W,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,W},
                    {W,D,W,W,W,W,D,W,W,D,W,W,W,W,W,W,W,W,D,W,W,D,W,W,W,W,D,W},
                    {W,D,W,W,W,W,D,W,W,D,W,W,W,W,W,W,W,W,D,W,W,D,W,W,W,W,D,W},
                    {W,D,D,D,D,D,D,W,W,D,D,D,D,W,W,D,D,D,D,W,W,D,D,D,D,D,D,W},
                    {W,W,W,W,W,W,D,W,W,W,W,W,E,W,W,E,W,W,W,W,W,D,W,W,W,W,W,W},
                    {E,E,E,E,E,W,D,W,W,W,W,W,E,W,W,E,W,W,W,W,W,D,W,E,E,E,E,E},
                    {E,E,E,E,E,W,D,W,W,E,E,E,E,E,E,E,E,E,E,W,W,D,W,E,E,E,E,E},
                    {E,E,E,E,E,W,D,W,W,E,W,W,W,W,W,W,W,W,E,W,W,D,W,E,E,E,E,E},
                    {W,W,W,W,W,W,D,W,W,E,W,E,E,E,E,E,E,W,E,W,W,D,W,W,W,W,W,W},
                    {E,E,E,E,E,E,D,E,E,E,W,E,E,E,E,E,E,W,E,E,E,D,E,E,E,E,E,E},
                    {W,W,W,W,W,W,D,W,W,E,W,E,E,E,E,E,E,W,E,W,W,D,W,W,W,W,W,W},
                    {E,E,E,E,E,W,D,W,W,E,W,W,W,W,W,W,W,W,E,W,W,D,W,E,E,E,E,E},
                    {E,E,E,E,E,W,D,W,W,E,E,E,E,E,E,E,E,E,E,W,W,D,W,E,E,E,E,E},
                    {E,E,E,E,E,W,D,W,W,E,W,W,W,W,W,W,W,W,E,W,W,D,W,E,E,E,E,E},
                    {W,W,W,W,W,W,D,W,W,E,W,W,W,W,W,W,W,W,E,W,W,D,W,W,W,W,W,W},
                    {W,D,D,D,D,D,D,D,D,D,D,D,D,W,W,D,D,D,D,D,D,D,D,D,D,D,D,W},
                    {W,D,W,W,W,W,D,W,W,W,W,W,D,W,W,D,W,W,W,W,W,D,W,W,W,W,D,W},
                    {W,D,W,W,W,W,D,W,W,W,W,W,D,W,W,D,W,W,W,W,W,D,W,W,W,W,D,W},
                    {W,L,D,D,W,W,D,D,D,D,D,D,D,E,E,D,D,D,D,D,D,D,W,W,D,D,L,W},
                    {W,W,W,D,W,W,D,W,W,D,W,W,W,W,W,W,W,W,D,W,W,D,W,W,D,W,W,W},
                    {W,W,W,D,W,W,D,W,W,D,W,W,W,W,W,W,W,W,D,W,W,D,W,W,D,W,W,W},
                    {W,D,D,D,D,D,D,W,W,D,D,D,D,W,W,D,D,D,D,W,W,D,D,D,D,D,D,W},
                    {W,D,W,W,W,W,W,W,W,W,W,W,D,W,W,D,W,W,W,W,W,W,W,W,W,W,D,W},
                    {W,D,W,W,W,W,W,W,W,W,W,W,D,W,W,D,W,W,W,W,W,W,W,W,W,W,D,W},
                    {W,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,D,W},
                    {W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W,W},
                    {E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E},
                    {E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E,E}};

    public Map()
    {
        //What do here?
    }

    //This method is the bane of my existence.
    //It's ugly, too!
    //Also, it tests if an entity e can move in direction d
    public boolean canMove(Entity e, Entity.Direction d)
    {
        // Get current position of entity
        int gridX = e.getGridX();
        int gridY = e.getGridY();
        int x = e.getCurrentX();
        int y = e.getCurrentY();

        double epsilon = .9; //Fuzzy approximation, increase/decrease this if errors start occurring
        Vector2 currentLocation = e.GridToPixel(new Vector2(gridX, gridY));
        Vector2 nextLocation = new Vector2(0, 0);
        if ((e.direction == Entity.Direction.Down && (gridX == 13 || gridX == 14) && gridY == 14))
            return false;
        // if entity is not in center of grid cell, return true - implied that it's already moving
        //Check if entity is fuzzy in the center of cell, and return true if it's moving forward in that direction
        if ((e.direction == d) && (((d == Entity.Direction.Up || d == Entity.Direction.Down) && !(Math.abs(y - currentLocation.y) < epsilon)) || ((d == Entity.Direction.Left || d == Entity.Direction.Right) && !(Math.abs(x - currentLocation.x) < epsilon))))
            return true;
        // If entity is in fuzzy center, check if grid cell in front of entity is wall or not
        if (e.direction != d) //This would break swiping in the current direction
        {
            //Check if entity is fuzzy in the center of cell
            if (Math.abs(x - currentLocation.x) < epsilon && Math.abs(y - currentLocation.y) < epsilon)
            {
                //Snap to grid to avoid localization errors
                e.current = currentLocation;
            }
            else // Entity is not in bounds to change direction, can return false right now
            {   return false;  }
        }
        //Check if space+1 in not wall
        //If not, return true
        switch (d)
        {
            //One pixel ahead in direction is part of wall element? No.
            case Up:
                nextLocation = e.PixelToGrid(new Vector2(x, y+23)); break;
            case Down:
                nextLocation = e.PixelToGrid(new Vector2(x, y-44)); break;
            case Left:
                nextLocation = e.PixelToGrid(new Vector2(x-22, y));
                if (x < 12) return true; break;
            case Right:
                nextLocation = e.PixelToGrid(new Vector2(x+23, y));
                if (nextLocation.x >= 28) return true; break;
        }
        return (grid[(int)nextLocation.y][(int)nextLocation.x] != W);
    }

    //Checks e event and returns score associated with tile, replacing the tile with an empty square
    public int gridEvent(Entity e)
    {
        if (e.getGridX() >= 28) return 0;
        switch(grid[e.getGridY()][e.getGridX()])
        {
            case D: grid[e.getGridY()][e.getGridX()] = E; numDots--; return 10; //Dots are worth 10
            case L: grid[e.getGridY()][e.getGridX()] = E; numDots--; return 50; //Large dots are worth 50
            default: return 0; //Otherwise, nothing else happens.
        }
    }

    // The number of dots left on the level
    public int getNumDots()
    {
        return this.numDots;
    }
}
