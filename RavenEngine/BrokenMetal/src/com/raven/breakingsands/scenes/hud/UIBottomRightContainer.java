package com.raven.breakingsands.scenes.hud;

import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.ui.UIContainer;

public class UIBottomRightContainer<S extends Scene>
        extends UIContainer<S> {

    public UIBottomRightContainer(S scene) {
        super(scene);
    }

    @Override
    public int getStyle() {
        return UIContainer.BOTTOM_RIGHT;
    }
}
