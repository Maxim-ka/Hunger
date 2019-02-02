package com.reschikov.gdx.game.hunger;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.DataInput;
import com.badlogic.gdx.utils.DataOutput;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.reschikov.gdx.game.hunger.units.Hero;
import com.reschikov.gdx.game.hunger.units.MiniMap;
import com.reschikov.gdx.game.hunger.units.Waste;

import java.io.*;

public class GameScreen implements Screen {

    private static final int QUANTITY_OF_LIFE = 3;
    private static final int RESULT_CANCEL = 0;
    private static final int RESULT_SAVE = 1;
    private static final int RESULT_EXIT = 2;
    private static final int NUMBER_STRINGS_TAKEN = 4;
    private static final float VOLUME = 0.4f;
    private static final String DRAWABLE_LIFE = "life";
    private static final String DRAWABLE_BUTTON_DIALOG = "buttonDialog";
    private static final String DRAWABLE_PAUSE = "pause";
    private static final String DRAWABLE_PLAY = "play";
    private static final String DRAWABLE_EXIT = "exit";
    private static final String STYLE_PAUSE_PLAY = "stylePausePlay";
    private static final String STYLE_EXIT = "styleExit";
    private static final String KEY_PAUSE = "pause";
    private static final String KEY_CONTINUED = "continued";
    private static final String KEY_END = "end";
    private static final String KEY_YES = "yes";
    private static final String KEY_NO = "no";
    private static final String KEY_CANCEL = "cancel";
    private static final String KEY_SAVE = "save";
    private final SpriteBatch batch;
    private boolean loadSaveGame;
    private Landscape landscape;
    private Hero hero;
    private EnemyEmitter hooligans;
    private FoodEmitter foods;
    private WasteEmitter waste;
    private ParticleEmitter particle;
    private TextureRegion life;
    private MiniMap miniMap;
    private Joystick joystick;
    private BitmapFont font;
    private FitViewport viewPortHero;
    private Camera cameraHero;
    private Music music;
    private Music heroReCreation;
    private Skin skin;
    private Stage controlPanel;
    private Dialog dialog;
    private Stage stageDialog;
    private InputProcessor back;
    private I18NBundle wordsGame;
    private GlyphLayout glPause;
    private GlyphLayout glContinued;
    private GlyphLayout glEnd;
    private boolean pause;
    private boolean exit;
    private int level;
    private int live;

    public I18NBundle getWordsGame() {
        return wordsGame;
    }

    public ParticleEmitter getParticle() {
        return particle;
    }

    void setLoadSaveGame(boolean loadSaveGame) {
        this.loadSaveGame = loadSaveGame;
    }

    public int getLevel() {
        return level;
    }

    public Landscape getLandscape() {
        return landscape;
    }

    public Music getHeroReCreation() {
        return heroReCreation;
    }

    public Music getMusic() {
        return music;
    }

    public FitViewport getViewPortHero() {
        return viewPortHero;
    }

    public Hero getHero() {
        return hero;
    }

    public EnemyEmitter getHooligans() {
        return hooligans;
    }

    public WasteEmitter getWaste() {
        return waste;
    }

    public FoodEmitter getFoods() {
        return foods;
    }

    GameScreen(SpriteBatch batch){
        this.batch = batch;
    }

    @Override
    public void show() {
        wordsGame = Assets.getInstance().getAssetManager().get(Rules.WORDS_GAME_SCREEN);
        boolean isAndroid = Gdx.app.getType() == Application.ApplicationType.Android;
        cameraHero = new OrthographicCamera(Rules.WORLD_WIDTH, Rules.WORLD_HEIGHT);
        viewPortHero = new FitViewport(Rules.WORLD_WIDTH, Rules.WORLD_HEIGHT, cameraHero);
        font = Assets.getInstance().getAssetManager().get(Rules.GABRIELA48_TTF);
        glPause = new GlyphLayout(font, wordsGame.get(KEY_PAUSE));
        glContinued = new GlyphLayout(font, wordsGame.get(KEY_CONTINUED));
        glEnd = new GlyphLayout(font, wordsGame.get(KEY_END));
        if (isAndroid) {
            joystick = new Joystick();
            Gdx.input.setCatchBackKey(true);
        }
        if (loadSaveGame){
            loadGame();
        }else {
            level = 0;
            live = QUANTITY_OF_LIFE;
            landscape = new Landscape(this);
            hero = new Hero(this, joystick);
            foods = new FoodEmitter(this);
            hooligans = new EnemyEmitter(this);
            waste = new WasteEmitter(this);
            particle = new ParticleEmitter();
        }
        life = Assets.getInstance().getAtlas().findRegion(DRAWABLE_LIFE);
        miniMap = new MiniMap(this);
        skin = new Skin(Assets.getInstance().getAtlas());
        installControlPanel();
        generateOutputDialog();
        if (isAndroid) addListenerBackButtons();
        InputMultiplexer inputMultiplexer = (isAndroid) ?
                new InputMultiplexer(controlPanel, stageDialog, joystick, back) :
                new InputMultiplexer(controlPanel, stageDialog);
        Gdx.input.setInputProcessor(inputMultiplexer);
        heroReCreation = Assets.getInstance().getAssetManager().get(Rules.TO_BE_CONTINUED_MP3);
        music = Assets.getInstance().getAssetManager().get(Rules.BEVERLY_HILLS_COP_1984_MP3);
        music.setLooping(true);
        music.play();
        music.setVolume(VOLUME);
    }

    private void addListenerBackButtons(){
        back = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    showDialog();
                    return true;
                }
                return false;
            }
        };
    }

    private void generateOutputDialog(){
        BitmapFont font26 = Assets.getInstance().getAssetManager().get(Rules.GABRIELA26_TTF);
        BitmapFont font32 = Assets.getInstance().getAssetManager().get(Rules.GABRIELA32_TTF);
        skin.add(Rules.FONT_26, font26);
        skin.add(Rules.FONT_32, font32);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.getDrawable(DRAWABLE_BUTTON_DIALOG);
        buttonStyle.font = skin.getFont(Rules.FONT_26);
        skin.add(Rules.BUTTON_STYLE, buttonStyle);

        Button butSave = new TextButton(wordsGame.get(KEY_YES), skin, Rules.BUTTON_STYLE);
        Button butExit = new TextButton(wordsGame.get(KEY_NO), skin, Rules.BUTTON_STYLE);
        Button butCancel = new TextButton(wordsGame.get(KEY_CANCEL), skin, Rules.BUTTON_STYLE);

        Label label = new Label(wordsGame.get(KEY_SAVE), skin, Rules.FONT_32, Color.WHITE);

        Window.WindowStyle windowStyle = new Window.WindowStyle(font26, Color.YELLOW,
                skin.getDrawable(Rules.DRAWABLE_WINDOW_DIALOG));

        stageDialog = new Stage(ScreenManager.getInstance().getViewPort(), batch);

        dialog = new Dialog(wordsGame.get(Rules.KEY_EXIT), windowStyle){
            @Override
            public void result(Object object){
                exit = false;
                pause = false;
                switch ((int)object){
                    case RESULT_CANCEL:
                        dialog.hide();
                        selectRenderingPause();
                        break;
                    case RESULT_SAVE:
                        saveGame();
                    case RESULT_EXIT:
                        recordScore();
                        ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.OVER);
                }
            }
        };
        dialog.text(label);
        dialog.button(butSave, RESULT_SAVE);
        dialog.button(butExit, RESULT_EXIT);
        dialog.button(butCancel, RESULT_CANCEL);
    }

    private void saveGame(){
        try(ObjectOutputStream save = new ObjectOutputStream(Gdx.files.local(Rules.SAVE_FILE)
                .write(false))) {
            save.writeInt(level);
            save.writeInt(live);
            save.writeObject(landscape);
            save.writeObject(hero);
            save.writeObject(foods);
            save.writeObject(hooligans);
            save.writeObject(waste);
            save.writeObject(particle);
            save.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGame(){
        try (ObjectInputStream load = new ObjectInputStream(Gdx.files.local(Rules.SAVE_FILE)
                .read())){
            level = load.readInt();
            live = load.readInt();
            landscape = (Landscape) load.readObject();
            landscape.setLoadedLandscape(this);
            hero = (Hero) load.readObject();
            hero.setLoadedHero(this, joystick);
            foods = (FoodEmitter) load.readObject();
            foods.setLoadedFoodEmitter(this);
            hooligans = (EnemyEmitter) load.readObject();
            hooligans.setLoadedEnemyEmitter(this);
            waste = (WasteEmitter) load.readObject();
            waste.setLoadedWaste(this);
            particle = (ParticleEmitter) load.readObject();
            particle.setLoadedParticleEmitter();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void installControlPanel(){
        controlPanel = new Stage(ScreenManager.getInstance().getViewPort(), batch);

        Button.ButtonStyle stylePausePlay = new Button.ButtonStyle();
        String image = (loadSaveGame) ? DRAWABLE_PLAY : DRAWABLE_PAUSE;
        stylePausePlay.up = skin.getDrawable(image);
        skin.add(STYLE_PAUSE_PLAY, stylePausePlay);

        Button.ButtonStyle styleExit = new Button.ButtonStyle();
        styleExit.up = skin.getDrawable(DRAWABLE_EXIT);
        skin.add(STYLE_EXIT, styleExit);

        Button butExitGame = new Button(skin, STYLE_EXIT);
        Button butPauseGame = new Button(skin, STYLE_PAUSE_PLAY);

        butExitGame.setPosition(Rules.WORLD_WIDTH - styleExit.up.getMinWidth() - Rules.INDENT,
            Rules.WORLD_HEIGHT  - styleExit.up.getMinHeight() - Rules.INDENT);
        butPauseGame.setPosition(Rules.WORLD_WIDTH - stylePausePlay.up.getMinWidth() - Rules.INDENT,
            Rules.WORLD_HEIGHT - styleExit.up.getMinHeight() - 2 * Rules.INDENT - 2 * stylePausePlay.up.getMinHeight());

        controlPanel.addActor(butExitGame);
        controlPanel.addActor(butPauseGame);

        butPauseGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!exit && live != 0) pause = !pause;
                selectRenderingPause();
            }
        });

        butExitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!exit && live != 0)showDialog();
            }
        });
    }

    private void showDialog(){
        exit = true;
        pause = live != 0;
        dialog.show(stageDialog);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(cameraHero.combined);
        batch.begin();
        landscape.render(batch);
        foods.render(batch);
        hero.render(batch);
        hooligans.render(batch);
        waste.render(batch);
        particle.render(batch);
        batch.end();

        batch.setProjectionMatrix(ScreenManager.getInstance().getCamera().combined);
        if (!exit)controlPanel.draw();
        if (exit) stageDialog.draw();
        batch.begin();
        if (joystick != null && joystick.isActive()) joystick.render(batch);
        showLives(batch);
        miniMap.render(batch);
        font.draw(batch, hero.getScoreLine(), Rules.INDENT, Rules.WORLD_HEIGHT - Rules.INDENT);
        if (pause){
            font.draw(batch, glPause,  ScreenManager.getInstance().getCamera().position.x - glPause.width / 2,
                ScreenManager.getInstance().getCamera().position.y - glPause.height / 2);
        }
        if (!exit && !hero.isActive() && live > 0){
            font.draw(batch, glContinued,  ScreenManager.getInstance().getCamera().position.x - glContinued.width / 2,
                ScreenManager.getInstance().getCamera().position.y + glContinued.height / 2);
        }
        if (live == 0){
            font.draw(batch, glEnd,  ScreenManager.getInstance().getCamera().position.x - glEnd.width / 2,
                ScreenManager.getInstance().getCamera().position.y - glEnd.height / 2);
        }
        batch.end();
    }

    private void recordScore(){
        StringBuilder sb = new StringBuilder();
        int scoreHero = hero.getScore();
        boolean last = true;
        if (Gdx.files.local(Rules.PATH_SCORE_HUNGER).exists()){
            try (DataInput input = new DataInput(Gdx.files.local(Rules.PATH_SCORE_HUNGER).read())){
                String string = input.readString();
                String[] strings = string.trim().split(Rules.SPLIT_PATTERN);
                for (int i = 0; i < NUMBER_STRINGS_TAKEN; i++) {
                    if (strings[i].equals(Rules.YOUR)) continue;
                    int score = Integer.parseInt(strings[i]);
                    if (scoreHero >= score && last) {
                        sb.append(Rules.YOUR).append(" ").append(scoreHero).append(Rules.LB);
                        last = false;
                    }
                    if (!last && i >= NUMBER_STRINGS_TAKEN - 1) break;
                    if (scoreHero != score) sb.append(strings[i]).append(Rules.LB);
                }
                if (last) sb.append(Rules.YOUR).append(" ").append(scoreHero);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (scoreHero >= 0) sb.append(Rules.YOUR).append(" ").append(scoreHero).append(Rules.LB).append(0).append(Rules.LB).append(0);
            else sb.append(0).append(Rules.LB).append(0).append(Rules.LB).append(Rules.YOUR).append(" ").append(scoreHero);
        }
        record(sb.toString());
    }

    private void record(String string){
        try (DataOutput out = new DataOutput(Gdx.files.local(Rules.PATH_SCORE_HUNGER).write(false))){
            out.writeString(string);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showLives(SpriteBatch batch){
        if (live > 0){
            batch.setColor(1,1,1,0.8f);
            for (int i = 0; i < live; i++) {
                batch.draw(life, Rules.WORLD_WIDTH / 2.0f - (life.getRegionWidth() + Rules.INDENT) *
                                live / 2 + life.getRegionWidth() + (Rules.INDENT + life.getRegionWidth()) * i,
                        Rules.WORLD_HEIGHT - Rules.INDENT - life.getRegionHeight());
            }
            batch.setColor(1,1,1,1);
        }
    }

    public void toLevel(){
        level = (hero.isFatty()) ? ++level : (level != 0) ? --level : 0;
    }

    private void update(float dt){
        controlPanel.act(dt);
        stageDialog.act(dt);
        if (pause){
            if (heroReCreation.isPlaying()) heroReCreation.pause();
            music.pause();
            return;
        }
        if (hero.isActive() && !music.isPlaying()) music.play();
        if (live > 0)landscape.update(dt);
        hero.update(dt);
        if (live == 0 && hero.getReCreationTime() <= 1){
            recordScore();
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.OVER);
        }
        cameraHero.position.set(hero.getPosition().x, hero.getPosition().y, 0);
        controlCameraHero();
        cameraHero.update();
        if (hero.isAtThatLevel()){
            hooligans.update(dt);
            foods.update(dt);
            waste.update(dt);
            miniMap.update(dt);
            particle.update(dt);
            checkForEatingFood();
            checkContact();
            checkForEatingAnother();
            if (loadSaveGame){
                pause = true;
                loadSaveGame = false;
            }
            return;
        }
        goToLevel();
    }

    private void controlCameraHero(){
        if (cameraHero.position.x < Rules.WORLD_WIDTH / 2) cameraHero.position.x = Rules.WORLD_WIDTH / 2.0f;
        if (cameraHero.position.y < Rules.WORLD_HEIGHT / 2) cameraHero.position.y = Rules.WORLD_HEIGHT / 2.0f;
        if (cameraHero.position.x > Rules.GLOBAL_WIDTH - Rules.WORLD_WIDTH / 2)
            cameraHero.position.x = Rules.GLOBAL_WIDTH - Rules.WORLD_WIDTH / 2.0f;
        if (cameraHero.position.y > Rules.GLOBAL_HEIGHT - Rules.WORLD_HEIGHT / 2)
            cameraHero.position.y = Rules.GLOBAL_HEIGHT - Rules.WORLD_HEIGHT / 2.0f;
    }

    private void goToLevel(){
        foods.toLeaveLevel();
        hooligans.toLeaveLevel();
        waste.toLeaveLevel();
        particle.toLeaveLevel();
    }

    private void checkContact(){
        for (int i = 0; i < hooligans.activeList.size(); i++) {
            for (int j = 0; j < waste.activeList.size() ; j++) {
                switch (waste.activeList.get(j).getType()){
                    case THORN:
                        waste.activeList.get(j).checkCollision(hero);
                        if (!hero.isActive()){
                            live--;
                            return;
                        }
                        break;
                    case CORPSE:
                        waste.activeList.get(j).checkCollision(hooligans.activeList.get(i));
                }
            }
        }
        for (int j = 0; j < particle.activeList.size() ; j++) {
            particle.activeList.get(j).toGetTo(hero);
            if (!hero.isActive()){
                live--;
                return;
            }
            for (int i = 0; i < hooligans.activeList.size(); i++)
                particle.activeList.get(j).toGetTo(hooligans.activeList.get(i));
        }
    }

    private void checkForEatingAnother(){
        for (int i = 0; i < hooligans.activeList.size(); i++) {
            if (hooligans.activeList.get(i).isRunOver(hero) || hero.isRunOver(hooligans.activeList.get(i))) {
                hooligans.activeList.get(i).smite(hero);
                if (!hero.isActive()) {
                    live--;
                    return;
                }
                if (!hooligans.activeList.get(i).isActive()) {
                    waste.getActiveElement().init(Waste.Type.CORPSE, hooligans.activeList.get(i));
                }
            }
        }
    }

    private void checkForEatingFood(){
        for (int i = 0; i < hooligans.activeList.size(); i++) {
            for (int j = 0; j < foods.activeList.size(); j++) {
                if (hooligans.activeList.get(i).isRunOver(foods.activeList.get(j))){
                    hooligans.activeList.get(i).gorge(foods.activeList.get(j));
                    if (hooligans.activeList.get(i).getScale() > Rules.SCALE_EATER)
                        waste.getActiveElement().init(Waste.Type.THORN, foods.activeList.get(j));
                }
            }
        }
        for (int j = 0; j < foods.activeList.size(); j++) {
            if (hero.isActive() && hero.isRunOver(foods.activeList.get(j))){
                hero.gorge(foods.activeList.get(j));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().resize(width, height);
        viewPortHero.update(width, height);
        viewPortHero.apply();
    }

    @Override
    public void pause() {
        pause = true;
    }

    private void selectRenderingPause(){
        Button buttonPause = (Button) controlPanel.getActors().get(1);
        Button.ButtonStyle style = buttonPause.getStyle();
        String image = (pause) ? DRAWABLE_PLAY : DRAWABLE_PAUSE;
        style.up = skin.getDrawable(image);
        buttonPause.setStyle(style);
    }

    @Override
    public void resume() {
        pause = true;
        selectRenderingPause();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stageDialog.dispose();
        controlPanel.dispose();
        Assets.getInstance().clear();
    }
}
