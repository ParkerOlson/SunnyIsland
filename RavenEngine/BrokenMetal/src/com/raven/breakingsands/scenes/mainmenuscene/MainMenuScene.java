package com.raven.breakingsands.scenes.mainmenuscene;

import com.raven.breakingsands.BrokenMetalGame;
import com.raven.breakingsands.scenes.hud.UIBottomLeftContainer;
import com.raven.breakingsands.scenes.hud.UIUpperRightContainer;
import com.raven.engine2d.GameProperties;
import com.raven.engine2d.graphics2d.shader.ShaderTexture;
import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.ui.UIFont;
import com.raven.engine2d.ui.UILabel;
import com.raven.engine2d.util.math.Vector2f;
import com.raven.engine2d.util.math.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MainMenuScene extends Scene<BrokenMetalGame> {

    private DisplayPawn pawn;

    public MainMenuScene(BrokenMetalGame game) {
        super(game);
    }

    @Override
    public void loadShaderTextures() {
        List<ShaderTexture> textures = getShaderTextures();

        textures.addAll(DisplayPawn.getSpriteSheets(this));
    }

    @Override
    public void onEnterScene() {

        // Pawn
        pawn = new DisplayPawn(this);
        addChild(pawn);

        // Background
        setBackgroundColor(new Vector3f(0, 0, 0));

        // World Offset
        Vector2f wo = getWorldOffset();

        wo.x = GameProperties.getDisplayWidth() / (GameProperties.getScaling() * 2) - 16;
        wo.y = GameProperties.getDisplayHeight() / (GameProperties.getScaling() * 2);

        // UI
        UIBottomLeftContainer<MainMenuScene> container = new UIBottomLeftContainer<>(this);
        addChild(container);

        Path charPath = Paths.get(getGame().getMainDirectory(), "save");

        File sFile = charPath.toFile();
        if (sFile.list() == null) {
            sFile.mkdir();
        }

        if (sFile.list().length > 0) {
            ContinueButton continueBtn = new ContinueButton(this);
            continueBtn.load();
            container.addChild(continueBtn);
        }

        NewGameButton newGameBtn = new NewGameButton(this);
        newGameBtn.load();
        container.addChild(newGameBtn);

        RecordsButton recordsButton = new RecordsButton(this);
        recordsButton.load();
        container.addChild(recordsButton);

        SettingsButton settingsButton = new SettingsButton(this);
        settingsButton.load();
        container.addChild(settingsButton);

        CreditsButton creditsButton = new CreditsButton(this);
        creditsButton.load();
        container.addChild(creditsButton);

        ExitButton exitBtn = new ExitButton(this);
        exitBtn.load();
        container.addChild(exitBtn);

        container.pack();

        // version
        UIUpperRightContainer<MainMenuScene> rightContainer = new UIUpperRightContainer<>(this);
        UILabel<MainMenuScene> version = new UILabel<>(this, "beta - 0.2.2", 60, 11);
        rightContainer.addChild(version);
        UIFont font = version.getFont();
        font.setSide(UIFont.Side.RIGHT);
        font.setSmall(true);
        version.load();
        addChild(rightContainer);
        rightContainer.pack();

        // Music
//        getGame().playSong("Chapter 50.wav");
    }

    @Override
    public void onExitScene() {

    }

    @Override
    public void onUpdate(float deltaTime) {

    }

    @Override
    public void inputKey(int key, int action, int mods) {
        if (GLFW.GLFW_KEY_ESCAPE == key && GLFW.GLFW_PRESS == action) {
            getGame().exit();
        }
    }
}
