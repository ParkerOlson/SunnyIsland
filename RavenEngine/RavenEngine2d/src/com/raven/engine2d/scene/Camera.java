package com.raven.engine2d.scene;

import com.raven.engine2d.GameProperties;
import com.raven.engine2d.util.math.Matrix4f;

/**
 * Created by cookedbird on 11/15/17.
 */
public class Camera {
    float x, y, zoom = -30f, zoomMin = -2f, zoomMax = -50f, xr, yr = 40, yrMin = 25f, yrMax = 85;
    float xs = x, ys = y, zooms = zoom, xrs = xr, yrs = yr, near = 2f, far = 200f, height = 0f;
    private boolean interactable = true;

    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f inverseProjectionViewMatrix = new Matrix4f();

    public Camera() {
        updateProjectionMatrix();
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

    public void setPosition(float x, float y) {
        this.x = -x;
        this.y = -y;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    private Matrix4f tempMat = new Matrix4f();
    private void updateViewMatrix() {
        // view
        viewMatrix.identity();
        tempMat.identity();

        viewMatrix.translate(0, 0, zooms, tempMat);
        tempMat.rotate(yrs, 1f, 0f, 0f, viewMatrix);

        viewMatrix.rotate(xrs, 0f, 1f, 0f, tempMat);
        tempMat.translate(xs, height, ys, viewMatrix);

        // IPV
        inverseProjectionViewMatrix.identity();
        tempMat.identity();

        tempMat.multiply(projectionMatrix, inverseProjectionViewMatrix);
        inverseProjectionViewMatrix.multiply(viewMatrix, tempMat);
        tempMat.invert(inverseProjectionViewMatrix);
    }

    private void updateProjectionMatrix() {
        Matrix4f.perspective(60f, ((float) GameProperties.getDisplayHeight())
                / ((float) GameProperties.getDisplayHeight()), near, far, projectionMatrix);
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

    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
    }

    public boolean isInteractable() {
        return interactable;
    }
    
    public void setZoomMin(float zoomMin) {
        this.zoomMin = zoomMin;
    }

    public void setZoomMax(float zoomMax) {
        this.zoomMax = zoomMax;
    }

    public void setZoom(float zoom, boolean smooth) {
        if (smooth) {
            this.zoom = Math.min(zoomMin, Math.max(zoomMax, zoom));
        } else {
            this.zoom = this.zooms = Math.min(zoomMin, Math.max(zoomMax, zoom));
        }
    }

    public void setNear(float near) {
        this.near = near;

        updateProjectionMatrix();
    }

    public void setFar(float far) {
        this.far = far;

        updateProjectionMatrix();
    }

    public void setYRotation(float yr) {
        this.yr = this.yrs = yr;
    }
}
