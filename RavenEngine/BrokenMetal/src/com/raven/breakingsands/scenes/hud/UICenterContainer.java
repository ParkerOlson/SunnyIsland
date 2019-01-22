package com.raven.breakingsands.scenes.hud;

import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.ui.UIContainer;
import com.raven.engine2d.util.math.Vector2f;

public class UICenterContainer<S extends Scene>
        extends UIContainer<S> {


    public UICenterContainer(S scene) {
        super(scene);
    }

    @Override
    public int getStyle() {
        return UIContainer.CENTER;
    }
}
