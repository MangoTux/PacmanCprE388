package com.liunoble.pacman.Gameplay;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.liunoble.pacman.Button;
import com.liunoble.pacman.MenuScreen;
import com.liunoble.pacman.Score;
import com.liunoble.pacman.ScoreScreen;

import java.util.ArrayList;

/**
 * Created by Carson  on 12/3/2015.
 */
public class GameplayScreen implements Screen, GestureDetector.GestureListener
{
    private Game MainGame;

    /* Highest abstraction game mechanics */
    public SpriteBatch batch;
    public BitmapFont font;
    public Player player;
    public ArrayList<Ghost> ghostList;
    public Map gameMap;

    /* Helpful constants */
    //Number of rows on sprite sheet
    private static final int FRAME_ROWS = 9;
    private enum GAMESTATE {Startup, Playing, Dying, Paused, LevelComplete, GameOver, HighScore}

    /* Assets */
    // The texture for map and other static elements
    Texture map;
    Texture map_complete;
    Texture highScoreBack;
    // The reference file that contains all mobile elements
    Texture spriteSheet;
    // Texture array that stores player animation frames
    TextureRegion[] playerFrames;
    // Texture array that stores player death animation frames - Distinct from above for clarity
    TextureRegion[] playerDeathFrames;
    // Current animation-step player texture
    TextureRegion currentPlayerFrame;
    // Array of animation frames for each ghost
    TextureRegion[][] ghostFrames;
    TextureRegion[] ghostScaredFrames;
    // Current animation-step ghost textures
    TextureRegion[] currentGhostFrame;
    // Texture used for small and large dots
    TextureRegion dotTexture;
    // Texture array for fruit/score bonuses
    TextureRegion[] fruitList;
    // The 'READY!' text uses 48 pixels
    TextureRegion[] readyText;

    Texture pauseMenu;

    Button  resumeButton;
    Button  quitButton;
    Button[] nameButton;
    Button doneButton;
    char[] name = {0, 0, 0};

    //The sound played at the beginning before control starts
    Music startMusic;
    // The sound played when individual dots are picked up
    // A is the first part, B is the second - Only play A if a single dot is picked up
    Sound chompA;
    Sound chompB;
    // The sound played when a ghost is eaten
    Sound eatGhost;
    // Music that plays when player collides with ghost, to be done concurrently with animation
    Music deathMusic;

    /* Control elements*/
    // Check if the player has just made a fling touch movement
    private boolean hasFlung;
    private boolean delayNeeded = false;
    // Animation cycle
    int cycleIndex = 0;
    int score = 0;
    int ghostCount = 0;
    // If the game has started yet - for setup and starting sound
    private GAMESTATE gameState = GAMESTATE.Startup;
    private GAMESTATE prevState = GAMESTATE.Startup;

    private boolean isScheduled = false;

    private int level = 1; // The current level the user is on

    Timer t = new Timer();

    public GameplayScreen(Game g)
    {
        MainGame = g;
    }

    @Override
    public void show()
    {
        // Create the SpriteBatch used for rendering
        batch = new SpriteBatch();

        // Create the BitmapFont and scale appropriately
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("SharedAssets/Fonts/textFont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 42;
        font = generator.generateFont(parameter);
        generator.dispose();

        // Load the map and sprite sheet
        map = new Texture(Gdx.files.internal("GameplayAssets/map.png"));
        map_complete = new Texture(Gdx.files.internal("GameplayAssets/map_white.png"));
        //TODO use proper texture for this
        highScoreBack = new Texture(Gdx.files.internal("GameplayAssets/highScoreBack.png"));

        spriteSheet = new Texture(Gdx.files.internal("GameplayAssets/sprite_condensed.png"));

        // Instantiate arrays and arrayList with sizes
        playerFrames = new TextureRegion[9];
        playerDeathFrames = new TextureRegion[11];
        ghostList = new ArrayList<Ghost>();
        currentGhostFrame = new TextureRegion[4];
        ghostFrames = new TextureRegion[8][4];
        ghostScaredFrames = new TextureRegion[4];
        fruitList = new TextureRegion[8];
        readyText = new TextureRegion[3];

        // Create the game map and entities
        gameMap = new Map();
        player = new Player(14, 26);
        ghostList.add(new Ghost(0, 14, 14));
        ghostList.add(new Ghost(1, 14, 17));
        ghostList.add(new Ghost(2, 12, 17));
        ghostList.add(new Ghost(3, 16, 17));

        //Splits the sprite map into distinct textures
        TextureRegion[][] temp = TextureRegion.split(spriteSheet, 16, 16);

        // Get player and ghost frames from sprite sheet and fill respective array
        for (int i = 0; i < FRAME_ROWS - 1; i++) {
            playerFrames[i] = temp[0][i]; //[0][i]
            for (int j = 0; j < 4; j++) {
                //noinspection ManualArrayCopy
                ghostFrames[i][j] = temp[j + 1][i];
            }
        }
        playerFrames[FRAME_ROWS - 1] = temp[0][FRAME_ROWS - 1]; // Player has more animation cells than ghosts

        // Build the scared ghost frames
        //noinspection ManualArrayCopy
        for (int i=0; i<4; i++)
        {
            ghostScaredFrames[i] = temp[i+1][8];
        }
        //noinspection ManualArrayCopy
        for (int i=0; i<FRAME_ROWS; i++) {
            playerDeathFrames[i] = temp[7][i];
        }
        playerDeathFrames[9] = temp[6][4];  // An 11-frame animation doesn't fit in 9 tiles.
        playerDeathFrames[10] = temp[6][5];

        // Create the dot and fruit textures
        dotTexture = temp[5][0];
        //noinspection ManualArrayCopy
        for (int i=0; i<8; i++)
            fruitList[i] = temp[5][i+1];
        //noinspection ManualArrayCopy
        for (int i=0; i<3; i++)
            readyText[i] = temp[6][i];

        pauseMenu = new Texture(Gdx.files.internal("GameplayAssets/pauseMenu.png"));
        resumeButton = new Button(new Texture(Gdx.files.internal("GameplayAssets/playButton.png")), 900);
        quitButton   = new Button(new Texture(Gdx.files.internal("GameplayAssets/quitButton.png")), 700);
        //TODO Get proper texture for button
        doneButton   = new Button(new Texture(Gdx.files.internal("GameplayAssets/doneButton.png")), 500);

        nameButton = new Button[6];
        for (int i=0; i<6; i++)
        {
            if (i<3)
            {
                nameButton[i] = new Button(new Texture(Gdx.files.internal("GameplayAssets/button_up.png")), 200+i*350, 1112);
            }
            else
            {
                nameButton[i] = new Button(new Texture(Gdx.files.internal("GameplayAssets/button_down.png")), 200+(i-3)*350, 712);
            }
        }
        // Initialize other components
        batch.enableBlending();
        currentPlayerFrame = playerFrames[8];
        //noinspection ManualArrayCopy
        for (int i=0; i<4; i++) {
            currentGhostFrame[i] = ghostFrames[0][i];
        }

        // Load all sounds and music
        startMusic = Gdx.audio.newMusic(Gdx.files.internal("GameplayAssets/sounds/opening.mp3"));
        startMusic.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music aMusic) {
                gameState = GAMESTATE.Playing;
                player.start();
            }
        });

        chompA = Gdx.audio.newSound(Gdx.files.internal("GameplayAssets/sounds/chomp_A.mp3")); // Wa
        chompB = Gdx.audio.newSound(Gdx.files.internal("GameplayAssets/sounds/chomp_B.mp3")); // Ka

        eatGhost = Gdx.audio.newSound(Gdx.files.internal("GameplayAssets/sounds/eatghost.wav"));

        deathMusic = Gdx.audio.newMusic(Gdx.files.internal("GameplayAssets/sounds/pacman_death.wav"));
        deathMusic.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music aMusic) {
                player.loseLife();
                if (player.getLives() <= 0)
                    gameState = GAMESTATE.GameOver;
                else {
                    player.reset();
                    gameState = GAMESTATE.Playing;
                    t.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            player.start();
                        }
                    }, 1);
                }
            }
        });

        // Play startup music, queue up all events for gameplay
        startMusic.play();

        // Initialize input detection
        Gdx.input.setInputProcessor(new GestureDetector(this));
    }

    @Override
    public void render(float delta)
    {
        switch (gameState)
        {
            case Startup: renderStartup(); break;
            case Playing: renderPlaying(); break;
            case Dying:   renderDying();   break;
            case Paused:  renderPaused();  break;
            case LevelComplete: renderComplete(); break;
            case GameOver: renderGameOver(); break;
            case HighScore: renderHighScore(); break;
        }

        // On pressing BACK, change game state to Paused
        if (Gdx.input.isKeyPressed(Input.Keys.BACK))
        {
            if (gameState != GAMESTATE.Paused) {
                prevState = gameState;
                if (startMusic.isPlaying()) {
                    startMusic.pause();
                }
                gameState = GAMESTATE.Paused;
            }
            if (gameState == GAMESTATE.Paused) {
                if (prevState == GAMESTATE.Startup)
                {
                    gameState = prevState;
                    startMusic.play();
                }
            }
        }
    }

    /**
     * Render methods also include setup/processing information for the render.
     */

    /**
     * On startup, draw map, 'ready!' text, and play sound. Switches to playing
     */
    private void renderStartup()
    {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        // Draw Map
        batch.draw(map, 0, 0);
        // Draw 'Ready!' text
        for (int i=0; i<3; i++)
            batch.draw(readyText[i], 474+i*84, 906, 84, 84);
        drawScore();
        drawDots();
        drawLives();
        drawPlayer();
        drawGhosts();
        batch.end();
    }

    /**
     * Playing involves whole game map and gameplay controls.
     */
    private void renderPlaying()
    {
        // Determine player and ghost animation steps based on position in cycle.
        if (player.isMoving && player.moveable) {
            if (cycleIndex / 8 == 0) {
                currentPlayerFrame = playerFrames[8]; // Fully closed position, constant for any direction
            } else {
                currentPlayerFrame = playerFrames[player.direction.ordinal() * 2 + ((cycleIndex / 8) + 1) % 2]; // 2 parts of 3-step animation for directional movement
            }
        } else {
            currentPlayerFrame = playerFrames[player.direction.ordinal() * 2]; // When a player is stuck, snap to specific animation frame
        }

        for (int i = 0; i < 4; i++)
        {
            if (ghostList.get(i).mode == Ghost.Mode.Spooked)
            {
                currentGhostFrame[i] = ghostScaredFrames[(cycleIndex/8)%2];
            }
            else {
                currentGhostFrame[i] = ghostFrames[ghostList.get(i).direction.ordinal() * 2 + (cycleIndex / 8) % 2][i];
            }
        }

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //--Drawing--//
        batch.begin();
        // Draw map
        batch.draw(map, 0, 0);
        drawScore();
        drawLives();
        drawDots();
        drawPlayer();
        drawGhosts(); // Something should really be done about these guys.
        batch.end();

        if (delayNeeded)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            delayNeeded = false;
        }

        //--Game Logic Updates--//
        // Update player and ghost positions
        movePlayer();
        moveGhosts();
        // Check for collisions
        for (Ghost g : ghostList)
        {
            if (player.grid.equals(g.grid)) // && Not energizer-ed
            {
                if (g.mode != Ghost.Mode.Hide && g.mode != Ghost.Mode.Spooked) {
                    cycleIndex = 0;
                    player.stop();
                    deathMusic.play();
                    gameState = GAMESTATE.Dying;
                    break;
                } else if (g.mode == Ghost.Mode.Spooked)
                {
                    eatGhost(g);
                }
            }
        }
        sleep(60); // FPS delay
        // Increase frame in player animation (during chomp - Should be different for death animation
        cycleIndex++;
        cycleIndex %= 32; //8 cycles per frame for a 4-frame animation
    }

    /**
     * Display map without ghosts, and cycle through death animation.
     */
    private void renderDying()
    {
        currentPlayerFrame = playerDeathFrames[cycleIndex/9];

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        // Draw map
        batch.draw(map, 0, 0);
        drawDots();
        drawScore();
        drawPlayer();
        drawLives();
        batch.end();

        cycleIndex++;

        if (cycleIndex >= 11*9)
        {
            cycleIndex = 0; // For use in renderPlaying
            delayNeeded = true;
            return;
        }

        sleep(60); // FPS delay
    }

    /**
     * For future reference, migrate clear to other methods - Everything on pause menu is static.
     * Display everything, overlayed with interactable pause menu.
     */
    private void renderPaused()
    {
        batch.begin();
        batch.draw(map, 0, 0);
        drawDots();
        drawScore();
        drawLives();
        batch.draw(pauseMenu, (1200 - pauseMenu.getWidth()) / 2, (1824 - pauseMenu.getHeight()) / 2);
        batch.draw(resumeButton.getTexture(), resumeButton.getX(), resumeButton.getY());
        batch.draw(quitButton.getTexture(), quitButton.getX(), quitButton.getY());
        batch.end();
    }

    /**
     * On level complete, pause movement and flash screen
     */
    private void renderComplete()
    {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw((cycleIndex) / 4 % 2 == 0 ? map : map_complete, 0, 0, 1200, 1824);
        drawPlayer();
        batch.end();
        cycleIndex++;
        sleep(15);
        if (cycleIndex > 30) nextLevel();

    }

    private void renderGameOver()
    {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(map, 0, 0);
        font.setColor(Color.WHITE);
        drawScore();
        font.setColor(Color.RED);
        font.draw(batch, "game over", 411, 969);
        batch.end();
        if (!isScheduled)
        {
            t.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    endGame();
                }
            }, 5);
            isScheduled = true;
        }
    }

    private void renderHighScore()
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        font.setColor(Color.YELLOW);
        batch.begin();
        batch.draw(highScoreBack, 0, 0);
        // Draw the buttons
        for (int i=0; i<6; i++)
        {
            batch.draw(nameButton[i].getTexture(), nameButton[i].getX(), nameButton[i].getY());
        }
        for (int i=0; i<3; i++)
        {
            font.draw(batch, Character.toString((char)(name[i]+65)), 229+i*350, 974);
        }
        batch.draw(doneButton.getTexture(), doneButton.getX(), doneButton.getY());
        batch.end();
    }

    /**
     * On successful completion of any given level, set up a new map and
     */
    private void nextLevel()
    {
        // Initialize a new map to be played on
        gameMap = new Map();
        // Set player position back to its origin
        player.reset();
        // Reset all ghosts to original positions
        ghostList.get(0).resetPosition(14, 14);
        ghostList.get(1).resetPosition(14, 17);
        ghostList.get(2).resetPosition(12, 17);
        ghostList.get(3).resetPosition(16, 17);
        gameState = GAMESTATE.Startup;
        level++;
        // Intermission?
        startMusic.setVolume(0);
        startMusic.play();

    }

    private void endGame()
    {
        Preferences prefs = Gdx.app.getPreferences("scores");
        String score;
        for (int i=0; i<10; i++)
        {
            score = "score"+i;
            if (player.getScore() > prefs.getInteger(score, 0))
            {
                gameState = GAMESTATE.HighScore;
                return;
            }
        }
        MainGame.setScreen(new ScoreScreen(MainGame));
    }

    /**
     * Draws remaining lives as markers in bottom-left
     */
    private void drawLives()
    {
        for (int i=0; i<player.getLives()-1; i++)
        {
            batch.draw(playerFrames[4], 84*i+117, 284, 84, 84);
        }
    }

    /**
     * Draws score in top-left corner
     */
    private void drawScore() {
        font.draw(batch, Integer.toString(player.getScore()), 201, 1746);
    }

    /**
     * Draw all dots on the screen
     */
    private void drawDots()
    {
        for (int i = 0; i < 36; i++)
        {
            for (int j = 0; j < 28; j++)
            {
                Vector2 coord = Entity.GridToPixel(new Vector2(j, i));
                if (gameMap.grid[i][j] == '.' || gameMap.grid[i][j] == ',') // Replace with actual dot sprites
                    batch.draw(dotTexture, coord.x - 7, coord.y - 7, 14, 14);
                else if (gameMap.grid[i][j] == 'o')
                    batch.draw(dotTexture, coord.x - 21, coord.y - 21, 42, 42);
            }
        }
    }

    private void drawPlayer() {
        this.drawScaled(batch, currentPlayerFrame, player.getCurrentX(), player.getCurrentY());
    }

    private void drawGhosts() // Currently an issue with centered-ness because of origin offset/ghost house shenanigans
    {
        for (int i = 0; i < 4; i++)
        {
            this.drawScaled(batch, currentGhostFrame[i], ghostList.get(i).getCurrentX(), ghostList.get(i).getCurrentY());
        }
    }

    @Override
    public void resize(int width, int height)
    {

    }

    // Draws sprites centered, based on 42-pixel tiles.
    public void drawScaled(SpriteBatch batch, TextureRegion img, int x, int y)
    {
        batch.draw(img, x - 39, y - 39, 39 * 2, 39 * 2);
    }

    // Handles all movement/scoring/logic for the player
    // Ugly, but it works!
    public void movePlayer()
    {
        if (player.moveable)
        {
            //The new direction of movement has a higher priority
            if (hasFlung && player.nextDirection != player.direction && gameMap.canMove(player, player.nextDirection)) {
                player.changeDirection(player.nextDirection);
                hasFlung = false; //Consume the fling
                player.isMoving = true;
                // As of right now, the only speeds that work are numbers that yield an integer when 42 is divided by speed
                // Rework move as series of single-space move/check canMove (If time)
                player.move(5.25f);

                player.updateScore(gameMap.gridEvent(player));
            } //If it can't move in a new direction,
            else if (gameMap.canMove(player, player.direction)) {
                player.isMoving = true;
                player.move(5.25f);
                if (player.isCentered())
                    dotHandler();
            } else {
                player.isMoving = false;
            }
        }
    }

    // Take care of all actions relating to score/dot pickup here
    public void dotHandler()
    {
        // Perform the grid event and get score
        score = gameMap.gridEvent(player);

        if (score != 0) // Small dot or energizer
        {
            if (player.chompCount++ % 2 == 0)
                chompA.play(); //Wa
            else
                chompB.play(); //Ka

            if (score == 50) // Energizer
            {
                scareGhosts();
            }
        }
        else
            player.chompCount = 0;
        // Update the score for the player here
        player.updateScore(score);

        // TODO Set up Dot count handlers for ghost behavior
        if (gameMap.getNumDots() == 0)
        {
            gameState = GAMESTATE.LevelComplete;
            cycleIndex = 0;
        }
    }

    private void scareGhosts()
    {
        for (Ghost g : ghostList)
        {
            g.mode = Ghost.Mode.Spooked;
        }
        t.clear(); // Clear task schedule for new delay setting
        t.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                for (Ghost g : ghostList) {
                    if (g.mode == Ghost.Mode.Spooked) {
                        g.mode = Ghost.Mode.Chase;
                    }
                }
                ghostCount = 0; // Reset ghost count for next energizer
            }
        }, 7);
    }

    //Handles all movement/logic for the list of ghosts
    //TODO implement
    public void moveGhosts()
    {
    }

    /**
     * Called when player collides with ghost in proper state - Sprite for ghost changes to eyes
     * @param g
     */
    public void eatGhost(Ghost g)
    {
        eatGhost.play();
        ghostCount++;
        g.mode = Ghost.Mode.Hide;
        player.updateScore(200*ghostCount);
        try {
            Thread.sleep(333);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause()
    {
        if (startMusic.isPlaying())
            startMusic.pause();
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
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    /**
     * Used to determine if user pressed a button and perform relevant action
     * @param x X-location of tap
     * @param y Y-location of tap
     * @param count ???
     * @param button ???
     * @return boolean if consumer
     */
    @Override
    public boolean tap(float x, float y, int count, int button)
    {
        if (gameState == GAMESTATE.Paused)
        {
            if (resumeButton.contains((int)x, (int)y)) {
                gameState = prevState;
                if (gameState == GAMESTATE.Playing)
                    delayNeeded = true;
            }
            else if (quitButton.contains((int)x, (int)y)) {
                //TODO Handle score submission, treat it as setting lives to 0 - Main menu, or third 'abandon' button?
                MainGame.setScreen(new MenuScreen(MainGame));
            }
        }
        else if (gameState == GAMESTATE.HighScore)
        {
            for (int i=0; i<3; i++)
            {
                if (nameButton[i].contains((int)x, (int)y))
                {
                    name[i]+=27; name[i]%=26; //Modular arithmetic to keep values in bounds
                }
                else if (nameButton[i+3].contains((int)x, (int)y))
                {
                    name[i]+=25; name[i]%=26; //Modular arithmetic
                }
            }
            if (doneButton.contains((int)x, (int)y))
            {
                for (int i=0; i<3; i++)
                {
                    name[i] = (char)(name[i]+65);
                }
                MainGame.setScreen(new ScoreScreen(MainGame, new Score(player.getScore(), new String(name))));
            }
        }
        return true;
    }

    /**
     * Implemented because mandatory
     * @param x X location of press
     * @param y Y location of press
     * @return false
     */
    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    /**
     * Gets fling input from user and interprets as desired direction to move next.
     */
    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if (velocityX > 0 && Math.abs(velocityY) < velocityX)
        {
            player.nextDirection = Entity.Direction.Right;
        }
        else if (velocityX < 0 && Math.abs(velocityY) < Math.abs(velocityX))
        {
            player.nextDirection = Entity.Direction.Left;
        }
        else if (velocityY > 0 &&  Math.abs(velocityX) < velocityY)
        {
            player.nextDirection = Entity.Direction.Down;
        }
        else if (velocityY < 0 && Math.abs(velocityX) < Math.abs(velocityY))
        {
            player.nextDirection = Entity.Direction.Up;
        }
        //Let the game know the player wants a new direction
        hasFlung = true;
        return true;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    private long diff, start = System.currentTimeMillis();
    // FPS sleep used for the render() method
    public void sleep(int fps)
    {
        if (fps > 0) {
            diff = System.currentTimeMillis() - start;
            long targetDelay = 1000 / fps;
            if (diff < targetDelay) {
                try {
                    Thread.sleep(targetDelay - diff);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            start = System.currentTimeMillis();
        }
    }
}
