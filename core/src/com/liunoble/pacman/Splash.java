package com.liunoble.pacman;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Carson on 12/3/2015.
 */

public class Splash implements Screen {

    private SpriteBatch batch;
    private Texture splsh;
    private Game MainGame;

    public Splash(Game g)
    {
        MainGame = g;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        splsh = new Texture(Gdx.files.internal("SplashAssets/splash.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(splsh, 0, 0);
        batch.end();

        if (Gdx.input.justTouched())
            MainGame.setScreen(new MenuScreen(MainGame));
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
