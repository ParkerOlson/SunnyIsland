package com.raven.engine2d.ui;


import com.raven.engine2d.util.math.Vector2i;

import static com.raven.engine2d.ui.UIFont.Side.LEFT;

public class UIFont {
    private boolean highlight = true;
    private boolean small = false;
    private boolean button = false;
    private boolean wrap = false;
    private Vector2i buttonOffset = new Vector2i(0, 32);

    private int x, y;

    private UIFont.Side side = LEFT;

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public enum Side {
        RIGHT, LEFT,
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setSmall(boolean small) {
        this.small = small;
    }

    public boolean isSmall() {
        return small;
    }

    public void setButton(boolean button) {
        this.button = button;
    }

    public boolean isButton() {
        return button;
    }

    public Vector2i getButtonOffset() {
        return buttonOffset;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public boolean isWrap() {
        return wrap;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public Side getSide() {
        return side;
    }


}
