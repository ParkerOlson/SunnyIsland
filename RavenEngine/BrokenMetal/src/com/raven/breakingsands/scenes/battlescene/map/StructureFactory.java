package com.raven.breakingsands.scenes.battlescene.map;

import com.raven.engine2d.database.GameData;
import com.raven.engine2d.util.Factory;
import com.raven.engine2d.util.math.Vector2i;
import com.sun.java.swing.plaf.windows.WindowsTreeUI;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StructureFactory extends Factory<Structure> {

    private Map map;

    private Structure connectedStructure;
    private boolean terminal;

    public StructureFactory(Map map) {
        this.map = map;
    }

    @Override
    public Structure getInstance() {

        if (connectedStructure == null) {
            return new Structure(map.getScene(), 0, 0);
        } else {
            // get random empty entrance
            // if nothing can connect to it, fail
            List<StructureEntrance> entrances = Arrays.stream(connectedStructure.getEntrances())
                    .filter(e -> !e.isConnected() && e.anyTerminal(terminal))
                    .collect(Collectors.toList());

//            System.out.println("OE " + entrances.size());

            int eCount = entrances.size();
            StructureEntrance connectedEntrance = entrances.get(map.getScene().getRandom().nextInt(eCount));
//            System.out.println("CE " + connectedEntrance.getName());
//            System.out.println("PS " + connectedEntrance.getPotentialStructures().size());

            // get potential structs
            List<PotentialStructure> potentialStructures = connectedEntrance.getPotentialStructures().stream()
                    .filter(ps -> ps.isTerminal() == terminal)
                    .collect(Collectors.toList());

//            System.out.println("F " + potentialStructures.size());

            while (potentialStructures.size() > 0) {

                // get and remove potential
                int psCount = potentialStructures.size();
                PotentialStructure potentialStructure = potentialStructures.get(map.getScene().getRandom().nextInt(psCount));
                potentialStructures.remove(potentialStructure);
                connectedEntrance.getPotentialStructures().remove(potentialStructure);

                GameData gdStructure = potentialStructure.getStructure();
                GameData gdEntrance = potentialStructure.getEntrance();


                // skip if it isn't a valid side
                // TODO move to potential structure generation
                switch (gdEntrance.getInteger("side")) {
                    case 0:
                        if (connectedEntrance.getSide() != 2) {
//                            System.out.println("SIDE 0 " + connectedEntrance.getName());
                            continue;
                        }
                        break;
                    case 1:
                        if (connectedEntrance.getSide() != 3) {
//                            System.out.println("SIDE 1 " + connectedEntrance.getName());
                            continue;
                        }
                        break;
                    case 2:
                        if (connectedEntrance.getSide() != 0) {
//                            System.out.println("SIDE 2 " + connectedEntrance.getName());
                            continue;
                        }
                        break;
                    case 3:
                        if (connectedEntrance.getSide() != 1) {
//                            System.out.println("SIDE 3 " + connectedEntrance.getName());
                            continue;
                        }
                        break;
                }


                Vector2i conPos = StructureEntrance.getEntrancePosition(
                        connectedEntrance.getSide(),
                        connectedEntrance.getLocation(),
                        connectedStructure.getWidth(),
                        connectedStructure.getHeight());

                Vector2i gdPos = StructureEntrance.getEntrancePosition(
                        gdEntrance.getInteger("side"),
                        gdEntrance.getInteger("location"),
                        gdStructure.getInteger("width"),
                        gdStructure.getInteger("height"));

                int len = connectedEntrance.getLength();

                Vector2i offset = StructureEntrance.getEntranceOffset(
                        connectedEntrance.getSide(),
                        len,
                        conPos,
                        gdPos);

                offset.x += connectedStructure.getMapX();
                offset.y += connectedStructure.getMapY();

                Structure structureToAdd = new Structure(
                        map.getScene(),
                        gdStructure,
                        gdEntrance,
                        offset.x, offset.y);

                // Check collision
                List<Structure> structures = map.getStructures();

                boolean safe = true;

                for (Structure structure : structures) {
                    if (structureToAdd.overlaps(structure)) {
                        safe = false;
                        break;
                    }
                }

                if (!safe) {
//                    System.out.println("NS " + structureToAdd.getName());
                    continue;
                }

                // check if entrances match
                for (Structure structure : structures) {
                    structureToAdd.tryConnect(structure);
                }

                return structureToAdd;

            }
        }

        return null;
    }

    @Override
    public void clear() {
        connectedStructure = null;
        terminal = false;
    }

    public void setConnection(Structure s) {
        connectedStructure = s;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }
}
