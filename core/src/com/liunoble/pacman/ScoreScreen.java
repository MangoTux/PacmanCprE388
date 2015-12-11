package com.liunoble.pacman;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.input.GestureDetector;

/**
 * Created by Carson on 12/3/2015.
 * Displays top 5 (10? screen size) high scores, with format /Rank/Name/Score
 */
public class ScoreScreen implements Screen, InputProcessor
{
    private Game MainGame;
    private SpriteBatch batch;
    private Button backButton;
    private BitmapFont textFont;

    private boolean backSelected = false;

    private Score[] topScores;

    private Color[] scoreColors = {
            new Color(220f/255, 220f/255, 220f/255, 255),  // Light Grey
            new Color(252f/255,   4f/255,   4f/255, 255),  // Red
            new Color(252f/255, 180f/255,  68f/255, 255),  // Light Orange
            new Color(252f/255, 180f/255, 148f/255, 255),  // Salmon
            new Color(244f/255, 204f/255,  52f/255, 255),  // Orange
            new Color(252f/255, 252f/255,   4f/255, 255),  // Gold
            new Color( 20f/255, 180f/255,  44f/255, 255),  // Green
            new Color(  4f/255, 252f/255, 220f/255, 255),  // Teal
            new Color( 20f/255, 156f/255, 180f/255, 255),  // Aqua
            new Color( 12f/255, 212f/255,  36f/255, 255)}; // Green
    /**
     * Sets up the high score screen
     */
    public ScoreScreen(Game g)
    {
        MainGame = g;
        ScoreBuilder scoreBuilder = new ScoreBuilder();
        topScores = scoreBuilder.getTopScores();

        Gdx.input.setInputProcessor(this);
    }

    // To be used when sent from gameplay
    public ScoreScreen(Game g, Score s)
    {
        MainGame = g;
        ScoreBuilder scoreBuilder = new ScoreBuilder(s);
        topScores = scoreBuilder.getTopScores();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void show()
    {
        batch = new SpriteBatch();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("SharedAssets/Fonts/textFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 42;
        textFont = generator.generateFont(parameter);
        generator.dispose();

        backButton = new Button(new Texture(Gdx.files.internal("SharedAssets/mainMenuText.png")), (int)(1200*.12), (int)(1824*.12));
        ScoreBuilder scoreBuilder = new ScoreBuilder();
        topScores = scoreBuilder.getTopScores();

        // If high score needs to be entered, receive that input, too.
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
            textFont.setColor(Color.YELLOW);
            textFont.draw(batch, String.format("%4s%9s%9s","RANK","SCORE","NAME"), (int)(1200*.12), (int)(1824*.875));
            for (int i=0; i<10; i++)
            {
                textFont.setColor(scoreColors[i]);
                drawScore(i);
            }
            batch.draw(backButton.getTexture(), backButton.getX(), backButton.getY());
        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK))
            MainGame.setScreen(new MenuScreen(MainGame));
    }

    public void drawScore(int i)
    {
        String scoreString = String.format("%2d.%9d     %s", i+1, topScores[i].getScore(), topScores[i].getName());
        textFont.draw(batch, scoreString, (int)(1200*.15), (int)((1824*.05*(20-i))-1824*.2));
    }

    @Override
    public void resize(int width, int height)
    {

    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {

    }

    @Override
    public boolean keyDown(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        backSelected = backButton.contains(screenX, screenY);
        return backSelected;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        if (backSelected && backButton.contains(screenX, screenY))
            MainGame.setScreen(new MenuScreen(MainGame));
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
