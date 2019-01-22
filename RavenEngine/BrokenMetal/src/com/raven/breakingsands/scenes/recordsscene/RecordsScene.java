package com.raven.breakingsands.scenes.recordsscene;

import com.raven.breakingsands.BrokenMetalGame;
import com.raven.breakingsands.scenes.hud.UICenterContainer;
import com.raven.engine2d.scene.Scene;

public class RecordsScene extends Scene<BrokenMetalGame> {

    private UIRecordsDisplay display;

    public RecordsScene(BrokenMetalGame game) {
        super(game);
    }

    @Override
    public void loadShaderTextures() {
        getEngine().getSpriteSheet("sprites/alphabet_small.png").load(this);
        getEngine().getSpriteSheet("sprites/alphabet.png").load(this);
    }

    @Override
    public void onEnterScene() {
        UICenterContainer<RecordsScene> centerContainer = new UICenterContainer<>(this);
        display = new UIRecordsDisplay(this);
        centerContainer.addChild(display);
        centerContainer.pack();
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
