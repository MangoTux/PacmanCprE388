package com.liunoble.pacman.Gameplay;

import com.badlogic.gdx.math.Vector2;

/**
 * Abstract entity class - contains position information/movement info
 */
public abstract class Entity
{
    public Vector2 current; //The pixelspace coordinates, origin bottom-left
    public Vector2 grid; //The gridspace coordinates, origin top-left
    public Direction direction;
    public Direction nextDirection; // The direction to move as soon as it can.
    public boolean moveable;        // Used for level completion and death
    public enum Direction {
        Up, Down, Left, Right;
    }
    //Gets the X position of the entity
    public int getCurrentX()
    {
        return (int)this.current.x;
    }
    //Gets the Y position of the entity
    public int getCurrentY()
    {
        return (int)this.current.y;
    }
    //Gets the X grid position of the entity
    public int getGridX()
    {
        return (int)this.grid.x;
    }
    //Gets the Y grid position of the entity
    public int getGridY()
    {
        return (int)this.grid.y;
    }

    public void move(float speed)
    {
        if (moveable) {
            switch (direction) {
                case Down:
                    current.y -= speed;
                    if (current.y < 0)
                        current.y = 1919;
                    break;
                case Up:
                    current.y += speed;
                    if (current.y > 1920)
                        current.y = 0;
                    break;
                case Left:
                    current.x -= speed;
                    if (current.x < 0)
                        current.x = GridToPixel(new Vector2(27, 0)).x;
                    break;
                case Right:
                    if (current.x > 1200)
                        current.x = GridToPixel(new Vector2(0, 0)).x;
                    current.x += speed;
                    break;
            }
            grid = PixelToGrid(current);
        }
    }

    //Returns the grid encompassing the pixel location (Working, probably!)
    public static Vector2 PixelToGrid(Vector2 pixelCoord)
    {
        //Keep in integer domain
        int x = (int)pixelCoord.x;
        x -= 12;    // 12 pixel left-offset due to screen imperfection
        x /= 42;    // 42-pixel grids, origin-x is left side for both

        int y = (int)pixelCoord.y;
        y -= 15;    // 15 pixel bottom-offset due to screen imperfection
        y = 1824 - y;  // Total working height of image, grid origin-y is top
        y /= 42;    // 42-pixel grids
        y -= 1;
        return new Vector2(x, y);
    }

    //Returns the pixel at the center of the grid location (Working!)
    public static Vector2 GridToPixel(Vector2 gridCoord)
    {
        //Keep in integer domain
        int x = (int)gridCoord.x;
        x *= 42;
        x += 12;    // I like x. X is simple.
        x += 21;
        int y = (int)gridCoord.y;
        y += 1;
        y *= 42;    // Adjust to pixel length
        y = 1824 - y;  // Flip the y axis
        y += 6;
        return new Vector2(x, y);
    }

    //Changes entity direction to newDir
    public void changeDirection(Direction newDir)
    {
        this.direction = newDir;
        if (this.direction == Direction.Up || this.direction == Direction.Down)
        {
            this.current.y = GridToPixel(this.grid).y; //Snaps user to y axis for up/down movement, avoid offset errors
        }
        else if (this.direction == Direction.Left || this.direction == Direction.Right) //Snaps user to x axis for left/right movement, avoid offset errors
        {
            this.current.x = GridToPixel(this.grid).x;
        }
    }

    public void stop()
    {
        this.moveable = false;
    }

    public void start()
    {
        this.moveable = true;
    }

    // Resets the entity position when needed, using same format as constructor.
    public void resetPosition(int gx, int gy)
    {
        this.grid = new Vector2(gx, gy);
        this.current = GridToPixel(this.grid);
    }

    // Returns whether or not entity is centered on a grid space: Same center as dot.
    public boolean isCentered()
    {
        Vector2 gridPos = GridToPixel(this.grid);
        return (Math.abs(getCurrentX() - gridPos.x) < 1 && Math.abs(getCurrentY() - gridPos.y) < 1);
    }
}
