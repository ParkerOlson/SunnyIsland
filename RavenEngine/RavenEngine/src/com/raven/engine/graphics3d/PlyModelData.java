package com.raven.engine.graphics3d;

import java.io.File;

/**
 * Created by cookedbird on 11/13/17.
 */
public class PlyModelData extends ModelData {
    private String src;

    public PlyModelData(String src) {
        this.src = src;
    }

    void load() {
        PlyImporter.Import(new File(src), this);
    }
}
