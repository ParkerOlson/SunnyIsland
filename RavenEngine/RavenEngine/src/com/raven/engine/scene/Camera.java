package com.raven.engine.scene;

import com.raven.engine.GameProperties;
import com.raven.engine.util.Matrix4f;
import com.raven.engine.util.Vector3f;

/**
 * Created by cookedbird on 11/15/17.
 */
public class Camera {
    float x, y, zoom = -30f, zoomMin = -25f, zoomMax = -50f, xr, yr = 40, yrMin = 25f, yrMax = 85;
    float xs = x, ys = y, zooms = zoom, xrs = xr, yrs = yr;

    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix;
    private Matrix4f inverseProjectionViewMatrix = new Matrix4f();

    public Camera() {
        projectionMatrix = Matrix4f.perspective(60f, ((float) GameProperties.getScreenWidth())
                / ((float) GameProperties.getScreenHeight()), 2f, 200.0f);

        updateViewMatrix();
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getInverseProjectionViewMatrix() {
        return inverseProjectionViewMatrix;
    }

    public void zoom(double yoffset) {
        zoom += yoffset * 3f;
        zoom = Math.min(zoomMin, Math.max(zoomMax, zoom));
    }

    public void rotate(double x, double y) {
        xr += x * .2f;
        yr += y * .2f;
        yr = Math.max(yrMin, Math.min(yrMax, yr));
    }

    public void move(double x, double y) {
        this.x += (x * +Math.cos(xrs / 180.0 * Math.PI) +
                   y * -Math.sin(xrs / 180.0 * Math.PI)) *
                -zoom * .001f;
        this.y += (x * +Math.sin(xrs / 180.0 * Math.PI) +
                   y * +Math.cos(xrs / 180.0 * Math.PI)) *
                -zoom * .001f;
    }

    // has 'memory leak'
    private void updateViewMatrix() {
        // view
        viewMatrix.identity();

        viewMatrix.translate(0, 0, zooms);
        viewMatrix.rotate(yrs, 1f, 0f, 0f);

        viewMatrix.rotate(xrs, 0f, 1f, 0f);
        viewMatrix.translate(xs, 0, ys);

        // IPV
        inverseProjectionViewMatrix.identity();
        inverseProjectionViewMatrix = inverseProjectionViewMatrix.multiply(projectionMatrix).multiply(viewMatrix);
        inverseProjectionViewMatrix.invert();
    }

    public void update(float deltaTime) {
        // smooth motion
        float deltaCorrection = .004f;
        zooms += (zoom - zooms) * 1f * deltaTime * deltaCorrection;
        xs += (x - xs) * 3f * deltaTime * deltaCorrection;
        ys += (y - ys) * 3f * deltaTime * deltaCorrection;
        xrs += (xr - xrs) * 3f * deltaTime * deltaCorrection;
        yrs += (yr - yrs) * 3f * deltaTime * deltaCorrection;

        updateViewMatrix();
    }

    public float getPitch() {
        return yrs;
    }
}
