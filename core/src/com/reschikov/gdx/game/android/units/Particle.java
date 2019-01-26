package com.reschikov.gdx.game.android.units;

import com.badlogic.gdx.math.Vector2;
import com.reschikov.gdx.game.android.Rules;

public class Particle extends GamePoint {

    private float r1, g1, b1, a1;
    private float r2, g2, b2, a2;
    private float time;
    private float timeMax;
    private float size1, size2;

    public float getR1() {
        return r1;
    }

    public float getG1() {
        return g1;
    }

    public float getB1() {
        return b1;
    }

    public float getA1() {
        return a1;
    }

    public float getR2() {
        return r2;
    }

    public float getG2() {
        return g2;
    }

    public float getB2() {
        return b2;
    }

    public float getA2() {
        return a2;
    }

    public float getTime() {
        return time;
    }

    public float getTimeMax() {
        return timeMax;
    }

    public float getSize1() {
        return size1;
    }

    public float getSize2() {
        return size2;
    }

    public Particle() {
        super(null, Rules.DRAWABLE_PARTICLE_DETONATION);
    }

    public void init(Vector2 posStart, float radius, float angle, float timeMax, float size1, float size2, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2) {
        position.set(posStart);
        velocity.set(radius * (float) Math.cos(Math.toRadians(angle)), radius * (float) Math.sin(Math.toRadians(angle)));
        this.r1 = r1;
        this.r2 = r2;
        this.g1 = g1;
        this.g2 = g2;
        this.b1 = b1;
        this.b2 = b2;
        this.a1 = a1;
        this.a2 = a2;
        this.time = 0.0f;
        this.timeMax = timeMax;
        this.size1 = size1;
        this.size2 = size2;
        this.active = true;
    }

    public void toGetTo(Eater eater){
        if (eater.getDistance(this) < eater.scale * eater.halfWidth + this.scale * this.halfWidth){
            eater.scale -= this.scale;
            this.deactivate();
            if (eater.scale < Rules.MIN_SCALE) eater.active = false;
        }
    }

    public void update(float dt) {
        time += dt;
        position.mulAdd(velocity, dt);
        if (time > timeMax) {
            deactivate();
        }
    }

    private void deactivate() {
        active = false;
    }
}
