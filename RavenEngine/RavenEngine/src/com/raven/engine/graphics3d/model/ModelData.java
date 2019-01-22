package com.raven.engine.graphics3d.model;

import com.raven.engine.graphics3d.model.animation.Animation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cookedbird on 5/8/17.
 */
public class ModelData {
    private List<VertexData> vertexList = new ArrayList<>();
    private ModelReference modelReference;

    public List<VertexData> getVertexData() {
        return vertexList;
    }

    public void addVertex(VertexData vertexData) {
        vertexList.add(vertexData);
    }

    public void setModelReference(ModelReference modelReference) {
        this.modelReference = modelReference;

        // delete the list
//        vertexList.clear();
    }

    public ModelReference getModelReference() {
        // If this has a null reference
        // then the ModelData probably wasn't loaded to the gpu
        return modelReference;
    }

    public void load() {

    }

    public void release() {
        modelReference.release();
    }
}
