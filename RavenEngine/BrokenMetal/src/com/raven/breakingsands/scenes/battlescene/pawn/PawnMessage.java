package com.raven.breakingsands.scenes.battlescene.pawn;

import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.engine2d.scene.Layer;
import com.raven.engine2d.ui.UIFont;
import com.raven.engine2d.worldobject.WorldTextObject;

public class PawnMessage extends WorldTextObject<BattleScene, Pawn> {
    public PawnMessage(BattleScene scene) {
        super(scene);

        UIFont font = getFont();
        font.setSmall(true);
        font.setHighlight(false);

        setHighlight(BattleScene.OFF);
    }

    @Override
    public void setText(String text) {
        super.setText(text);

        setVisibility(true);
    }

    @Override
    public Layer.Destination getDestination() {
        return Layer.Destination.Details;
    }

    @Override
    public float getZ() {
        return .1f;
    }
}
