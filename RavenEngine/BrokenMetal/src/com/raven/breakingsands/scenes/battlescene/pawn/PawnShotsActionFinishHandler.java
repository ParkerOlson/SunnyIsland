package com.raven.breakingsands.scenes.battlescene.pawn;

import com.raven.breakingsands.character.Effect;
import com.raven.engine2d.graphics2d.sprite.handler.ActionFinishHandler;
import com.raven.engine2d.graphics2d.sprite.handler.CountdownActionFinishHandler;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PawnShotsActionFinishHandler implements ActionFinishHandler {

    private final Pawn from;
    private final Pawn target;
    private final AtomicInteger shotCount;
    private final boolean directional;
    private final boolean directionUp;
    private final ActionFinishHandler onAttackDone;

    public PawnShotsActionFinishHandler(
            Pawn from, Pawn target, AtomicInteger shotCount, boolean directional, boolean directionUp, ActionFinishHandler onAttackDone) {
        this.from = from;
        this.target = target;
        this.shotCount = shotCount;
        this.directional = directional;
        this.directionUp = directionUp;
        this.onAttackDone = onAttackDone;
    }

    @Override
    public void onActionFinish() {

        if (shotCount.get() > 1) {

            shotCount.addAndGet(-1);

            if (directional)
                if (directionUp)
                    from.getAnimationState().setAction("ranged up shot");
                else
                    from.getAnimationState().setAction("ranged down shot");
            else
                from.getAnimationState().setAction("ranged shot");

            Effect effect = from.getWeapon().getEffect();
            if (effect != null) {
                effect.setVisibility(true);
                effect.getAnimationState().setFlip(from.getAnimationState().getFlip());
                target.addChild(effect);
                if (directional)
                    if (directionUp)
                        effect.getAnimationState().setAction("up");
                    else
                        effect.getAnimationState().setAction("down");
                effect.getAnimationState().addActionFinishHandler(() -> target.removeChild(effect));
            }

            from.getAnimationState().addActionFinishHandler(new PawnShotsActionFinishHandler(from, target, shotCount, directional, directionUp, onAttackDone));

        } else {

            if (directional)
                if (directionUp)
                    from.getAnimationState().setAction("ranged up end");
                else
                    from.getAnimationState().setAction("ranged down end");
            else
                from.getAnimationState().setAction("ranged end");

            Effect effect = from.getWeapon().getEffect();
            if (effect != null) {
                effect.setVisibility(true);
                effect.getAnimationState().setFlip(from.getAnimationState().getFlip());
                target.addChild(effect);
                if (directional)
                    if (directionUp)
                        effect.getAnimationState().setAction("up");
                    else
                        effect.getAnimationState().setAction("down");
                effect.getAnimationState().addActionFinishHandler(() -> target.removeChild(effect));
            }

            AtomicReference<Boolean> a = new AtomicReference<>(false);
            AtomicReference<Boolean> b = new AtomicReference<>(false);

            ActionFinishHandler mHandler = new CountdownActionFinishHandler(onAttackDone, 2);

            from.attack(target, from.getWeapon().getDamage(), from.getWeapon().getPiercing() + from.getBonusPiercing(), from.getWeapon().getShots(), mHandler);
            from.getAnimationState().addActionFinishHandler(mHandler);
            from.getAnimationState().addActionFinishHandler(() -> from.getAnimationState().setActionIdle(false));
        }

    }
}
