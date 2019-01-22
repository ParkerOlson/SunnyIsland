package com.raven.engine2d.ui;

import com.raven.engine2d.graphics2d.DrawStyle;
import com.raven.engine2d.graphics2d.shader.LayerShader;
import com.raven.engine2d.graphics2d.shader.RenderTarget;
import com.raven.engine2d.graphics2d.sprite.SpriteAnimationState;
import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.util.math.Vector2f;
import com.raven.engine2d.worldobject.Parentable;

public abstract class UIText<S extends Scene>
        extends UIObject<S, UIObject<S, Parentable<UIObject>>> {

    private String text;
    private String currentText;
    private final String backgroundSrc;
    private UITexture image;
    private UITextWriter textWriter;

    private UIFont font = new UIFont();

    private Vector2f position = new Vector2f();

    public UIText(S scene, String text) {
        this(scene, text, null);
    }

    public UIText(S scene, String text, String backgroundSrc) {
        super(scene);

        currentText = this.text = text;
        this.backgroundSrc = backgroundSrc;

        textWriter = new UITextWriter(getScene().getEngine(), getScene());
    }

    public UIFont getFont() {
        return font;
    }


    public void load() {
        this.load(null);
    }

    public void load(UITextWriterHandler handler) {
        if (image == null) {
            if (font.isButton())
                image = new UITexture(getScene().getEngine(), (int) getWidth(), (int) getHeight() * 2);
            else
                image = new UITexture(getScene().getEngine(), (int) getWidth(), (int) getHeight());

            getScene().getEngine().getWindow().printErrors("pre cat (ut) ");
            image.load(getScene());
            getScene().getEngine().getWindow().printErrors("post cat (ut) ");
        }

        if (backgroundSrc != null)
            textWriter.setBackground(backgroundSrc);

        textWriter.setImageDest(image);
        textWriter.setText(text, font);
        textWriter.setHandler(handler);

        needsRedraw();

        getScene().addTextToWrite(textWriter);
    }

    public void draw(LayerShader shader, RenderTarget target) {
        shader.draw(image, target, getSpriteAnimationState(), getWorldPosition(), null, getID(), getWorldZ(), null, DrawStyle.UI);
    }

    @Override
    public float getZ() {
        return .02f;
    }

    public void setAnimationAction(String action) {
        this.getSpriteAnimationState().setAction(action);
    }

    public abstract SpriteAnimationState getSpriteAnimationState();

    @Override
    public float getHeight() {
        return 64;
    }

    @Override
    public float getWidth() {
        return 100;
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

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (getSpriteAnimationState() != null) {
            getSpriteAnimationState().update(deltaTime);
        }

        for (UIObject o : this.getChildren()) {
            o.update(deltaTime);
        }
    }

    @Override
    public void release() {
        super.release();
        if (image != null)
            image.release();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
