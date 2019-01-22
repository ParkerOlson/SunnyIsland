package com.raven.breakingsands.scenes.battlescene.menu;

import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.breakingsands.scenes.hud.UICenterContainer;
import com.raven.breakingsands.scenes.mainmenuscene.MainMenuScene;
import com.raven.engine2d.util.math.Vector4f;

public class Menu extends UICenterContainer<BattleScene> {

    public Menu(BattleScene scene) {
        super(scene);

        Menu menu = this;
        MenuButton resumeButton = new MenuButton(getScene(), "resume") {
            @Override
            public void handleMouseClick() {
                menu.setVisibility(false);
                menu.getScene().setPaused(false);
            }
        };
        resumeButton.load();
        addChild(resumeButton);

        MenuButton mainMenuButton = new MenuButton(getScene(), "main menu") {
            @Override
            public void handleMouseClick() {
                getScene().getGame().prepTransitionScene(new MainMenuScene(getScene().getGame()));
            }
        };
        mainMenuButton.load();
        addChild(mainMenuButton);

        MenuButton exitButton = new MenuButton(getScene(), "exit") {
            @Override
            public void handleMouseClick() {
                getScene().getGame().exit();
            }
        };
        exitButton.load();
        addChild(exitButton);

        pack();
    }
}
