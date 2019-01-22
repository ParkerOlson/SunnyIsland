package com.raven.breakingsands.scenes.battlescene.levelup;

import com.raven.breakingsands.character.Ability;
import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.engine2d.graphics2d.sprite.SpriteAnimationState;
import com.raven.engine2d.ui.UIImage;

public class LevelUpHexConnection extends UIImage<BattleScene> {

    private LevelUpHexButton buttonA, buttonB;

    public LevelUpHexConnection(BattleScene scene, LevelUpHexButton buttonA, LevelUpHexButton buttonB) {
        super(scene, 0, 0, "sprites/connection.png");

        this.buttonA = buttonA;
        this.buttonB = buttonB;

        buttonA.addConnection(this);
        buttonB.addConnection(this);

        if (buttonA.getX() == buttonB.getX()) {

            setSpriteAnimation(new SpriteAnimationState(this, scene.getEngine().getAnimation("connection")));

            if (buttonA.getY() > buttonB.getY()) {
                setY(buttonB.getY() + buttonB.getHeight());
                setX(buttonB.getX());
            } else {
                setY(buttonA.getY() + buttonA.getHeight());
                setX(buttonA.getX());
            }

        } else if (buttonA.getX() > buttonB.getX()) {

            if (buttonA.getY() > buttonB.getY()) {

                SpriteAnimationState state = new SpriteAnimationState(this, scene.getEngine().getAnimation("connectiondag"));
                state.setFlip(true);
                setSpriteAnimation(state);
                setY(buttonA.getY() - 3);
                setX(buttonA.getX() - state.getWidth() + 2);

            } else {

                SpriteAnimationState state = new SpriteAnimationState(this, scene.getEngine().getAnimation("connectiondag"));
                setSpriteAnimation(state);
                setY(buttonA.getY() + buttonA.getHeight() - 2);
                setX(buttonA.getX() - state.getWidth() + 4);

            }
        } else {

            if (buttonA.getY() > buttonB.getY()) {

                setSpriteAnimation(new SpriteAnimationState(this, scene.getEngine().getAnimation("connectiondag")));
                setY(buttonA.getY() - 3);
                setX(buttonA.getX() + buttonA.getWidth() - 2);
            } else {

                SpriteAnimationState state = new SpriteAnimationState(this, scene.getEngine().getAnimation("connectiondag"));
                state.setFlip(true);
                setSpriteAnimation(state);
                setY(buttonA.getY() + buttonA.getHeight() - 2);
                setX(buttonA.getX() + buttonA.getWidth() - 4);
            }
        }

        checkConnection();
    }

    public void checkConnection() {

        setAnimationAction("idle");

        Ability a = buttonA.getAbility();
        Ability b = buttonB.getAbility();

        if (a != null && b != null) {
            if (a.replace != null && !b.name.equals(a.replace) ||
                    a.requires != null && !b.name.equals(a.requires)) {
                setVisibility(false);
                return;
            } else if (b.replace != null && !a.name.equals(b.replace) ||
                    b.requires != null && !a.name.equals(b.requires)) {
                setVisibility(false);
                return;
            } else {
                setVisibility(true);
            }
        } else {
            setVisibility(true);
        }

        if (buttonA.getType() == LevelUpHexButton.Type.CLASS && b != null && (b.replace != null || b.requires != null)) {
            setVisibility(false);
            return;
        } else if (buttonB.getType() == LevelUpHexButton.Type.CLASS && a != null && (a.replace != null || b.requires != null)) {
            setVisibility(false);
            return;
        }

        if (buttonA.isActive() && buttonB.isActive()) {
            setAnimationAction("connected");
        } else if (((a != null && b != null) ||
                (buttonA.getType() == LevelUpHexButton.Type.START && !buttonA.isDisabled()) ||
                (buttonB.getType() == LevelUpHexButton.Type.START && !buttonA.isDisabled()) ||
                (buttonA.getType() == LevelUpHexButton.Type.CLASS) ||
                (buttonB.getType() == LevelUpHexButton.Type.CLASS)) &&
                ((buttonA.isActive() && !buttonB.isLocked()) || (buttonB.isActive() && !buttonA.isLocked()))) {
            if (buttonA.isActive()) {
                buttonB.setDisable(false);
            } else {
                buttonA.setDisable(false);
            }
            setAnimationAction("connected partial");
        }
    }
}
