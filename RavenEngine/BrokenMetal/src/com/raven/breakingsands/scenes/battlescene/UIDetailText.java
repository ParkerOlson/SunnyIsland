package com.raven.breakingsands.scenes.battlescene;

import com.raven.breakingsands.character.Ability;
import com.raven.breakingsands.character.Weapon;
import com.raven.breakingsands.scenes.battlescene.pawn.Pawn;
import com.raven.engine2d.GameEngine;
import com.raven.engine2d.graphics2d.sprite.SpriteAnimationState;
import com.raven.engine2d.ui.*;
import com.raven.engine2d.util.math.Vector2f;
import com.raven.engine2d.worldobject.MouseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UIDetailText
        extends UIObject<BattleScene, UIContainer<BattleScene>>
        implements MouseHandler {

    private static final String bcgImgRightSrc = "sprites/character ui.png";
    private final Pawn pawn;

    private Vector2f position = new Vector2f();

    private UIImage<BattleScene> backgroundImg;
    private UIImage<BattleScene> pawnImg;

    private Map<Ability, UIImage<BattleScene>> abilityImgList = new HashMap<>();
    private Map<Weapon, UIImage<BattleScene>> weaponImgList = new HashMap<>();

    private SelectionDetails details;

    private UILabel<BattleScene>
            uiName, uiLvl,
            uiWeapon, uiAttacks,
            uiHP, uiLblHP,
            uiMov, uiLblMov,
            uiRes, uiLblRes,
            uiSld, uiLblSld,
            uiDmg, uiLblDmg,
            uiPir, uiLblPir,
            uiRng, uiLblRng,
            uiShots, uiLblShots;

    public UIDetailText(BattleScene scene, Pawn pawn) {
        super(scene);

        this.addMouseHandler(this);

        this.pawn = pawn;

        if (0 == pawn.getTeam(true)) {
            initLeft();
        } else {
            initRight();
        }

        init();
    }

    private void initLeft() {
        backgroundImg = new UIImage<>(getScene(),
                220, 54,
                bcgImgRightSrc);
        SpriteAnimationState state = new SpriteAnimationState(backgroundImg, getScene().getEngine().getAnimation("details"));
        state.setFlip(false);
        backgroundImg.setSpriteAnimation(state);
        addChild(backgroundImg);

        pawnImg = new UIImage<>(getScene(),
                32, 32,
                pawn.getSpriteSheetName());
        pawnImg.setZ(.02f);
        state = new SpriteAnimationState(pawnImg, getScene().getEngine().getAnimation(pawn.getAnimationName()));
        state.setFlip(true);
        pawnImg.setSpriteAnimation(state);
        pawnImg.setX(91);
        pawnImg.setY(-1);
        addChild(pawnImg);

        uiName = new UILabel<>(getScene(), "-", 128, 10);
        UIFont font = uiName.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiName.setX(8);
        uiName.setY(28);
        uiName.load();

        addChild(uiName);

        uiLvl = new UILabel<>(getScene(), "", 100, 10);
        font = uiLvl.getFont();
        font.setSmall(true);
        font.setSide(UIFont.Side.RIGHT);
        font.setHighlight(false);
        uiLvl.setX(58 - 70);
        uiLvl.setY(28);
        uiLvl.load();

        addChild(uiLvl);

        uiHP = new UILabel<>(getScene(), "-", 30, 10);
        font = uiHP.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiHP.setX(23);
        uiHP.setY(13);
        uiHP.load();

        addChild(uiHP);

        uiLblHP = new UILabel<>(getScene(), "hp:", 60, 10);
        font = uiLblHP.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblHP.setX(2);
        uiLblHP.setY(13);
        uiLblHP.load();

        addChild(uiLblHP);

        uiSld = new UILabel<>(getScene(), "-", 30, 10);
        font = uiSld.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiSld.setX(23);
        uiSld.setY(3);
        uiSld.load();

        addChild(uiSld);

        uiLblSld = new UILabel<>(getScene(), "sld:", 30, 10);
        font = uiLblSld.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblSld.setX(2);
        uiLblSld.setY(3);
        uiLblSld.load();

        addChild(uiLblSld);

        uiLblMov = new UILabel<>(getScene(), "mov:", 30, 10);
        font = uiLblMov.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblMov.setX(56);
        uiLblMov.setY(13);
        uiLblMov.load();

        addChild(uiLblMov);

        uiMov = new UILabel<>(getScene(), "-", 30, 10);
        font = uiMov.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiMov.setX(64);
        uiMov.setY(13);
        uiMov.load();

        addChild(uiMov);

        uiRes = new UILabel<>(getScene(), "-", 30, 10);
        font = uiRes.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiRes.setX(64);
        uiRes.setY(3);
        uiRes.load();

        addChild(uiRes);

        uiLblRes = new UILabel<>(getScene(), "res:", 30, 10);
        font = uiLblRes.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblRes.setX(56);
        uiLblRes.setY(3);
        uiLblRes.load();

        addChild(uiLblRes);

        // Weapon
        uiWeapon = new UILabel<>(getScene(), "-", 128, 10);
        font = uiWeapon.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiWeapon.setX(126);
        uiWeapon.setY(28);
        uiWeapon.load();

        addChild(uiWeapon);

        // Attacks
        uiAttacks = new UILabel<>(getScene(), "-", 80, 10);
        font = uiAttacks.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiAttacks.setX(126);
        uiAttacks.setY(28);
        uiAttacks.load();

        addChild(uiAttacks);

        // Damage
        uiDmg = new UILabel<>(getScene(), "-", 30, 10);
        font = uiDmg.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiDmg.setX(272 / 2);
        uiDmg.setY(13);
        uiDmg.load();

        addChild(uiDmg);

        uiLblDmg = new UILabel<>(getScene(), "dmg:", 30, 10);
        font = uiLblDmg.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblDmg.setX(121);
        uiLblDmg.setY(13);
        uiLblDmg.load();

        addChild(uiLblDmg);

        // Piercing
        uiPir = new UILabel<>(getScene(), "-", 30, 10);
        font = uiPir.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiPir.setX(184);
        uiPir.setY(3);
        uiPir.load();

        addChild(uiPir);

        uiLblPir = new UILabel<>(getScene(), "pir:", 30, 10);
        font = uiLblPir.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblPir.setX(169);
        uiLblPir.setY(3);
        uiLblPir.load();

        addChild(uiLblPir);

        // Range
        uiRng = new UILabel<>(getScene(), "-", 30, 10);
        font = uiRng.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiRng.setX(184);
        uiRng.setY(13);
        uiRng.load();

        addChild(uiRng);

        uiLblRng = new UILabel<>(getScene(), "rng:", 30, 10);
        font = uiLblRng.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblRng.setX(169);
        uiLblRng.setY(13);
        uiLblRng.load();

        addChild(uiLblRng);

        // Shots
        uiShots = new UILabel<>(getScene(), "-", 30, 10);
        font = uiShots.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiShots.setX(136);
        uiShots.setY(3);
        uiShots.load();

        addChild(uiShots);

        uiLblShots = new UILabel<>(getScene(), "shots:", 30, 10);
        font = uiLblShots.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblShots.setX(121);
        uiLblShots.setY(3);
        uiLblShots.load();

        addChild(uiLblShots);
    }

    private void initRight() {
        int offset = 5;

        backgroundImg = new UIImage<>(getScene(),
                220, 54,
                bcgImgRightSrc);
        SpriteAnimationState state = new SpriteAnimationState(backgroundImg, getScene().getEngine().getAnimation("details"));
        state.setFlip(true);
        backgroundImg.setSpriteAnimation(state);
        addChild(backgroundImg);

        pawnImg = new UIImage<>(getScene(),
                32, 32,
                pawn.getSpriteSheetName());
        pawnImg.setZ(.02f);
        state = new SpriteAnimationState(pawnImg, getScene().getEngine().getAnimation(pawn.getAnimationName()));
        state.setFlip(false);
        pawnImg.setSpriteAnimation(state);
        pawnImg.setX(92 + offset);
        pawnImg.setY(-1);
        addChild(pawnImg);

        uiName = new UILabel<>(getScene(), "-", 128, 10);
        UIFont font = uiName.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiName.setX(8 + offset);
        uiName.setY(28);
        uiName.load();

        addChild(uiName);

        uiLvl = new UILabel<>(getScene(), "", 100, 10);
        font = uiLvl.getFont();
        font.setSmall(true);
        font.setSide(UIFont.Side.RIGHT);
        font.setHighlight(false);
        uiLvl.setX(-12 + offset);
        uiLvl.setY(28);
        uiLvl.load();

        addChild(uiLvl);

        uiHP = new UILabel<>(getScene(), "-", 30, 10);
        font = uiHP.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiHP.setX(23 + offset);
        uiHP.setY(13);
        uiHP.load();

        addChild(uiHP);

        uiLblHP = new UILabel<>(getScene(), "hp:", 60, 10);
        font = uiLblHP.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblHP.setX(2 + offset);
        uiLblHP.setY(13);
        uiLblHP.load();

        addChild(uiLblHP);

        uiSld = new UILabel<>(getScene(), "-", 30, 10);
        font = uiSld.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiSld.setX(23 + offset);
        uiSld.setY(3);
        uiSld.load();

        addChild(uiSld);

        uiLblSld = new UILabel<>(getScene(), "sld:", 30, 10);
        font = uiLblSld.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblSld.setX(2 + offset);
        uiLblSld.setY(3);
        uiLblSld.load();

        addChild(uiLblSld);

        uiLblMov = new UILabel<>(getScene(), "mov:", 30, 10);
        font = uiLblMov.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblMov.setX(56 + offset);
        uiLblMov.setY(13);
        uiLblMov.load();

        addChild(uiLblMov);

        uiMov = new UILabel<>(getScene(), "-", 30, 10);
        font = uiMov.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiMov.setX(64 + offset);
        uiMov.setY(13);
        uiMov.load();

        addChild(uiMov);

        uiRes = new UILabel<>(getScene(), "-", 30, 10);
        font = uiRes.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiRes.setX(64 + offset);
        uiRes.setY(3);
        uiRes.load();

        addChild(uiRes);

        uiLblRes = new UILabel<>(getScene(), "res:", 30, 10);
        font = uiLblRes.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblRes.setX(56 + offset);
        uiLblRes.setY(3);
        uiLblRes.load();

        addChild(uiLblRes);

        // Weapon
        uiWeapon = new UILabel<>(getScene(), "-", 128, 10);
        font = uiWeapon.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiWeapon.setX(127 + offset);
        uiWeapon.setY(28);
        uiWeapon.load();

        addChild(uiWeapon);

        // Attacks
        uiAttacks = new UILabel<>(getScene(), "-", 80, 10);
        font = uiAttacks.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiAttacks.setX(127);
        uiAttacks.setY(28);
        uiAttacks.load();

        addChild(uiAttacks);

        // Damage
        uiDmg = new UILabel<>(getScene(), "-", 30, 10);
        font = uiDmg.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiDmg.setX(136 + offset);
        uiDmg.setY(13);
        uiDmg.load();

        addChild(uiDmg);

        uiLblDmg = new UILabel<>(getScene(), "dmg:", 30, 10);
        font = uiLblDmg.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblDmg.setX(121 + offset);
        uiLblDmg.setY(13);
        uiLblDmg.load();

        addChild(uiLblDmg);

        // Piercing
        uiPir = new UILabel<>(getScene(), "-", 30, 10);
        font = uiPir.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiPir.setX(184 + offset);
        uiPir.setY(3);
        uiPir.load();

        addChild(uiPir);

        uiLblPir = new UILabel<>(getScene(), "pir:", 30, 10);
        font = uiLblPir.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblPir.setX(169 + offset);
        uiLblPir.setY(3);
        uiLblPir.load();

        addChild(uiLblPir);

        // Range
        uiRng = new UILabel<>(getScene(), "-", 30, 10);
        font = uiRng.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiRng.setX(184 + offset);
        uiRng.setY(13);
        uiRng.load();

        addChild(uiRng);

        uiLblRng = new UILabel<>(getScene(), "rng:", 30, 10);
        font = uiLblRng.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblRng.setX(169 + offset);
        uiLblRng.setY(13);
        uiLblRng.load();

        addChild(uiLblRng);

        // Shots
        uiShots = new UILabel<>(getScene(), "-", 30, 10);
        font = uiShots.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setSide(UIFont.Side.RIGHT);
        uiShots.setX(136 + offset);
        uiShots.setY(3);
        uiShots.load();

        addChild(uiShots);

        uiLblShots = new UILabel<>(getScene(), "shots:", 30, 10);
        font = uiLblShots.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        uiLblShots.setX(121 + offset);
        uiLblShots.setY(3);
        uiLblShots.load();

        addChild(uiLblShots);
    }

    private void init() {
        uiName.setToolTipSrc("name");
        uiLvl.setToolTipSrc("xp");

        uiLblHP.setToolTipSrc("health");
        uiHP.setToolTipSrc("health");
        uiLblSld.setToolTipSrc("shield");
        uiSld.setToolTipSrc("shield");
        uiLblMov.setToolTipSrc("movement");
        uiMov.setToolTipSrc("movement");
        uiLblRes.setToolTipSrc("resistance");
        uiRes.setToolTipSrc("resistance");

        uiWeapon.setToolTipSrc("weapon");
        uiAttacks.setToolTipSrc("attacks");

        uiLblDmg.setToolTipSrc("damage");
        uiDmg.setToolTipSrc("damage");
        uiLblRng.setToolTipSrc("range");
        uiRng.setToolTipSrc("range");
        uiLblShots.setToolTipSrc("shots");
        uiShots.setToolTipSrc("shots");
        uiLblPir.setToolTipSrc("piercing");
        uiPir.setToolTipSrc("piercing");
    }

    public void setAnimationAction(String animation) {
        backgroundImg.setAnimationAction(animation);
    }

    public Pawn getPawn() {
        return pawn;
    }

    @Override
    public Vector2f getPosition() {
        return position;
    }

    @Override
    public int getStyle() {
        return getParent().getStyle();
    }

    @Override
    public float getHeight() {
        return 44;
    }

    @Override
    public float getWidth() {
        return 220;
    }

    @Override
    public final float getY() {
        return position.y;
    }

    @Override
    public final void setY(float y) {
        position.y = y;
    }

    @Override
    public final float getX() {
        return position.x;
    }

    @Override
    public final void setX(float x) {
        position.x = x;
    }

    public void setDetails(SelectionDetails details) {
        if (details.canAttack) {
            this.backgroundImg.getSpriteAnimation().setIdleAction("idle");
        } else {
            this.backgroundImg.getSpriteAnimation().setIdleAction("disable");
        }

        if (pawn.canLevel()) {
            backgroundImg.setSprite("sprites/character ui levelup.png");
        } else {
            backgroundImg.setSprite("sprites/character ui.png");
        }

        this.details = details;

        if (!uiName.getText().equals(details.name)) {
            uiName.setText(details.name);
            uiName.load();
        }

        if (!uiLvl.getText().equals(details.level)) {
            uiLvl.setText(details.level);
            uiLvl.load();
        }

        if (!uiHP.getText().equals(details.hp)) {
            uiHP.setText(details.hp);
            uiHP.load();
        }

        if (!uiMov.getText().equals(details.movement)) {
            uiMov.setText(details.movement);
            uiMov.load();
        }

        if (!uiRes.getText().equals(details.resistance)) {
            uiRes.setText(details.resistance);
            uiRes.load();
        }

        if (!uiSld.getText().equals(details.shield)) {
            uiSld.setText(details.shield);
            uiSld.load();
        }

        if (!uiWeapon.getText().equals(details.weapon)) {
            uiWeapon.setText(details.weapon);
            uiWeapon.load();
        }

        if (!uiAttacks.getText().equals(details.attacks)) {
            uiAttacks.setText(details.attacks);
            uiAttacks.load();
        }

        if (!uiDmg.getText().equals(details.damage)) {
            uiDmg.setText(details.damage);
            uiDmg.load();
        }

        if (!uiPir.getText().equals(details.piercing)) {
            uiPir.setText(details.piercing);
            uiPir.load();
        }

        if (!uiRng.getText().equals(details.range)) {
            uiRng.setText(details.range);
            uiRng.load();
        }

        if (!uiShots.getText().equals(details.shots)) {
            uiShots.setText(details.shots);
            uiShots.load();
        }

        int offset = 6;

        // add new abilities
        pawn.getAbilities().stream().filter(a -> !a.action).forEach(a -> {
            if (abilityImgList.keySet().stream().noneMatch(ai -> ai == a)) {
                UIImage<BattleScene> abilityImg = new UIImage<>(getScene(), 10, 9, a.icon);
                abilityImg.setSpriteAnimation(new SpriteAnimationState(this, getScene().getEngine().getAnimation("hexbutton")));
                abilityImg.addMouseHandler(new MouseHandler() {
                    @Override
                    public void handleMouseClick() {

                    }

                    @Override
                    public void handleMouseEnter() {
                        if (getScene().getActiveTeam() == 0 && BattleScene.stateIsSelect(getScene().getState(), true)) {
                            getScene().setTempPawn(pawn);
                            getScene().setTempAbility(a);
                            getScene().setTempState(BattleScene.State.SELECT_ABILITY);
                        }
                    }

                    @Override
                    public void handleMouseLeave() {
                        getScene().clearTempState();
                    }

                    @Override
                    public void handleMouseHover(float delta) {

                    }
                });
                addChild(abilityImg);

                abilityImg.setToolTip(a.name, a.getDescription());

                abilityImgList.put(a, abilityImg);
            }
        });

        // remove gone abilities
        Map<Ability, UIImage<BattleScene>> toRemove = new HashMap<>();

        abilityImgList.forEach((ai, img) -> {
            if (pawn.getAbilities().stream().noneMatch(a -> ai == a)) {
                toRemove.put(ai, img);
            }
        });

        toRemove.forEach((ai, img) -> {
            abilityImgList.remove(ai);
            removeChild(img);
        });

        AtomicInteger i = new AtomicInteger();
        // order images
        abilityImgList.forEach((ai, img) -> {
            img.setX(getWidth() + 1 + i.get() * 11);
            img.setY(10 + offset);
            i.getAndIncrement();
        });

        // add new weapons
        pawn.getWeapons().forEach(w -> {
            if (weaponImgList.keySet().stream().noneMatch(wi -> wi == w)) {
                UIImage<BattleScene> weaponImg = new UIImage<>(getScene(), 10, 9, "sprites/gun hex.png");
                weaponImg.setSpriteAnimation(new SpriteAnimationState(this, getScene().getEngine().getAnimation("hexbutton")));
                weaponImg.addMouseHandler(new MouseHandler() {
                    @Override
                    public void handleMouseClick() {

                    }

                    @Override
                    public void handleMouseEnter() {
                        if (getScene().getActiveTeam() == 0 && BattleScene.stateIsSelect(getScene().getState(), true)) {
                            getScene().setTempPawn(pawn);
                            getScene().setTempWeapon(w);
                            getScene().setTempState(BattleScene.State.SELECT_ATTACK);
                        }
                    }

                    @Override
                    public void handleMouseLeave() {
                        getScene().clearTempState();
                    }

                    @Override
                    public void handleMouseHover(float delta) {

                    }
                });
                addChild(weaponImg);

                weaponImg.setToolTip(w.getName(), Weapon.getDescription(w.toGameData(), null));

                weaponImgList.put(w, weaponImg);
            }
        });

        i.set(0);
        // order images
        weaponImgList.forEach((wi, img) -> {
            img.setX(getWidth() + 1 + i.get() * 11);
            img.setY(10 - offset);
            i.getAndIncrement();
        });
    }

    @Override
    public void handleMouseClick() {
        if (!getScene().isPaused())
            pawn.getParent().handleMouseClick();
    }

    @Override
    public void handleMouseEnter() {
        if (!getScene().isPaused())
            pawn.getParent().handleMouseEnter();
    }

    @Override
    public void handleMouseLeave() {
        if (!getScene().isPaused())
            pawn.getParent().handleMouseLeave();
    }

    @Override
    public void handleMouseHover(float delta) {
        if (!getScene().isPaused())
            pawn.getParent().handleMouseHover(delta);
    }
}
