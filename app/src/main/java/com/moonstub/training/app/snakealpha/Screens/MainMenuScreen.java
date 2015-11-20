package com.moonstub.training.app.snakealpha.Screens;

import com.moonstub.training.app.snakealpha.GameAssets;
import com.moonstub.training.app.snakealpha.GameState;
import com.moonstub.training.app.snakealpha.MenuItem;
import com.moonstub.training.app.snakealpha.framework.GameActivity;
import com.moonstub.training.app.snakealpha.framework.GameGraphics;
import com.moonstub.training.app.snakealpha.framework.GameScreen;
import com.moonstub.training.app.snakealpha.input.TouchEvent;

import java.util.ArrayList;
import java.util.List;

public class MainMenuScreen extends GameScreen {

    public MenuItem PlayMenuItem;
    private List<Section> levelSection;

    public MainMenuScreen(GameActivity game) {
        super(game);
    }

    @Override
    public void init() {
        //TODO this loads levels I want to try and use it to make the apples turn to walls
        LoadLevel loadLevel = new LoadLevel(getGameActivity().getGameIO());
        loadLevel.loadFile("level_1_01.txt");
        levelSection = loadLevel.parseString(loadLevel.stringLevel);
        PlayMenuItem = new MenuItem(getGameActivity(),getCanvas().getWidth()/2,400);
        PlayMenuItem.setImages(GameAssets.PlayBtnMenuDefault, GameAssets.PlayBtnMenuPressed);

        mGameState = GameState.RUNNING;

        GameAssets.SnakeSection = getGameGraphics().newImage("section_full.png", GameGraphics.ImageFormat.ARGB8888);
        GameAssets.SnakeFoodApple = getGameGraphics().newImage("food_green_apple.png", GameGraphics.ImageFormat.ARGB8888);
    }

    @Override
    public void update(float delta) {
        ArrayList<TouchEvent.TouchEvents> events = (ArrayList<TouchEvent.TouchEvents>) getGameActivity()
                .getGameInput().getTouchEvents();

        switch (mGameState){
            case INIT:
                updateInit();
                break;
            case PAUSED:
                updatePaused(events);
                break;
            case RESUME:
                updateResume(events);
                break;
            case RUNNING:
                updateRunning(events);
                break;
            case  NEXT:
                updateNext();
                break;
            //case LOADING:
            //    updateLoading();
            //    break;
            //case DEMO:
            //    updateDemo(events);
            //    break;
            default:
                throw new IllegalArgumentException("GameState Error");
        }

    }

    private void updateNext() {
        getGameActivity().setCurrentScreen(new BoardScreen(getGameActivity(), (ArrayList<Section>) levelSection));
    }

    private void updateRunning(ArrayList<TouchEvent.TouchEvents> events) {
        int length = events.size();
        //Process Events
        for(int i = 0; i < length; i++){
            if(events.get(i).type == TouchEvent.TouchEvents.TOUCH_DOWN){
                if(PlayMenuItem.checkCollision(events.get(i).x,events.get(i).y)){
                    PlayMenuItem.touched(events.get(i));
                }
            } else if(events.get(i).type == TouchEvent.TouchEvents.TOUCH_UP){
                if(PlayMenuItem.checkCollision(events.get(i).x, events.get(i).y)){
                    mGameState = GameState.NEXT;
                }
                PlayMenuItem.touched(events.get(i));

            } else if(events.get(i).type == TouchEvent.TouchEvents.TOUCH_DRAGGED){
                PlayMenuItem.touched(events.get(i));
            }
        }
    }

    private void updateResume(ArrayList<TouchEvent.TouchEvents> events) {

    }

    private void updatePaused(ArrayList<TouchEvent.TouchEvents> events) {

    }

    private void updateInit() {

    }

    @Override
    public void draw(float delta) {

        getGameGraphics().clearScreen(0);
        getGameGraphics().drawImage(GameAssets.SplashImage, 0, 0);
        PlayMenuItem.draw(getGameGraphics());

    }

    @Override
    public void resume() {
        if(mGameState == GameState.PAUSED){
            mGameState = GameState.RESUME;
        }
    }

    @Override
    public void pause() {
        if(mGameState == GameState.PAUSED){
            mGameState = GameState.RESUME;
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean onBackPressed() {
        return true;
    }
}
