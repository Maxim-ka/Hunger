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
import com.badlogic.gdx.utils.StringBuilder;
import java.io.IOException;

public class MenuScreen implements Screen{

    private static final int RESULT_OK = 0;
    private static final float PADDING_BOTTOM = 5.0f;
    private static final String DRAWABLE_HEADPIECE = "headpiece";
    private static final String DRAWABLE_BUTTON = "button";
    private static final String DRAWABLE_OK = "OK";
    private static final String H_U_N_G_E_R = "H U N G E R";
    private static final String KEY_NOT_FOUND = "notFound";
    private static final String KEY_TITLE = "title";
    private static final String KEY_START_NEW_GAME = "startNewGame";
    private static final String KEY_LOAD_GAME = "loadGame";
    private static final String KEY_COPY_RIGHT = "copyRight";
    private final SpriteBatch batch;
    private TextureRegion regionPicture;
    private BitmapFont font92;
    private BitmapFont font48;
    private BitmapFont font16;
    private Skin skin;
    private Stage stageDialog;
    private Dialog dialog;
    private Stage stage;
    private I18NBundle wordsMenu;
    private GlyphLayout glTitle;
    private GlyphLayout glScore;
    private GlyphLayout glCopyRight;
    private boolean exit;

    public boolean isExit() {
        return exit;
    }

    MenuScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void show() {
        regionPicture = Assets.getInstance().getAtlas().findRegion(DRAWABLE_HEADPIECE);

        wordsMenu = Assets.getInstance().getAssetManager().get(Rules.WORDS_MENU_SCREEN);

        font92 = Assets.getInstance().getAssetManager().get(Rules.GABRIELA92_TTF);
        font48 = Assets.getInstance().getAssetManager().get(Rules.GABRIELA48_TTF);
        font16 = Assets.getInstance().getAssetManager().get(Rules.GABRIELA16_TTF);

        glTitle = new GlyphLayout(font92, H_U_N_G_E_R);
        glScore = new GlyphLayout(font48, getHighScore());
        glCopyRight = new GlyphLayout(font16, wordsMenu.get(KEY_COPY_RIGHT));

        stage = new Stage(ScreenManager.getInstance().getViewPort(), batch);
        Gdx.input.setInputProcessor(stage);

        BitmapFont font32 = Assets.getInstance().getAssetManager().get(Rules.GABRIELA32_TTF);
        skin = new Skin(Assets.getInstance().getAtlas());
        skin.add(Rules.FONT_32, font32);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.getDrawable(DRAWABLE_BUTTON);
        buttonStyle.font = skin.getFont(Rules.FONT_32);
        skin.add(Rules.BUTTON_STYLE, buttonStyle);

        Button buttonNewGame = new TextButton(wordsMenu.get(KEY_START_NEW_GAME), skin, Rules.BUTTON_STYLE);
        Button buttonLoadGame = new TextButton(wordsMenu.get(KEY_LOAD_GAME), skin, Rules.BUTTON_STYLE);
        Button buttonExitGame = new TextButton(wordsMenu.get(Rules.KEY_EXIT), skin, Rules.BUTTON_STYLE);
        buttonNewGame.setPosition(buttonStyle.up.getMinWidth() / 2,
            Rules.WORLD_HEIGHT / 2.f - regionPicture.getRegionHeight() / 2.0f - buttonStyle.up.getMinHeight());
        buttonLoadGame.setPosition(Rules.WORLD_WIDTH / 2.0f - buttonStyle.up.getMinWidth() / 2,
            Rules.WORLD_HEIGHT / 2.0f - regionPicture.getRegionHeight() / 2.0f - buttonStyle.up.getMinHeight());
        buttonExitGame.setPosition(Rules.WORLD_WIDTH - buttonStyle.up.getMinWidth() - buttonStyle.up.getMinWidth() / 2,
            Rules.WORLD_HEIGHT / 2.0f - regionPicture.getRegionHeight() / 2.0f - buttonStyle.up.getMinHeight());
        stage.addActor(buttonNewGame);
        stage.addActor(buttonLoadGame);
        stage.addActor(buttonExitGame);

        buttonNewGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().getGs().setLoadSaveGame(false);
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });

        buttonLoadGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Gdx.files.local(Rules.SAVE_FILE).exists()) {
                    ScreenManager.getInstance().getGs().setLoadSaveGame(true);
                    ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
                }else{
                    ScreenManager.getInstance().getGs().setLoadSaveGame(false);
                    Gdx.input.setInputProcessor(stageDialog);
                    dialog.show(stageDialog);
                }
            }
        });

        buttonExitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                exit = true;
                Gdx.app.exit();
            }
        });
        generateErrorDialog();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float centerX = Rules.WORLD_WIDTH / 2.0f;
        batch.begin();
        font92.draw(batch, glTitle,  centerX - glTitle.width / 2,
                Rules.WORLD_HEIGHT - glTitle.height / 2);
        font48.draw(batch, glScore, centerX - glScore.width / 2,
                Rules.WORLD_HEIGHT - glTitle.height - 2 * glScore.height);
        batch.draw(regionPicture,centerX - regionPicture.getRegionWidth() / 2.0f,
                Rules.WORLD_HEIGHT / 2.0f - regionPicture.getRegionHeight() / 2.0f);
        font16.draw(batch, glCopyRight, centerX - glCopyRight.width / 2, 2 * glCopyRight.height);
        batch.end();
        stage.draw();
        stageDialog.draw();
    }

    private String getHighScore(){
        StringBuilder stringBuilder = new StringBuilder(Rules.HIGH_SCORE);
        if (Gdx.files.local(Rules.PATH_SCORE_HUNGER).exists()){
            try (DataInput input = new DataInput(Gdx.files.local(Rules.PATH_SCORE_HUNGER).read())){
                String[] strings = input.readString().split(Rules.SPLIT_PATTERN);
                if (strings[0].equals(Rules.YOUR)) stringBuilder.append(strings[1]);
                else stringBuilder.append(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else stringBuilder.append(0);
        return stringBuilder.toString();
    }

    private void generateErrorDialog(){
        BitmapFont font26 = Assets.getInstance().getAssetManager().get(Rules.GABRIELA26_TTF);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font26;
        windowStyle.background = skin.getDrawable(Rules.DRAWABLE_WINDOW_DIALOG);

        Button butOK = new Button(skin.getDrawable(DRAWABLE_OK));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont(Rules.FONT_32);
        Label label = new Label(wordsMenu.format(KEY_NOT_FOUND, Rules.SAVE_FILE), labelStyle);

        stageDialog = new Stage(ScreenManager.getInstance().getViewPort(), batch);

        dialog = new Dialog(wordsMenu.get(KEY_TITLE), windowStyle){
            @Override
            public void result(Object object){
                if ((int)object == RESULT_OK){
                    Gdx.input.setInputProcessor(stage);
                    dialog.hide();
                }
            }
        };
        dialog.text(label);
        dialog.button(butOK, RESULT_OK);
        dialog.padBottom(PADDING_BOTTOM);
    }

    private void update(float dt){
        stage.act(dt);
        stageDialog.act(dt);
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
        stageDialog.dispose();
        stage.dispose();
        Assets.getInstance().clear();
    }
}