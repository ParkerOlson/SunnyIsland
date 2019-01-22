package com.raven.engine2d.graphics2d.sprite;

import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDataTable;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteAnimation {

    private String name;
    private Map<String, SpriteAnimationAction> actions = new HashMap<>();

    public SpriteAnimation(GameDataTable table) {
        name = table.getName();

        for (GameData gdAction : table) {
            SpriteAnimationAction action = new SpriteAnimationAction(gdAction);

            actions.put(action.getName(), action);
        }
    }

    public SpriteAnimationAction getAction(String name) {
        SpriteAnimationAction action = actions.get(name);

        if (action == null) {
            action = actions.values().stream().findFirst().get();
        }

        return action;
    }

    public boolean hasAction(String action) {
        return actions.keySet().contains(action);
    }
}
