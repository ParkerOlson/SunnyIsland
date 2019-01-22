package com.raven.breakingsands.character;

import com.raven.breakingsands.ZLayer;
import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.breakingsands.scenes.battlescene.pawn.Pawn;
import com.raven.engine2d.GameEngine;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDataList;
import com.raven.engine2d.database.GameDatabase;
import com.raven.engine2d.database.GameDatable;
import com.raven.engine2d.graphics2d.sprite.SpriteAnimationState;
import com.raven.engine2d.graphics2d.sprite.SpriteSheet;
import com.raven.engine2d.graphics2d.sprite.handler.ActionFinishHandler;
import com.raven.engine2d.scene.Layer;
import com.raven.engine2d.scene.Scene;
import com.raven.engine2d.worldobject.WorldObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Weapon
        extends WorldObject<BattleScene, Pawn, WorldObject>
        implements GameDatable {

    class WeaponShotsActionFinishHandler implements ActionFinishHandler {

        private final boolean directionUp;
        private final AtomicInteger shotCount;

        public WeaponShotsActionFinishHandler(AtomicInteger shotCount, boolean directionUp) {
            this.shotCount = shotCount;
            this.directionUp = directionUp;
        }

        @Override
        public void onActionFinish() {

            if (shotCount.get() > 1) {

                shotCount.addAndGet(-1);

                if (directional)
                    if (directionUp)
                        getAnimationState().setAction("attack up shot");
                    else
                        getAnimationState().setAction("attack down shot");
                else
                    getAnimationState().setAction("attack shot");

                getAnimationState().addActionFinishHandler(new WeaponShotsActionFinishHandler(shotCount, directionUp));
            } else {
                if (directional)
                    if (directionUp)
                        getAnimationState().setAction("attack up end");
                    else
                        getAnimationState().setAction("attack down end");
                else
                    getAnimationState().setAction("attack end");

                getAnimationState().addActionFinishHandler(() -> {
                    getAnimationState().setActionIdle();
//                    setVisibility(false);
                });
            }
        }
    }

    public static List<SpriteSheet> getSpriteSheets(BattleScene scene) {
        List<SpriteSheet> data = new ArrayList<>();

        for (GameData gameData : GameDatabase.all("weapon")) {
            gameData.ifHas("sprite", gd ->
                    data.add(scene.getEngine().getSpriteSheet(gd.asString())));
        }

        return data;
    }

    private static String description;
    public static String getDescription(GameData gdWeapon, String name) {

        if (name != null && !name.equals("")) {
            description = name + "\n";
        } else {
            description = "";
//            gdWeapon.ifHas("name", s -> description += s + "\n");
        }

        description += "damage: " + gdWeapon.getInteger("damage") + "\n";

        gdWeapon.ifHas("piercing",
                gd -> description += "piercing: " + gd.asInteger() + "\n",
                () -> description += "piercing: 0\n");
        gdWeapon.ifHas("shots",
                gd -> description += "shots: " + gd.asInteger() + "\n",
                () -> description += "shots: 1\n");

        if (gdWeapon.getString("type").equals("ranged")) {
            if (gdWeapon.has("range")) {
                if (gdWeapon.getData("range").isList()) {
                    GameDataList rl = gdWeapon.getList("range");
                    description += "range: " + rl.get(0) + "-" + rl.get(1) + "\nranged";
                } else {
                    if (gdWeapon.getInteger("range") == 1)
                        description += "range: 1\nranged";
                    else
                        description += "range: 1-" + gdWeapon.getInteger("range") + "\nranged";
                }
            } else {
                description += "range: 1\nranged";
            }
        } else {
            description += "range: 1\nmelee";
        }

        return description;
    }

    private GameData gameData;
    private int damage, piercing = 0, rangeMax, rangeMin, shots;
    private boolean directional, passesPawn, selfDestruct;
    private WeaponType weaponType;
    private RangeStyle style;
    private String name;
    private Effect effect;

    public Weapon(BattleScene scene, GameData gameData) {
        super(scene, gameData);

        gameData.ifHas("display",
                d -> name = d.asString(),
                () -> name = gameData.getString("name"));


        damage = gameData.getInteger("damage");

        gameData.ifHas("passes_pawn", p -> passesPawn = p.asBoolean());

        gameData.ifHas("style",
                s -> {
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
                },
                () -> style = RangeStyle.DIAMOND);

        gameData.ifHas("type", t -> {
            switch (t.asString()) {
                case "melee":
                    weaponType = WeaponType.MELEE;
                    break;
                case "ranged":
                    weaponType = WeaponType.RANGED;
                    break;
                case "area":
                    weaponType = WeaponType.AREA;
                    break;
            }
        });

        if (gameData.getData("range").isInteger()) {
            rangeMax = gameData.getInteger("range");
            rangeMin = 1;
        } else {
            List<GameData> ranges = gameData.getList("range");
            rangeMax = ranges.get(1).asInteger();
            rangeMin = ranges.get(0).asInteger();
        }

        if (gameData.has("directional")) {
            directional = gameData.getBoolean("directional");
        }

        gameData.ifHas("self_destruct", gd -> selfDestruct = gd.asBoolean());
        gameData.ifHas("piercing", gd -> piercing = gd.asInteger());

        gameData.ifHas("shots", gd -> shots = gd.asInteger(), () -> shots = 1);

        if (gameData.has("effect")) {
            GameData gdEffect = gameData.getData("effect");

            if (gdEffect.isString()) {
                GameDatabase.all("effect").stream()
                        .filter(e -> e.getString("name").equals(gdEffect.asString()))
                        .findFirst()
                        .ifPresent(e -> effect = new Effect(scene, e));
            } else {
                effect = new Effect(scene, gdEffect);
            }
        }

        this.gameData = new GameData(gameData);
    }

    public void runAttackAnimation(boolean directionUp) {
        if (getAnimationState() != null) {

            AtomicInteger shotCount = new AtomicInteger(getShots());

            if (directional)
                if (directionUp)
                    getAnimationState().setAction("attack up start");
                else
                    getAnimationState().setAction("attack down start");
            else
                getAnimationState().setAction("attack start");

            getAnimationState().addActionFinishHandler(new WeaponShotsActionFinishHandler(shotCount, directionUp));
        }
    }

    public int getDamage() {
        return damage;
    }

    public int getPiercing() {
        return piercing;
    }

    public int getRangeMax() {
        return rangeMax;
    }

    public int getRangeMin() {
        return rangeMin;
    }

    public int getShots() {
        return shots;
    }

    public boolean isSelfDestruct() {
        return selfDestruct;
    }

    public RangeStyle getStyle() {
        return style;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public String getName() {
        return name;
    }

    public Effect getEffect() {
        return effect;
    }

    @Override
    public GameData toGameData() {
        return gameData;
    }

    @Override
    public Layer.Destination getDestination() {
        return Layer.Destination.Effects;
    }

    @Override
    public float getZ() {
        return ZLayer.WEAPON.getValue();
    }

    public boolean getDirectional() {
        return directional;
    }

    public boolean getPassesPawn() {
        return passesPawn;
    }
}
