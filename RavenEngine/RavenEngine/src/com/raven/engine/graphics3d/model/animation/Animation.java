package com.raven.engine.graphics3d.model.animation;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Animation {

    private HashMap<String, AnimatedAction> actions = new HashMap<>();
    private AnimatedAction activeAction;
    private String name;

    public Animation(String name) {

    }

    public void addAction(AnimatedAction action) {
        actions.put(action.getName(), action);
        activeAction = action;
    }

    public void toBuffer(FloatBuffer aBuffer, AnimationState animationState) {
        activeAction.toBuffer(aBuffer, animationState);
    }
}
