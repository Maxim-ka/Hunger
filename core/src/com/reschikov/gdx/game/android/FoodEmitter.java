package com.reschikov.gdx.game.android;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.reschikov.gdx.game.android.units.Food;

public class FoodEmitter extends ObjectPool<Food>{

    private static final int NUMBER_FOODS = 30;
    private static final int FIFTY_FIFTY = 50;
    private static final int PERCENTAGE_OF_BAD_FOOD = 15;
    private static final int CHANGE_STEP = 5;
    private static final float CREATION_TIME_FOOD = 0.5f;
    private final String[] textureName = {"pizza", "lemon", "doughnut"};
    private transient TextureRegion[] regions = new TextureRegion[textureName.length];
    private transient GameScreen gs;
    private float time;

    FoodEmitter(GameScreen gs){
        this.gs = gs;
        toRegions();
        addObjectsToFreeList(NUMBER_FOODS);
    }

    private void toRegions(){
        for (int i = 0; i < regions.length; i++) {
            regions[i] = Assets.getInstance().getAtlas().findRegion(textureName[i]);
        }
    }

    void render(SpriteBatch batch){
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch);
        }
    }

    private void generateFood() {
        int shareOfCalories = FIFTY_FIFTY + gs.getLevel()* CHANGE_STEP;
        int shareBadFood = PERCENTAGE_OF_BAD_FOOD + gs.getLevel() * CHANGE_STEP;
        Food.Type goodFood = (MathUtils.random(Rules.RANGE_100) < shareOfCalories) ? Food.Type.PIZZA : Food.Type.DOUGHNUT;
        Food.Type type = (MathUtils.random(Rules.RANGE_100) < shareBadFood) ? Food.Type.LEMON : goodFood;
        getActiveElement().init(type);
    }

    void update(float dt){
        if (activeList.size() <= NUMBER_FOODS) {
            time += dt;
            if (time >= CREATION_TIME_FOOD + CREATION_TIME_FOOD * gs.getLevel()){
                time = 0;
                generateFood();
            }
        }
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }

    @Override
    protected Food newObject() {
        return new Food(gs, regions);
    }

    void setLoadedFoodEmitter(GameScreen gs){
        this.gs = gs;
        regions = new TextureRegion[textureName.length];
        toRegions();
        for (int i = 0; i < freeList.size(); i++) {
            freeList.get(i).reload(gs, regions);
        }
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).reload(gs,regions);
            activeList.get(i).reloadTextureRegion();
        }
    }
}
