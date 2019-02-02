package com.reschikov.gdx.game.hunger.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.reschikov.gdx.game.hunger.Assets;
import com.reschikov.gdx.game.hunger.Rules;
import com.reschikov.gdx.game.hunger.GameScreen;

import java.io.Serializable;

public class MiniMap extends GamePoint implements Serializable {

    private static final String SCANER_MINI_MAP = "scanerMiniMap";
    private static final String MINI_HERO = "miniHero";
    private static final String MINI_ENEMY = "miniEnemy";
    private static final String MINI_GOOD_FOOD = "miniGoodFood";
    private final transient TextureRegion regionMiniHero;
    private final transient TextureRegion regionMiniEnemy;
    private final transient TextureRegion regionMiniGoodFood;
    private final int halfWidthMiniHero;
    private final int halfHeightMiniHero;
    private final int halfWidthMiniEnemy;
    private final int halfHeightMiniEnemy;
    private final int halfWidthMiniGoodFood;
    private final int halfHeightMiniGoodFood;
    private final float scanRadius;

    public MiniMap(GameScreen gs) {
        super(gs, SCANER_MINI_MAP);
        regionMiniHero = Assets.getInstance().getAtlas().findRegion(MINI_HERO);
        regionMiniEnemy = Assets.getInstance().getAtlas().findRegion(MINI_ENEMY);
        regionMiniGoodFood = Assets.getInstance().getAtlas().findRegion(MINI_GOOD_FOOD);
        halfWidthMiniHero = regionMiniHero.getRegionWidth() / 2;
        halfHeightMiniHero = regionMiniHero.getRegionHeight()/2;
        halfWidthMiniEnemy = regionMiniEnemy.getRegionWidth() / 2;
        halfHeightMiniEnemy = regionMiniEnemy.getRegionHeight() / 2;
        halfWidthMiniGoodFood = regionMiniGoodFood.getRegionWidth() / 2;
        halfHeightMiniGoodFood = regionMiniGoodFood.getRegionHeight() / 2;
        position.set(Rules.WORLD_WIDTH - halfWidth - Rules.INDENT, halfHeight + Rules.INDENT);
        active = true;
        scanRadius = 1000.0f;
    }

    public void render(SpriteBatch batch){
        super.render(batch);
        if (gs.getHero().isActive()){
            batch.draw(regionMiniHero, this.position.x - halfWidthMiniHero,
                    this.position.y - halfHeightMiniHero);
        }
        for (int i = 0; i < gs.getHooligans().getActiveList().size(); i++) {
            if (isScans(gs.getHooligans().getActiveList().get(i)))
                batch.draw(regionMiniEnemy, this.position.x - halfWidthMiniEnemy + tmp.x,
                        this.position.y + tmp.y - halfHeightMiniEnemy);
        }
        for (int i = 0; i < gs.getFoods().getActiveList().size(); i++) {
            if (gs.getFoods().getActiveList().get(i).getType() != Food.Type.LEMON){
                if (isScans(gs.getFoods().getActiveList().get(i)))
                    batch.draw(regionMiniGoodFood, this.position.x + tmp.x - halfWidthMiniGoodFood,
                            this.position.y + tmp.y - halfHeightMiniGoodFood);
            }
        }
    }

    private boolean isScans(GamePoint unit){
        if (gs.getHero().getDistance(unit) <= scanRadius){
            tmp.set(unit.position);
            tmp.sub(gs.getHero().position);
            tmp.scl(halfWidth / scanRadius);
            return true;
        }
        return false;
    }
}
