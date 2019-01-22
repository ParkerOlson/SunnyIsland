package com.raven.engine2d.ui;

import com.raven.engine2d.database.GameData;
import com.raven.engine2d.graphics2d.sprite.SpriteAnimationState;
import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.util.math.Vector2f;
import com.raven.engine2d.util.math.Vector2i;
import com.raven.engine2d.util.math.Vector4f;
import com.raven.engine2d.worldobject.Parentable;

public class UILabel<S extends Scene>
        extends UIText<S> {

    private int width, height;

    public UILabel(S scene, String text, int width, int height) {
        super(scene, text);

        this.width = width;
        this.height = height;
    }

    @Override
    public int getStyle() {
        return getParent().getStyle();
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public SpriteAnimationState getSpriteAnimationState() {
        return null;
    }
}
