package com.reschikov.gdx.game.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.DataInput;
import com.badlogic.gdx.utils.I18NBundle;

import java.io.IOException;

public class GameOverScreen implements Screen {

    private static final int PIECE = 6;
    private static final String DRAWABLE_OVER_GAME = "overGame";
    private static final String DRAWABLE_MENU = "menu";
    private static final String DRAWABLE_EXIT = "exit";
    private static final String BUT_STYLE_MENU = "butStyleMenu";
    private static final String BUT_STYLE_EXIT = "butStyleExit";
    private static final String KEY_MENU = "menu";
    private static final String KEY_YOUR = "your";
    private final StringBuilder stringBuilder = new StringBuilder();
    private BitmapFont font72;
    private final SpriteBatch batch;
    private TextureRegion regionPicture;
    private Stage stage;
    private I18NBundle wordsOver;
    private GlyphLayout glHighScore;
    private boolean exit;

    public boolean isExit() {
        return exit;
    }

    GameOverScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void show() {
        regionPicture = Assets.getInstance().getAtlas().findRegion(DRAWABLE_OVER_GAME);
        wordsOver = Assets.getInstance().getAssetManager().get(Rules.WORDS_OVER_SCREEN);
        font72 = Assets.getInstance().getAssetManager().get(Rules.GABRIELA72_TTF);
        glHighScore = new GlyphLayout(font72, Rules.HIGH_SCORE);
        BitmapFont font48 = Assets.getInstance().getAssetManager().get(Rules.GABRIELA48_TTF);

        stage = new Stage(ScreenManager.getInstance().getViewPort(), batch);
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Assets.getInstance().getAtlas());
        skin.add(Rules.FONT_48, font48);

        ImageTextButton.ImageTextButtonStyle butStyleMenu = new ImageTextButton.ImageTextButtonStyle();
        butStyleMenu.up = skin.getDrawable(DRAWABLE_MENU);
        butStyleMenu.font = skin.getFont(Rules.FONT_48);
        skin.add(BUT_STYLE_MENU, butStyleMenu);

        ImageTextButton.ImageTextButtonStyle butStyleExit = new ImageTextButton.ImageTextButtonStyle();
        butStyleExit.up = skin.getDrawable(DRAWABLE_EXIT);
        butStyleExit.font = skin.getFont(Rules.FONT_48);
        skin.add(BUT_STYLE_EXIT, butStyleExit);

        ImageTextButton buttonMenu = new ImageTextButton(wordsOver.get(KEY_MENU), skin, BUT_STYLE_MENU);
        ImageTextButton buttonExit = new ImageTextButton(wordsOver.get(Rules.KEY_EXIT), skin, BUT_STYLE_EXIT);

        buttonMenu.right();
        buttonExit.right();

        buttonMenu.setPosition(Rules.INDENT + butStyleMenu.up.getMinWidth() / PIECE,
                Rules.WORLD_HEIGHT  - Rules.INDENT - butStyleMenu.up.getMinHeight());
        buttonExit.setPosition(Rules.INDENT + butStyleExit.up.getMinWidth() / PIECE,
                Rules.WORLD_HEIGHT  - 2 * butStyleExit.up.getMinHeight());

        stage.addActor(buttonMenu);
        stage.addActor(buttonExit);

        buttonMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });
        buttonExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                exit = true;
                Gdx.app.exit();
            }
        });
        getScore();
    }

    private void getScore(){
        stringBuilder.delete(0, stringBuilder.length());
        try (DataInput input = new DataInput(Gdx.files.local(Rules.PATH_SCORE_HUNGER).read())){
            stringBuilder.append(input.readString());
            int start = stringBuilder.indexOf(Rules.YOUR);
            stringBuilder.replace(start, start + Rules.YOUR.length(), wordsOver.get(KEY_YOUR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(regionPicture,Rules.WORLD_WIDTH / 2.0f - regionPicture.getRegionWidth() / 2.0f,
                Rules.WORLD_HEIGHT / 2.0f - regionPicture.getRegionHeight() / 2.0f);
        font72.draw(batch, glHighScore, Rules.WORLD_WIDTH / 2.0f - 2 * glHighScore.width / 3,
                Rules.WORLD_HEIGHT / 2.0f + font72.getLineHeight() / 2);
        font72.draw(batch, stringBuilder, Rules.WORLD_WIDTH / 2.0f + glHighScore.width / 3,
                Rules.WORLD_HEIGHT / 2.0f + font72.getLineHeight() / 2);
        batch.end();
        stage.draw();
    }

    private void update(float dt){
        stage.act(dt);
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().resize(width, height);
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
        stage.dispose();
        Assets.getInstance().clear();
    }
}
