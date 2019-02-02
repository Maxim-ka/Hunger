package com.reschikov.gdx.game.hunger;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.reschikov.gdx.game.hunger.units.Particle;

public class ParticleEmitter extends ObjectPool<Particle>{

    private static final int NUMBER_PARTICLE = 200;

    ParticleEmitter() {
        addObjectsToFreeList(NUMBER_PARTICLE);
    }

    public void render(SpriteBatch batch) {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = 0; i < activeList.size(); i++) {
            Particle o = activeList.get(i);
            float t = o.getTime() / o.getTimeMax();
            o.setScale(lerp(o.getSize1(), o.getSize2(), t));
            setColorParticle(batch, o, t);
            o.render(batch);
        }
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (int i = 0; i < activeList.size(); i++) {
            Particle o = activeList.get(i);
            float t = o.getTime() / o.getTimeMax();
            o.setScale(lerp(o.getSize1(), o.getSize2(), t));
            setColorParticle(batch, o, t);
            o.render(batch);
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void setColorParticle(SpriteBatch batch, Particle o, float point) {
        float timePeriod = o.getTimeMax() / 4;
        int indexPeriod = 0;
        while (o.getTime() >= timePeriod){
            timePeriod += timePeriod;
            indexPeriod++;
        }
        switch (indexPeriod){
            case 0:
                batch.setColor(o.getR1(), o.getG1(), o.getB1(), o.getA1());
                break;
            case 1:
                batch.setColor(o.getR1(), lerp(o.getG1(), o.getG2(), point), o.getB2(), o.getA1());
                break;
            case 2:
                batch.setColor(lerp(o.getR1(), o.getR2(), point), o.getG2(), o.getB2(), o.getA1());
                break;
            case 3:
                batch.setColor(o.getR2(), o.getG2(), o.getB2(), lerp(o.getA1(), o.getA2(), point));
                break;
            default:
                batch.setColor(o.getR2(), o.getG2(), o.getB2(), o.getA2());
        }
    }

    private float lerp(float value1, float value2, float point) {
        return value1 + (value2 - value1) * point;
    }

    public void launch(Vector2 posStart, float radius, float angle, float timeMax, float size1, float size2, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
        getActiveElement().init(posStart, radius, angle, timeMax, size1, size2, r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }

    @Override
    protected Particle newObject() {
        return new Particle();
    }

    void setLoadedParticleEmitter(){
        for (int i = 0; i < freeList.size(); i++) {
            freeList.get(i).setRegion(Assets.getInstance().getAtlas().findRegion(Rules.DRAWABLE_PARTICLE_DETONATION));
        }
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).setRegion(Assets.getInstance().getAtlas().findRegion(Rules.DRAWABLE_PARTICLE_DETONATION));
        }
    }
}
