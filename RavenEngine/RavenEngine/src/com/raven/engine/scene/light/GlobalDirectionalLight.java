package com.raven.engine.scene.light;

import com.raven.engine.graphics3d.shader.ShadowShader;
import com.raven.engine.util.Matrix4f;
import com.raven.engine.util.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

/**
 * Created by cookedbird on 11/30/17.
 */
public class GlobalDirectionalLight extends Light {
    public Vector3f origin = new Vector3f();

    private Matrix4f shadowViewMatrix;
    private Matrix4f shadowProjectionMatrix;
    public Vector3f color = new Vector3f();
    public float intensity = 1f;
    private Vector3f direction = new Vector3f();
    public float length = 1f;
    private Vector3f ambient = new Vector3f(.1f, .1f, .1f);
    public float shadowTransparency = 1.0f;

    public GlobalDirectionalLight() {
        this(new Vector3f(1, 1, 0), .5f, new Vector3f(0, -1, 0), 25f);
    }

    public GlobalDirectionalLight(Vector3f color, float intensity, Vector3f direction, float size) {
        this.color = color;
        this.intensity = intensity;
        this.direction = direction;

//        shadowProjectionMatrix = Matrix4f.orthographic(-size, size, -size, size, 1f, 60f);
        shadowProjectionMatrix = new Matrix4f();

//        shadowViewMatrix = new Matrix4f().translate(0, 0, -30);
        shadowViewMatrix = new Matrix4f();

        Matrix4f.direction(direction, null);

        shadowShader = new ShadowShader();
    }

    FloatBuffer lBuffer = BufferUtils.createFloatBuffer(16 * 2 + 4 * 3);

    @Override
    public FloatBuffer toFloatBuffer() {
        shadowViewMatrix.toBuffer(lBuffer);
        shadowProjectionMatrix.toBuffer(lBuffer);
        color.toBuffer(lBuffer);
        lBuffer.put(intensity);
        direction.toBuffer(lBuffer);
        lBuffer.put(length);
        ambient.toBuffer(lBuffer);
        lBuffer.put(shadowTransparency);
        lBuffer.flip();
        return lBuffer;
    }

    @Override
    public int getLightType() {
        return Light.GLOBAL_DIRECTIONAL;
    }

    public Vector3f getDirection() {
        return direction;
    }

    // has 'memory leak'
    public void setDirection(Vector3f direction) {
        this.direction = direction.normalize();

        shadowViewMatrix.shadowSkew(
                this.direction,
                origin,
                20f, 4f);

        length = Matrix4f.shadowSkewLength(this.direction, 20f, 4f);
    }
}

