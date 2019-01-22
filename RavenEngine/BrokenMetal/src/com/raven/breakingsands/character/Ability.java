package com.raven.breakingsands.character;

import com.raven.breakingsands.scenes.battlescene.pawn.Pawn;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDataList;
import com.raven.engine2d.database.GameDatabase;
import com.raven.engine2d.database.GameDatable;

import java.util.ArrayList;
import java.util.List;

public class Ability implements GameDatable {

    private GameData gameData;

    public Pawn owner;

    public enum Type {SELF, AURORA, TARGET, TRIGGER, BUTTON}

    public enum Trigger {ATTACK, KILL, DAMAGE, FLOOR, UNMOVED}

    public enum Condition {NO_TEMP_DAMAGE, HAS_ATTACKS, NO_ATTACKS}

    public static class Target {
        static final public int ALL = 0b1111, SELF = 0b1000, ALLY = 0b0001, ENEMY = 0b0010, EMPTY = 0b0100, NOT_SELF = 0x0111;
    }

    public enum UseRegainType {TURN, LEVEL}

    public String name, upgrade;
    private String description = "";
    private String showDescription = "";
    public String weapon;
    public String buttonIcon;
    public Type type;
    public Trigger trigger;
    public int target;
    public RangeStyle style;
    public UseRegainType useRegainType = UseRegainType.TURN;
    public String replace, requires, icon = "sprites/ability hex.png";

    public Integer size, damage, uses;
    public Integer remainingUses;
    public Integer hp, shield, attacks, movement, resistance, piercing, maxRange, minRange, xpModifier,
            restore, restore_attack, restore_movement, restore_shield,
            temp_resistance, temp_damage, temp_range, temp_piercing,
            bonus_movement;
    public boolean action, remain, passesPawn, passesWall, usedThisTurn,
            taunt, push_blast, hook_pull,
            hack, instant_hack, transferable, cure,
            blink, recall, recall_unit, heal, rest_heal,
            quick_swap;
    public List<Condition> conditions;

    public Ability bonusAbility;

    public Ability(GameData gameData) {
        this.gameData = new GameData(gameData);

        name = gameData.getString("name");

        gameData.ifHas("desc", d -> description = d.asString());
        gameData.ifHas("weapon", w -> weapon = w.asString());

        gameData.ifHas("action", a -> action = a.asBoolean());
        gameData.ifHas("icon", a -> icon = a.asString());
        gameData.ifHas("button_icon", u -> buttonIcon = u.asString());

        if (weapon != null) {

            GameData gdWeapon = GameDatabase.all("weapon").stream()
                    .filter(gd -> gd.getString("name").equals(weapon))
                    .findFirst().get();

            description = Weapon.getDescription(gdWeapon, description);

//            if (!description.equals("")) {
//                description += "\n";
//            }
//
//            description += "damage: " + gdWeapon.getInteger("damage") + "\n";
//            gdWeapon.ifHas("piercing",
//                    gd -> description += "piercing: " + gd.asInteger() + "\n",
//                    () -> description += "piercing: 0\n");
//            gdWeapon.ifHas("shots",
//                    gd -> description += "shots: " + gd.asInteger() + "\n",
//                    () -> description += "shots: 1\n");
//
//            if (gdWeapon.getString("type").equals("ranged")) {
//                if (gdWeapon.has("range")) {
//                    if (gdWeapon.getData("range").isList()) {
//                        GameDataList rl = gdWeapon.getList("range");
//                        description += "range: " + rl.get(0) + "-" + rl.get(1) + "\nranged";
//                    } else {
//                        if (gdWeapon.getInteger("range") == 1)
//                            description += "range: 1\nranged";
//                        else
//                            description += "range: 1-" + gdWeapon.getInteger("range") + "\nranged";
//                    }
//                } else {
//                    description += "range: 1\nranged";
//                }
//            } else {
//                description += "range: 1\nmelee";
//            }
        }

        switch (gameData.getString("type")) {
            default:
            case "self":
                type = Type.SELF;
                break;
            case "aurora":
                type = Type.AURORA;
                break;
            case "target":
                type = Type.TARGET;
                break;
            case "trigger":
                type = Type.TRIGGER;
                break;
            case "button":
                type = Type.BUTTON;
                break;
        }

        gameData.ifHas("trigger", t -> {
            switch (t.asString()) {
                default:
                case "floor":
                    trigger = Trigger.FLOOR;
                    break;
                case "attack":
                    trigger = Trigger.ATTACK;
                    break;
                case "kill":
                    trigger = Trigger.KILL;
                    break;
                case "damage":
                    trigger = Trigger.DAMAGE;
                    break;
                case "unmoved":
                    trigger = Trigger.UNMOVED;
                    break;
            }
        });

        gameData.ifHas("target", t -> {
            switch (t.asString()) {
                case "all":
                    target = Target.ALL;
                    break;
                case "self":
                    target = Target.SELF;
                    break;
                case "ally":
                    target = Target.ALLY;
                    break;
                case "enemy":
                    target = Target.ENEMY;
                    break;
                case "empty":
                    target = Target.EMPTY;
                    break;
                case "not self":
                    target = Target.NOT_SELF;
                    break;
            }
        });

        gameData.ifHas("use_regain_type", t -> {
            switch (t.asString()) {
                case "turn":
                    useRegainType = UseRegainType.TURN;
                    break;
                case "level":
                    useRegainType = UseRegainType.LEVEL;
                    break;
            }
        }, () -> useRegainType = UseRegainType.TURN);

        gameData.ifHas("style", s -> {
            switch (s.asString()) {
                case "straight":
                    style = RangeStyle.STRAIGHT;
                    break;
                case "square":
                    style = RangeStyle.SQUARE;
                    break;
                case "diamond":
                    style = RangeStyle.DIAMOND;
                    break;
            }
        }, () -> style = RangeStyle.DIAMOND);

        gameData.ifHas("conditions", c -> {
            conditions = new ArrayList<>();

            c.asList().forEach(s -> {
                switch (s.asString()) {
                    case "no_attacks":
                        conditions.add(Condition.NO_ATTACKS);
                        break;
                    case "has_attacks":
                        conditions.add(Condition.HAS_ATTACKS);
                        break;
                    case "no_temp_damage":
                        conditions.add(Condition.NO_TEMP_DAMAGE);
                        break;
                }
            });
        });

        gameData.ifHas("replace", r -> replace = r.asString());
        gameData.ifHas("requires", r -> requires = r.asString());
        gameData.ifHas("passes_pawn", p -> passesPawn = p.asBoolean());
        gameData.ifHas("passes_wall", p -> passesWall = p.asBoolean());
        gameData.ifHas("transferable", t -> transferable = t.asBoolean());
        gameData.ifHas("upgrade", u -> upgrade = u.asString());
        gameData.ifHas("size", s -> size = s.asInteger());
        gameData.ifHas("damage", d -> damage = d.asInteger());
        gameData.ifHas("hp", h -> hp = h.asInteger());
        gameData.ifHas("shield", s -> shield = s.asInteger());
        gameData.ifHas("attacks", m -> attacks = m.asInteger());
        gameData.ifHas("movement", m -> movement = m.asInteger());
        gameData.ifHas("resistance", r -> resistance = r.asInteger());
        gameData.ifHas("piercing", r -> piercing = r.asInteger());
        gameData.ifHas("max_range", r -> maxRange = r.asInteger());
        gameData.ifHas("min_range", r -> minRange = r.asInteger());
        gameData.ifHas("xp_modifier", r -> xpModifier = r.asInteger());
        gameData.ifHas("restore", r -> restore = r.asInteger());
        gameData.ifHas("restore_attack", r -> restore_attack = r.asInteger());
        gameData.ifHas("restore_movement", r -> restore_movement = r.asInteger());
        gameData.ifHas("restore_shield", r -> restore_shield = r.asInteger());
        gameData.ifHas("temp_resistance", r -> temp_resistance = r.asInteger());
        gameData.ifHas("temp_damage", r -> temp_damage = r.asInteger());
        gameData.ifHas("temp_range", r -> temp_range = r.asInteger());
        gameData.ifHas("temp_piercing", r -> temp_piercing = r.asInteger());
        gameData.ifHas("bonus_movement", r -> bonus_movement = r.asInteger());
        gameData.ifHas("quick_swap", r -> quick_swap = r.asBoolean());

        if (gameData.has("remaining_uses")) {
            gameData.ifHas("remaining_uses", u -> remainingUses = u.asInteger());
            gameData.ifHas("uses", u -> uses = u.asInteger());
        } else {
            gameData.ifHas("uses", u -> remainingUses = uses = u.asInteger());
        }

        gameData.ifHas("used_this_turn", u -> usedThisTurn = u.asBoolean());
        gameData.ifHas("remain", c -> remain = c.asBoolean());
        gameData.ifHas("taunt", t -> taunt = t.asBoolean());
        gameData.ifHas("push_blast", p -> push_blast = p.asBoolean());
        gameData.ifHas("hook_pull", h -> hook_pull = h.asBoolean());
        gameData.ifHas("hack", h -> hack = h.asBoolean());
        gameData.ifHas("instant_hack", h -> instant_hack = h.asBoolean());
        gameData.ifHas("cure", h -> cure = h.asBoolean());
        gameData.ifHas("blink", h -> blink = h.asBoolean());
        gameData.ifHas("recall", h -> recall = h.asBoolean());
        gameData.ifHas("recall_unit", h -> recall_unit = h.asBoolean());
        gameData.ifHas("heal", h -> heal = h.asBoolean());
        gameData.ifHas("rest_heal", h -> rest_heal = h.asBoolean());
        gameData.ifHas("ability", h -> bonusAbility = new Ability(h));

        updateShowDescription();
    }

    @Override
    public GameData toGameData() {
        if (remainingUses != null) {
            gameData.asMap().put("remaining_uses", new GameData(remainingUses));
        }
        gameData.asMap().put("used_this_turn", new GameData(usedThisTurn));

        return new GameData(gameData);
    }

    public void upgrade(Ability ability, boolean add) {

        if (ability.size != null) {
            if (this.size == null)
                this.size = ability.size;
            else
                this.size += ability.size;
        }
        if (ability.temp_damage != null) {
            if (this.temp_damage == null)
                this.temp_damage = ability.temp_damage;
            else
                this.temp_damage += ability.temp_damage;
        }
        if (ability.shield != null) {
            if (this.shield == null)
                this.shield = ability.shield;
            else
                this.shield += ability.shield;
        }
        if (ability.hp != null) {
            if (this.hp == null)
                this.hp = ability.hp;
            else
                this.hp += ability.hp;
        }
        if (ability.damage != null) {
            if (this.damage == null)
                this.damage = ability.damage;
            else
                this.damage += ability.damage;
        }
        if (ability.resistance != null) {
            if (this.resistance == null)
                this.resistance = ability.resistance;
            else
                this.resistance += ability.resistance;
        }
        if (ability.movement != null) {
            if (this.movement == null)
                this.movement = ability.movement;
            else
                this.movement += ability.movement;
        }
        if (ability.restore != null) {
            if (this.restore == null)
                this.restore = ability.restore;
            else
                this.restore += ability.restore;
        }
        if (ability.uses != null) {
            if (this.uses == null) {
                this.uses = ability.uses;
                if (add)
                    this.remainingUses = ability.uses;
            } else {
                this.uses += ability.uses;
                if (add)
                    this.remainingUses += ability.uses;
            }
            ability.remainingUses = 0;
        }
        if (ability.instant_hack) {
            this.instant_hack = true;
        }
        if (ability.transferable) {
            this.transferable = true;
        }
        if (ability.target != 0) {
            this.target |= ability.target;
        }
        this.remain |= ability.remain;
        this.passesPawn |= ability.passesPawn;
        this.passesWall |= ability.passesWall;

        updateShowDescription();
    }

    private void updateShowDescription() {

        if (description != null)
            showDescription = description
                    .replace("temp_damage", temp_damage != null ? Integer.toString(temp_damage) : "");
        else
            showDescription = "";

    }

    public String getDescription() {
        return showDescription;
    }

    @Override
    public String toString() {
        return gameData.toString();
    }
}
