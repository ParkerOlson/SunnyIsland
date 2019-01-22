package com.raven.engine.database;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by cookedbird on 11/15/17.
 */
public class GameDataList extends ArrayList<GameData> {
    static Random rand = new Random();

    public GameData queryFirst(GameDataQuery query) {
        for (GameData row : this) {
            if (query.matches(row)) {
                return row;
            }
        }
        return null;
    }

    public GameDataList queryAll(GameDataQuery query) {
        GameDataList l = new GameDataList();

        for (GameData row : this) {
            if (query.matches(row)) {
                l.add(row);
            }
        }
        return l;
    }

    public GameData getRandom() {
        return get((int)(rand.nextFloat() * size()));
    }

    public GameData queryRandom(GameDataQuery gameDataQuery) {
        return queryAll(gameDataQuery).getRandom();
    }
}
