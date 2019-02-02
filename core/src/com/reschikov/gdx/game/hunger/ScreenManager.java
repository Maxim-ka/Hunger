package com.reschikov.gdx.game.hunger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ScreenManager {

    public enum ScreenType{
        MENU, GAME, OVER
    }

    private static final ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

    private HungerGame game;
    private LoadingScreen ls;
    private GameScreen gs;
    private MenuScreen ms;
    private GameOverScreen gos;
    private Screen targetScreen;

    private SpriteBatch batch;
    private FitViewport viewPort;
    private Camera camera;

    public GameScreen getGs() {
        return gs;
    }

    public MenuScreen getMs() {
        return ms;
    }

    public GameOverScreen getGos() {
        return gos;
    }

    Camera getCamera() {
        return camera;
    }

    FitViewport getViewPort() {
        return viewPort;
    }

    private ScreenManager() {
    }

    void init(HungerGame hg, SpriteBatch batch){
        this.game = hg;
        this.batch = batch;
        camera = new OrthographicCamera(Rules.WORLD_WIDTH, Rules.WORLD_HEIGHT);
        camera.position.set(Rules.WORLD_WIDTH / 2.0f, Rules.WORLD_HEIGHT / 2.0f, 0);
        camera.update();
        viewPort = new FitViewport(Rules.WORLD_WIDTH, Rules.WORLD_HEIGHT, camera);
        gs = new GameScreen(batch);
        ms = new MenuScreen(batch);
        gos = new GameOverScreen(batch);
        ls = new LoadingScreen(batch);
    }

    void resize(int x, int y){
        viewPort.update(x, y);
        viewPort.apply();
    }

    void changeScreen(ScreenType type){
        Screen screen = game.getScreen();
        Assets.getInstance().clear();
        Gdx.input.setInputProcessor(null);
        if (screen != null) screen.dispose();
        batch.setProjectionMatrix(camera.combined);
        game.setScreen(ls);
        switch (type){
            case MENU:
                targetScreen = ms;
                Assets.getInstance().loadAssets(ScreenType.MENU);
                break;
            case GAME:
                targetScreen = gs;
                Assets.getInstance().loadAssets(ScreenType.GAME);
                break;
            case OVER:
                targetScreen = gos;
                Assets.getInstance().loadAssets(ScreenType.OVER);
                break;
        }
    }

    void gotoTarget(){
        game.setScreen(targetScreen);
    }
}
