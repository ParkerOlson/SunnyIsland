package com.raven.breakingsands.scenes.battlescene.levelup;

import com.raven.breakingsands.character.Ability;
import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.breakingsands.scenes.battlescene.pawn.Pawn;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.ui.*;
import com.raven.engine2d.util.math.Vector2f;

public class UILevelUp extends UIObject<BattleScene, UIContainer<BattleScene>> {

    private Vector2f position = new Vector2f();

    private Pawn pawn;

    private UIImage<BattleScene> background;
    private UILabel<BattleScene> lblLevelUp;
    private UILabel<BattleScene> lblDesc;
    private UITextButton<BattleScene> btnConfirmCancel;
    private LevelUpBasicStar starBasic;
    private LevelUpAdvancedStar starAdvanced;

    private Object reward;
    private LevelUpHexButton.Type rewardType;
    private LevelUpHexButton rewardButton;

    private String messageCanLevel = "Pick a new ability, weapon, or class.";
    private String messageCantLevel = "View available abilities, weapons, or classes.";

    public UILevelUp(BattleScene scene) {
        super(scene);

        background = new UIImage<>(scene, 256, 256, "sprites/level up.png");
        this.addChild(background);

        lblLevelUp = new UILabel<>(getScene(), "level up!", 256, 14);
        lblLevelUp.setX(45);
        lblLevelUp.setY(236);
        UIFont font = lblLevelUp.getFont();
        font.setSmall(false);
        font.setHighlight(true);
        lblLevelUp.load();
        addChild(lblLevelUp);

        lblDesc = new UILabel<>(getScene(), messageCanLevel, 244, 100);
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
                if (reward != null) selectReward();
                else {
                    close();
                }
            }
        };

        btnConfirmCancel.setX(-btnConfirmCancel.getWidth() / 2 + getWidth() / 2);
        btnConfirmCancel.setY(-btnConfirmCancel.getHeight() / 2);
        btnConfirmCancel.load();
        addChild(btnConfirmCancel);

        starBasic = new LevelUpBasicStar(this);
        starBasic.setX(getWidth() / 4);
        starBasic.setY(getHeight() / 4);
        addChild(starBasic);

        starAdvanced = new LevelUpAdvancedStar(this);
        starAdvanced.setX(getWidth() * 3 / 4);
        starAdvanced.setY(getHeight() / 4);
        addChild(starAdvanced);
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

    public void close() {
        clearReward();
        setVisibility(false);
        getScene().setPaused(false);
        getScene().setActivePawn(pawn, true);
    }

    public void setPawn(Pawn pawn) {
        this.pawn = pawn;

        clearReward();

        if (pawn.canLevel())
            lblLevelUp.setText(pawn.getName() + " leveled up!");
        else
            lblLevelUp.setText(pawn.getName() + " abilities");
        lblLevelUp.load();

        starBasic.clear();
        starAdvanced.clear();

        starBasic.setPawn(pawn);
        starAdvanced.setPawn(pawn);
    }

    public void setReward(LevelUpHexButton.Type type, Object reward, String description, LevelUpHexButton button) {
        clearReward();

        this.rewardType = type;
        this.reward = reward;
        this.rewardButton = button;

        button.setSpriteAnimation("hexbuttonactive");
        button.setActive(true);

        lblDesc.setText(description);
        lblDesc.load();

        if (pawn.canLevel()) {
            btnConfirmCancel.setText("confirm");
            btnConfirmCancel.load();
        }
    }

    public void clearReward() {
        if (rewardButton != null) {
            rewardButton.setSpriteAnimation("hexbutton");
            rewardButton.setActive(false);
//            rewardButton.setDisable(true);
        }

        this.rewardType = null;
        this.reward = null;
        this.rewardButton = null;

        if (pawn.canLevel())
            lblDesc.setText(messageCanLevel);
        else
            lblDesc.setText(messageCantLevel);
        lblDesc.load();

        btnConfirmCancel.setText("cancel");
        btnConfirmCancel.load();
    }

    private void selectReward() {
        if (pawn.canLevel()) {
            switch (rewardType) {
                default:
                case START:
                    break;
                case CLASS:
                    pawn.setCharacterClass((GameData) reward);
                    break;
                case WEAPON:
                    pawn.setWeapon((String) reward);
                    break;
                case ABILITY:
                    pawn.addAbility((Ability) reward);
                    break;
            }

            pawn.setLevel(pawn.getLevel() + 1);
        }
        close();
    }

    public Pawn getPawn() {
        return pawn;
    }
}
