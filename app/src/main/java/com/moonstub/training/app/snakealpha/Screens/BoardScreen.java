package com.moonstub.training.app.snakealpha.Screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;

import com.moonstub.training.app.snakealpha.GameAssets;
import com.moonstub.training.app.snakealpha.GameState;
import com.moonstub.training.app.snakealpha.framework.GameActivity;
import com.moonstub.training.app.snakealpha.framework.GameGraphics;
import com.moonstub.training.app.snakealpha.framework.GameScreen;
import com.moonstub.training.app.snakealpha.framework.GameSettings;
import com.moonstub.training.app.snakealpha.input.TouchEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardScreen extends GameScreen {

    ImageSection apple;
    ArrayList<ImageSection> snake;
    ArrayList<Section> level;
    SnakeDirection mDirection = SnakeDirection.EAST;
    boolean firstStart = true;
    boolean forward = true;
    String scoreMessage = "";
    String gameMessage = "";
    String gameMessage2 = "";
    String gameMessage3 = "";
    String gameMessage4 = "";
    float elapsedTime = 0.0f;
    Boolean isFoodRotten = false;
    String gameMessageScore = "";
    private List<Section> levelSection;


    public BoardScreen(GameActivity game, ArrayList<Section> levelSection) {
        super(game);
        level = levelSection;
    }
    public void rotten() {
        level.add(new Section(0,0,0));
        level.get(level.size() - 1).setPoint(apple.x, apple.y);
        isFoodRotten=false;
        dropApple();
    }

    @Override
    public void init() {
        GameSettings.SCORE=100*GameSettings.lvl;

        /*
        LoadLevel loadLevel = new LoadLevel(getGameActivity().getGameIO());
        loadLevel.loadFile("level_1_01.txt");
        levelSection = loadLevel.parseString(loadLevel.stringLevel);*/

        apple = new ImageSection(GameAssets.SnakeFoodApple);
        firstStart = true;
        mDirection = SnakeDirection.EAST;
        int x = 450;
        int y = 100;
        snake = new ArrayList<>();
        int initialSize = 3;

        for (int index = 0; index < initialSize; index++) {
            snake.add(new ImageSection(GameAssets.SnakeSection));
            snake.get(index).setPoint(x - (index * GameSettings.GRID_SIZE), y);
        }
        mGameState = GameState.PAUSED;

    }

    @Override
    public void update(float delta) {
        elapsedTime += delta;
        if (elapsedTime > GameSettings.SPEED) {
            GameSettings.RottenTime++;
            elapsedTime = 0;
            //TODO Change the apple image at GameSettings.RottenTime==30 to a red to show it is going bad.
            if (GameSettings.RottenTime==35) {
                GameSettings.RottenTime = 0;
                isFoodRotten = true;
            }

            ArrayList<TouchEvent.TouchEvents> events = (ArrayList<TouchEvent.TouchEvents>) getGameActivity().getGameInput().getTouchEvents();
            switch (mGameState) {
                case GAME_OVER:
                    updateGameOver(events);
                    break;
                case PAUSED:
                    updatePaused(events.size());
                    break;
                case RUNNING:
                    updateRunning(delta, events);
                    break;
                case LOADING:
                    break;
                case DEMO:
                    break;
                case RESUME:
                    break;
                case NEXT:
                    break;
            }
        }
    }

    private void updateGameOver(ArrayList<TouchEvent.TouchEvents> events) {
        if(events.size() > 0){
            //mGameState = GameState.INIT;
            getGameActivity().setCurrentScreen(new MainMenuScreen(getGameActivity()));
            //init();
        }
    }

    private void updateRunning(float delta, ArrayList<TouchEvent.TouchEvents> events) {
        SharedPreferences pref = getGameActivity().getSharedPreferences("MyPref", GameActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.commit();
        scoreMessage="SCORE: "+GameSettings.SCORE;
        gameMessage = "High score:"+pref.getInt("highScore",0);
        int eventSize = events.size();

        if(isFoodRotten) {
            rotten();
        }
        if (eventSize > 0) {
            for (int i = 0; i < eventSize; i++) {
                if (events.get(i).type == TouchEvent.TouchEvents.TOUCH_DOWN){
                    if(events.get(i).x > getCanvas().getWidth() / 2){ forward = true;} else { forward = false;}
                    changeDirection(forward);
                    break;
                }
            }
        }
        int length = snake.size();
        //Sets the x and y of the pieces of the snake starting at tail and ending at head.
        for (int index = length - 1; index >= 0; index--) {
            ImageSection s = snake.get(index);
            if (index == 0) {
                moveSnake(mDirection);
                //Check wall Collision
                if(s.checkCollisionList((ArrayList<Section>) level)){
                    //TODO Shared preferences to save high score.

                    if(pref.getInt("highScore",0)<=GameSettings.SCORE){
                        editor.putInt("highScore", GameSettings.SCORE);
                        editor.commit();
                    }

                    gameMessage = "Game Over.";
                    gameMessage2 = "Score:"+GameSettings.SCORE;
                    gameMessage3 = "High score:"+pref.getInt("highScore",0);
                    gameMessage4 = "Tap to Try Again";
                    GameSettings.RottenTime = 0;
                    GameSettings.lvl = 0;
                    mGameState = GameState.GAME_OVER;
                }
                //Check Apple Collision
                if(s.checkCollisionSelf(apple)){
                    //TODO make it go to next level after 10 apples
                    GameSettings.SCORE=GameSettings.SCORE+10;
                    GameSettings.RottenTime = 0;
                    if(GameSettings.SCORE%100==0 && GameSettings.SCORE<=500) {
                        GameSettings.lvl++;
                        nextLvl();
                    }
                    dropApple();
                    addSnakeSection();
                }
            } else {
                ImageSection o = snake.get(index - 1);
                s.setPoint(o.x, o.y);
            }
        }



    }

    private void nextLvl(){
        LoadLevel loadLevel = new LoadLevel(getGameActivity().getGameIO());
        loadLevel.loadFile(getNextLvl(GameSettings.lvl));
        levelSection = loadLevel.parseString(loadLevel.stringLevel);
        getGameActivity().setCurrentScreen(new BoardScreen(getGameActivity(), (ArrayList<Section>) levelSection));
    }

    private String getNextLvl(int num){
        String ans="level_5_01.txt";
        if(num==1){
            ans="level_2_01.txt";
        }
        else if(num==2){
            ans="level_3_01.txt";
        }
        else if(num==3){
            ans="level_4_01.txt";
        }
        return(ans);
    }

    private void dropApple() {
        //TODO drops apple
        GameSettings.RottenTime = 0;
        Random rand = new Random();
        int x = rand.nextInt(24) * GameSettings.GRID_SIZE;
        int y = rand.nextInt(16) * GameSettings.GRID_SIZE;

        apple.setPoint(x,y);

        if(apple.checkImageList(snake)){
            dropApple();
        } else if(apple.checkCollisionList(level)){
            dropApple();
        }

    }

    private void checkCollision() {
        ImageSection head = snake.get(0);
        for(int index = 0; index < level.size(); index++) {
            Section wall = level.get(index);
            //wall check
            if (head.x == wall.x && head.y == wall.y) {
                mGameState = GameState.GAME_OVER;
            }
        }
    }

    private void changeDirection(boolean forward) {
        if (forward) {
            switch (mDirection) {

                case EAST:
                    mDirection = SnakeDirection.SOUTH;
                    break;
                case NO_DIRECTION:
                case NORTH:
                    mDirection = SnakeDirection.EAST;
                    break;
                case WEST:
                    mDirection = SnakeDirection.NORTH;
                    break;
                case SOUTH:
                    mDirection = SnakeDirection.WEST;
                    break;

            }
        } else {
            switch (mDirection) {

                case EAST:
                    mDirection = SnakeDirection.NORTH;
                    break;
                case NORTH:
                    mDirection = SnakeDirection.WEST;
                    break;
                case WEST:
                    mDirection = SnakeDirection.SOUTH;
                    break;
                case NO_DIRECTION:
                case SOUTH:
                    mDirection = SnakeDirection.EAST;
                    break;

            }
        }
    }

    private void moveSnake(SnakeDirection direction) {
        ImageSection s = snake.get(0);
        switch (direction) {

            case EAST:
                s.setX(s.x + GameSettings.GRID_SIZE);

                if (!checkBounds(s.x, s.y)) {
                    s.setX(0);
                }
                break;
            case NORTH:
                s.setY(s.y - GameSettings.GRID_SIZE);
                if (!checkBounds(s.x, s.y)) {
                    s.setY(getCanvas().getHeight() - GameSettings.GRID_SIZE);
                }
                break;
            case WEST:
                s.setX(s.x - GameSettings.GRID_SIZE);

                if (!checkBounds(s.x, s.y)) {
                    s.setX(getCanvas().getWidth() - GameSettings.GRID_SIZE);
                }
                break;
            case SOUTH:
                s.setY(s.y + GameSettings.GRID_SIZE);
                if (!checkBounds(s.x, s.y)) {
                    s.setY(0);
                }
                break;
            case NO_DIRECTION:
                break;
        }
    }

    private boolean checkBounds(int x, int y) {
        return new Rect(0, 0, getCanvas().getWidth(), getCanvas().getHeight()).contains(x, y);
    }

    private void addSnakeSection() {
        snake.add(new ImageSection(GameAssets.SnakeSection));
    }

    private void updatePaused(int eventsSize) {
        if(firstStart) {
            gameMessage = "Tap the screen to start.";
            dropApple();
        } else {
            gameMessage = "Tap the screen to continue.";
        }
        if (eventsSize > 0) {
            firstStart = false;
            mGameState = GameState.RUNNING;
        }
    }

    @Override
    public void draw(float delta) {
        GameGraphics g = getGameGraphics();
        g.clearScreen(0);
        if(level.size() > 0){
            for(int index = 0; index < level.size(); index++){
                level.get(index).draw(g);
            }
        }

        switch (mGameState) {

            case INIT:
                break;
            case GAME_OVER:
                drawGameOver(g);
                break;
            case PAUSED:
                drawPaused(g);
                break;
            case RUNNING:
                drawRunning(g);
                break;
            case LOADING:
                break;
            case DEMO:
                break;
            case RESUME:
                break;
            case NEXT:
                break;
        }

    }

    private void drawRunning(GameGraphics g) {
        getPaint().setColor(Color.WHITE);
        getPaint().setTextSize(45.0f);
        g.drawString(scoreMessage, 100, 40, getPaint());
        g.drawString(gameMessage, 800, 40, getPaint());
        if (snake.size() > 0) {
            for (int index = 0; index < snake.size(); index++) {
                snake.get(index).draw(g);
            }
        }
        apple.draw(g);
        //draw Score
        //
    }

    private void drawGameOver(GameGraphics g) {
        getPaint().setColor(Color.WHITE);
        getPaint().setTextSize(45.0f);
        g.drawString(gameMessage, 100, 100, getPaint());
        g.drawString(gameMessage2, 100, 200, getPaint());
        g.drawString(gameMessage3, 100, 300, getPaint());
        g.drawString(gameMessage4, 100, 400, getPaint());
    }

    private void drawPaused(GameGraphics g) {
        getPaint().setColor(Color.WHITE);
        getPaint().setTextSize(45.0f);
        g.drawString(gameMessage, 100, 100, getPaint());
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean onBackPressed() {
        if(mGameState == GameState.PAUSED) {
            getGameActivity().setCurrentScreen(new MainMenuScreen(getGameActivity()));
            return false;
        } else {
            mGameState = GameState.PAUSED;
            return false;
        }
    }
}
