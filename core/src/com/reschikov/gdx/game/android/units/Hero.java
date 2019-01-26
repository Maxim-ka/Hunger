package com.reschikov.gdx.game.android.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.reschikov.gdx.game.android.Assets;
import com.reschikov.gdx.game.android.GameScreen;
import com.reschikov.gdx.game.android.Joystick;
import com.reschikov.gdx.game.android.Rules;

public class Hero extends Eater {

    private static final int TO_LEFT = 1;
    private static final int TO_RIGHT = -1;
    private static final float ACCELERATION = 300.0f;
    private static final float TRANSITION_TIME_OF_LEVEL = 2.0f;
    private static final float VOLUME = 0.05f;
    private static final String DRAWABLE_HERO = "hero";
    private static final String DRAWABLE_BEATEN = "beaten";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_SCORE = "score";
    private static final String KEY_CONTINUED = "continued";
    private final String level = gs.getWordsGame().get(KEY_LEVEL);
    private final StringBuilder scoreLine = new StringBuilder(level);
    private transient TextureRegion beaten;
    private transient TextureRegion hero;
    private transient GlyphLayout glContinued;
    private float reCreationTime = 5.0f;
    private float transitionTime = TRANSITION_TIME_OF_LEVEL;
    private boolean atThatLevel;
    private boolean fatty;
    private boolean lose;
    private float fat;
    private int score;
    private float tmpScale;
    private transient Joystick joystick;

    public float getReCreationTime() {
        return reCreationTime;
    }

    public int getScore() {
        return score;
    }

    public boolean isFatty() {
        return fatty;
    }

    public boolean isAtThatLevel() {
        return atThatLevel;
    }

    public StringBuilder getScoreLine() {
        return scoreLine;
    }

    public Hero(GameScreen gs, Joystick joystick) {
        super(gs, DRAWABLE_HERO);
        this.joystick = joystick;
        beaten = Assets.getInstance().getAtlas().findRegion(DRAWABLE_BEATEN);
        hero = region;
        glContinued = new GlyphLayout(gs.getFont(), gs.getWordsGame().get(KEY_CONTINUED));
        init();
    }

    @Override
    void init(){
        position.set(Rules.GLOBAL_WIDTH / 2.0f, Rules.GLOBAL_HEIGHT / 2.0f);
        scale = Rules.SCALE_EATER;
        region = hero;
        reCreationTime = 5.0f;
        angle = -Rules.ANGLE_90_DEGREES;
        acceleration = ACCELERATION;
        gs.getLandscape().initMapLevel(this);
        atThatLevel = true;
        active = true;
    }

    @Override
    public void render(SpriteBatch batch){
        super.render(batch);
        if (!isActive() && gs.getLive() > 0){
            gs.getFont().draw(batch, glContinued,  position.x - glContinued.width / 2,
                    position.y - glContinued.height / 2);
        }
    }

    @Override
    public void gorge(GamePoint another){
        float boost;
        if (fat < 0){
            fat += another.satiety;
            boost = 500;
        } else {
            tmpScale += another.satiety;
            if (gs.getLandscape().isCellEmpty(position.x, position.y, halfWidth * (scale + tmpScale))) {
                scale += tmpScale;
                tmpScale = 0.0f;
            }
            boost = 1000;
        }
        if (this.scale <= Rules.MIN_SCALE) this.scale = Rules.MIN_SCALE;
        acceleration += another.satiety * (boost + boost * gs.getLevel()) ;
        another.active = false;
        if (scale + tmpScale - Rules.SCALE_EATER >= Rules.MAX_SCALE){
            fat += scale + tmpScale - Rules.SCALE_EATER;
            fatty = true;
            return;
        }
        if (fat >= Rules.MAX_SCALE && scale < Rules.SCALE_EATER){
            fat -= Rules.MAX_SCALE;
            lose = true;
        }
    }

    private boolean isMovedToLevel(float dt){
        velocity.setZero();
        atThatLevel = false;
        transitionTime -= dt;
        if (transitionTime > 0) {
            int zoom = (fatty) ? TO_RIGHT : TO_LEFT;
            this.angle += zoom * Rules.ANGLE_360_DEGREES / TRANSITION_TIME_OF_LEVEL * dt;
            scale += zoom * (Rules.MAX_SCALE - Rules.SCALE_EATER) / TRANSITION_TIME_OF_LEVEL * dt;
            return true;
        }
        return false;
    }

    private void comeToLevel(){
        position.set(Rules.GLOBAL_WIDTH / 2.0f, Rules.GLOBAL_HEIGHT / 2.0f);
        gs.toLevel();
        gs.getLandscape().initMapLevel(this);
        transitionTime = TRANSITION_TIME_OF_LEVEL;
        if (fatty) scale = Rules.SCALE_EATER;
        if (lose) scale = Rules.MAX_SCALE;
        atThatLevel = true;
    }

    @Override
    public void update(float dt){
        if (isActive()) {
            super.update(dt);
            if (fatty || lose){
                if (isMovedToLevel(dt)) return;
                else{
                    comeToLevel();
                    fatty = false;
                    lose = false;
                }
            }
            score = Math.round((scale - Rules.SCALE_EATER + fat) * 100);
            scoreLine.delete(level.length(), scoreLine.length());
            scoreLine.append(" ").append(gs.getLevel()).append(Rules.LB)
                .append(gs.getWordsGame().get(KEY_SCORE)).append(" ").append(score);
        } else{
            reCreationTime -= dt;
            if (reCreationTime > 0){
                if (region != beaten && isAtThatLevel()){
                    region = beaten;
//                    fat -= scale;
//                    scale = (scale < Rules.SCALE_EATER) ? Rules.SCALE_EATER : scale;
                    velocity.setZero();
                    angle = -Rules.ANGLE_90_DEGREES;
                    gs.getMusic().pause();
                    gs.getHeroReCreation().play();
                    gs.getHeroReCreation().setVolume(VOLUME);
                    atThatLevel = false;
                }
                return;
            }
            init();
        }
        if (isAtThatLevel() && Gdx.input.isTouched()){
            if (joystick == null){
                target.set(Gdx.input.getX(), Gdx.input.getY());
                gs.getViewPortHero().unproject(target);
                angleToTarget = target.sub(position).angle();
            } else {
                if (joystick.isActive()){
                    angleToTarget = joystick.getAngle();
//                    acceleration += acceleration * joystick.getPower();
                }
            }
            run(dt);
        }
    }

    private void run(float dt){
        float velLen = getGoing(dt);
        for (int i = 0; i < velLen; i++) {
            tmp.set(position.x + nX, position.y + nY);
            if (isCollided()) return;
            position.set(tmp);
            if (tmpScale != 0.0f && gs.getLandscape().isCellEmpty(position.x, position.y, halfWidth * (scale + tmpScale))){
                scale += tmpScale;
                tmpScale = 0.0f;
            }
        }
    }

    private boolean isCollided(){
        return gs.getLandscape()
                .collide(tmp.x, tmp.y, halfWidth * scale, angle) != Rules.NOT_FOUND;
    }

    public void setLoadedHero(GameScreen gs, Joystick joystick){
        this.gs = gs;
        this.joystick = joystick;
        beaten = Assets.getInstance().getAtlas().findRegion(DRAWABLE_BEATEN);
        hero = Assets.getInstance().getAtlas().findRegion(DRAWABLE_HERO);
        region = (isActive()) ? hero : beaten;
        glContinued = new GlyphLayout(gs.getFont(), gs.getWordsGame().get(KEY_CONTINUED));
    }
}
