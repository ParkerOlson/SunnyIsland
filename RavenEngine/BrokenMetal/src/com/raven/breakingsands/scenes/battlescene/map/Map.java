package com.raven.breakingsands.scenes.battlescene.map;

import com.raven.breakingsands.ZLayer;
import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.breakingsands.scenes.battlescene.decal.WallFactory;
import com.raven.breakingsands.scenes.battlescene.pawn.Pawn;
import com.raven.engine2d.Game;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDataList;
import com.raven.engine2d.database.GameDatable;
import com.raven.engine2d.scene.Layer;
import com.raven.engine2d.util.pathfinding.Path;
import com.raven.engine2d.util.pathfinding.PathFinder;
import com.raven.engine2d.worldobject.WorldObject;
import org.lwjgl.system.CallbackI;

import java.util.*;
import java.util.stream.Collectors;

public class Map extends WorldObject<BattleScene, BattleScene, WorldObject>
        implements GameDatable {

    private List<Structure> structures = new ArrayList<>();
    private List<Terrain> terrain = new ArrayList<>();

    private Structure firstStructure;

    private int size;
    private int i = 0;
    private int tries = 0;

    public Map(BattleScene scene, int level) {
        super(scene);

        this.size = Math.min(level / 4 + 1, 4);
    }

    public Map(BattleScene scene, GameData gameData) {
        super(scene);

        for (GameData gdStructure : gameData.getList("structures")) {
            Structure structure = new Structure(scene, gdStructure);
            addStructure(structure);
        }
    }

    public GameData toGameData() {
        HashMap<String, GameData> map = new HashMap<>();
        map.put("structures", new GameDataList(structures).toGameData());
        return new GameData(map);
    }

    public void generate() {
        while (structures.size() == 0 || !structures.contains(firstStructure) || terrain.stream().noneMatch(Terrain::isSpawn)) {

            removeAllChildren();
            structures.clear();
            terrain.clear();
            firstStructure = null;

            i = size;
            startGeneration();
            removeIslands();
        }

        addWalls();

//        System.out.println(tries + ", " + size + ", " + structures.size());

        // setText all connections

    }

    private void startGeneration() {
        System.out.println("Starting Map Gen");

        StructureFactory structureFactory = new StructureFactory(this);

        Structure s = firstStructure = structureFactory.getInstance();
        addStructure(s);

        do {
            i = size - structures.size() + 2;
            tries++;
        } while (generate(structureFactory));
    }

    private boolean generate(StructureFactory structureFactory) {
        boolean t = structures.stream().filter(s -> !s.isTerminal()).count() - 1 >= size;

        // find a structure with open connections
        List<Structure> openStructures = this.structures.stream()
                .filter(st -> Arrays.stream(st.getEntrances()).anyMatch(e -> !e.isConnected() && e.anyTerminal(t)))
                .collect(Collectors.toList());

        int sCount = openStructures.size();

        if (sCount == 0) {
            openStructures = this.structures.stream()
                    .filter(st -> Arrays.stream(st.getEntrances()).anyMatch(e -> !e.isConnected() && e.anyTerminal(true)))
                    .collect(Collectors.toList());
//            System.out.println("None Left");;
            sCount = openStructures.size();

            if (sCount == 0) {
//            System.out.println("None Left");
                return false;
            }

            structureFactory.setTerminal(true);
        } else {
            structureFactory.setTerminal(t);
        }

        Structure buildFrom = openStructures.get(getScene().getRandom().nextInt(sCount));

        structureFactory.setConnection(buildFrom);
//        System.out.println("BF " + buildFrom.getName());

        Structure s = structureFactory.getInstance();

        if (s == null) {
            if (buildFrom == firstStructure) return false;
            removeStructure(buildFrom);
        } else {
            addStructure(s);
//            System.out.println("Add " + s.getName());
        }

        return true;
    }

    private void addStructure(Structure s) {
        structures.add(s);
        terrain.addAll(s.getTerrainList());
        addChild(s);
    }

    private void removeStructure(Structure s) {
        removeOnlyStructure(s);

        // remove all not connected to the first
        // get list of connected
        List<Structure> connected = firstStructure.getConnections();

        // remove the ones not there
        List<Structure> toRemove = new ArrayList<>(structures);
        toRemove.removeAll(connected);
        int removedCount = toRemove.size();

        for (Structure r : toRemove) {
            removeOnlyStructure(r);
        }

        redoConnections();
    }

    private void removeOnlyStructure(Structure s) {
        this.structures.remove(s);
        terrain.removeAll(s.getTerrainList());
        removeChild(s);
    }

    private void redoConnections() {
        // redo connections
        for (Structure toRedo : structures) {
            for (StructureEntrance se : toRedo.getEntrances()) {
                se.setConnected(null);
            }
        }

        structures.forEach(st -> structures.forEach(st::tryConnect));
    }

    private void removeIslands() {
        List<Terrain> toRemove = new ArrayList<>();

        Terrain start = terrain.stream().filter(Terrain::isStart).findFirst().get();

        PathFinder<Terrain, Terrain.PathFlag> terrainPathFinder = new PathFinder<>();

        terrain.forEach(t -> {
            Path<Terrain> path = terrainPathFinder.findTarget(t, start);
            if (path == null) {
                toRemove.add(t);
            }
        });

        terrain.removeAll(toRemove);
        removeChildren(toRemove);
        toRemove.forEach(t -> t.getParent().removeTerrain(t));
    }

    private void addWalls() {
        WallFactory f = new WallFactory(getScene());
        for (Terrain terrain : terrain) {
            f.clear();

            Optional<Terrain> north = get(terrain.getMapX(), terrain.getMapY() + 1);
            Optional<Terrain> west = get(terrain.getMapX() - 1, terrain.getMapY());
            Optional<Terrain> above = get(terrain.getMapX() - 1, terrain.getMapY() + 1);

            if (!north.isPresent() && !west.isPresent()) {
                f.addTypeRestriction("in");

                terrain.setWall(f.getInstance());
            } else if (!north.isPresent()) {
                f.addTypeRestriction("north");

                if (above.isPresent()) {
                    f.addTypeRestriction("corner");
                } else {
                    f.addTypeRestriction("wall");
                }

                terrain.setWall(f.getInstance());
            } else if (!west.isPresent()) {
                f.addTypeRestriction("west");

                if (above.isPresent()) {
                    f.addTypeRestriction("corner");
                } else {
                    f.addTypeRestriction("wall");
                }

                terrain.setWall(f.getInstance());
            } else if (!above.isPresent()) {
                f.addTypeRestriction("out");

                terrain.setWall(f.getInstance());
            }
        }
    }

    public Optional<Terrain> get(int x, int y) {
        return terrain.stream()
                .filter(t -> t.getMapX() == x && t.getMapY() == y).findFirst();
    }

    public List<Terrain> getTerrainList() {
        return terrain;
    }

    public void setPawn(Terrain t, Pawn p) {
        t.setPawn(p);
    }

    public void setState(Terrain.State state) {
        for (Terrain t : terrain) {
            t.setState(state);
        }
    }

    public List<Structure> getStructures() {
        return structures;
    }

    public Structure getFirstStructure() {
        return firstStructure;
    }

    @Override
    public Layer.Destination getDestination() {
        return Layer.Destination.Terrain;
    }

    @Override
    public float getZ() {
        return ZLayer.TERRAIN.getValue();
    }
}
