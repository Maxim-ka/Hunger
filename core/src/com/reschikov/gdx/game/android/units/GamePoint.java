package com.reschikov.gdx.game.android.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.reschikov.gdx.game.android.Assets;
import com.reschikov.gdx.game.android.GameScreen;
import com.reschikov.gdx.game.android.Poolable;
import com.reschikov.gdx.game.android.Rules;

import java.io.Serializable;

public abstract class GamePoint implements Poolable, Serializable {

    transient GameScreen gs;
    transient TextureRegion region;
    final Vector2 position;
    final Vector2 velocity;
    final Vector2 tmp;
    float scale;
    float satiety;
    float angle;
    int width;
    private int height;
    int halfWidth;
    int halfHeight;
    boolean active;

    public int getHalfWidth() {
        return halfWidth;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setRegion(TextureRegion region) {
        this.region = region;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getScale() {
        return scale;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    GamePoint(GameScreen gs, String textureName){
        this.gs = gs;
        if (textureName != null){
            this.region = Assets.getInstance().getAtlas().findRegion(textureName);
            toSize(region);
        }
        position = new Vector2();
        velocity = new Vector2();
        tmp = new Vector2();
        scale = 1.0f;
    }

    void toSize(TextureRegion region){
        width = region.getRegionWidth();
        height = region.getRegionHeight();
        halfWidth = region.getRegionWidth() / 2;
        halfHeight = region.getRegionHeight() / 2;
    }

    public void render(SpriteBatch batch){
        batch.draw(region, position.x - halfWidth, position.y - halfHeight, halfWidth,
                halfHeight, width, height, scale, scale, angle);
    }

    void init(){
        float x, y;
        do{
            x = getCoordinate(Rules.GLOBAL_WIDTH);
            y = getCoordinate(Rules.GLOBAL_HEIGHT);
        }while (!gs.getLandscape().isCellEmpty(x, y, halfWidth * scale));
        position.set(x, y);
        active = true;
    }

    public void update(float dt){
        if (position.x < 0) position.x = Rules.GLOBAL_WIDTH;
        if (position.y < 0) position.y = Rules.GLOBAL_HEIGHT;
        if (position.x > Rules.GLOBAL_WIDTH) position.x = 0;
        if (position.y > Rules.GLOBAL_HEIGHT) position.y = 0;
    }

    void bounceOffWall(float dt){
        float normal = Rules.ANGLE_90_DEGREES;
        float angleIncidence = velocity.angle();
        if (angleIncidence > Rules.ANGLE_360_DEGREES) angleIncidence -= Rules.ANGLE_360_DEGREES;
        if (angleIncidence % Rules.ANGLE_90_DEGREES == 0) {
            velocity.set(velocity.scl(Rules.TURN));
            move(dt);
        } else {
            while (angleIncidence > normal){
                normal += Rules.ANGLE_90_DEGREES;
            }
            reflect(angleIncidence, normal, dt);
        }
    }

    private void reflect(float angleIncidence, float normal, float dt){
        float angleReflection = normal - angleIncidence;
        float angle = normal + angleReflection;
        if (angle > Rules.ANGLE_360_DEGREES) angle -= Rules.ANGLE_360_DEGREES;
        velocity.setAngle(angle);
        move(dt);
        if (gs.getLandscape().isCellEmpty(tmp.x, tmp.y, halfWidth * scale)) return;
        normal -= Rules.ANGLE_90_DEGREES;
        angleReflection = Math.abs(normal - angleIncidence);
        if (normal == Rules.ANGLE_0_DEGREES) normal = Rules.ANGLE_360_DEGREES;
        velocity.setAngle(normal - angleReflection);
        move(dt);
        if (gs.getLandscape().isCellEmpty(tmp.x, tmp.y, halfWidth * scale)) return;
        velocity.set(velocity.scl(Rules.TURN));
        move(dt);
    }

    void move(float dt){
        tmp.set(position);
        tmp.mulAdd(velocity, dt);
    }

    private float getCoordinate(float limit){
        return MathUtils.random(limit);
    }
}
