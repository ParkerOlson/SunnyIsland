package com.raven.breakingsands.scenes.battlescene.menu;

import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.engine2d.GameEngine;
import com.raven.engine2d.graphics2d.sprite.SpriteSheet;
import com.raven.engine2d.ui.UIButton;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.ui.UITextButton;

public abstract class MenuButton extends UITextButton<BattleScene> {

    private static final String btnImgSrc = "sprites/button.png";

    public MenuButton(BattleScene scene, String text) {
        super(scene, text, btnImgSrc, "mainbutton");
    }
}
