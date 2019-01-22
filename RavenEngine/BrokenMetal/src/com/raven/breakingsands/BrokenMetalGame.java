package com.raven.breakingsands;

import com.raven.breakingsands.scenes.battlescene.BattleScene;
import com.raven.breakingsands.scenes.splashscreenscene.SplashScreenScene;
import com.raven.engine2d.Game;
import com.raven.engine2d.GameEngine;
import com.raven.engine2d.GameProperties;
import com.raven.engine2d.database.GameData;
import com.raven.engine2d.database.GameDataList;
import com.raven.engine2d.database.GameDataTable;
import com.raven.engine2d.database.GameDatable;
import com.raven.engine2d.scene.Scene;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class BrokenMetalGame extends Game<BrokenMetalGame> {

    private static String mainDirectory = "BrokenMetal";
    private int gameId;

    private GameDataTable gdtToSave = new GameDataTable("current_save");

    public static void main(String[] args) {
        GameEngine.Launch(new BrokenMetalGame());
        System.out.println("Lunched");
    }

    public BrokenMetalGame() {
        GameData settings = loadGameData("settings").get(0);

        settings.ifHas("music", s -> GameProperties.setMusicVolume(s.asInteger()));
        settings.ifHas("sfx", s -> GameProperties.setSFXVolume(s.asInteger()));
        settings.ifHas("width", s -> GameProperties.setDisplayWidth(s.asInteger()));
        settings.ifHas("height", s -> GameProperties.setDisplayHeight(s.asInteger()));
        settings.ifHas("scaling", s -> GameProperties.setScaling(s.asInteger()));
        settings.ifHas("vsync", s -> GameProperties.setVSync(s.asBoolean()));
        settings.ifHas("win_mode", s -> GameProperties.setWindowMode(s.asInteger()));
    }

    @Override
    public void setup() {
        playSong("hf.wav");
    }

    @Override
    public void breakdown() {
        setRunning(false);
    }

    @Override
    public Scene loadInitialScene() {
        return new SplashScreenScene(this);
    }

    @Override
    public String getTitle() {
        return "Broken Metal";
    }

    @Override
    public String getMainDirectory() {
        return mainDirectory;
    }

    @Override
    public boolean saveGame() {
        return saveGame(true);
    }

    public boolean saveGame(boolean clearSaves) {
        Scene scene = getCurrentScene();

        System.out.println("Clear Saves: " + clearSaves);

        if (scene instanceof BattleScene) {
            saveRecords();

            if (clearSaves) {
                this.gdtToSave.clear();
            }

            this.gdtToSave.add(((BattleScene) scene).toGameData());
            GameDataTable gdtToSave = new GameDataTable(this.gdtToSave.getName(), (List<GameData>) this.gdtToSave);

            saveDataTablesThreaded(Collections.singletonList(gdtToSave));
            return true;
        } else {
            System.out.println("Saving Failed");
            return false;
        }
    }

    public void deleteSaveGame() {
        deleteDataTables("save");
    }

    @Override
    public boolean loadGame() {

        gdtToSave = new GameDataTable("current_save", loadSavedGameData("current_save"));
        if (gdtToSave != null && gdtToSave.size() > 0) {
            prepTransitionScene(new BattleScene(this, gdtToSave.get(gdtToSave.size() - 1)));
            return true;
        } else {
            System.out.println("Loading Failed");
            return false;
        }
    }

    public void saveSettings() {
        HashMap<String, GameData> map = new HashMap<>();
        map.put("music", new GameData(GameProperties.getMusicVolume()));
        map.put("sfx", new GameData(GameProperties.getSFXVolume()));
        map.put("width", new GameData(GameProperties.getDisplayWidth()));
        map.put("height", new GameData(GameProperties.getDisplayHeight()));
        map.put("scaling", new GameData(GameProperties.getScaling()));
        map.put("vsync", new GameData(GameProperties.getVSync()));
        map.put("win_mode", new GameData(GameProperties.getWindowMode()));

        GameDataTable settings = new GameDataTable("settings");
        settings.add(new GameData(map));

        saveDataTableThreaded(settings);
    }

    public GameDataList loadRecords() {
        return loadGameData("records");
    }

    private void saveRecords() {
        BattleScene scene = ((BattleScene) this.getCurrentScene());
        GameDataList records = loadRecords();

        Optional<GameData> existing = records.stream()
                .filter(d -> d.getInteger("id") == gameId)
                .findFirst();

        if (existing.isPresent()) {
            GameData data = existing.get();

            Map<String, GameData> map = data.asMap();
            map.put("floor", new GameData(scene.getDifficulty()));
            DateFormat df = new SimpleDateFormat("M/dd/yyyy");
            map.put("date", new GameData(df.format(new Date())));
        } else {
            Map<String, GameData> map = new HashMap<>();
            map.put("floor", new GameData(scene.getDifficulty()));
            DateFormat df = new SimpleDateFormat("M/dd/yyyy");
            map.put("date", new GameData(df.format(new Date())));
            map.put("id", new GameData(gameId));
            records.add(new GameData(map));
        }


        this.saveDataTableThreaded(new GameDataTable("records", (List<? extends GameDatable>) records));
    }

    public void setGameID(int id) {
        gameId = id;
    }

    public int getGameID() {
        return gameId;
    }

    public GameDataTable getSaves() {
        return gdtToSave;
    }
}
