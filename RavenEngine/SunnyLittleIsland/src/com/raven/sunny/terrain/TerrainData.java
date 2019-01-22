package com.raven.sunny.terrain;

import com.raven.engine.scene.Layer;
import com.raven.engine.util.Vector3f;
import com.raven.engine.worldobject.WorldObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cookedbird on 5/20/17.
 */
public class TerrainData {
    public static final int Beach = 0, Sand = 1, Stone = 2, Stone_add = 3, Water = 4;

    private int x_arr, z_arr;

    private Vector3f[] vertices;
    private int type;

    private Terrain terrain;
    private TerrainData[][] data;
    private WorldObject decor;

    public static Float[] getColorOfType(int type) {
        switch (type) {
            case Stone:
                return new Float[] {0x99 / 255f, 0x99 / 255f, 0x99 / 255f};
            case Stone_add:  // shouldn't be drawn
                return new Float[] {0xFF / 255f, 0xFF / 255f, 0xFF / 255f};
            case Water:
                return new Float[] {0x40 / 255f, 0x40 / 255f, 0xB2 / 255f};
            case Sand:
            case Beach:
            default:
                return new Float[] {0xE1 / 255f, 0xC0 / 255f, 0x8F / 255f};
        }
    }

    public TerrainData(TerrainData[][] data, int x, int z) {
        this.terrain = terrain;
        this.data = data;
        x_arr = x;
        z_arr = z;
    }

    public int getType() {
        return type;
    }

    public Float[] getTypeColor() {
        return getColorOfType(getType());
    }

    public void setVertices(Vector3f... vertices) {
        this.vertices = vertices;
    }

    public Float[] getVerticesAsArray() {
        Float[] arr = new Float[vertices.length * 3];

        for (int i = 0; i < vertices.length; i++) {
            arr[i * 3 + 0] = vertices[i].x;
            arr[i * 3 + 1] = vertices[i].y;
            arr[i * 3 + 2] = vertices[i].z;
        }

        return arr;
    }

    public Vector3f getNormal() {
        // assuming 3 vectors
        return vertices[1].subtract(vertices[0]).cross(vertices[2].subtract(vertices[0])).normalize();
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getMinHeight() {
        float minHeight = vertices[0].y;

        for (int i = 1; i < vertices.length; i++) {
            if (minHeight > vertices[i].y) {
                minHeight = vertices[i].y;
            }
        }

        return minHeight;
    }

    public float getMaxHeight() {
        float maxHeight = vertices[0].y;

        for (int i = 1; i < vertices.length; i++) {
            if (maxHeight < vertices[i].y) {
                maxHeight = vertices[i].y;
            }
        }

        return maxHeight;
    }

    public Vector3f getCenter() {
        Vector3f v = new Vector3f();

        v.x = (vertices[0].x + vertices[1].x +vertices[2].x) / 3f;
        v.y = (vertices[0].y + vertices[1].y +vertices[2].y) / 3f;
        v.z = (vertices[0].z + vertices[1].z +vertices[2].z) / 3f;

        return v;
    }

    public TerrainData[] getAdjacentTerrainData() {
        List<TerrainData> adjList = new ArrayList<>();

        int adjCount = 1;

        if ((x_arr % 2 + z_arr % 2) % 2 == 0 && x_arr != data.length - 1) {
            adjList.add(data[x_arr + 1][z_arr]);
        } else if (x_arr != 0) {
            adjList.add(data[x_arr - 1][z_arr]);
        } else {
            adjCount -= 1;
        }

        if (z_arr != 0) {
            adjList.add(data[x_arr][z_arr - 1]);
            adjCount += 1;
        }

        if (z_arr != data[x_arr].length - 1) {
            adjList.add(data[x_arr][z_arr + 1]);
            adjCount += 1;
        }

        TerrainData[] adj = new TerrainData[adjCount];
        return adjList.toArray(adj);
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public void setDecor(WorldObject decor) {
        this.decor = decor;

        Vector3f center = getCenter();

        decor.setX(center.x);
        decor.setY(center.y);
        decor.setZ(center.z);
        decor.setScale(.35f);
    }
}
