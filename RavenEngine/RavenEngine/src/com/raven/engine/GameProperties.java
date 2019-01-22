package com.raven.engine;

/**
 * A class to access most the general properties of the
 * engine or game
 * Created by cookedbird on 11/15/17.
 */
public class GameProperties {
    private static int multiSample = 0, waterQuality = 1;
    private static boolean reflectTerrain, reflectObjects, supportsOpenGL4 = true;

    public static int getScreenWidth() {
        return 1920;
    }

    public static int getScreenHeight() {
        return 1080;
    }

    public static String getMainDirectory() {
        return GameEngine.getEngine().getGame().getMainDirectory();
    }

    public static void setMultisampleCount(int multiSample) {
        GameProperties.multiSample = multiSample;
    }

    public static int getMultisampleCount() {
        return multiSample;
    }

    public static void setWaterQuality(int waterQuality) {
        GameProperties.waterQuality = waterQuality;
    }
    public static int getWaterQuality() {
        return waterQuality;
    }

    public static void setReflectTerrain(boolean reflectTerrain) {
        GameProperties.reflectTerrain = reflectTerrain;
    }

    public static boolean getReflectTerrain() {
        return reflectTerrain;
    }

    public static void setReflectObjects(boolean reflectObjects) {
        GameProperties.reflectObjects = reflectObjects;
    }

    public static boolean getReflectObjects() {
        return reflectObjects;
    }

    public static void setSupportsOpenGL4(boolean supportsOpenGL4) {
        GameProperties.supportsOpenGL4 = supportsOpenGL4;
    }

    public static boolean supportsOpenGL4() {
        return supportsOpenGL4;
    }

    private GameProperties() {}

}
