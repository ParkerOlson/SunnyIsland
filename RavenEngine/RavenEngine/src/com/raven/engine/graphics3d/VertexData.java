package com.raven.engine.graphics3d;

/**
 * Created by cookedbird on 11/8/17.
 */
public class VertexData {
    public Float x, y, z, nx, ny, nz, red, green, blue;

    public VertexData(String[] data) {
        x = Float.parseFloat(data[0]);
        y = Float.parseFloat(data[1]);
        z = Float.parseFloat(data[2]);
        nx = Float.parseFloat(data[3]);
        ny = Float.parseFloat(data[4]);
        nz = Float.parseFloat(data[5]);
        red = Integer.parseInt(data[6]) / 255f;
        green = Integer.parseInt(data[7]) / 255f;
        blue = Integer.parseInt(data[8]) / 255f;
    }

    public VertexData() {}
}
