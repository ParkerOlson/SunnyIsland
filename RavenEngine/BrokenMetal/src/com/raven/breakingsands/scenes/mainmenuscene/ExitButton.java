package com.raven.breakingsands.scenes.mainmenuscene;

import com.raven.engine2d.ui.UITextButton;

public class ExitButton
        extends UITextButton<MainMenuScene> {

    public ExitButton(MainMenuScene scene) {
        super(scene, "exit", "sprites/button.png", "mainbutton");
    }

    @Override
    public void handleMouseClick() {
        getScene().getGame().exit();
    }
}
