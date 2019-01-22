package com.raven.breakingsands.scenes.battlescene.levelup;

import com.raven.breakingsands.character.Ability;
import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.breakingsands.scenes.battlescene.pawn.Pawn;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.ui.UIButton;

import java.util.ArrayList;
import java.util.List;

public class LevelUpHexButton extends UIButton<BattleScene> {

    public enum Type {
        START, ABILITY, CLASS, WEAPON
    }

    private Type type;
    private UILevelUp uiLevelUp;
    private List<LevelUpHexConnection> connections = new ArrayList<>();

    private static String getSprite(Type type) {
        switch (type) {
            default:
            case START:
                return "sprites/start hex.png";
            case CLASS:
                return "sprites/class hex.png";
            case WEAPON:
                return "sprites/gun hex.png";
            case ABILITY:
                return "sprites/ability hex.png";
        }
    }

    private Ability ability;
    private GameData pawnClass;

    private String description;

    public LevelUpHexButton(UILevelUp uiLevelUp, Type type) {
        super(uiLevelUp.getScene(), getSprite(type), "hexbutton");

        setDisable(true);

        this.type = type;
        this.uiLevelUp = uiLevelUp;
    }

    @Override
    public void handleMouseClick() {
        if (!isActive() && !isDisabled()) {
            switch (type) {
                default:
                case START:
                    break;
                case CLASS:
                    uiLevelUp.setReward(type, pawnClass, description, this);
                    break;
                case WEAPON:
                    uiLevelUp.setReward(type, ability.weapon, description, this);
                    break;
                case ABILITY:
                    uiLevelUp.setReward(type, ability, description, this);
                    break;
            }

//            pawn.setLevel(pawn.getLevel() + 1);
//
//            uiLevelUp.close();
        } else if (!isDisabled()) {
            uiLevelUp.clearReward();
        }
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
    }

    public void setAbility(Ability ability) {
        this.ability = ability;

        Pawn pawn = uiLevelUp.getPawn();

        if (ability.weapon != null) {
            type = Type.WEAPON;
            boolean active = pawn.getWeapons().stream().anyMatch(w -> w.getName().equals(ability.weapon));
            if (active) {
                setDisable(false);
                setActive(true);
            }
            description = ability.name + "\n\n" + ability.getDescription() + "\n\nwarning: this will replace the current weapon";
        } else {
            type = Type.ABILITY;

            boolean active = pawn.getAbilities().stream().anyMatch(a -> a.name.equals(ability.name) || (a.replace != null && a.replace.equals(ability.name)));
            if (active) {
                setDisable(false);
                setActive(true);
            }
            description = ability.name + "\n\n" + ability.getDescription();
        }

        setSprite(ability.icon);

        this.setToolTip(ability.name, ability.getDescription());

        connections.forEach(LevelUpHexConnection::checkConnection);
    }

    public void checkConnections() {
        connections.forEach(LevelUpHexConnection::checkConnection);
    }

    public Ability getAbility() {
        return  ability;
    }

    public void setClass(GameData pawnClass, boolean used) {
        this.pawnClass = pawnClass;

        Pawn pawn = uiLevelUp.getPawn();

        boolean active = pawn.getCharacterClass().equals(pawnClass.getString("name"));
        used |= active;
        used |= !pawn.getCharacterClass().equals("amateur");

        if (used) {
            setSprite("sprites/start hex.png");
        } else {
            setSprite("sprites/class hex.png");
        }

        setDisable(!active);
        setActive(active);
        if (isActive()) {
            setLocked(true);
        }

        setDisable(!pawn.getCharacterClass().equals(pawnClass.getString("name")) || used);
        setLocked(!pawn.getCharacterClass().equals("amateur")|| used);

        pawnClass.ifHas("desc", d -> description = d.asString(), () -> description = "");
        this.setToolTip(pawnClass.getString("name"), description);

        connections.forEach(LevelUpHexConnection::checkConnection);
    }

    public Type getType() {
        return type;
    }

    public void addConnection(LevelUpHexConnection connection) {
        this.connections.add(connection);
    }

    public void clear() {
        if (type != Type.START && type != Type.CLASS) {
            setSprite("sprites/ability hex.png");
        }

        ability = null;
        pawnClass = null;

        setLocked(false);
        setDisable(true);
        setActive(false);

        connections.forEach(LevelUpHexConnection::checkConnection);

        this.removeToolTip();
    }
}
