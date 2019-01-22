package com.raven.breakingsands.scenes.hud;

import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.ui.UIContainer;
import com.raven.engine2d.util.math.Vector2f;

public class UIUpperContainer<S extends Scene>
        extends UIContainer<S> {


    public UIUpperContainer(S scene) {
        super(scene);
    }

    @Override
    public int getStyle() {
        return UIContainer.UPPER;
    }
}
