package com.raven.engine2d.util.math;

public class Vector2i {
    public int x, y;

    public Vector2i() {

    }

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f add(Vector2f other, Vector2f out) {
        out.x = this.x + other.x;
        out.y = this.y + other.y;

        return out;
    }

    public Vector2f subtract(Vector2f other, Vector2f out) {
        out.x = this.x - other.x;
        out.y = this.y - other.y;

        return out;
    }

    @Override
    public String toString() {
        return "" + x + " " + y;
    }
}