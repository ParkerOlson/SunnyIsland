package com.raven.engine2d.ui;

import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.util.math.Vector2f;
import com.raven.engine2d.worldobject.Parentable;

import java.util.List;

public class UISelector<S extends Scene, V extends Object> extends UIObject<S, UIObject<S, Parentable<UIObject>>> {

    private Vector2f position = new Vector2f();
    private UIImage<S> image;
    private UIButton<S> leftBtn, rightBtn;
    private UILabel<S> uiText, uiTitle;
    private List<V> values;
    private List<String> displays;
    private int index;

    public UISelector(S scene,
                      String imgSrc, String leftBtnSrc, String rightBtnSrc,
                      String title,
                      List<V> values, List<String> displays, V selected) {
        super(scene);

        this.values = values;
        this.displays = displays;
        index = values.indexOf(selected);

        image = new UIImage<>(scene, (int) getWidth(), (int) getHeight() * 2, imgSrc);
        addChild(image);

        leftBtn = new UIButton<S>(scene, leftBtnSrc, "selectorbutton") {
            @Override
            public void handleMouseClick() {
                index--;
                if (index < 0) {
                    index = values.size() - 1;
                }

                uiText.setText(displays.get(index));
                uiText.load();
            }
        };
        leftBtn.setY(4);
        addChild(leftBtn);

        rightBtn = new UIButton<S>(scene, rightBtnSrc, "selectorbutton") {
            @Override
            public void handleMouseClick() {
                index++;
                if (index >= values.size()) {
                    index = 0;
                }

                uiText.setText(displays.get(index));
                uiText.load();
            }
        };
        rightBtn.setY(4);
        rightBtn.setX(100 + rightBtn.getWidth());
        addChild(rightBtn);

        uiTitle = new UILabel<>(scene, title, 100, 24);
        uiTitle.setX(32);
        uiTitle.setY(0);
        UIFont f = uiTitle.getFont();
        f.setSmall(true);
        uiTitle.load();
        addChild(uiTitle);

        uiText = new UILabel<>(scene, displays.get(index), 100, 24);
        uiText.setX(32);
        uiText.setY(-10);
        f = uiText.getFont();
        f.setSmall(true);
        uiText.load();
        addChild(uiText);
    }

    public V getValue() {
        return values.get(index);
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
    public int getStyle() {
        return getParent().getStyle();
    }

    @Override
    public float getHeight() {
        if (image == null) {
            return 0;
        } else {
            return image.getTexture().getHeight();
        }
    }

    @Override
    public float getWidth() {
        if (image == null) {
            return 0;
        } else {
            return image.getTexture().getWidth();
        }
    }
}
