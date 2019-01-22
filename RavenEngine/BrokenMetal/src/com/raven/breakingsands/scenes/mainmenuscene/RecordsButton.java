package com.raven.breakingsands.scenes.mainmenuscene;

import com.raven.breakingsands.BrokenMetalGame;
import com.raven.breakingsands.scenes.recordsscene.RecordsScene;
import com.raven.engine2d.ui.UITextButton;

public class RecordsButton
        extends UITextButton<MainMenuScene> {

    public RecordsButton(MainMenuScene scene) {
        super(scene, "records", "sprites/button.png", "mainbutton");
    }

    @Override
    public void handleMouseClick() {
        BrokenMetalGame game = getScene().getGame();

        game.prepTransitionScene(new RecordsScene(game));
    }
}
