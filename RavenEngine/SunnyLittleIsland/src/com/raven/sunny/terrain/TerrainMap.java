package com.raven.sunny.terrain;

import com.raven.engine.graphics3d.ModelData;
import com.raven.engine.graphics3d.VertexData;
import com.raven.engine.scene.Scene;
import com.raven.engine.util.SimplexNoise;
import com.raven.engine.util.Vector3f;
import com.raven.engine.worldobject.WorldObject;
import com.raven.sunny.Bush;
import com.raven.sunny.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by cookedbird on 5/26/17.
 */
public class TerrainMap {
    private Vector3f[][] heightPoints;
    private TerrainData[][] data;

    public static Terrain genTerrain(Scene scene, int width, int height) {
        // Create starting vertices
        Random r = new Random();
        // int seed = -377377594;
        // int seed = -699290749;
        int seed = r.nextInt();
        System.out.println("Seed: " + seed);
        SimplexNoise noise = new SimplexNoise(seed);

        Vector3f[][] heightPoints = new Vector3f[width + 1][];
        float length_modifier = (float)Math.sqrt(.5);

        for (int x = 0; x < width + 1; x++) {
            heightPoints[x] = new Vector3f[height + x % 2];

            for (int z = 0; z < height + x % 2; z++) {
                float x_pos = x - (width + 1) / 2;
                float z_pos = z - (height) / 2 - (x % 2) / 2f;
                x_pos *= length_modifier;


                float y_pos = 0f;

                for (int i = 1; i <= 4; i++) {

                    float scale = 14f / i;
                    float noise_height = 1.5f / (1 + i);

                    float noise_x = x_pos / scale + i * 2;
                    float noise_z = z_pos / scale + i * 2;

                    y_pos += noise.noise(noise_x, noise_z) * noise_height;
                }

                // y_pos = (float)Math.exp(y_pos * 1.3) * (float)Math.max(0.0, 1.1 - (x_pos * x_pos + z_pos * z_pos) / 300.0);
                y_pos = (float)Math.exp(y_pos * 1.1) * (float)Math.max(0.0, 1.3 - (x_pos * x_pos + z_pos * z_pos) / 500.0);
                y_pos += (.5f - (x_pos * x_pos + z_pos * z_pos) / 200.0f);
                y_pos -= .45f;

                if (y_pos < 0f) {
                    y_pos -= .1;
                }

                heightPoints[x][z] = new Vector3f(x_pos, y_pos, z_pos);
            }
        }

        // Create faces
        TerrainData[][] data = new TerrainData[width][];
        for (int x = 0; x < width; x++) {
            data[x] = new TerrainData[height * 2 - 1];

            for (int z = 0; z < height * 2 - 1; z++) {
                TerrainData d = new TerrainData(data, x, z);

                Vector3f[] vs;

                if (x % 2 == 0) {
                    if (z % 2 == 0) {
                        vs = new Vector3f[] { heightPoints[x + 1][z / 2], heightPoints[x][z / 2], heightPoints[x + 1][z / 2 + 1] };
                    } else {
                        vs = new Vector3f[] { heightPoints[x + 1][z / 2 + 1], heightPoints[x][z / 2], heightPoints[x][z / 2 + 1] };
                    }
                } else {
                    if (z % 2 == 0) {
                        vs = new Vector3f[] { heightPoints[x][z / 2], heightPoints[x][z / 2 + 1], heightPoints[x + 1][z / 2] };
                    } else {
                    vs = new Vector3f[] { heightPoints[x][z / 2 + 1], heightPoints[x + 1][z / 2 + 1], heightPoints[x + 1][z / 2] };
                    }
                }

                // Smooth Water edges
                int countBelow = 0;
                for (Vector3f v : vs) {
                    if (v.y < 0f) {
                        countBelow += 1;
                    }
                }

                if (countBelow == 1 || countBelow == 2) {
                    // more points above than below
                    for (Vector3f v : vs) {
                        if (v.y > 0f) {
                            v.y = .05f + .05f * r.nextFloat();
                        } else {
                            v.y = -.05f - .05f * r.nextFloat();
                        }
                    }
                }
//                else if (countBelow == 2) {
//                    for (Vector3f v : vs) {
//                        if (v.y > 0.05f) {
//                            v.y = 0.05f;
//                        }
//                    }
//                }

                d.setVertices(vs);

                data[x][z] = d;
            }
        }

        // Set Types
        for (TerrainData[] ds : data) {
            for (TerrainData d : ds) {
                if (d.getMinHeight() > .05f) {
                    d.setType(TerrainData.Sand);
                }

                if (d.getNormal().y < .6f) {
                    d.setType(TerrainData.Stone);
                }
            }
        }

        for (TerrainData[] ds : data) {
            for (TerrainData d : ds) {
                int stoneCount = 0;

                for (TerrainData adjD : d.getAdjacentTerrainData()) {
                    if (adjD.getType() == TerrainData.Stone)
                        stoneCount += 1;
                }

                if (stoneCount >= 2 && d.getType() != TerrainData.Stone) {
                    d.setType(TerrainData.Stone_add);
                }
            }
        }

        for (TerrainData[] ds : data) {
            for (TerrainData d : ds) {
                if (d.getType() == TerrainData.Stone_add) {
                    d.setType(TerrainData.Stone);
                }
            }
        }

        Terrain terrain = new Terrain(scene, getModelData(width, height, data), data);

        // Add decor
        for (TerrainData[] tds : data) {
            for (TerrainData td : tds) {
                if (td.getType() == TerrainData.Sand) {
                    float chance = r.nextFloat();

                    if (chance < .1f) {

                        WorldObject woTree = new Tree(scene);
                        td.setDecor(woTree);
                        woTree.setRotation(r.nextFloat() * 360);
                    } else if (chance < .3f) {

                        WorldObject woBush = new Bush(scene);
                        td.setDecor(woBush);
                        woBush.setRotation(r.nextFloat() * 360);
                    }
                }
            }
        }

        return terrain;
    }

    private static ModelData getModelData(int width, int height, TerrainData[][] data) {
        ModelData model = new ModelData();

        List<Float> vertices = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> colors = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height * 2 - 1; z++) {
                TerrainData d = data[x][z];

                vertices.addAll(Arrays.asList(d.getVerticesAsArray()));

                Float[] normal = d.getNormal().toArray();
                normals.addAll(Arrays.asList(normal));
                normals.addAll(Arrays.asList(normal));
                normals.addAll(Arrays.asList(normal));

                Float[] color = d.getTypeColor();
                colors.addAll(Arrays.asList(color));
                colors.addAll(Arrays.asList(color));
                colors.addAll(Arrays.asList(color));
            }
        }

        for (int i = 0; i < vertices.size(); i += 3) {
            VertexData vertexData = new VertexData();

            vertexData.x = vertices.get(i);
            vertexData.y = vertices.get(i + 1);
            vertexData.z = vertices.get(i + 2);
            vertexData.nx = normals.get(i);
            vertexData.ny = normals.get(i + 1);
            vertexData.nz = normals.get(i + 2);
            vertexData.red = colors.get(i);
            vertexData.green = colors.get(i + 1);
            vertexData.blue = colors.get(i + 2);

            model.addVertex(vertexData);
        }

//        model.setVertexData(vertices);
//        model.setNormalData(normals);
//        model.setColorData(colors);

        return model;
    }
}
