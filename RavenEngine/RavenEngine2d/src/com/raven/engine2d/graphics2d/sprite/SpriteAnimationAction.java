package com.raven.engine2d.graphics2d.sprite;

import com.raven.engine2d.database.GameData;

import java.util.ArrayList;
import java.util.List;

public class SpriteAnimationAction {
    private String name;
    private int time;
    private List<SpriteAnimationFrame> frames = new ArrayList<>();

    public SpriteAnimationAction(GameData gdAction) {
        name = gdAction.getString("name");

        int i = 0;
        for (GameData gdFrame : gdAction.getList("frames")) {
            frames.add(new SpriteAnimationFrame(gdAction, gdFrame, i));
            i++;
        }

        time = frames.stream().mapToInt(SpriteAnimationFrame::getTime).sum();
    }

    public int getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public List<SpriteAnimationFrame> getFrames() {
        return frames;
    }

    public SpriteAnimationFrame getNextFrame(SpriteAnimationFrame activeFrame, float time) {
        int index = activeFrame.getIndex() + 1;

        if (index >= frames.size()) {
            index = 0;
        }

        return frames.get(index);
    }
}
