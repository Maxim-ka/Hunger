package com.reschikov.gdx.game.android;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.utils.I18NBundle;

public class Assets {

    private static final int BORDER_THICKNESS = 2;
    private static final int SHADOW = 4;
    private static final int COPY_RIGHT_FONT_SIZE = 16;
    private static final String FONT_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГҐДЂЕЁЄЖЗЅИІЇЙЈКЛЉМНЊОПРСТЋУЎФХЦЧЏШЩЪЫЬЭЮЯабвгґдђеёєжзѕиіїйјклљмнњопрстћуўфхцчџшщъыьэюя1234567890‘?’“!”(%)[#]{@}/&\\<-+÷×=>®©$€£¥¢:;,.*";
    private static final String HUNGER_GAME_PACK = "hunger_game.pack";
    private static final String MENU_HUNGER_PACK = "menu_hunger.pack";
    private static final String HUNGER_OVER_PACK = "hunger_over.pack";
    private static final String GABRIELA_TTF = "gabriela.ttf";
    private static final String GABRIELA = "gabriela";
    private static final String TTF = ".ttf";
    private static final Assets ourInstance = new Assets();
    private final AssetManager assetManager;
    private TextureAtlas atlas;

    AssetManager getAssetManager() {
        return assetManager;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public static Assets getInstance() {
        return ourInstance;
    }

    private Assets() {
        assetManager = new AssetManager();
    }

    void loadAssets(ScreenManager.ScreenType type){
        switch (type){
            case GAME:
                assetManager.load(HUNGER_GAME_PACK, TextureAtlas.class);
                assetManager.load(Rules.WORDS_GAME_SCREEN, I18NBundle.class);
                assetManager.load(Rules.TO_BE_CONTINUED_MP3, Music.class);
                assetManager.load(Rules.BEVERLY_HILLS_COP_1984_MP3, Music.class);
                createStdFont(26);
                createStdFont(32);
                createStdFont(48);
                break;
            case MENU:
                assetManager.load(MENU_HUNGER_PACK, TextureAtlas.class);
                assetManager.load(Rules.WORDS_MENU_SCREEN, I18NBundle.class);
                createStdFont(COPY_RIGHT_FONT_SIZE);
                createStdFont(26);
                createStdFont(32);
                createStdFont(48);
                createStdFont(92);
                break;
            case OVER:
                assetManager.load(HUNGER_OVER_PACK, TextureAtlas.class);
                assetManager.load(Rules.WORDS_OVER_SCREEN, I18NBundle.class);
                createStdFont(48);
                createStdFont(72);
                break;
        }
    }

    void makeLinks(){
        if (assetManager.isLoaded(MENU_HUNGER_PACK)){
            atlas = assetManager.get(MENU_HUNGER_PACK);
            return;
        }
        if (assetManager.isLoaded(HUNGER_GAME_PACK)) {
            atlas = assetManager.get(HUNGER_GAME_PACK);
            return;
        }
        if (assetManager.isLoaded(HUNGER_OVER_PACK))
            atlas = assetManager.get(HUNGER_OVER_PACK);
    }

    private void createStdFont(int size) {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, TTF, new FreetypeFontLoader(resolver));
        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = GABRIELA_TTF;
        fontParameter.fontParameters.characters = FONT_CHARACTERS;
        fontParameter.fontParameters.size = size;
        fontParameter.fontParameters.color = Color.YELLOW;
        if (size > COPY_RIGHT_FONT_SIZE){
            fontParameter.fontParameters.borderWidth = BORDER_THICKNESS;
            fontParameter.fontParameters.borderColor = Color.CORAL;
            fontParameter.fontParameters.shadowOffsetX = SHADOW;
            fontParameter.fontParameters.shadowOffsetY = SHADOW;
            fontParameter.fontParameters.shadowColor = Color.BLACK;
        }
        assetManager.load(GABRIELA + size + TTF, BitmapFont.class, fontParameter);
    }

    void clear(){
        assetManager.clear();
    }
}
