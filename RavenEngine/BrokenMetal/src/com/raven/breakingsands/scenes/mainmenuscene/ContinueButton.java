package com.raven.breakingsands.scenes.mainmenuscene;

import com.raven.engine2d.ui.UIButton;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.ui.UIContainer;
import com.raven.engine2d.ui.UITextButton;

public class ContinueButton
        extends UITextButton<MainMenuScene> {


    public ContinueButton(MainMenuScene scene) {
        super(scene, "Continue", "sprites/button.png", "mainbutton");
    }

    @Override
    public void handleMouseClick() {
        getScene().getGame().loadGame();
    }
}
