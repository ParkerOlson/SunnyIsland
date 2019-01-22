package com.raven.engine2d.ui;

import com.raven.engine2d.graphics2d.DrawStyle;
import com.raven.engine2d.graphics2d.shader.LayerShader;
import com.raven.engine2d.graphics2d.shader.RenderTarget;
import com.raven.engine2d.graphics2d.sprite.SpriteAnimationState;
import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.util.math.Vector2f;

public class UIMultipartImage<S extends Scene> extends UIImage<S> {

    private SpriteAnimationState spriteAnimationTop;
    private SpriteAnimationState spriteAnimationMid;
    private SpriteAnimationState spriteAnimationBottom;
    private Vector2f offset = new Vector2f();

    private int rows = 1;

    public UIMultipartImage(S scene, String src, String anim) {
        super(scene, 0, 0, src);

        spriteAnimationTop = new SpriteAnimationState(this, scene.getEngine().getAnimation(anim));
        spriteAnimationTop.setIdleAction("top");
        spriteAnimationTop.setActionIdle();
        spriteAnimationMid = new SpriteAnimationState(this, scene.getEngine().getAnimation(anim));
        spriteAnimationMid.setIdleAction("mid");
        spriteAnimationMid.setActionIdle();
        spriteAnimationBottom = new SpriteAnimationState(this, scene.getEngine().getAnimation(anim));
        spriteAnimationBottom.setIdleAction("bottom");
        spriteAnimationBottom.setActionIdle();


        int h = spriteAnimationTop.getHeight();
        for (int i = 0; i < rows; i++) {
            h += spriteAnimationMid.getHeight();
        }
        h += spriteAnimationBottom.getHeight();
        setHeight(h);
        setWidth(spriteAnimationTop.getWidth());
    }

    @Override
    public void draw(LayerShader shader, RenderTarget target) {
        offset.y = getHeight();
        offset.y -= spriteAnimationTop.getHeight();
        shader.draw(getTexture(), target, spriteAnimationTop, getWorldPosition(), offset, getID(), getWorldZ(), null, DrawStyle.UI);


        for (int i = 0; i < rows; i++) {
            offset.y -= spriteAnimationMid.getHeight();
            shader.draw(getTexture(), target, spriteAnimationMid, getWorldPosition(), offset, getID(), getWorldZ(), null, DrawStyle.UI);

        }
        offset.y -= spriteAnimationBottom.getHeight();
        shader.draw(getTexture(), target, spriteAnimationBottom, getWorldPosition(), offset, getID(), getWorldZ(), null, DrawStyle.UI);
    }

    @Override
    public void setSpriteAnimation(SpriteAnimationState spriteAnimation) {
        throw new UnsupportedOperationException();
    }

    public void setRows(int rows) {
        this.rows = rows;

        this.setHeight(spriteAnimationTop.getHeight() +
                spriteAnimationMid.getHeight() * rows +
                spriteAnimationBottom.getHeight());

        setY(((UIObject) getParent()).getHeight() - getHeight());
    }
}
