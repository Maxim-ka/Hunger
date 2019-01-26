package com.reschikov.gdx.game.android.units;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.reschikov.gdx.game.android.GameScreen;
import com.reschikov.gdx.game.android.Rules;

public class Food extends GamePoint{

    public enum Type {
        PIZZA(0, 0.05f), LEMON(1, -0.1f), DOUGHNUT(2, 0.1f);

        private final int textureIndex;
        private final float satiety;

        Type(int textureIndex, float satiety) {
            this.textureIndex = textureIndex;
            this.satiety = satiety;
        }
    }

    private Type type;
    private transient TextureRegion[] textureRegions;

    Type getType() {
        return type;
    }

    public Food(GameScreen gs, TextureRegion[] textureRegions){
        super(gs, null);
        this.textureRegions = textureRegions;
    }

    public void init(Type type){
        if (this.type != type || region == null) {
            this.type = type;
            region = textureRegions[type.textureIndex];
            toSize(region);
        }
        super.init();
        velocity.set(MathUtils.random(-Rules.ANGLE_90_DEGREES, Rules.ANGLE_90_DEGREES), MathUtils.random(-Rules.ANGLE_90_DEGREES, Rules.ANGLE_90_DEGREES));
        satiety = type.satiety;
    }

    public void update(float dt) {
        super.update(dt);
        move(dt);
        if(!gs.getLandscape().isCellEmpty(tmp.x, tmp.y, halfWidth * scale)){
            bounceOffWall(dt);
        }
        position.set(tmp);
        angle = (velocity.x < 0) ? angle + Rules.ANGLE_90_DEGREES * dt : angle - Rules.ANGLE_90_DEGREES * dt;
    }

    public void reload(GameScreen gs, TextureRegion[] regions){
        this.gs = gs;
        textureRegions = regions;
    }

    public void reloadTextureRegion(){
        region = textureRegions[type.textureIndex];
    }
}
