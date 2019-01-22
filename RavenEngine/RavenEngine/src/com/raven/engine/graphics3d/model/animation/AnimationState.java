package com.raven.engine.graphics3d.model.animation;

import java.nio.FloatBuffer;

public class AnimationState {
    private String actionName = "idle";
    private float time;
    private Animation animation;

    public AnimationState(Animation animation) {
        this.animation = animation;
    }

    public String getActionName() {
        return actionName;
    }

    public float getTime() {
        return time;
    }

    public void update(float deltaTime) {
        time += deltaTime;
    }

    public void toBuffer(FloatBuffer aBuffer) {
        animation.toBuffer(aBuffer, this);
    }
}
