package com.raven.engine2d.util.math;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

/**
 * Created by cookedbird on 11/22/17.
 */
public class Plane {
    public float a, b, c, d;
    FloatBuffer buffer = BufferUtils.createFloatBuffer(4);

    public Plane(float a, float b, float c, float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public FloatBuffer toBuffer() {
        buffer.put(a).put(b).put(c).put(d);
        buffer.flip();

        return buffer;
    }
 }
