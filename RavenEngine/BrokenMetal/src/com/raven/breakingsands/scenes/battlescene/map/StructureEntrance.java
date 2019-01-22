package com.raven.breakingsands.scenes.battlescene.map;

import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDataList;
import com.raven.engine2d.database.GameDatabase;
import com.raven.engine2d.util.math.Vector2i;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StructureEntrance {

    private int side, location, length;
    private String name;

    private List<PotentialStructure> potentialStructures = new LinkedList<>();
    private StructureEntrance connection;
    private Structure structure;

    public static Vector2i getEntrancePosition(int side, int location, int width, int height) {
        Vector2i pos = new Vector2i();

        switch (side) {
            case 0:
                pos.x = location;
                pos.y = height;
                break;
            case 1:
                pos.x = width;
                pos.y = height - location;
                break;
            case 2:
                pos.x = width - location;
                pos.y = 0;
                break;
            case 3:
                pos.x = 0;
                pos.y = location;
                break;
        }

        return pos;
    }

    public static Vector2i getEntranceOffset(int side, int len, Vector2i a, Vector2i b) {
        Vector2i offset = new Vector2i();

        switch (side) {
            case 0:
                offset.x = a.x + len - b.x;
                offset.y = a.y - b.y;
                break;
            case 1:
                offset.x = a.x - b.x;
                offset.y = a.y - len - b.y;
                break;
            case 2:
                offset.x = a.x - len - b.x;
                offset.y = a.y - b.y;
                break;
            case 3:
                offset.x = a.x - b.x;
                offset.y = a.y + len - b.y;
                break;
        }

        return offset;
    }

    public static boolean isConnected(int side, int len,
                                      Vector2i a, Vector2i b) {
        switch (side) {
            case 0:
                return a.x + len == b.x && a.y == b.y;
            case 1:
                return a.x == b.x && a.y - len == b.y;
            case 2:
                return a.x - len == b.x && a.y == b.y;
            case 3:
                return a.x == b.x && a.y + len == b.y;
        }

        return false;
    }

    public StructureEntrance(Structure structure, GameData data) {
        this.structure = structure;

        side = data.getInteger("side");
        location = data.getInteger("location");
        length = data.getInteger("length");
        name = data.getString("name");

        // Create Potential Structures
        createPotentialStructures();
    }

    private void createPotentialStructures() {
        List<GameData> potential = new ArrayList<>();

        GameDataList connectionsTable = GameDatabase.all("connections");

        connectionsTable.stream()
                .filter(row -> row.getList("a").stream().anyMatch(a ->
                        a.getString("name").equals(structure.getName()) &&
                                a.getString("entrance").equals(name)
                ))
                .map(connections -> connections.getList("b"))
                .forEach(potential::addAll);

        connectionsTable.stream()
                .filter(row -> row.getList("b").stream().anyMatch(b ->
                        b.getString("name").equals(structure.getName()) &&
                                b.getString("entrance").equals(name)
                ))
                .map(connections -> connections.getList("a"))
                .forEach(potential::addAll);

        for (GameData connection : potential) {
            PotentialStructure ps = new PotentialStructure(this, connection);

            if (ps.getStructure() != null && ps.getEntrance() != null) {
                potentialStructures.add(ps);
            }
        }
    }

    public int getLength() {
        return length;
    }

    public int getSide() {
        return side;
    }

    public int getLocation() {
        return location;
    }

    public void setConnected(StructureEntrance connection) {
        this.connection = connection;
    }

    public boolean isConnected() {
        return connection != null;
    }

    public static boolean connectionContainsBoth(StructureEntrance sa, StructureEntrance sb) {
        GameDataList connections = GameDatabase.all("connections");

        return connections.stream().anyMatch(con ->
                (con.getList("a").stream().anyMatch(a -> entranceMatch(a, sa)) &&
                        con.getList("b").stream().anyMatch(b -> entranceMatch(b, sb))) ||
                        (con.getList("b").stream().anyMatch(b -> entranceMatch(b, sa)) &&
                                con.getList("a").stream().anyMatch(a -> entranceMatch(a, sb))));
    }

    private static boolean entranceMatch(GameData gdEntrance, StructureEntrance entrance) {
        return
                gdEntrance.getString("name").equals(entrance.structure.getName()) &&
                        gdEntrance.getString("entrance").equals(entrance.getName());
    }

    public void tryConnect(StructureEntrance other) {
        if (this.getLength() == this.getLength()) {
            if (connectionContainsBoth(this, other)) {

                switch (side) {
                    case 0:
                        if (other.side != 2) return;
                        break;
                    case 1:
                        if (other.side != 3) return;
                        break;
                    case 2:
                        if (other.side != 0) return;
                        break;
                    case 3:
                        if (other.side != 1) return;
                        break;
                }

                Vector2i conPos = StructureEntrance.getEntrancePosition(
                        side,
                        location,
                        structure.getWidth(),
                        structure.getHeight());

                conPos.x += structure.getMapX();
                conPos.y += structure.getMapY();

                Vector2i gdPos = StructureEntrance.getEntrancePosition(
                        other.side,
                        other.location,
                        other.structure.getWidth(),
                        other.structure.getHeight());

                gdPos.x += other.structure.getMapX();
                gdPos.y += other.structure.getMapY();

                if (StructureEntrance.isConnected(
                        this.getSide(),
                        this.getLength(),
                        conPos, gdPos)) {

                    this.setConnected(other);
                    other.setConnected(this);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public StructureEntrance getConnection() {
        return connection;
    }

    public Structure getStructure() {
        return structure;
    }

    public List<PotentialStructure> getPotentialStructures() {
        return potentialStructures;
    }

    public boolean anyTerminal(boolean terminal) {
        return potentialStructures.stream().anyMatch(p -> p.isTerminal() == terminal);
    }
}
