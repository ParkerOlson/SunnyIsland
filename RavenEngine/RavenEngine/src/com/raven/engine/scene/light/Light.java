package com.raven.engine.scene.light;

import com.raven.engine.graphics3d.shader.ShadowShader;
import com.raven.engine.util.Vector3f;

import java.nio.FloatBuffer;

/**
 * Created by cookedbird on 11/30/17.
 */
public abstract class Light {
    public final static int AMBIANT = 0, GLOBAL_DIRECTIONAL = 1;

    protected ShadowShader shadowShader;

    public abstract FloatBuffer toFloatBuffer();

    public abstract int getLightType();

    public ShadowShader getShadowShader() {
        return shadowShader;
    }
}
