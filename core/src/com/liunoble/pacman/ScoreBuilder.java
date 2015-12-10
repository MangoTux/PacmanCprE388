package com.liunoble.pacman;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Carson on 12/3/2015.
 *
 * Builds the score table for use in the score screen
 */
public class ScoreBuilder
{
    private Score[] topScores;
    Preferences prefs;

    public ScoreBuilder() {
        topScores = new Score[10];
        prefs = Gdx.app.getPreferences("scores");
        String name, score;
        for (int i = 0; i < 10; i++) {
            name = "name"+i;
            score = "score"+i;
            topScores[i] = new Score(prefs.getInteger(score, 1), prefs.getString(name, "BRD"));
        }
        prefs.flush();
    }

    public ScoreBuilder(Score playerScore)
    {
        topScores = new Score[10];
        prefs = Gdx.app.getPreferences("scores");
        String name, score;
        boolean scoreRecorded = false;
        for (int i = 0; i < 10; i++) {
            name = "name"+i;
            score = "score"+i;
            Score currentScore = new Score(prefs.getInteger(score, 1), prefs.getString(name, "BRD"));
            if (!scoreRecorded && currentScore.getScore() < playerScore.getScore())
            {
                shiftDown(i);
                prefs.putString(name, playerScore.getName());
                prefs.putInteger(score, playerScore.getScore());
                scoreRecorded = true;
            }
            topScores[i] = new Score(prefs.getInteger(score, 1), prefs.getString(name, "BRD"));
        }
        prefs.flush();
    }

    public void shiftDown(int s)
    {
        String cName, pName, cScore, pScore;
        for (int i=9; i>s; i--)
        {
            cName = "name"+i;
            pName = "name"+(i-1);
            cScore = "score"+i;
            pScore = "score"+(i-1);

            prefs.putInteger(cScore, prefs.getInteger(pScore));
            prefs.putString(cName, prefs.getString(pName));
        }
    }
    public Score[] getTopScores()
    {
        return this.topScores;
    }
}
