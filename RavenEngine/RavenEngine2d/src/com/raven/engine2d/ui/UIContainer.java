package com.raven.engine2d.ui;

import com.raven.engine2d.Game;
import com.raven.engine2d.GameProperties;
import com.raven.engine2d.scene.Layer;
import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.util.math.Vector2f;
import com.raven.engine2d.worldobject.GameObject;

import java.util.List;
import java.util.stream.Collectors;

public abstract class UIContainer<S extends Scene>
        extends UIObject<S, Scene<? extends Game>> {

    public static final int CENTER = 0, UPPER_LEFT = 1, BOTTOM_LEFT = 2, BOTTOM = 3, UPPER_RIGHT = 4, BOTTOM_RIGHT = 5, RIGHT = 6, BOTTOM_CENTER = 7, UPPER = 8;

    protected float width, height;
    private Vector2f position = new Vector2f();

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public UIContainer(S scene) {
        super(scene);
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
    public Vector2f getPosition() {
        return position;
    }

    public void pack() {
        int doubleScale = (2 * GameProperties.getScaling());

        final List<UIObject> children = this.getChildren().stream().filter(GameObject::isVisible).collect(Collectors.toList());

        switch (getStyle()) {
            case BOTTOM:
                width = 0;
                height = 0f;

                for (UIObject obj : children) {
                    height = Math.max(obj.getHeight(), height);
                    width += obj.getWidth();
                }

                // Get Offset
                float offset = 0f;

                for (int i = 0; i < children.size(); i++) {
                    UIObject obj = children.get(i);

                    obj.setX(offset);

                    offset += obj.getWidth();
                }
                break;
            case BOTTOM_LEFT:
                width = 0;
                height = 0f;

                for (UIObject obj : children) {
                    height = Math.max(obj.getHeight(), height);
                    width += obj.getWidth();
                }

                // Get Offset
                offset = 0f;

                for (int i = 0; i < children.size(); i++) {
                    UIObject obj = children.get(i);

                    obj.setX(offset);

                    offset += obj.getWidth();
                }
                break;
            case UPPER_LEFT:
                width = 0;
                height = 0f;

                for (UIObject obj : children) {
                    height = Math.max(obj.getHeight(), height);
                    width += obj.getWidth();
                }

                // Get Offset
//                offset = 0f;
                float yOffset = GameProperties.getDisplayHeight() / GameProperties.getScaling();

                for (int i = 0; i < children.size(); i++) {
                    UIObject obj = children.get(i);

//                    obj.setX(offset);
                    yOffset -= obj.getHeight();
                    obj.setY(yOffset);

//                    offset += obj.getWidth() * 2f;
                }
                break;
            case UPPER_RIGHT:
                width = 0;
                height = 0f;

                for (UIObject obj : children) {
                    height = Math.max(obj.getHeight(), height);
                    width += obj.getWidth();
                }

                // Get Offset
//                offset = 0f;
                yOffset = GameProperties.getDisplayHeight() / GameProperties.getScaling();

                for (int i = 0; i < children.size(); i++) {
                    UIObject obj = children.get(i);

                    float xOffset = GameProperties.getDisplayWidth() / GameProperties.getScaling() - obj.getWidth();

//                    obj.setX(offset);
                    yOffset -= obj.getHeight();
                    obj.setY(yOffset);
                    obj.setX(xOffset);

//                    offset += obj.getWidth() * 2f;
                }
                break;
            case UPPER:
                width = 0;
                height = 0f;

                for (UIObject obj : children) {
                    height = Math.max(obj.getHeight(), height);
                    width += obj.getWidth();
                }

                // Get Offset
//                offset = 0f;
                yOffset = GameProperties.getDisplayHeight() / GameProperties.getScaling();

                for (int i = 0; i < children.size(); i++) {
                    UIObject obj = children.get(i);

                    float xOffset = GameProperties.getDisplayWidth() / doubleScale - obj.getWidth() / 2f;

//                    obj.setX(offset);
                    yOffset -= obj.getHeight();
                    obj.setY(yOffset);
                    obj.setX(xOffset);

//                    offset += obj.getWidth() * 2f;
                }
                break;
            case BOTTOM_RIGHT:
                width = 0;
                height = 0f;

                for (UIObject obj : children) {
                    height = Math.max(obj.getHeight(), height);
                    width += obj.getWidth();
                }

                // Get Offset
                offset = GameProperties.getDisplayWidth() / GameProperties.getScaling();

                for (int i = 0; i < children.size(); i++) {
                    UIObject obj = children.get(i);

                    offset -= obj.getWidth();

                    obj.setX(offset);
                }
                break;
            case RIGHT:
                width = 0f;
                height = 0;

                for (UIObject obj : children) {
                    width = Math.max(obj.getWidth(), width);
                    height += obj.getHeight();
                }

                // Get Offset
                offset = 0f;

                for (int i = 0; i < children.size(); i++) {
                    UIObject obj = children.get(i);

                    offset += obj.getHeight();

                    obj.setY(height - offset + GameProperties.getDisplayHeight() / GameProperties.getScaling());
                    obj.setX(GameProperties.getDisplayWidth() / GameProperties.getScaling() - obj.getWidth());
                }
                break;
            case BOTTOM_CENTER:
                width = 0;
                height = 0f;

                for (UIObject obj : children) {
                    height = Math.max(obj.getHeight(), height);
                    width += obj.getWidth() / 2f;
                }

                // Get Offset
                offset = GameProperties.getDisplayWidth() / GameProperties.getScaling();

                for (int i = 0; i < children.size(); i++) {
                    UIObject obj = children.get(children.size() - i - 1);

                    offset -= obj.getWidth();

                    obj.setX(offset);
                }

                for (int i = 0; i < children.size(); i++) {
                    UIObject obj = children.get(i);
                    obj.setX(obj.getX() - GameProperties.getDisplayWidth() / doubleScale + width);
                }
                break;
            case CENTER: // center
            default:
                width = 0f;
                height = 0;

                for (UIObject obj : children) {
                    width = Math.max(obj.getWidth(), width);
                    height += obj.getHeight();
                }

                // Get Offset
                offset = 0f;

                for (int i = 0; i < children.size(); i++) {
                    UIObject obj = children.get(i);

                    offset += obj.getHeight();

                    obj.setY(height / 2 - offset + GameProperties.getDisplayHeight() / doubleScale);
                    obj.setX(GameProperties.getDisplayWidth() / doubleScale - obj.getWidth() / 2f);


//                    offset += obj.getHeight();
                }
                break;
        }
    }
}
