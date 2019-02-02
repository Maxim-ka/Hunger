package com.reschikov.gdx.game.hunger;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.reschikov.gdx.game.hunger.units.Waste;

public class WasteEmitter extends ObjectPool<Waste> {

    private final String[] textureName = {"corpse", "thorn"};
    private transient TextureRegion[] regions = new TextureRegion[textureName.length];
    private transient GameScreen gs;

    WasteEmitter(GameScreen gs){
        this.gs = gs;
        toRegions();
        addObjectsToFreeList(gs.getHooligans().freeList.size() * gs.getFoods().freeList.size());
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

    void update(float dt){
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }

    @Override
    protected Waste newObject() {
        return new Waste(gs, regions);
    }

    void setLoadedWaste(GameScreen gs){
        regions = new TextureRegion[textureName.length];
        toRegions();
        this.gs = gs;
        for (int i = 0; i < freeList.size(); i++) {
            freeList.get(i).reload(gs, regions);
        }
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).reload(gs, regions);
            activeList.get(i).reloadTextureRegion();
        }
    }
}
