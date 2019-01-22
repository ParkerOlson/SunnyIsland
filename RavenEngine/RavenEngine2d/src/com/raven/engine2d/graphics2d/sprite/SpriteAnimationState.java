package com.raven.engine2d.graphics2d.sprite;

import com.raven.engine2d.GameEngine;
import com.raven.engine2d.GameProperties;
import com.raven.engine2d.graphics2d.sprite.handler.ActionFinishHandler;
import com.raven.engine2d.graphics2d.sprite.handler.FrameFinishHandler;
import com.raven.engine2d.worldobject.GameObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SpriteAnimationState {
    private SpriteAnimation animation;
    private SpriteAnimationAction activeAction;
    private SpriteAnimationFrame activeFrame;

    private List<ActionFinishHandler> actionFinishHandlers = new ArrayList<>();
    private List<ActionFinishHandler> actionFinishHandlersOverflow = new ArrayList<>();

    private float time = 0;
    private boolean flip = false;
    private boolean processing;
    private GameObject gameObject;
    private String idleAction = "idle";

    public SpriteAnimationState(GameObject gameObject, SpriteAnimation animation) {
        this.gameObject = gameObject;
        this.animation = animation;
        this.activeAction = animation.getAction(idleAction);
        this.activeFrame = activeAction.getFrames().get(0);
    }

    public void update(float deltaTime) {
        time += deltaTime * GameProperties.getAnimationSpeed();

        if (time > activeFrame.getTime()) {
            if (activeAction.getFrames().size() > 1 || actionFinishHandlers.size() > 0)
            gameObject.needsRedraw();

            time -= activeFrame.getTime();
            // TODO make get NExt Frame recursive
            activeFrame = activeAction.getNextFrame(activeFrame, time);

            if (activeFrame.getIndex() == 0) {
                processing = true;
                for (ActionFinishHandler handler : actionFinishHandlers) {
                    handler.onActionFinish();
                }
                actionFinishHandlers.clear();
                processing = false;
                actionFinishHandlers.addAll(actionFinishHandlersOverflow);
                actionFinishHandlersOverflow.clear();
            }
        }
    }

    public int getX() {
        return activeFrame.getX();
    }

    public int getY() {
        return activeFrame.getY();
    }

    public float getXOffset() {
        return activeFrame.getXOffset() +
                (getFlip() ? activeFrame.getFlipXOffset() : -activeFrame.getFlipXOffset());
    }

    public float getYOffset() {
        return activeFrame.getYOffset();
    }

    public int getWidth() {
        return activeFrame.getWidth();
    }

    public int getHeight() {
        return activeFrame.getHeight();
    }

    public boolean hasAction(String action) {
        return animation.hasAction(action);
    }

    public void setAction(String action) {
        setAction(action, true);
    }

    public void setAction(String action, boolean reset) {
        if (reset || !activeAction.getName().equals(action)) {
            this.activeAction = animation.getAction(action);
            this.activeFrame = activeAction.getFrames().get(0);
            this.time = 0;
            gameObject.needsRedraw();
        }
    }

    public void setIdleAction(String idleAction) {
        if (activeAction.getName().equals(this.idleAction)) {
            setAction(idleAction);
        }
        this.idleAction = idleAction;
    }

    public String getIdleAction() {
        return idleAction;
    }

    public void setActionIdle() {
        setAction(idleAction);
    }

    public void setActionIdle(boolean b) {
        setAction(idleAction, b);
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean getFlip() {
        return flip;
    }

    public void addActionFinishHandler(ActionFinishHandler handler) {
        if (handler != null)
            if (processing)
                actionFinishHandlersOverflow.add(handler);
            else
                actionFinishHandlers.add(handler);
    }
}
