package com.reschikov.gdx.game.android.units;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.reschikov.gdx.game.android.GameScreen;
import com.reschikov.gdx.game.android.Rules;

public abstract class Eater extends GamePoint {

    private static final float MIN_SPEED = 200.0f;
    private static final float MAX_SPEED = 400.0f;
    float acceleration;
    float angleToTarget;
    float nX;
    float nY;
    final Vector2 target;

    Eater(GameScreen gs, String textureName){
        super(gs, textureName);
        target = new Vector2();
    }

    @Override
    public void update(float dt){
        super.update(dt);
    }

    float getGoing(float dt){
        if (angle < angleToTarget) angle = (Math.abs(angle - angleToTarget) <= Rules.ANGLE_180_DEGREES) ?
                angle + Rules.ANGLE_180_DEGREES * dt : angle - Rules.ANGLE_180_DEGREES * dt;
        if (angle > angleToTarget) angle = (Math.abs(angle - angleToTarget) <= Rules.ANGLE_180_DEGREES) ?
                angle - Rules.ANGLE_180_DEGREES * dt : angle + Rules.ANGLE_180_DEGREES * dt;
        if (angle < Rules.ANGLE_0_DEGREES) angle += Rules.ANGLE_360_DEGREES;
        if (angle > Rules.ANGLE_360_DEGREES) angle -= Rules.ANGLE_360_DEGREES;
        velocity.set(acceleration * MathUtils.cosDeg(angle) * dt, acceleration * MathUtils.sinDeg(angle) * dt);
        float velLen = velocity.len() * dt;
        velocity.nor();
        nX = velocity.x;
        nY = velocity.y;
        return velLen;
    }

    public void smite(Eater another){
        if (another.isActive()){
            if (this.scale * this.halfWidth < another.scale * another.halfWidth)
                another.gorge(this);
            else this.gorge(another);
        }
    }

    public void gorge(GamePoint another){
        acceleration += another.satiety;
        scale += another.satiety;
        if (scale <= Rules.MIN_SCALE) scale = Rules.MIN_SCALE;
        if (scale > Rules.MAX_SCALE) scale = Rules.MAX_SCALE;
        satiety = scale;
        another.active = false;
    }

    public boolean isRunOver(GamePoint another) {
        return  this.scale * this.halfWidth > getDistance(another);
    }

    float getDistance(GamePoint another){
        return this.position.dst(another.position);
    }

    float getRandomAcceleration(){
        return MathUtils.random(MIN_SPEED, MAX_SPEED);
    }
}
