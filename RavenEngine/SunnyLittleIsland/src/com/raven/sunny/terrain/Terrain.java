package com.raven.sunny.terrain;

import com.raven.engine.graphics3d.ModelData;
import com.raven.engine.scene.Scene;
import com.raven.engine.worldobject.WorldObject;

/**
 * Created by cookedbird on 5/20/17.
 */
public class Terrain extends WorldObject {
    TerrainData[][] terrainData;
    ModelData model;

    public Terrain(Scene scene, ModelData model, TerrainData[][] terrainData) {
        super(scene, model);

        this.model = model;
        this.terrainData = terrainData;

        for (TerrainData[] tds : terrainData) {
            for (TerrainData td : tds) {
                td.setTerrain(this);
            }
        }
    }

    public ModelData getModelData() {
        return model;
    }
}
