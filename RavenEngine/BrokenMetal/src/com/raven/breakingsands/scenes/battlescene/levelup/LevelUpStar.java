package com.raven.breakingsands.scenes.battlescene.levelup;

import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.breakingsands.scenes.battlescene.pawn.Pawn;
import com.raven.engine2d.ui.UIObject;

public abstract class LevelUpStar extends UIObject<BattleScene, UILevelUp> {

    public LevelUpStar(BattleScene scene) {
        super(scene);
    }

    public abstract void setPawn(Pawn pawn);

    public abstract void clear();
}
