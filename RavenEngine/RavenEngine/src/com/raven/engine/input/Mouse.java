package com.raven.engine.input;

import org.lwjgl.glfw.GLFW;

/**
 * Created by cookedbird on 11/16/17.
 */
public class Mouse {
    double x, y;
    boolean rightButtonDown, middleButtonDown, leftButtonDown;
    boolean rightButtonClick, middleButtonClick, leftButtonClick;

    public void setRightButtonDown(boolean rightButtonDown) {
        this.rightButtonDown = rightButtonDown;
    }

    public void setLeftButtonDown(boolean leftButtonDown) {
        this.leftButtonDown = leftButtonDown;
    }

    public void setRightButtonClick(boolean rightButtonClick) {
        this.rightButtonClick = rightButtonClick;
    }

    public void setMiddleButtonDown(boolean middleButtonDown) {
        this.middleButtonDown = middleButtonDown;
    }

    public boolean isMiddleButtonClick() {
        return middleButtonClick;
    }

    public void setMiddleButtonClick(boolean middleButtonClick) {
        this.middleButtonClick = middleButtonClick;
    }

    public boolean isLeftButtonDown() {
        return leftButtonDown;
    }

    public boolean isLeftButtonClick() {
        return leftButtonClick;
    }

    public void setLeftButtonClick(boolean leftButtonClick) {
        this.leftButtonClick = leftButtonClick;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void buttonAction(int button, int action) {
        switch (button) {
            case (GLFW.GLFW_MOUSE_BUTTON_LEFT):
                if (action == GLFW.GLFW_PRESS) {
                    leftButtonDown = true;
                    leftButtonClick = true;
                } else if (action == GLFW.GLFW_RELEASE) {
                    leftButtonDown = false;
                }
                break;
            case (GLFW.GLFW_MOUSE_BUTTON_MIDDLE):
                if (action == GLFW.GLFW_PRESS) {
                    middleButtonDown = true;
                    middleButtonClick = true;
                } else if (action == GLFW.GLFW_RELEASE) {
                    middleButtonDown = false;
                }
                break;
            case (GLFW.GLFW_MOUSE_BUTTON_RIGHT):
                if (action == GLFW.GLFW_PRESS) {
                    rightButtonDown = true;
                    rightButtonClick = true;
                } else if (action == GLFW.GLFW_RELEASE) {
                    rightButtonDown = false;
                }
                break;
            default:
        }
    }

    public boolean isRightButtonDown() {
        return rightButtonDown;
    }

    public boolean isMiddleButtonDown() {
        return middleButtonDown;
    }
}
