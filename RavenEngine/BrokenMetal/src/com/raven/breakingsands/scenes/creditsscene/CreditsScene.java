package com.raven.breakingsands.scenes.creditsscene;

import com.raven.breakingsands.BrokenMetalGame;
import com.raven.breakingsands.scenes.hud.UICenterContainer;
import com.raven.breakingsands.scenes.mainmenuscene.MainMenuScene;
import com.raven.breakingsands.scenes.settingsscene.SettingsScene;
import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.ui.UIContainer;
import com.raven.engine2d.ui.UIFont;
import com.raven.engine2d.ui.UILabel;
import com.raven.engine2d.ui.UITextButton;

public class CreditsScene extends Scene<BrokenMetalGame> {
    public CreditsScene(BrokenMetalGame game) {
        super(game);
    }

    @Override
    public void loadShaderTextures() {

    }

    @Override
    public void onEnterScene() {

        UICenterContainer<CreditsScene> container = new UICenterContainer<>(this);
        addChild(container);

        addCredit(container, "published",
                "armadillo game studios (parker olson)");
        addCredit(container, "game design",
                "parker olson");
        addCredit(container, "art",
                "parker olson");
        addCredit(container, "sound effects",
                "parker olson",
                "Additional sound effects from https://www.zapsplat.com");
        addCredit(container, "qa",
                "parker olson");
        addCredit(container, "main theme",
                "Harmful or Fatal Kevin MacLeod (incompetech.com)",
                "Licensed under Creative Commons: By Attribution 3.0 License",
                "http://creativecommons.org/licenses/by/3.0/");

        UITextButton<CreditsScene> exitBtn = new UITextButton<CreditsScene>(this, "main menu", "sprites/button.png", "mainbutton") {
            @Override
            public void handleMouseClick() {
                getGame().prepTransitionScene(new MainMenuScene(getGame()));
            }
        };
        exitBtn.load();
        container.addChild(exitBtn);

        container.pack();
    }

    private void addCredit(UIContainer<CreditsScene> container, String title, String... text) {
        int height = 27 + text.length * 11;

        StringBuilder s = new StringBuilder(title + " -");
        for (String t : text) {
            s.append("\n    ").append(t);
        }

        UILabel<CreditsScene> lbl = new UILabel<>(this, s.toString(), 300, height);
        UIFont font = lbl.getFont();
        font.setSmall(true);
        font.setWrap(true);
        lbl.load();
        container.addChild(lbl);
    }

    @Override
    public void onExitScene() {

    }

    @Override
    public void onUpdate(float deltaTime) {

    }

    @Override
    public void inputKey(int key, int action, int mods) {

    }
}
