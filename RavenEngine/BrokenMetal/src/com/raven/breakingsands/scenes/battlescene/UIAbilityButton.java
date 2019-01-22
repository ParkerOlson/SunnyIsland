package com.raven.breakingsands.scenes.battlescene;

import com.raven.breakingsands.character.Ability;
import com.raven.engine2d.graphics2d.sprite.SpriteAnimationState;
import com.raven.engine2d.ui.UIButton;
import com.raven.engine2d.ui.UIImage;

import java.util.ArrayList;
import java.util.List;

public abstract class UIAbilityButton extends UIButton<BattleScene> {
    private Ability ability;

    private List<UIImage<BattleScene>> abilityImgList = new ArrayList<>();

    public UIAbilityButton(BattleScene scene, String btnImgSrc, String animation) {
        super(scene, btnImgSrc, animation);
    }

    public Ability getAbility() {
        return ability;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;

        abilityImgList.forEach(this::removeChild);
        abilityImgList.clear();
    }

    public void addBonusAbility(Ability ability) {
        UIImage<BattleScene> abilityImg = new UIImage<>(getScene(), 10, 9, ability.icon);
        abilityImg.setSpriteAnimation(new SpriteAnimationState(abilityImg, getScene().getEngine().getAnimation("hexbutton")));
        addChild(abilityImg);

        abilityImg.setToolTip(ability.name, ability.getDescription());

        abilityImg.setY(getHeight() + 1 + (abilityImgList.size() / 2) * 11);
        if (abilityImgList.size() % 2 == 0) {
            abilityImg.setX(14);
        } else {
            abilityImgList.get(abilityImgList.size() - 1).setX(8);
            abilityImg.setX(20);
        }
        abilityImgList.add(abilityImg);
    }
}
