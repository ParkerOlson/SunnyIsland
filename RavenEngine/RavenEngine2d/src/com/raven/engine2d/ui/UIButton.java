package com.raven.engine2d.ui;

import com.raven.engine2d.GameEngine;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.graphics2d.sprite.SpriteAnimationState;
import com.raven.engine2d.graphics2d.sprite.SpriteSheet;
import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.ui.UIContainer;
import com.raven.engine2d.ui.UIText;
import com.raven.engine2d.util.math.Vector2f;
import com.raven.engine2d.util.math.Vector4f;
import com.raven.engine2d.worldobject.MouseHandler;
import com.raven.engine2d.worldobject.Parentable;
import com.sun.org.glassfish.gmbal.ManagedObject;

public abstract class UIButton<S extends Scene>
        extends UIObject<S, UIObject<S, Parentable<UIObject>>>
        implements MouseHandler {

    private Vector2f position = new Vector2f();
    private SpriteAnimationState spriteAnimationState;
    private UIImage<S> image;
    private boolean disable, active, locked;

    public UIButton(S scene, String btnImgSrc, String animation) {
        super(scene);

        spriteAnimationState = new SpriteAnimationState(this, scene.getEngine().getAnimation(animation));
        image = new UIImage<>(scene, (int) getWidth(), (int) getHeight() * 2, btnImgSrc);
        image.setSpriteAnimation(spriteAnimationState);

        addChild(image);

        this.addMouseHandler(this);
    }

    public void setSprite(String src) {
        image.setSprite(src);
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setDisable(boolean disable) {
        if (!locked) {
            this.disable = disable;

            if (disable) {
                spriteAnimationState.setAction("disable");
            } else if (active) {
                spriteAnimationState.setAction("active");
            } else if (isMouseHovering()) {
                spriteAnimationState.setAction("hover");
            } else {
                spriteAnimationState.setActionIdle();
            }
        }
    }

    public boolean isDisabled() {
        return disable;
    }

    public void setActive(boolean active) {
        if (!locked) {
            if (!disable) {
                this.active = active;

                if (active)
                    spriteAnimationState.setAction("active");
                else {
                    if (isMouseHovering()) {
                        spriteAnimationState.setAction("hover");
                    } else {
                        spriteAnimationState.setActionIdle();
                    }
                }
            } else if (!active) {
                this.active = false;
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void release() {
        super.release();
    }

    @Override
    public int getStyle() {
        return getParent().getStyle();
    }

    @Override
    public void handleMouseHover(float delta) {

    }

    @Override
    public float getHeight() {
        return spriteAnimationState.getHeight();
    }

    @Override
    public float getWidth() {
        return spriteAnimationState.getWidth();
    }

    @Override
    public final float getY() {
        return position.y;
    }

    @Override
    public final void setY(float y) {
        position.y = y;
    }

    @Override
    public final float getX() {
        return position.x;
    }

    @Override
    public final void setX(float x) {
        position.x = x;
    }

    @Override
    public final Vector2f getPosition() {
        return position;
    }

    public SpriteAnimationState getSpriteAnimationState() {
        return spriteAnimationState;
    }

    public void setSpriteAnimation(String animation) {
        spriteAnimationState = new SpriteAnimationState(image, getScene().getEngine().getAnimation(animation));
        image.setSpriteAnimation(spriteAnimationState);
    }

    @Override
    public void handleMouseEnter() {
        if (!disable && !active) spriteAnimationState.setAction("hover");
    }

    @Override
    public void handleMouseLeave() {
        if (!disable && !active) spriteAnimationState.setActionIdle();
    }

    public boolean getActive() {
        return active;
    }
}
