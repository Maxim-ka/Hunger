package com.reschikov.gdx.game.hunger;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Joystick extends InputAdapter {

    private static final int NO_PRESSING = -1;
    private static final int initX = 50;
    private static final int initY = 50;
    private static final String JOYSTICK = "joystick";
    private final TextureRegion back;
    private final TextureRegion stick;
    private final Rectangle rectangle;
    private int lastId;
    private final Vector2 vs;
    private final Vector2 tmp;
    private final Vector2 position;
    private final float maxShift;

    public boolean isActive() {
        return lastId > NO_PRESSING;
    }

    Joystick() {
        TextureRegion texture = Assets.getInstance().getAtlas().findRegion(JOYSTICK);
        back = new TextureRegion(texture, 0, 0, 200, 200);
        stick = new TextureRegion(texture, 0, 200, 50, 50);
        rectangle = new Rectangle(initX, initY, back.getRegionWidth(), back.getRegionHeight());
        maxShift = rectangle.width / 2 - stick.getRegionWidth() / 2.0f;
        vs = new Vector2();
        tmp = new Vector2();
        position = new Vector2();
        lastId = NO_PRESSING;
    }

    public void render(SpriteBatch batch) {
        if (lastId != NO_PRESSING) {
            batch.setColor(1, 1, 1, 0.5f);
            batch.draw(back, rectangle.x, rectangle.y);
            batch.setColor(1, 1, 1, 0.7f);
            batch.draw(stick, position.x + vs.x - stick.getRegionWidth() / 2.0f,
                    position.y + vs.y - stick.getRegionHeight() / 2.0f);
            batch.setColor(1, 1, 1, 1.0f);
        }
    }

    private void checkPosition(){
        float xAxisDistance = Rules.INDENT + rectangle.width / 2;
        float yAxisDistance = Rules.INDENT + rectangle.height / 2;
        if (tmp.x < xAxisDistance) tmp.x = xAxisDistance;
        if (tmp.y < yAxisDistance) tmp.y = yAxisDistance;
        if (tmp.x > Rules.WORLD_WIDTH - xAxisDistance) tmp.x = Rules.WORLD_WIDTH - xAxisDistance;
        if (tmp.y > Rules.WORLD_HEIGHT - yAxisDistance) tmp.y = Rules.WORLD_HEIGHT - yAxisDistance;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        tmp.set(screenX, screenY);
        ScreenManager.getInstance().getViewPort().unproject(tmp);
        if (lastId == NO_PRESSING) {
            lastId = pointer;
            checkPosition();
            position.set(tmp);
            rectangle.x = position.x - rectangle.width / 2;
            rectangle.y = position.y - rectangle.height / 2;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (lastId == pointer) {
            lastId = NO_PRESSING;
            vs.setZero();
            return true;
        }
        return false;
    }

    public float getAngle() {
        return vs.angle();
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        tmp.set(screenX, screenY);
        ScreenManager.getInstance().getViewPort().unproject(tmp);
        if (lastId == pointer) {
            vs.set(tmp.sub(position));
            if (vs.len() >= maxShift) vs.nor().scl(maxShift);
            return true;
        }
        return false;
    }
}
