package com.raven.breakingsands.scenes.battlescene.map;

import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.engine2d.Game;
import com.raven.engine2d.GameEngine;
import com.raven.engine2d.database.*;
import com.raven.engine2d.scene.Layer;
import com.raven.engine2d.util.math.Vector2i;
import com.raven.engine2d.worldobject.WorldObject;

import java.util.*;

public class Structure extends WorldObject<BattleScene, Map, WorldObject>
        implements GameDatable {

    private int width = 3, height = 3;
    private int x, y;
    private boolean terminal;

    private String name;

    private StructureEntrance[] entrances;

    private List<Terrain> terrainList = new ArrayList<>();

    public Structure(BattleScene scene, int x, int y) {
        this(scene,
                GameDatabase.all("structure").stream()
                        .filter(s -> s.getString("name").equals("center"))
                        .findAny()
                        .get(),
                null,
                x, y);
    }

    public Structure(BattleScene scene, GameData gameData, GameData connected, int x, int y) {
        super(scene);

        this.x = x;
        this.y = y;
        this.setX(x);
        this.setY(y);

        gameData.ifHas("terminal", t -> terminal = t.asBoolean());

        width = gameData.getInteger("width");
        height = gameData.getInteger("height");

        name = gameData.getString("name");

        boolean variations = gameData.has("variations") && gameData.getBoolean("variations");
        GameDataList gdtList = gameData.getList("terrain");

        TerrainFactory tf = new TerrainFactory(this);

        if (variations) {
            gdtList = gdtList.getRandom(scene.getRandom()).asList();
        }

        for (GameData gdt : gdtList) {
            tf.setPropertyData(gdt);

            Terrain t = tf.getInstance();
            addChild(t);
            terrainList.add(t);
        }

        GameDataList gdcList = gameData.getList("entrance");

        entrances = new StructureEntrance[gdcList.size()];

        for (int i = 0; i < gdcList.size(); i++) {
            entrances[i] = new StructureEntrance(this, gdcList.get(i));
        }

    }

    public Structure(BattleScene scene, GameData gdStructure) {
        super(scene);

        this.x = gdStructure.getInteger("x");
        this.y = gdStructure.getInteger("y");
        this.setX(x);
        this.setY(y);

        width = gdStructure.getInteger("width");
        height = gdStructure.getInteger("height");

        name = gdStructure.getString("name");

        for (GameData gdTerrain : gdStructure.getList("terrain")) {
            Terrain terrain = new Terrain(scene, this, gdTerrain);
            terrainList.add(terrain);
            addChild(terrain);
        }
    }

    @Override
    public GameData toGameData() {
        HashMap<String, GameData> map = new HashMap<>();

        map.put("x", new GameData(x));
        map.put("y", new GameData(y));
        map.put("width", new GameData(width));
        map.put("height", new GameData(height));
        map.put("name", new GameData(name));
        map.put("terrain", new GameDataList(terrainList).toGameData());

        return new GameData(map);
    }

    public StructureEntrance[] getEntrances() {
        return entrances;
    }

    public List<Terrain> getTerrainList() {
        return terrainList;
    }

    public void removeTerrain(Terrain t) {
        if (terrainList.remove(t)) {
            removeChild(t);
        }
    }

    public String getName() {
        return name;
    }

    public int getMapX() {
        return x;
    }

    public int getMapY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean overlaps(Structure other) {
        return overlaps(x, y, width, height,
                other.x, other.y, other.width, other.height);
    }

    public static boolean overlaps(int x, int y, int width, int height,
                                   int x2, int y2, int width2, int height2) {
        return
                x2 + width2 > x &&
                        y2 + height2 > y &&
                        x2 < x + width &&
                        y2 < y + height;
    }

    public void tryConnect(Structure other) {

        Arrays.stream(entrances)
                .filter(e -> !e.isConnected())
                .forEach(e -> Arrays.stream(other.entrances)
                        .filter(o -> !o.isConnected())
                        .forEach(e::tryConnect));
    }

    public List<Structure> getConnections() {
        List<Structure> connections = new ArrayList<>();
        return getConnections(connections);
    }

    public List<Structure> getConnections(List<Structure> connections) {

        connections.add(this);

        for (StructureEntrance entrance : entrances) {
            if (entrance.getConnection() != null) {
                Structure s = entrance.getConnection().getStructure();

                // if part of the map structures and not in the list
                // add to list
                if (s.getParent().getStructures().contains(s) &&
                        !connections.contains(s)) {
                    s.getConnections(connections);
                }
            }
        }

        return connections;
    }

    @Override
    public Layer.Destination getDestination() {
        return Layer.Destination.Terrain;
    }

    @Override
    public float getZ() {
        return 0f;
    }

    public boolean isTerminal() {
        return terminal;
    }
}
