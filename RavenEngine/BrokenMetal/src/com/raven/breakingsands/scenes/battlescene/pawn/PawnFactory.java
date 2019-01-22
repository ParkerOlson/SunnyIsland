package com.raven.breakingsands.scenes.battlescene.pawn;

import com.raven.engine2d.database.GameDataList;
import com.raven.engine2d.util.Factory;
import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDataQuery;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PawnFactory extends Factory<Pawn> {
    private BattleScene scene;
    private String name = null;
    private Integer team = null;
    private Integer maxXp = null;

    public PawnFactory(BattleScene scene) {
        this.scene = scene;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public void setMaxXp(int maxXp) {
        this.maxXp = maxXp;
    }

    public Pawn getInstance() {
        Stream<GameData> stream = Pawn.getDataList().stream().filter(d -> {
            boolean found = true;

            if (name != null) {
                found &= d.getString("name").equals(name);
            }
            if (team != null) {
                found &= d.getInteger("team") == team;
            }
            if (maxXp != null) {
                if (!d.has("xp_gain"))
                    return false;
                found &= d.getInteger("xp_gain") <= maxXp;
            }

            return found;
        });

        if (maxXp != null) {
            Optional<GameData> o = stream.max(Comparator.comparingInt(a -> a.getInteger("xp_gain")));

            return o.map(gameData -> new Pawn(scene, gameData)).orElse(null);
        }

        GameDataList gameDataList = stream.collect(Collectors.toCollection(GameDataList::new));

        return new Pawn(scene, gameDataList.getRandom(scene.getRandom()));
    }

    public void clear() {
        name = null;
        team = null;
        maxXp = null;
    }
}
