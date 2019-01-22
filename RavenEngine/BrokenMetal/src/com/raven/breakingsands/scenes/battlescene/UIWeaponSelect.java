package com.raven.breakingsands.scenes.battlescene;

import com.raven.breakingsands.character.Weapon;
import com.raven.breakingsands.scenes.battlescene.pawn.Pawn;
import com.raven.engine2d.ui.*;
import com.raven.engine2d.util.math.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class UIWeaponSelect extends UIObject<BattleScene, UIContainer<BattleScene>> {

    private Vector2f position = new Vector2f();

    private Pawn pawn;
    private boolean quickSwap = false;
    private Weapon weapon;

    private UIImage<BattleScene> background;
    private UILabel<BattleScene> lblTitle;
    private UILabel<BattleScene> lblDesc;
    private UITextButton<BattleScene> btnConfirmCancel;
    private List<UITextButton<BattleScene>> btns = new ArrayList<>();

    private int offset = 16;

    private String message = "Pick a weapon.";

    public UIWeaponSelect(BattleScene scene) {
        super(scene);

        background = new UIImage<>(scene, 256, 256, "sprites/level up.png");
        this.addChild(background);

        lblTitle = new UILabel<>(getScene(), "Weapon Select", 256, 14);
        lblTitle.setX(45);
        lblTitle.setY(236);
        UIFont font = lblTitle.getFont();
        font.setSmall(false);
        font.setHighlight(true);
        lblTitle.load();
        addChild(lblTitle);

        lblDesc = new UILabel<>(getScene(), message, 244, 100);
        lblDesc.setY(125);
        lblDesc.setX(6);
        font = lblDesc.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setWrap(true);
        lblDesc.load();
        addChild(lblDesc);

        btnConfirmCancel = new UITextButton<BattleScene>(scene, "Cancel", "sprites/button.png", "mainbutton") {
            @Override
            public void handleMouseClick() {
                if (weapon != null) {
                    confirm();
                } else {
                    close();
                }
            }
        };
        btnConfirmCancel.setX(-btnConfirmCancel.getWidth() / 2 + getWidth() / 2);
        btnConfirmCancel.setY(-btnConfirmCancel.getHeight() / 2);
        btnConfirmCancel.load();
        addChild(btnConfirmCancel);

        UITextButton<BattleScene> btn = new UITextButton<BattleScene>(scene, "Cancel", "sprites/button.png", "mainbutton") {
            @Override
            public void handleMouseClick() {
                if (!isDisabled()) {
                    setWeapon(0);
                }
            }
        };
        btn.setX(offset);
        btn.setY(getHeight() / 2 - btn.getHeight() - offset);
        btn.load();
        addChild(btn);
        font = btn.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setWrap(true);
        font.setY(12);
        btns.add(btn);

        btn = new UITextButton<BattleScene>(scene, "Cancel", "sprites/button.png", "mainbutton") {
            @Override
            public void handleMouseClick() {
                if (!isDisabled()) {
                    setWeapon(1);
                }
            }
        };
        btn.setX(getWidth() - btn.getWidth() - offset);
        btn.setY(getHeight() / 2 - btn.getHeight() - offset);
        btn.load();
        addChild(btn);
        font = btn.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setWrap(true);
        font.setY(12);
        btns.add(btn);

        btn = new UITextButton<BattleScene>(scene, "Cancel", "sprites/button.png", "mainbutton") {
            @Override
            public void handleMouseClick() {
                if (!isDisabled()) {
                    setWeapon(2);
                }
            }
        };
        btn.setX(offset);
        btn.setY(getHeight() / 2 - btn.getHeight() * 2 - offset * 2);
        btn.load();
        addChild(btn);
        font = btn.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setWrap(true);
        font.setY(12);
        btns.add(btn);

        btn = new UITextButton<BattleScene>(scene, "Cancel", "sprites/button.png", "mainbutton") {
            @Override
            public void handleMouseClick() {
                if (!isDisabled()) {
                    setWeapon(3);
                }
            }
        };
        btn.setX(getWidth() - btn.getWidth() - offset);
        btn.setY(getHeight() / 2 - btn.getHeight() * 2 - offset * 2);
        btn.load();
        addChild(btn);
        font = btn.getFont();
        font.setSmall(true);
        font.setHighlight(false);
        font.setWrap(true);
        font.setY(12);
        btns.add(btn);

    }

    private void setWeapon(int i) {
        UIButton btn = btns.get(i);
        if (btn.isActive()) {

            btn.setActive(false);

            weapon = null;

            lblDesc.setText(message);
            lblDesc.load();

            btnConfirmCancel.setText("cancel");
            btnConfirmCancel.load();
        } else {

            btns.forEach(b -> b.setActive(false));
            btn.setActive(true);

            weapon = pawn.getWeapons().get(i);

            lblDesc.setText(Weapon.getDescription(weapon.toGameData(), weapon.getName() + "\n") +
                    (pawn.getWeapon() == weapon ?
                            ("\n\ncurrent weapon") :
                            (!quickSwap ? "\n\nwarning: this will cost one attack" : "")
                    ));
            lblDesc.load();

            btnConfirmCancel.setText("confirm");
            btnConfirmCancel.load();
        }
    }

    public void setPawn(Pawn pawn) {
        this.pawn = pawn;
        this.weapon = null;
        lblDesc.setText(message);
        lblDesc.load();
        quickSwap = pawn.getAbilities().stream().anyMatch(a -> a.quick_swap);

        btns.forEach(b -> {
            b.setActive(false);
            b.setDisable(true);
        });

        for (int i = 0; i < pawn.getWeapons().size(); i++) {
            UITextButton btn = btns.get(i);
            btn.setDisable(false);
            btn.setText(pawn.getWeapons().get(i).getName());
            btn.load();
        }
    }

    private void confirm() {
        if (pawn.getWeapon() != weapon) {
            pawn.setWeapon(weapon);
            if (!quickSwap) {
                pawn.reduceAttacks();
            }
        }
        close();
    }

    public void close() {
        setVisibility(false);
        getScene().setPaused(false);
        getScene().setActivePawn(pawn, true);
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

    @Override
    public float getHeight() {
        return 256;
    }

    @Override
    public float getWidth() {
        return 256;
    }
}
