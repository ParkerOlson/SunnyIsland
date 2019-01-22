package com.raven.breakingsands.scenes.hud;

import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.ui.UIContainer;

public class UIUpperRightContainer<S extends Scene>
        extends UIContainer<S> {

    public UIUpperRightContainer(S scene) {
        super(scene);
    }

    @Override
    public int getStyle() {
        return UIContainer.UPPER_RIGHT;
    }
}