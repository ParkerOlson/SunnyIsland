package com.raven.engine.graphics3d.model;

import java.io.File;

/**
 * Created by cookedbird on 11/13/17.
 */
public class PlyModelData extends ModelData {
    private String src;
    private boolean loaded = false;

    public PlyModelData(String src) {
        this.src = src;
    }

    public void load() {
        if (!loaded) {
            PlyImporter.Import(new File(src), this);

            loaded = true;
        }
    }
}
