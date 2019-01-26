package com.reschikov.gdx.game.android.units;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.reschikov.gdx.game.android.GameScreen;
import com.reschikov.gdx.game.android.Rules;

public class Waste extends GamePoint{

    public enum Type {

        CORPSE(0, 15.0f, -0.25f), THORN(1, 10.0f, -0.25f);

        private final int textureIndex;
        private final float lifetime;
        private final float satiety;

        Type(int textureIndex, float lifetime, float satiety) {
            this.textureIndex = textureIndex;
            this.lifetime = lifetime;
            this.satiety = satiety;
        }
    }

    private static final float EXPLOSION_TIME = 2.0f;
    private Type type;
    private transient TextureRegion[] textureRegions;
    private boolean hook;

    public Type getType() {
        return type;
    }

    private float time;

    public Waste(GameScreen gs, TextureRegion[] textureRegions) {
        super(gs, null);
        this.textureRegions = textureRegions;
    }

    public void checkCollision(Eater eater){
        if (eater.getDistance(this) < eater.scale * eater.halfWidth + scale * halfWidth){
            eater.scale += type.satiety;
            switch (getType()){
                case CORPSE:
                    eater.acceleration = 0;
                    scale -= eater.satiety;
                    if (scale < 0){
                        active = false;
                        eater.acceleration = eater.getRandomAcceleration();
                    }
                    break;
                case THORN:
                    hook = true;
            }
            if (eater.scale < Rules.MIN_SCALE) eater.active = false;
        }
    }

    public void init(Type  type, GamePoint another){
        if (this.type != type || region == null) {
            this.type = type;
            region = textureRegions[type.textureIndex];
            toSize(region);
        }
        position.set(another.position);
        scale = (getType() == Type.CORPSE) ? scale + another.scale : another.scale ;
        angle = MathUtils.random(-Rules.ANGLE_90_DEGREES, Rules.ANGLE_90_DEGREES);
        active = true;
        time = type.lifetime;
    }

    public void update(float dt) {
        time -= dt;
        if (hook) time = EXPLOSION_TIME;
        if (time <= EXPLOSION_TIME && getType() == Type.THORN) explode();
        if (time <= 0){
            scale = 1.0f;
            active = false;
            return;
        }
        super.update(dt);
        if (getType() == Type.THORN) angle = (angle > 0)? angle + Rules.ANGLE_90_DEGREES * dt :
                angle - Rules.ANGLE_90_DEGREES * dt;
    }

    private void explode(){
        gs.getParticle().launch(position, 32 + MathUtils.random(-8, 16),
                MathUtils.random(-360, 360), MathUtils.random(1,3), MathUtils.random(0.75f, 1.0f),
                MathUtils.random(0.15f, 0.25f), 1,0.823f,0,1,0,0,0,0);
    }

    public void reload(GameScreen gs, TextureRegion[] regions){
        this.gs = gs;
        textureRegions = regions;
    }

    public void reloadTextureRegion(){
        region = textureRegions[type.textureIndex];
    }
}
