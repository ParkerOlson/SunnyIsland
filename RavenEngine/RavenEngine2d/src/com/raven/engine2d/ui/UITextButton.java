package com.raven.engine2d.ui;

import com.raven.engine2d.graphics2d.sprite.SpriteAnimationState;
import com.raven.engine2d.scene.Scene;

public abstract class UITextButton<S extends Scene>
    extends  UIButton<S> {

    private UIText<S> uiText;

    public UITextButton(S scene, String text, String btnImgSrc, String animation) {
        super(scene, btnImgSrc, animation);

        uiText = new UIText<S>(scene, text) {
            @Override
            public SpriteAnimationState getSpriteAnimationState() {
                return UITextButton.this.getSpriteAnimationState();
            }

            @Override
            public int getStyle() {
                return UITextButton.this.getStyle();
            }
        };

        uiText.getFont().setX(8);
        uiText.getFont().setY(10);
        uiText.getFont().setButton(true);
        addChild(uiText);
    }

    public void load() {
        uiText.load();
    }

    public void setText(String text) {
        uiText.setText(text);
    }

    public UIFont getFont() {
        return uiText.getFont();
    }
}
