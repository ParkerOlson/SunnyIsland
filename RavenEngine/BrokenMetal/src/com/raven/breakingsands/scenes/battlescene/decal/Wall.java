package com.raven.breakingsands.scenes.battlescene.decal;

import com.raven.breakingsands.ZLayer;
import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.breakingsands.scenes.battlescene.map.Terrain;
import com.raven.engine2d.GameEngine;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDataList;
import com.raven.engine2d.database.GameDatabase;
import com.raven.engine2d.database.GameDatable;
import com.raven.engine2d.graphics2d.sprite.SpriteSheet;
import com.raven.engine2d.scene.Layer;
import com.raven.engine2d.worldobject.WorldObject;

import java.util.ArrayList;
import java.util.List;

public class Wall extends WorldObject<BattleScene, Terrain, WorldObject> implements GameDatable {

    public static GameDataList getDataList(BattleScene scene) {
        return GameDatabase.all("wall");
    }

    public static List<SpriteSheet> getSpriteSheets(BattleScene scene) {
        List<SpriteSheet> data = new ArrayList<>();

        for (GameData gameData : getDataList(scene)) {
            data.add(scene.getEngine().getSpriteSheet(gameData.getString("sprite")));
        }

        return data;
    }

    // instance
    private GameData gameData;
    private String name = "";

    public Wall(BattleScene scene, GameData gameData) {
        super(scene, gameData);

        this.gameData = new GameData(gameData);

        name = gameData.getString("name");
    }


    public String getName() {
        return name;
    }

    @Override
    public Layer.Destination getDestination() {
        return Layer.Destination.Details;
    }

    @Override
    public float getZ() {
        return ZLayer.DECAL.getValue();
    }

    @Override
    public GameData toGameData() {
        return gameData;
    }
}
