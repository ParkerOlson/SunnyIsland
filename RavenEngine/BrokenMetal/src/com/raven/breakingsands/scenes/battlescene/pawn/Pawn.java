package com.raven.breakingsands.scenes.battlescene.pawn;

import com.raven.breakingsands.ZLayer;
import com.raven.breakingsands.character.Ability;
import com.raven.breakingsands.character.Effect;
import com.raven.breakingsands.character.Weapon;
import com.raven.breakingsands.character.WeaponType;
import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.breakingsands.scenes.battlescene.SelectionDetails;
import com.raven.breakingsands.scenes.battlescene.UIDetailText;
import com.raven.breakingsands.scenes.battlescene.map.Terrain;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDataList;
import com.raven.engine2d.database.GameDatabase;
import com.raven.engine2d.database.GameDatable;
import com.raven.engine2d.graphics2d.sprite.SpriteAnimationState;
import com.raven.engine2d.graphics2d.sprite.SpriteSheet;
import com.raven.engine2d.graphics2d.sprite.handler.ActionFinishHandler;
import com.raven.engine2d.graphics2d.sprite.handler.CountdownActionFinishHandler;
import com.raven.engine2d.scene.Layer;
import com.raven.engine2d.util.math.Vector2f;
import com.raven.engine2d.worldobject.WorldObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Pawn extends WorldObject<BattleScene, Terrain, WorldObject>
        implements GameDatable {

    public static GameDataList getDataList() {
        return GameDatabase.all("pawn");
    }

    public static List<SpriteSheet> getSpriteSheets(BattleScene scene) {
        List<SpriteSheet> data = new ArrayList<>();

        for (GameData gameData : getDataList()) {
            data.add(scene.getEngine().getSpriteSheet(gameData.getString("sprite")));
        }

        return data;
    }

    // instance
    private List<Weapon> weapons = new ArrayList<>();
    private Weapon weapon;
    private String name = "", charClass = "amateur", spriteNormal, spriteHack, weaponHack;
    private GameData weaponNormal;
    private int level = 0, xp, team,
            maxHitPoints, remainingHitPoints, bonusHp, bonusHpLoss,
            maxShield, remainingShield, bonusShield, bonusShieldLoss,
            maxMovement, remainingMovement,
            resistance, bonusResistance, maxAttacks = 1, remainingAttacks,
            bonusPiercing, bonusMinRange, bonusMaxRange, bonusMovement,
            tempResistance = 0, tempDamage = 0, tempRange = 0, tempPiercing = 0,
            xpModifier = 1, xpGain;
    private Hack hack;
    private boolean unmoved = true;
    private boolean ready = true;
    private boolean hasAttacked = false;
    private int abilityOrder = 0;
    private List<Ability> abilities = new ArrayList<>();
    private List<Ability> abilityAffects = new ArrayList<>();

    private SelectionDetails details = new SelectionDetails();
    private UIDetailText uiDetailText;
    private PawnMessage pawnMessage;
    private float messageShowTime;

    public Pawn(BattleScene scene, GameData gameData) {
        super(scene, gameData);

        name = gameData.getString("name");
        gameData.ifHas("class", c -> charClass = c.asString());
        gameData.ifHas("level", l -> level = l.asInteger());
        gameData.ifHas("xp", x -> xp = x.asInteger());
        gameData.ifHas("ability_order",
                o -> abilityOrder = o.asInteger(),
                () -> abilityOrder = new Random().nextInt());
        team = gameData.getInteger("team");

        gameData.ifHas("sprite", s -> spriteNormal = s.asString());
        gameData.ifHas("sprite_hack", h -> spriteHack = h.asString());
        gameData.ifHas("weapon_hack", h -> weaponHack = h.asString());

        gameData.ifHas("bonus_hp_loss", g -> bonusHpLoss = g.asInteger()); // TODO check after abilities
        gameData.ifHas("remaining_hit_points",
                r -> {
                    remainingHitPoints = r.asInteger();
                    maxHitPoints = gameData.getInteger("hp");
                },
                () -> remainingHitPoints = maxHitPoints = gameData.getInteger("hp"));


        gameData.ifHas("bonus_shield_loss", g -> bonusShieldLoss = g.asInteger()); // TODO check after abilities
        gameData.ifHas("remaining_shield",
                r -> {
                    remainingShield = r.asInteger();
                    maxShield = gameData.getInteger("shield");
                },
                () -> gameData.ifHas("shield", s -> remainingShield = maxShield = s.asInteger()));

        maxMovement = gameData.getInteger("movement");

        gameData.ifHas("remaining_movement", m -> remainingMovement = m.asInteger());
        gameData.ifHas("resistance", m -> resistance = m.asInteger());
        gameData.ifHas("bonus_piercing", m -> bonusPiercing = m.asInteger());
        gameData.ifHas("bonus_min_range", m -> bonusMinRange = m.asInteger());
        gameData.ifHas("bonus_max_range", m -> bonusMaxRange = m.asInteger());
        gameData.ifHas("total_attacks", m -> maxAttacks = m.asInteger());
        gameData.ifHas("remaining_attacks", m -> remainingAttacks = m.asInteger());
        gameData.ifHas("bonus_movement", m -> bonusMovement = m.asInteger());
        gameData.ifHas("temp_range", m -> tempRange = m.asInteger());
        gameData.ifHas("temp_piercing", m -> tempPiercing = m.asInteger());
        gameData.ifHas("temp_damage", m -> tempDamage = m.asInteger());
        gameData.ifHas("temp_resistance", m -> tempResistance = m.asInteger());

        gameData.ifHas("xp_gain", x -> xpGain = x.asInteger());
        gameData.ifHas("xp_modifier", x -> xpModifier = x.asInteger());

        GameDatabase db = scene.getEngine().getGameDatabase();

        // weapons
        gameData.ifHas("weapons", ws -> {
            ws.asList().forEach(w -> {
                setWeapon(new Weapon(scene, w));
            });
        });

        // weapon
        if (gameData.has("weapon")) {
            if (gameData.has("weapon_normal")) {
                weaponNormal = gameData.getData("weapon_normal");
            } else {
                weaponNormal = gameData.getData("weapon");
            }
            setWeapon(gameData.getData("weapon"));
        } else {
            setWeapon(new Weapon(scene, db.getTable("weapon").getRandom(scene.getRandom())));
        }

        // abilities
        if (gameData.has("abilities")) {
            for (GameData gdAbility : gameData.getList("abilities")) {
                addAbility(new Ability(gdAbility), false);
            }
        }

        // hack
        gameData.ifHas("hack", h -> hack(new Hack(scene, this, h)));

        gameData.ifHas("ready", x -> ready = x.asBoolean());
        gameData.ifHas("has_attacked", x -> hasAttacked = x.asBoolean());
        gameData.ifHas("unmoved", x -> unmoved = x.asBoolean());

        // message
        pawnMessage = new PawnMessage(scene);
        Vector2f pos = pawnMessage.getWorldPosition();
        pos.x -= .9;
        pos.y += 1.3;
        pawnMessage.setPosition(pos);
        addChild(pawnMessage);

        setHighlight(BattleScene.OFF);
    }

    @Override
    public GameData toGameData() {
        HashMap<String, GameData> map = new HashMap<>();

        GameData woData = getWorldObjectData();
        for (String key : woData.asMap().keySet()) {
            map.put(key, woData.getData(key));
        }

        map.put("name", new GameData(name));
        map.put("class", new GameData(charClass));
        if (spriteHack != null)
            map.put("sprite_hack", new GameData(spriteHack));
        map.put("weapon_hack", new GameData(weaponHack));
        map.put("weapon_normal", new GameData(weaponNormal));
        map.put("level", new GameData(level));
        map.put("xp", new GameData(xp));
        map.put("xp_modifier", new GameData(xpModifier));
        map.put("ability_order", new GameData(abilityOrder));
        map.put("team", new GameData(team));
        map.put("hp", new GameData(maxHitPoints));
        map.put("remaining_hit_points", new GameData(remainingHitPoints));
        map.put("bonus_hp_loss", new GameData(bonusHpLoss));
        map.put("shield", new GameData(maxShield));
        map.put("remaining_shield", new GameData(remainingShield));
        map.put("bonus_shield_loss", new GameData(bonusShieldLoss));
        map.put("movement", new GameData(maxMovement));
        map.put("remaining_movement", new GameData(remainingMovement));
        map.put("resistance", new GameData(resistance));
        map.put("bonus_piercing", new GameData(bonusPiercing));
        map.put("bonus_min_range", new GameData(bonusMinRange));
        map.put("bonus_max_range", new GameData(bonusMaxRange));
        map.put("temp_range", new GameData(tempRange));
        map.put("temp_piercing", new GameData(tempPiercing));
        map.put("temp_damage", new GameData(tempDamage));
        map.put("temp_resistance", new GameData(tempResistance));
        map.put("total_attacks", new GameData(maxAttacks));
        map.put("remaining_attacks", new GameData(remainingAttacks));
        map.put("bonus_movement", new GameData(bonusMovement));
        map.put("xp_gain", new GameData(xpGain));
        map.put("weapon", weapon.toGameData());
        List<Weapon> weaponsRemoved = new ArrayList<>(weapons);
        weaponsRemoved.remove(weapon);
        map.put("weapons", new GameDataList(weaponsRemoved).toGameData());
        map.put("abilities", new GameDataList(abilities).toGameData());
        if (hack != null) {
            map.put("hack", hack.toGameData());
        }

        map.put("ready", new GameData(ready));
        map.put("has_attacked", new GameData(hasAttacked));
        map.put("unmoved", new GameData(unmoved));

        return new GameData(map);
    }

    public String getName() {
        return name;
    }

    public int getTeam(boolean withHack) {
        if (withHack) {
            if (hack == null)
                return team;
            else
                return hack.getTeam();
        } else
            return team;
    }

    public int getAbilityOrder() {
        return abilityOrder;
    }

    public void setTeam(int i) {
        team = i;
    }

    public Hack getHack() {
        return hack;
    }

    public int getHitPoints() {
        return maxHitPoints;
    }

    public int getRemainingHitPoints() {
        return remainingHitPoints;
    }

    public int getBonusHp() {
        return Math.max(bonusHp + bonusHpLoss, 0);
    }

    public int getMaxShield() {
        return maxShield;
    }

    public int getRemainingShield() {
        return remainingShield;
    }

    public int getBonusShield() {
        return Math.max(bonusShield + bonusShieldLoss, 0);
    }

    public boolean canMove() {
        boolean canMove = true;

        canMove &= getRemainingMovement() > 0;

//        canMove &= remainingAttacks == maxAttacks;
//
//        for (Ability a : abilities) {
//            canMove &= a.uses == null || (a.remainingUses == a.uses || a.remain);
//        }

        return canMove;
    }

    public boolean canLevel() {
//        return true;
        return xp >= getNextLevelXp();
    }

    public boolean canAttack() {
        final boolean[] canMove = {true};

        canMove[0] &= getRemainingAttacks() > 0;

        abilities.stream().filter(a -> a.upgrade == null).forEach(a -> {
            if (a.useRegainType == Ability.UseRegainType.TURN)
                canMove[0] &= a.uses == null || (!a.usedThisTurn || a.remainingUses.equals(a.uses) || a.remain);
            else
                canMove[0] &= a.uses == null || (!a.usedThisTurn || a.remain);
        });

        return canMove[0];
    }

    public boolean canAbility(Ability ability) {

        final boolean[] canAbility = {true};

        if (ability.conditions != null) {
            ability.conditions.forEach(c -> {
                switch (c) {
                    case NO_ATTACKS:
                        canAbility[0] &= remainingAttacks <= 0;
                        break;
                    case HAS_ATTACKS:
                        canAbility[0] &= remainingAttacks > 0;
                        break;
                    case NO_TEMP_DAMAGE:
                        canAbility[0] &= tempDamage <= 0;
                        break;
                }
            });
        }

        if (!canAbility[0])
            return false;

        if (ability.recall_unit) {
            return true;
        }

        if (ability.remain && ability.uses != null && ability.remainingUses > 0) {
            return true;
        }

        if (ability.usedThisTurn && ability.useRegainType == Ability.UseRegainType.LEVEL && !ability.remain) {
            return false;
        }

        if (remainingAttacks != maxAttacks && !ability.remain) {
            return false;
        }

        canAbility[0] &= !hasAttacked || ability.remain;

        abilities.stream().filter(a -> a.upgrade == null).forEach(a -> {
            if (a == ability) {
                canAbility[0] &= a.uses == null || (a.remainingUses > 0);
            } else if (a.useRegainType == Ability.UseRegainType.TURN) {
                canAbility[0] &= a.uses == null || (!a.usedThisTurn || a.remainingUses.equals(a.uses) || a.remain);
            } else {
                canAbility[0] &= a.uses == null || (!a.usedThisTurn || a.remain);
            }
        });

        return canAbility[0];
    }

    public void setUnmoved(boolean b) {
        unmoved = b;
    }

    public int getMaxMovement() {
        return maxMovement;
    }

    public int getRemainingMovement() {
        return remainingMovement + bonusMovement;
    }

    public int getRemainingAttacks() {
        return remainingAttacks;
    }

    public int getResistance() {
        return resistance + bonusResistance + tempResistance;
    }

    public int getTempDamage() {
        return tempDamage;
    }

    public int getBonusResistance() {
        return bonusResistance + tempResistance;
    }

    public int getBonusPiercing() {
        return bonusPiercing + tempPiercing;
    }

    public int getBonusMinRange() {
        return getWeapon().getWeaponType() == WeaponType.MELEE ? 0 : bonusMinRange;
    }

    public int getBonusMaxRange() {
        return getWeapon().getWeaponType() == WeaponType.MELEE ? 0 : (bonusMaxRange + tempRange);
    }

    public int getResistance(boolean b) {
        if (b) return getResistance();
        else return resistance;
    }

    public String getCharacterClass() {
        return charClass;
    }

    public void setCharacterClass(GameData newCharClass) {
        this.name = this.charClass = newCharClass.getString("name");

        GameData bonus = newCharClass.getData("bonus");

        bonus.ifHas("hp", gd -> {
            maxHitPoints += gd.asInteger();
            remainingHitPoints += gd.asInteger();
        });
        bonus.ifHas("shield", gd -> {
            maxShield += gd.asInteger();
            remainingShield += gd.asInteger();
        });
        bonus.ifHas("resistance", gd -> resistance += gd.asInteger());
        bonus.ifHas("movement", gd -> {
            maxMovement += gd.asInteger();
            remainingMovement += gd.asInteger();
        });
        bonus.ifHas("attacks", gd -> {
            maxAttacks += gd.asInteger();
            remainingAttacks += gd.asInteger();
        });

        // add ability to the pawn if it is part of the class upgrade
        bonus.ifHas("ability", a -> {
            GameDatabase.all("abilities").stream()
                    .filter(c -> c.has("class") && c.getString("class").equals(this.charClass))
                    .filter(ab -> ab.getString("name").equals(a.asString()))
                    .findFirst()
                    .ifPresent(ability -> addAbility(new Ability(ability)));
        });
    }

    public void addAbility(Ability ability) {
        addAbility(ability, true, -1);
    }

    public void addAbility(Ability ability, boolean add) {
        addAbility(ability, add, -1);
    }

    public void addAbility(Ability ability, int index) {
        addAbility(ability, true, index);
    }

    public void addAbility(Ability ability, boolean add, int index) {
        ability.owner = this;

        if (ability.replace != null) {
            abilities.stream()
                    .filter(a -> a.name.equals(ability.replace))
                    .findFirst()
                    .ifPresent(a -> {
                        this.removeAbility(a);

                        if (a.uses != null) {
                            int addUses = ability.uses - a.uses;
                            ability.remainingUses = a.remainingUses + addUses;
                        }
                    });
        }

        if (ability.type == Ability.Type.SELF) {
            if (ability.upgrade == null && add) {
                if (ability.hp != null) {
                    maxHitPoints += ability.hp;
                    remainingHitPoints += ability.hp;
                }
                if (ability.shield != null) {
                    maxShield += ability.shield;
                    remainingShield += ability.shield;
                }
                if (ability.attacks != null) {
                    maxAttacks += ability.attacks;
                    remainingAttacks += ability.attacks;
                }
                if (ability.movement != null) {
                    maxMovement += ability.movement;
                    remainingMovement += ability.movement;
                }
                if (ability.resistance != null) {
                    resistance += ability.resistance;
                }
                if (ability.piercing != null) {
                    bonusPiercing += ability.piercing;
                }
                if (ability.maxRange != null) {
                    bonusMaxRange += ability.maxRange;
                }
                if (ability.minRange != null) {
                    bonusMinRange += ability.minRange;
                }
                if (ability.xpModifier != null) {
                    xpModifier *= ability.xpModifier;
                }
            } else { // upgrade existing ability
                List<Ability> as = abilities.stream()
                        .filter(a -> a.name.equals(ability.upgrade))
                        .collect(Collectors.toList());

                as.forEach(a -> {
                    int i = removeAbility(a);

                    a.upgrade(ability, add);

                    addAbility(a, i);
                });
            }
        }
        if (index >= 0) {
            abilities.add(index, ability);
        } else
            abilities.add(ability);

        if (getParent() != null)
            getParent().setPawn(this); // Shitty way of making sure the aurora effect is there
    }

    public int removeAbility(Ability ability) {
        int index = abilities.indexOf(ability);
        abilities.remove(ability);
        if (getParent() != null)
            getParent().removePawnAbility(ability);

        if (ability.upgrade != null)
            abilities.stream()
                    .filter(a -> a.name.equals(ability.upgrade))
                    .forEach(a -> {
                        System.out.println(ability.name);
                        System.out.println(ability);
                        if (ability.size != null) {
                            a.size -= ability.size;
                        }
                        if (ability.damage != null) {
                            a.damage -= ability.damage;
                        }
                        if (ability.uses != null) { // TODO
                            a.uses -= ability.uses;
                            a.remainingUses -= ability.uses;
                        }
                        if (ability.instant_hack) {
                            a.instant_hack = false;
                        }
                        if (ability.remain)
                            a.remain = false;
                    });

        return index;
    }

    public List<Ability> getAbilities() {
        return new ArrayList<>(abilities);
    }

    public void triggerAbilities(Ability.Trigger trigger) {
        getAbilities().stream()
                .filter(a -> a.type == Ability.Type.TRIGGER && a.trigger == trigger)
                .forEach(this::doAbilityAffect);
    }

    public void doAbilityAffect(Ability a) {
        if ((a.target & Ability.Target.ALL) == Ability.Target.ALL ||
                ((a.target & Ability.Target.SELF) == Ability.Target.SELF && a.owner == this) ||
                ((a.target & Ability.Target.ALLY) == Ability.Target.ALLY && getTeam(true) == 0) ||
                ((a.target & Ability.Target.ENEMY) == Ability.Target.ENEMY && getTeam(true) == 1)) {

            if (a.restore != null) {
                this.remainingHitPoints += a.restore;
                int overflow = this.remainingHitPoints - this.maxHitPoints;
                this.remainingHitPoints = Math.min(this.remainingHitPoints, this.maxHitPoints);

                // restore loss
                if (overflow > 0) {
                    bonusHpLoss = Math.min(bonusHpLoss + overflow, 0);
                }
            }
            if (a.restore_shield != null) {
                this.remainingShield += a.restore_shield;
                int overflow = this.remainingShield - this.maxShield;
                this.remainingShield = Math.min(this.remainingShield, this.maxShield);

                // restore loss
                if (overflow > 0) {
                    bonusShieldLoss = Math.min(bonusShieldLoss + overflow, 0);
                }
            }
            if (a.restore_attack != null) {
                this.remainingAttacks += a.restore_attack;
                this.remainingAttacks = Math.min(this.remainingAttacks, this.maxAttacks);
            }
            if (a.restore_movement != null) {
                this.remainingMovement += a.restore_movement;
                this.remainingMovement = Math.min(this.remainingMovement, this.maxMovement);
            }
            if (a.bonus_movement != null) {
                this.bonusMovement += a.bonus_movement;
            }
            if (a.temp_resistance != null) {
                this.tempResistance += a.temp_resistance;
            }
            if (a.temp_damage != null) {
                this.tempDamage += a.temp_damage;
            }
            if (a.temp_range != null) {
                this.tempRange += a.temp_range;
            }
            if (a.temp_piercing != null) {
                this.tempPiercing += a.temp_piercing;
            }
            if (a.rest_heal) { // Shouldn't trigger?
                System.out.println("Ummm.....");
                abilities.stream()
                        .filter(ab -> ab.heal)
                        .forEach(this::doAbilityAffect);
            }
        }
    }

    public void addAbilityAffect(Ability a) {
        if ((a.target & Ability.Target.ALL) == Ability.Target.ALL ||
                ((a.target & Ability.Target.ALLY) == Ability.Target.ALLY && getTeam(true) == 0) ||
                ((a.target & Ability.Target.ENEMY) == Ability.Target.ENEMY && getTeam(true) == 1)) {

            if (abilityAffects.stream().noneMatch(e -> e.name.equals(a.name))) {
                if (abilityAffects.stream().anyMatch(e -> a.name.equals(e.replace))) {
                    // if already replaced, do nothing
                } else {
                    Optional<Ability> o = abilityAffects.stream()
                            .filter(e -> e.name.equals(a.replace))
                            .findFirst();

                    if (o.isPresent()) {
                        // if will replace, add difference
                        Ability existing = o.get();

                        if (a.hp != null)
                            this.bonusHp += a.hp - (existing.hp != null ? existing.hp : 0);
                        if (a.shield != null)
                            this.bonusShield += a.shield - (existing.shield != null ? existing.shield : 0);
                        if (a.resistance != null)
                            this.bonusResistance += a.resistance - (existing.resistance != null ? existing.resistance : 0);
                    } else {
                        // otherwise, add normally
                        if (a.hp != null)
                            this.bonusHp += a.hp;
                        if (a.shield != null)
                            this.bonusShield += a.shield;
                        if (a.resistance != null)
                            this.bonusResistance += a.resistance;
                    }
                }

                updateDetailText();
            }

            abilityAffects.add(a);
        }
    }

    public void removeAbilityAffect(Ability a) {
        if (abilityAffects.remove(a)) {
            if ((a.target & Ability.Target.ALL) == Ability.Target.ALL ||
                    ((a.target & Ability.Target.ALLY) == Ability.Target.ALLY && getTeam(true) == 0) ||
                    ((a.target & Ability.Target.ENEMY) == Ability.Target.ENEMY && getTeam(false) == 1)) {

                if (abilityAffects.stream().noneMatch(e -> e.name.equals(a.name))) {
                    if (abilityAffects.stream().anyMatch(e -> a.name.equals(e.replace))) {
                        // if already replaced, do nothing
                    } else {
                        Optional<Ability> o = abilityAffects.stream()
                                .filter(e -> e.name.equals(a.replace))
                                .findFirst();

                        if (o.isPresent()) {
                            // if was replacing, subtract difference
                            Ability existing = o.get();

                            if (a.hp != null)
                                this.bonusHp -= a.hp - (existing.hp != null ? existing.hp : 0);
                            if (a.shield != null)
                                this.bonusShield -= a.shield - (existing.shield != null ? existing.shield : 0);
                            if (a.resistance != null)
                                this.bonusResistance -= a.resistance - (existing.resistance != null ? existing.resistance : 0);


                        } else {
                            // otherwise, remove normally
                            if (a.hp != null)
                                this.bonusHp -= a.hp;
                            if (a.shield != null)
                                this.bonusShield -= a.shield;
                            if (a.resistance != null)
                                this.bonusResistance -= a.resistance;

                            updateDetailText();
                        }
                    }
                }
            }
        }
    }

    public List<Ability> getAbilityAffects() {
        return abilityAffects;
    }

    public void runFloorAbilities() {
        abilities.stream()
                .filter(a -> a.type == Ability.Type.TRIGGER && a.trigger == Ability.Trigger.FLOOR)
                .forEach(a -> {
                    if (a.rest_heal) {
                        abilities.stream()
                                .filter(ab -> ab.heal)
                                .forEach(aa -> getScene().getPawns().forEach(p -> p.doAbilityAffect(aa)));
                    } else {
                        getScene().getPawns().forEach(p -> p.doAbilityAffect(a));
                    }
                });
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int lvl) {
        level = lvl;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapon(String weapon) {
        GameDatabase.all("weapon").stream()
                .filter(w -> w.getString("name").equals(weapon))
                .findFirst()
                .ifPresent(w -> setWeapon(new Weapon(getScene(), w)));
    }

    public void setWeapon(GameData gdWeapon) {
        if (gdWeapon.isString()) {
            setWeapon(gdWeapon.asString());
        } else {
            setWeapon(new Weapon(getScene(), gdWeapon));
        }
    }

    public void setWeapon(Weapon weapon) {
        if (this.weapon != null)
            removeChild(weapon);

        this.weapon = weapon;
        if (!weapons.contains(weapon))
            weapons.add(weapon);

        if (weapon != null) {
            addChild(weapon);
        }
    }

    public void ready() {
        setReady(true);
        hasAttacked = false;
        unmoved = true;
        remainingMovement = maxMovement;
        remainingAttacks = maxAttacks;

        tempResistance = 0;

        abilities.forEach(a -> {
            if (a.uses != null && a.useRegainType == Ability.UseRegainType.TURN) {
                a.remainingUses = a.uses;
            }
            a.usedThisTurn = false;
        });
    }

    public void endTurn() {
        if (unmoved && !hasAttacked)
            triggerAbilities(Ability.Trigger.UNMOVED);
    }

    public void reduceMovement(int amount) {
        unmoved = false;

        int overflow = bonusMovement - amount;
        bonusMovement = Math.max(bonusMovement - amount, 0);
        amount = -overflow;

        if (amount > 0)
            remainingMovement = Math.max(remainingMovement - amount, 0);
    }

    public void runAttackAnimation(Pawn target, ActionFinishHandler onAttackDone) {
        boolean directional = getWeapon().getDirectional();
        boolean directionUp = target.getParent().getMapX() < getParent().getMapX() ||
                target.getParent().getMapY() > getParent().getMapY();

        weapon.playClip("attack");

        setFlip(target.getParent().getMapY() > getParent().getMapY() ||
                target.getParent().getMapX() > getParent().getMapX());

        switch (weapon.getWeaponType()) {
            case MELEE:
                runMeleeAnimation(target, directional, directionUp, onAttackDone);
                break;
            case RANGED:
                runRangedAnimation(target, directional, directionUp, onAttackDone);
                break;
            case AREA:
                runAreaAnimation(onAttackDone);
                break;
        }
    }

    private void runAreaAnimation(ActionFinishHandler onAttackDone) {

        getAnimationState().setAction("ranged start");

        getAnimationState().addActionFinishHandler(() -> {
            getAnimationState().setAction("ranged end");

            List<Terrain> targets = getParent().selectRange(weapon.getStyle(), weapon.getRangeMin(), weapon.getRangeMax(), false, weapon.getPassesPawn()).stream()
                    .filter(t -> t.getPawn() != null)
                    .collect(Collectors.toList());

            CountdownActionFinishHandler mHandler = new CountdownActionFinishHandler(onAttackDone, 1 + targets.size());

            for (Terrain target : targets) {
                attack(target.getPawn(), weapon.getDamage(), weapon.getPiercing() + getBonusPiercing(), weapon.getShots(), mHandler);
            }

            if (!weapon.isSelfDestruct()) {
                getAnimationState().addActionFinishHandler(mHandler);
                getAnimationState().addActionFinishHandler(() -> getAnimationState().setActionIdle(false));
            } else {
                getAnimationState().addActionFinishHandler(() -> this.die(mHandler));
            }
        });
    }

    private void runMeleeAnimation(Pawn target, boolean directional, boolean directionUp, ActionFinishHandler onAttackDone) {

        if (directional)
            if (directionUp)
                getAnimationState().setAction("melee up start");
            else
                getAnimationState().setAction("melee down start");
        else
            getAnimationState().setAction("melee start");

        getAnimationState().addActionFinishHandler(() -> {

            if (directional)
                if (directionUp)
                    getAnimationState().setAction("melee up end");
                else
                    getAnimationState().setAction("melee down end");
            else
                getAnimationState().setAction("melee end");

            Effect effect = weapon.getEffect();
            if (effect != null) {
                effect.setVisibility(true);
                target.addChild(effect);
                effect.getAnimationState().addActionFinishHandler(() -> target.removeChild(effect));
            }

            ActionFinishHandler mHandler = new CountdownActionFinishHandler(onAttackDone, 2);

            attack(target, weapon.getDamage(), weapon.getPiercing() + getBonusPiercing(), weapon.getShots(), mHandler);
            getAnimationState().addActionFinishHandler(mHandler);
            getAnimationState().addActionFinishHandler(() -> getAnimationState().setActionIdle(false));

        });

        weapon.runAttackAnimation(directionUp);
    }

    private void runRangedAnimation(Pawn target, boolean directional, boolean directionUp, ActionFinishHandler onAttackDone) {

        AtomicInteger shotCount = new AtomicInteger(weapon.getShots());

        if (directional)
            if (directionUp)
                getAnimationState().setAction("ranged up start");
            else
                getAnimationState().setAction("ranged down start");
        else
            getAnimationState().setAction("ranged start");

        getAnimationState().addActionFinishHandler(new PawnShotsActionFinishHandler(this, target, shotCount, directional, directionUp, onAttackDone));

        weapon.runAttackAnimation(directionUp);
    }

    public int getDamage(int damage, int piercing, int shots, int tempDamage) {
        int remainingResistance = Math.max(getResistance() - piercing, 0);
        return Math.max(Math.max(damage - remainingResistance, 0) * (shots - 1) + Math.max((damage + tempDamage) - remainingResistance, 0), 1);
    }

    public void attack(Pawn target, int damage, int piercing, int shots, ActionFinishHandler onAttackDone) {
        reduceAttacks();

        int dealtDamage = target.getDamage(damage, piercing, shots, tempDamage);
        tempDamage = 0;
        tempPiercing = 0;
        tempRange = 0;

        triggerAbilities(Ability.Trigger.ATTACK);

        boolean killed = target.damage(dealtDamage, onAttackDone);

        if (killed) {

            triggerAbilities(Ability.Trigger.KILL);

            if (hack != null) {
                if (getTeam(true) == 0) {
                    hack.getHacker().gainXP(target.xpGain, false);

                    getScene().getPawns().stream()
                            .filter(p -> p.getTeam(false) == 0 && p != hack.getHacker())
                            .forEach(p -> {
                                p.gainXP(target.xpGain, true);
                            });
                }
            } else {
                if (getTeam(false) == 0) {
                    gainXP(target.xpGain, false);

                    getScene().getPawns().stream()
                            .filter(p -> p.getTeam(false) == 0 && p != this)
                            .forEach(p -> {
                                p.gainXP(target.xpGain, true);
                            });
                }
            }
        }
    }

    public void reduceAttacks() {
        hasAttacked = true;
        remainingAttacks = Math.max(remainingAttacks - 1, 0);
    }

    public boolean damage(int dealtDamage, ActionFinishHandler onAttackDone) {
        if (dealtDamage <= 0) {
            dealtDamage = 1;
        }

        // shield
        int rolloverBonusShieldDamage = dealtDamage;
        if (getBonusShield() > 0) {
            rolloverBonusShieldDamage = -Math.min(getBonusShield() - dealtDamage, 0);
            this.bonusShieldLoss = Math.max(-dealtDamage + this.bonusShieldLoss, Math.min(-this.bonusShield, this.bonusShieldLoss));
        }

        int rolloverShieldDamage = -Math.min(this.remainingShield - rolloverBonusShieldDamage, 0);
        this.remainingShield = Math.max(this.remainingShield - rolloverBonusShieldDamage, 0);

        // hp
        int rolloverBonusHp = rolloverShieldDamage;
        if (getBonusHp() > 0) {
            rolloverBonusHp = -Math.min(getBonusHp() - rolloverShieldDamage, 0);
            this.bonusHpLoss = Math.max(-dealtDamage + this.bonusHpLoss, Math.min(-this.bonusHp, this.bonusHpLoss));
        }

        this.remainingHitPoints = Math.max(this.remainingHitPoints - rolloverBonusHp, 0);

        // death
        if (this.remainingHitPoints <= 0) {
            this.die(onAttackDone);
        } else if (onAttackDone != null) {
            onAttackDone.onActionFinish();
        }

        triggerAbilities(Ability.Trigger.DAMAGE);

        this.updateDetailText();

        this.showMessage("-" + Integer.toString(dealtDamage));

        return this.remainingHitPoints <= 0;
    }

    public void showMessage(String msg) {
        pawnMessage.setText(msg);
        messageShowTime = 0;
    }

    public void gainXP(int xp, boolean indirect) {
        int gain;

        if (indirect) {
            gain = (xp / 3) * xpModifier;
        } else {
            gain = xp;
        }

        this.xp += gain;

        showMessage("+" + Integer.toString(gain) + "xp");

        updateDetailText();
    }

    public void hack(Hack hack) {
        if (hack != null) {

            bonusHp += hack.getHP();
            bonusShield += hack.getShield();
            bonusResistance += hack.getResistance();

            if (hack.isInstant()) {
                ready();
            } else {
                setReady(false);
            }
            hack.setInstant(false);

            if (this.spriteHack != null)
                this.setSpriteSheet(spriteHack);
            if (this.weaponHack != null) {
                weapons.remove(this.weapon);
                setWeapon(this.weaponHack);
            }
        } else if (this.hack != null) {
            bonusHp -= this.hack.getHP();
            bonusShield -= this.hack.getShield();
            bonusResistance -= this.hack.getResistance();

            setReady(false);

            if (this.spriteNormal != null)
                this.setSpriteSheet(spriteNormal);
            if (this.weaponNormal != null) {
                weapons.remove(this.weapon);
                setWeapon(this.weaponNormal);
            }
        }

        this.hack = hack;

        setUIDetailText(new UIDetailText(getScene(), this));

        updateDetailText();
    }

    public void die(ActionFinishHandler onAttackDone) {
        if (getAnimationState().hasAction("die")) {
            getAnimationState().setAction("die");
            getAnimationState().addActionFinishHandler(this::onDie);
            if (onAttackDone != null)
                getAnimationState().addActionFinishHandler(onAttackDone);
        } else {
            onDie();
            if (onAttackDone != null)
                onAttackDone.onActionFinish();
        }
    }

    private void onDie() {
        remainingHitPoints = 0;
        getParent().removePawn();
        getScene().removePawn(this);
    }

    @Override
    public void onUpdate(float deltaTime) {
        messageShowTime += deltaTime;

        if (messageShowTime > 750 && pawnMessage.isVisible()) {
            pawnMessage.setVisibility(false);
        }
    }

    @Override
    public Layer.Destination getDestination() {
        return Layer.Destination.Details;
    }

    @Override
    public float getZ() {
        Vector2f wPos = getWorldPosition();

        return ZLayer.PAWN.getValue() +
                ((getParent().getMapY() - getParent().getMapX()) / 1000f) +
                -(wPos.y - wPos.x) / 1000f;
    }

    public void setFlip(boolean flip) {
        getAnimationState().setFlip(flip);

        SpriteAnimationState weaponState = getWeapon().getAnimationState();
        if (weaponState != null) {
            weaponState.setFlip(flip);
        }
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void prepLevel() {
        bonusHpLoss = 0;
        bonusShieldLoss = 0;
        bonusMovement = 0;
        remainingShield = maxShield;
        tempRange = 0;
        tempPiercing = 0;
        tempDamage = 0;
        tempResistance = 0;

        abilities.forEach(a -> {
            if (a.uses != null) {
                a.remainingUses = a.uses;
                a.usedThisTurn = false;
            }
        });

        ready();
    }

    public int getXp() {
        return xp;
    }

    public int getNextLevelXp() {
        return level * (level + 1) + Math.max(1, level + (level / 5) * ((level - 1) * (level - 1))) * 3;
//        return 0;
    }

    public void setUIDetailText(UIDetailText uiDetailText) {
        if (this.uiDetailText != null)
            getScene().removeUIDetails(this.uiDetailText);

        this.uiDetailText = uiDetailText;
        getScene().addUIDetails(this.uiDetailText);
    }

    public void updateDetailText() {
        if (uiDetailText != null) {
            if (getScene().getActivePawn() == this) {
                uiDetailText.setAnimationAction("active");
            } else {
                if (getParent() != null && getParent().isMouseHovering()) {
                    uiDetailText.setAnimationAction("hover");
                } else {
                    if (this.isReady() && getTeam(true) == getScene().getActiveTeam())
                        uiDetailText.setAnimationAction("idle");
                    else
                        uiDetailText.setAnimationAction("disable");
                }
            }

            details.name = getName();

            if (getTeam(false) == 0) {
                details.name = getName() + " " + getLevel();
//                details.level = getXp() + "/" + getNextLevelXp();
                details.level = Integer.toString(Math.max(getNextLevelXp() - getXp(), 0));
            } else {
                details.level = "";
            }

            details.hp = getRemainingHitPoints() + "/" + getHitPoints();
            if (getBonusHp() > 0) {
                details.hp += "+" + getBonusHp();
            }

            details.shield = getRemainingShield() + "/" + getMaxShield();
            if (getBonusShield() > 0) {
                details.shield += "+" + getBonusShield();
            }

            if (getTeam(true) == getScene().getActiveTeam()) {
                details.movement = getRemainingMovement() + "/" + getMaxMovement();
            } else
                details.movement = Integer.toString(getMaxMovement());

            details.resistance = Integer.toString(getResistance(false));
            if (getBonusResistance() > 0) {
                details.resistance += "+" + getBonusResistance();
            }

            details.weapon = getWeapon().getName();

            if (getTeam(true) == getScene().getActiveTeam()) {
                if (canAttack()) {
                    details.attacks = remainingAttacks + "/" + maxAttacks;
                } else {
                    details.attacks = "0/" + maxAttacks;
                }
            } else {
                details.attacks = Integer.toString(maxAttacks);
            }

            details.damage = Integer.toString(getWeapon().getDamage());
            if (tempDamage != 0) {
                details.damage += "+" + tempDamage;
            }
            details.piercing = Integer.toString(getWeapon().getPiercing());
            if (getBonusPiercing() > 0) {
                details.piercing += "+" + getBonusPiercing();
            }
            if (getWeapon().getRangeMax() != getWeapon().getRangeMin()) {
                details.range = Integer.toString(getWeapon().getRangeMin());

                if (getBonusMinRange() > 0) {
                    details.range += "+" + getBonusMinRange();
                }

                details.range += "-" + Integer.toString(getWeapon().getRangeMax());

                if (getBonusMaxRange() > 0) {
                    details.range += "+" + getBonusMaxRange();
                }
            } else {
                if (getWeapon().getWeaponType() == WeaponType.RANGED) {
                    details.range = Integer.toString(getWeapon().getRangeMax());

                    if (getBonusMaxRange() > 0) {
                        details.range += "+" + getBonusMaxRange();
                    }
                } else {
                    details.range = Integer.toString(getWeapon().getRangeMax());
                }
            }
            details.shots = Integer.toString(getWeapon().getShots());

            details.canAttack = canAttack() || canMove();

            uiDetailText.setDetails(details);
        }
    }

    public UIDetailText getUIDetailText() {
        return uiDetailText;
    }

    public void heal(int restore) {
        this.remainingHitPoints = Math.min(this.remainingHitPoints + restore, this.maxHitPoints);

        showMessage("+" + Integer.toString(restore) + "hp");

        updateDetailText();
    }
}
