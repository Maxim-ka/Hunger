package com.reschikov.gdx.game.hunger.units;

import com.badlogic.gdx.math.MathUtils;
import com.reschikov.gdx.game.hunger.Assets;
import com.reschikov.gdx.game.hunger.Rules;
import com.reschikov.gdx.game.hunger.GameScreen;

import static com.reschikov.gdx.game.hunger.units.Food.Type.LEMON;

public class Enemy extends Eater{

    private static final String DRAWABLE_BOUAAAAAH = "bouaaaaah";
    private static final float SCATTER = 0.05f;
    private final Eater hero;
    private boolean choice;

    public Enemy(GameScreen gs){
        super(gs, DRAWABLE_BOUAAAAAH);
        hero = gs.getHero();
        active = false;
    }

    public void init(){
        super.init();
        angle = MathUtils.random(Rules.ANGLE_0_DEGREES, Rules.ANGLE_360_DEGREES);
        scale = Rules.SCALE_EATER + MathUtils.random(-SCATTER, SCATTER);
        acceleration = getRandomAcceleration();
        satiety = scale;
    }

    @Override
    public void update(float dt){
        super.update(dt);
        targetSelection(dt);
        if (bounceOff(dt)) {
            position.set(tmp);
            return;
        }
        float velLen = getGoing(dt);
        for (int i = 0; i < velLen; i++) {
            tmp.set(position.x + nX, position.y + nY);
            faceFront(halfWidth * scale);
            position.set(tmp);
        }
    }

    private boolean bounceOff(float dt){
        for (int i = 0; i < gs.getHooligans().getActiveList().size(); i++) {
            Enemy rival = gs.getHooligans().getActiveList().get(i);
            if (rival == this) continue;
            if(isTouched(rival)){
                float rebound = rival.halfWidth * rival.scale;
                velocity.add(MathUtils.random(-rebound, rebound), MathUtils.random(-rebound, rebound));
                tmp.set(position);
                tmp.mulAdd(velocity, dt);
                return gs.getLandscape().isCellEmpty(tmp.x, tmp.y, halfWidth * scale);
            }
        }
        return false;
    }

    private void faceFront(float radius){
        float contact = gs.getLandscape().collide(tmp.x, tmp.y, radius, angle);
        float side;
        if (contact != Rules.NOT_FOUND){
            int back = 1;
            float difference = contact - angle;
            if (difference > Rules.ANGLE_90_DEGREES) difference -= Rules.ANGLE_360_DEGREES;
            if (Math.abs(difference) <= Rules.ANGLE_30_DEGREES) side = chooseSide(radius, angle);
            else if (difference < Rules.ANGLE_0_DEGREES)  side = moveLeft(radius, contact);
            else side = moveRight(radius, contact);
            if (side == Rules.NOT_FOUND) {
                back = -1;
                angle += Rules.ANGLE_180_DEGREES;
                if (angle > Rules.ANGLE_360_DEGREES) angle -= Rules.ANGLE_360_DEGREES;
            } else if (angle != side) angle = side;
            shift(angle, nX * back, nY * back);
        }
    }

    private float chooseSide(float radius, float hook){
        float side;
        if (choice){
            if ((side = moveRight(radius, hook)) == Rules.NOT_FOUND) side = moveLeft(radius, hook);
        } else if ((side = moveLeft(radius, hook)) == Rules.NOT_FOUND) side = moveRight(radius, hook);
        return side;
    }

    private void shift(float side, float nX, float nY){
        float axisX = MathUtils.cosDeg(side);
        float axisY = MathUtils.sinDeg(side);
        int offsetX = (Math.abs(axisX) <= 0.5f) ? 0 : (axisX < 0) ? -1 : 1;
        int offsetY = (Math.abs(axisY) <= 0.5f) ? 0 : (axisY < 0) ? -1 : 1;
        tmp.set(position.x + nX * offsetX, position.y + nY * offsetY);
    }

    private float moveRight(float radius, float hook){
        float side = angle - Rules.ANGLE_90_DEGREES;
        if (side < Rules.ANGLE_0_DEGREES) side += Rules.ANGLE_360_DEGREES;
        if (!gs.getLandscape().isContacts(tmp.x, tmp.y, radius, side)){
            hook -= Rules.ANGLE_90_DEGREES;
            if (hook < Rules.ANGLE_0_DEGREES) hook += Rules.ANGLE_360_DEGREES;
            return hook;
        }
        return Rules.NOT_FOUND;
    }

    private float moveLeft(float radius, float hook){
        float side = angle + Rules.ANGLE_90_DEGREES;
        if (side > Rules.ANGLE_360_DEGREES) side -= Rules.ANGLE_360_DEGREES;
        if (!gs.getLandscape().isContacts(tmp.x, tmp.y, radius, side)){
            hook += Rules.ANGLE_90_DEGREES;
            if (hook > Rules.ANGLE_360_DEGREES) hook -= Rules.ANGLE_360_DEGREES;
            return hook;
        }
        return Rules.NOT_FOUND;
    }

    private void targetSelection(float dt){
        angleToTarget = angle;
        if (hero.isActive() && hero.scale < this.scale){
            tmp.set(hero.position);
            target.set(tmp.mulAdd(hero.velocity, dt));
            angleToTarget = target.sub(this.position).angle();
        }
        for (int i = 0; i < gs.getFoods().getActiveList().size(); i++){
            if (isDiscovered(gs.getFoods().getActiveList().get(i), dt)){
                Food food = gs.getFoods().getActiveList().get(i);
                tmp.set(food.position);
                target.set(tmp.mulAdd(food.velocity, dt));
                if (food.getType() == LEMON) target.set(target.scl(Rules.TURN));
                angleToTarget = target.sub(this.position).angle();
                break;
            }
        }
        if (isDiscovered(hero, dt)) if (hero.scale > this.scale) turnAround();
        stumbleOn(dt);
        choice = angle > angleToTarget;
    }

    private void stumbleOn(float dt){
        for (int i = 0; i < gs.getWaste().getActiveList().size(); i++) {
            if (isDiscovered(gs.getWaste().getActiveList().get(i), dt)){
                turnAround();
                return;
            }
        }
    }

    private void turnAround(){
        angleToTarget += Rules.ANGLE_180_DEGREES;
        if (angleToTarget > Rules.ANGLE_360_DEGREES) angleToTarget -= Rules.ANGLE_360_DEGREES;
    }

    private boolean isDiscovered(GamePoint unit, float dt){
        if (unit.isActive()){
            float radiusOfDetection = (scale > Rules.SCALE_EATER) ? width * scale :
                    width * Rules.SCALE_EATER;
            float ratio = (this.scale > unit.scale) ? this.scale / unit.scale : 1.0f;
            float distance = getDistance(unit);
            if (distance <= radiusOfDetection * ratio + unit.halfWidth * unit.scale){
                target.set(unit.position.mulAdd(unit.velocity, dt));
                angleToTarget = target.sub(this.position).angle();
                acceleration += acceleration * ratio * dt;
                return true;
            }
        }
        acceleration = getRandomAcceleration();
        return false;
    }

    public  void reload(GameScreen gs){
        this.gs = gs;
        region = Assets.getInstance().getAtlas().findRegion(DRAWABLE_BOUAAAAAH);
    }
}
