package com.reschikov.gdx.game.hunger.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.reschikov.gdx.game.hunger.HungerGame;
import com.reschikov.gdx.game.hunger.Rules;
import com.reschikov.gdx.game.hunger.ScreenManager;

class DesktopLauncher {

    private static final String TITLE = "HUNGER v0.0.1";

    public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Rules.WORLD_WIDTH;
		config.height = Rules.WORLD_HEIGHT;
		config.title = TITLE;
		new LwjglApplication(new HungerGame(), config){
            @Override
            public void exit() {
                if (ScreenManager.getInstance().getMs().isExit() ||
                    ScreenManager.getInstance().getGos().isExit()) super.exit();
            }
        };
	}
}
