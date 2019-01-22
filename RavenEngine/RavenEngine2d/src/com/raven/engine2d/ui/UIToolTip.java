package com.raven.engine2d.ui;

import com.raven.engine2d.GameProperties;
import com.raven.engine2d.database.GameDataList;
import com.raven.engine2d.database.GameDataTable;
import com.raven.engine2d.scene.Layer;
import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.util.math.Vector2f;
import com.raven.engine2d.worldobject.Parentable;

public class UIToolTip<S extends Scene> extends UIObject<S, UIObject<S, Parentable<UIObject>>> {

    private final int width;
    private final int height;
    private Vector2f position = new Vector2f();

    private final GameDataTable tips;

    private UIMultipartImage<S> background;
    private UILabel<S> title;
    private UILabel<S> text;

    public UIToolTip(S scene, int width, int height, String backgroundSrc, String animation, GameDataTable tips) {
        super(scene);
        setDestination(Layer.Destination.ToolTip);
        clearID();

        setVisibility(false);

        this.width = width;
        this.height = height;

        this.tips = tips;

        if (backgroundSrc != null) {
            background = new UIMultipartImage<>(scene, backgroundSrc, animation);
            background.setDestination(Layer.Destination.ToolTip);
            background.clearID();
            addChild(background);

            background.setY(getHeight() - background.getHeight());
        }

        title = new UILabel<>(scene, "", width, height);
        UIFont font = title.getFont();
        font.setSmall(true);
        title.setY(title.getY() - 5);
        title.setX(title.getX() + 7);
        title.load();
        title.setDestination(Layer.Destination.ToolTip);
        title.clearID();
        addChild(title);

        text = new UILabel<>(scene, "", 100, height);
        font = text.getFont();
        font.setSmall(true);
        font.setWrap(true);
        text.setY(text.getY() - 19);
        text.setX(text.getX() + 5);
        text.load();
        text.setDestination(Layer.Destination.ToolTip);
        text.clearID();
        addChild(text);
    }

    public GameDataTable getTips() {
        return tips;
    }

    public void setText(String text) {
        this.text.setText(text);
        this.text.load(lines -> background.setRows(lines));
    }

    public void setTitle(String title) {
        this.title.setText(title);
        this.title.load();
    }

    boolean getting = false;
    @Override
    public float getZ() {
        return .5f;
    }

    @Override
    public Vector2f getPosition() {
        position.x = getX();
        position.y = getY();

        return position;
    }

    @Override
    public int getStyle() {
        return 0;
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
    public float getY() {

      float y = GameProperties.getDisplayHeight() / GameProperties.getScaling() - height - (float) (getScene().getEngine().getMouse().getY() / GameProperties.getScaling());
//        float y = GameProperties.getDisplayHeight() - height - (float) (getScene().getEngine().getMouse().getY()) * GameProperties.getDisplayHeight() / (GameProperties.getDisplayHeight() * GameProperties.getScaling());

        if (background != null) {
            if (y - background.getHeight() < 0) {
                y += background.getHeight();
            }
        }

        return y;

    }

    @Override
    public void setY(float y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getX() {
      float x = (float) (getScene().getEngine().getMouse().getX() / GameProperties.getScaling());
//        float x = (float) (getScene().getEngine().getMouse().getX() * GameProperties.getDisplayWidth()) / (GameProperties.getDisplayWidth() * GameProperties.getScaling());

        if (background != null) {
            if (background.getWidth() + x > GameProperties.getDisplayWidth() / GameProperties.getScaling()) {
                x -= background.getWidth();
            }
        }

        return x;
    }

    @Override
    public void setX(float x) {
        throw new UnsupportedOperationException();
    }

}
