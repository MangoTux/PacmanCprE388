package com.liunoble.pacman;

/**
 * Created by Carson on 12/3/2015.
 *
 * Container class to store the score/name pair for top scores
 */
public class Score
{
    private String name;
    private int score;
    public Score(int s, String n)
    {
        score = s; name = n;
    }

    public int getScore()
    {
        return score;
    }

    public String getName()
    {
        return name;
    }
}
