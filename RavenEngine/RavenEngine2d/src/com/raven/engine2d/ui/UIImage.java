package com.raven.engine2d.ui;

import com.raven.engine2d.graphics2d.DrawStyle;
import com.raven.engine2d.graphics2d.shader.LayerShader;
import com.raven.engine2d.graphics2d.shader.RenderTarget;
import com.raven.engine2d.graphics2d.sprite.SpriteAnimationState;
import com.raven.engine2d.graphics2d.sprite.SpriteSheet;
import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.util.math.Vector2f;
import com.raven.engine2d.worldobject.Parentable;

public class UIImage<S extends Scene> extends UIObject<S, Parentable<UIObject>> {

    private Vector2f position = new Vector2f();
    private SpriteSheet texture;

    private int width, height;
    private SpriteAnimationState spriteAnimation;

    public UIImage(S scene, int width, int height, String src) {
        super(scene);

        this.width = width;
        this.height = height;

        texture = scene.getEngine().getSpriteSheet(src);
        texture.load(scene);
    }

    public final void setSprite(String src) {
        texture = getScene().getEngine().getSpriteSheet(src);
        texture.load(getScene());
    }

    @Override
    public int getStyle() {
        return 0;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public float getY() {
        return position.y;
    }

    @Override
    public void setY(float y) {
        position.y = y;
    }

    @Override
    public float getX() {
        return position.x;
    }

    @Override
    public void setX(float x) {
        position.x = x;
    }

    @Override
    public Vector2f getPosition() {
        return position;
    }

    @Override
    public void draw(LayerShader shader, RenderTarget target) {
        shader.draw(texture, target, spriteAnimation, getWorldPosition(), null, getID(), getWorldZ(), null, DrawStyle.UI);
    }

    @Override
    public void update(float deltaTime) {
        if (spriteAnimation != null) {
            spriteAnimation.update(deltaTime);
        }

        this.onUpdate(deltaTime);

        for (UIObject c : getChildren()) {
            c.update(deltaTime);
        }
    }

    protected SpriteSheet getTexture() {
        return texture;
    }

    public void setSpriteAnimation(SpriteAnimationState spriteAnimation) {
        this.spriteAnimation = spriteAnimation;
    }

    public void setAnimationAction(String action) {
        if (action.equals("idle"))
            this.spriteAnimation.setActionIdle();
        else
            this.spriteAnimation.setAction(action);
    }

    public SpriteAnimationState getSpriteAnimation() {
        return this.spriteAnimation;
    }

    @Override
    public void release() {
        super.release();
        texture.release();
    }
}
