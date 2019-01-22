package com.raven.breakingsands.scenes.mainmenuscene;

import com.raven.breakingsands.BrokenMetalGame;
import com.raven.breakingsands.scenes.settingsscene.SettingsScene;
import com.raven.engine2d.ui.UITextButton;

public class SettingsButton
        extends UITextButton<MainMenuScene> {

    public SettingsButton(MainMenuScene scene) {
        super(scene, "settings", "sprites/button.png", "mainbutton");
    }

    @Override
    public void handleMouseClick() {
        BrokenMetalGame game = getScene().getGame();

        game.prepTransitionScene(new SettingsScene(game));
    }
}
