package com.liunoble.pacman;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.liunoble.pacman.*;

/**
 * Created by Carson on 12/3/2015.
 */
public class MenuScreen implements Screen, InputProcessor
{
    private SpriteBatch batch;

    private Texture mainMenuTexture;

    private Button newGameButton;
    private Button scoresButton;
    private Button soundButton;

    private Game MainGame;

    private Music menuMusic;

    private Texture soundTexture[];
    private int soundIndex;

    private boolean newGameSelected = false;
    private boolean scoresSelected = false;
    private boolean soundSelected = false;

    private Preferences audioPreferences;

    public MenuScreen(Game g)
    {
        MainGame = g;
    }
    /**
     * Called when this screen becomes the current screen for a Game
     */
    @Override
    public void show()
    {
        batch = new SpriteBatch();
        mainMenuTexture = new Texture(Gdx.files.internal("MenuAssets/mainScreen.png"));
        newGameButton = new Button(new Texture(Gdx.files.internal("MenuAssets/newGameText.png")), (int)(1824*.55f));
        scoresButton = new Button(new Texture(Gdx.files.internal("MenuAssets/scoresText.png")), (int)(1824*.45f));
        soundButton = new Button(new Texture(Gdx.files.internal("MenuAssets/volumeBounds.png")), (int)(1200*.85), (int)(1824*.1));

        soundTexture = new Texture[2];
        soundTexture[0] = new Texture(Gdx.files.internal("MenuAssets/muted.png"));
        soundTexture[1] = new Texture(Gdx.files.internal("MenuAssets/unmuted.png"));

        audioPreferences = Gdx.app.getPreferences("options");
        soundIndex = audioPreferences.getBoolean("muted", false)?0:1;

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("MenuAssets/Sounds/menu.mp3"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(.75f);

        if (!audioPreferences.getBoolean("muted", false))
        {
            menuMusic.play();
        }

        Gdx.input.setInputProcessor(this);
    }

    /**
     * The render method for the main menu
     * @param delta The time in seconds since the last render
     */
    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
            batch.draw(mainMenuTexture, 0, 0);
            batch.draw(newGameButton.getTexture(), newGameButton.getX(), newGameButton.getY());
            batch.draw(scoresButton.getTexture(), scoresButton.getX(), scoresButton.getY());
            batch.draw(soundTexture[soundIndex], soundButton.getX(), soundButton.getY(), 84, 84);
        batch.end();
        // Back behavior is to exit the application here. Other screens take user here
        if (Gdx.input.isKeyPressed(Input.Keys.BACK))
            Gdx.app.exit();
    }

    @Override
    public void resize(int width, int height) {

    }

    // Save preferences on pause
    @Override
    public void pause() {
        audioPreferences.flush();
    }

    @Override
    public void resume()
    {

    }

    /**
     * Called when the screen is no longer the current screen for the Game
     */
    @Override
    public void hide()
    {
        if (menuMusic.isPlaying())
            menuMusic.stop();
        audioPreferences.flush();
    }

    /**
     * Dispose all resources here
     */
    @Override
    public void dispose() {
        audioPreferences.flush();
    }

    @Override
    public boolean keyDown(int keycode) {
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

    /**
     * We get some signal bouncing and the program gets jumpy if this isn't implemented. Records if the user is intending to press either button
     * @param screenX X-coordinate of screen press
     * @param screenY Y-coordinate of screen press
     * @param pointer
     * @param button
     * @return whether or not this event was consumed here
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        newGameSelected = newGameButton.contains(screenX, screenY);
        scoresSelected = scoresButton.contains(screenX, screenY);
        soundSelected = soundButton.contains(screenX, screenY);
        return (newGameSelected || scoresSelected || soundSelected);
    }

    // Used to select menu item
    // screenY origin is top for this.
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        if (newGameSelected && newGameButton.contains(screenX, screenY))
        {
            audioPreferences.flush();
            // Set screen to GameplayScreen
            MainGame.setScreen(new com.liunoble.pacman.Gameplay.GameplayScreen(MainGame));
        }
        else if (scoresSelected && scoresButton.contains(screenX, screenY))
        {
            audioPreferences.flush();
            // Set screen to ScoreScreen
            MainGame.setScreen(new ScoreScreen(MainGame));
        } else if (soundSelected && soundButton.contains(screenX, screenY))
        {
            audioPreferences.putBoolean("muted", !audioPreferences.getBoolean("muted"));
            soundIndex = 1-soundIndex;
            if (menuMusic.isPlaying())
                menuMusic.pause();
            else
                menuMusic.play();
        }
        return true;
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
