package com.raven.engine2d.graphics2d.shader;

import com.raven.engine2d.GameEngine;
import com.raven.engine2d.scene.Scene;

public abstract class ShaderTexture {

    private GameEngine engine;

    protected ShaderTexture(GameEngine engine) {
        this.engine = engine;
    }

    protected GameEngine getEngine() {
        return engine;
    }

    public abstract void load(Scene scene);

    public abstract int getTexture();

    public abstract void release();

    public abstract int getWidth();

    public abstract int getHeight();
}
