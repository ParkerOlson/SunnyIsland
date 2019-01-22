package com.raven.engine.graphics3d.model;

import java.io.File;

public class RavModelData extends ModelData {
    private String src;
    private boolean loaded = false;

    public RavModelData(String src) {
        this.src = src;
    }

    public void load() {
        if (!loaded) {
            RavImporter.Import(new File(src), this);

            loaded = true;
        }
    }
}