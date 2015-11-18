package com.moonstub.training.app.snakealpha.Screens;

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
    String gameMessage = "";
    float elapsedTime = 0.0f;


    public BoardScreen(GameActivity game, ArrayList<Section> levelSection) {
        super(game);
        level = levelSection;
    }

    @Override
    public void init() {
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
            elapsedTime = 0;

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
        gameMessage = "Game Over Man Tap to Try Again";
        if(events.size() > 0){
            //mGameState = GameState.INIT;
            init();
        }
    }

    private void updateRunning(float delta, ArrayList<TouchEvent.TouchEvents> events) {
        int eventSize = events.size();

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
                    mGameState = GameState.GAME_OVER;
                }
                //Check Apple Collision
                if(s.checkCollisionSelf(apple)){
                    dropApple();
                    addSnakeSection();
                }
            } else {
                ImageSection o = snake.get(index - 1);
                s.setPoint(o.x, o.y);
            }
        }



    }

    private void dropApple() {
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
