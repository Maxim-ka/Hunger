package com.reschikov.gdx.game.android;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.reschikov.gdx.game.android.units.Eater;
import com.reschikov.gdx.game.android.units.Hero;

import java.io.Serializable;

public class Landscape implements Serializable {

    private static final int NUMBER_POINT = 8;
    private static final int PERCENT_FILLING = 5;
    private static final int BLOCK = 1;
    private static final int EMPTINESS = 0;
    private static final String DRAWABLE_EMBOSSED_TILE = "embossedTile";
    private static final String DRAWABLE_TREE_IN_BOX = "treeInBox";
    private transient TextureRegion flooring;
    private transient TextureRegion tree;
    private final int cellSize;
    private final int numberCellX, numberCellY;
    private final byte[][] data;
    private transient GameScreen gs;

    Landscape(GameScreen gs) {
        this.gs = gs;
        flooring = Assets.getInstance().getAtlas().findRegion(DRAWABLE_EMBOSSED_TILE);
        tree = Assets.getInstance().getAtlas().findRegion(DRAWABLE_TREE_IN_BOX);
        cellSize = flooring.getRegionWidth();
        numberCellX = Rules.GLOBAL_WIDTH / cellSize;
        numberCellY = Rules.GLOBAL_HEIGHT / cellSize;
        data = new byte[numberCellX][numberCellY];
    }

    public void initMapLevel(Hero hero){
        for (int i = 0; i < numberCellX; i++) {
            for (int j = 0; j < numberCellY; j++) {
                if (MathUtils.random(Rules.RANGE_100) <= PERCENT_FILLING + gs.getLevel() &&
                        !isPlaceForHero(hero, i, j))
                    data[i][j] = BLOCK;
                else data[i][j] = EMPTINESS;
            }
        }
    }

    public void render(SpriteBatch batch){
        for (int i = 0; i < numberCellX; i++) {
            for (int j = 0; j < numberCellY; j++) {
                batch.draw(flooring, i * cellSize, j * cellSize);
                if (data[i][j] == BLOCK) batch.draw(tree, i * cellSize , j * cellSize);
            }
        }
    }

    public void update(float dt){}

    private boolean isPlaceForHero(Eater eater, int cellX, int cellY) {
        if((int)eater.getPosition().x /cellSize == cellX && (int)eater.getPosition().y / cellSize == cellY) return true;
        float radius = eater.getScale() * eater.getHalfWidth();
        for (int i = 0; i < NUMBER_POINT; i++) {
            float angle = (2 * MathUtils.PI / NUMBER_POINT) * i;
            float tmpX = eater.getPosition().x + radius * MathUtils.cos(angle);
            float tmpY = eater.getPosition().y + radius * MathUtils.sin(angle);
            if (cellX == (int) tmpX / cellSize && cellY == (int) tmpY / cellSize) return true;
        }
        return false;
    }

    private int checkX(int cellX){
        if (cellX >= numberCellX) cellX = 0;
        if (cellX < 0) cellX = numberCellX - 1;
        return cellX;
    }

    private int checkY(int cellY){
        if (cellY >= numberCellY) cellY = 0;
        if (cellY < 0) cellY = numberCellY - 1;
        return cellY;
    }

    public boolean isContacts(float x, float y, float radius, float angle){
        float tmpX = x + radius * MathUtils.cosDeg(angle);
        float tmpY = y + radius * MathUtils.sinDeg(angle);
        int cellX = (int) tmpX/ cellSize;
        int cellY = (int) tmpY/ cellSize;
        return data[checkX(cellX)][checkY(cellY)] == BLOCK;
    }

    public float collide(float x, float y, float radius, float direction){
        float startAngle = direction - Rules.ANGLE_90_DEGREES + Rules.ANGLE_30_DEGREES;
        if (startAngle < 0) startAngle += Rules.ANGLE_360_DEGREES;
        return getAngleContact(x, y, radius, startAngle);
    }

    private float getAngleContact(float x, float y, float radius, float startAngle){
        int numberPoint = (int) (Rules.ANGLE_120_DEGREES / Rules.ANGLE_30_DEGREES);
        for (int i = 0; i <= numberPoint; i++) {
            float angle = startAngle + Rules.ANGLE_30_DEGREES * i;
            if (angle > Rules.ANGLE_360_DEGREES) angle -= Rules.ANGLE_360_DEGREES;
            if (isContacts(x, y, radius, angle)) return angle;
        }
        return Rules.NOT_FOUND;
    }

    private boolean isCellEmpty(float x, float y){
        int cellX = (int) x/ cellSize;
        int cellY = (int) y/ cellSize;
        return data[checkX(cellX)][checkY(cellY)] != BLOCK;
    }

    public boolean isCellEmpty(float x, float y, float radius){
        if (!isCellEmpty(x, y)) return false;
        for (int i = 0; i < NUMBER_POINT; i++) {
            float angle = (2 * MathUtils.PI / NUMBER_POINT) * i;
            float tmpX = x + radius * MathUtils.cos(angle);
            float tmpY = y + radius * MathUtils.sin(angle);
            int cellX = (int) tmpX/ cellSize;
            int cellY = (int) tmpY/ cellSize;
            if (data[checkX(cellX)][checkY(cellY)] == BLOCK) return false;
        }
        return true;
    }

    void setLoadedLandscape(GameScreen gs){
        flooring = Assets.getInstance().getAtlas().findRegion(DRAWABLE_EMBOSSED_TILE);
        tree = Assets.getInstance().getAtlas().findRegion(DRAWABLE_TREE_IN_BOX);
        this.gs = gs;
    }
}