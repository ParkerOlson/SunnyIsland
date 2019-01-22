package com.raven.breakingsands.scenes.mainmenuscene;

import com.raven.breakingsands.BrokenMetalGame;
import com.raven.breakingsands.scenes.creditsscene.CreditsScene;
import com.raven.breakingsands.scenes.settingsscene.SettingsScene;
import com.raven.engine2d.ui.UITextButton;

public class CreditsButton
        extends UITextButton<MainMenuScene> {

    public CreditsButton(MainMenuScene scene) {
        super(scene, "credits", "sprites/button.png", "mainbutton");
    }

    @Override
    public void handleMouseClick() {
        BrokenMetalGame game = getScene().getGame();

        game.prepTransitionScene(new CreditsScene(game));
    }
}
