package com.raven.breakingsands.scenes.hud;

import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.ui.UIContainer;

public abstract class UILeftContainer<S extends Scene>
        extends UIContainer<S> {

    private float x, y;

    public UILeftContainer(S scene) {
        super(scene);
    }

    @Override
    public int getStyle() {
        return 2;
    }
}
