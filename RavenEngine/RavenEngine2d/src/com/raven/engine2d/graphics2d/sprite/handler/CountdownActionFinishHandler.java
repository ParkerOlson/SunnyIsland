package com.raven.engine2d.graphics2d.sprite.handler;

import com.raven.engine2d.graphics2d.sprite.SpriteAnimationState;
import org.omg.CORBA.OBJ_ADAPTER;

import java.util.ArrayList;
import java.util.List;

public class CountdownActionFinishHandler implements ActionFinishHandler {

    private final ActionFinishHandler handler;
    private int remaining;

    public CountdownActionFinishHandler(ActionFinishHandler handler, int size) {
        this.handler = handler;
        remaining = size;
    }

    @Override
    public void onActionFinish() {
        remaining--;

        System.out.println("Count: " + remaining);

        if (remaining == 0)
            handler.onActionFinish();
    }
}
