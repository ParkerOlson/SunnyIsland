package com.raven.engine.graphics3d;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class ModelReference {

    private static ModelReference blank;

    private final static int vertex_size = 3;

    private static ArrayList<Float> vertex_list = new ArrayList<Float>();
    private static ArrayList<Float> normal_list = new ArrayList<Float>();
    private static ArrayList<Float> colors_list = new ArrayList<Float>();

    private static int vbo_vertex_handle, vbo_normal_handle, vbo_colors_handle;

    private static Float[][][][] mapColor;

    public static void load(ModelData modelData) {
        // make sure all the data is loaded into the modelData
        modelData.load();

        ModelReference modelr = new ModelReference();

        int vertex_start = vertex_list.size();

        modelData.getVertexData().stream()
                .forEach(ModelReference::vertexDataToLists);

        int vertex_count = modelr.vertices = vertex_list.size() - vertex_start;

        ShortBuffer index_list_buffer = BufferUtils
                .createShortBuffer(vertex_count / vertex_size);
        for (short i = (short) (vertex_start / vertex_size); i < (vertex_count + vertex_start) / vertex_size; i++) {
            index_list_buffer.put(i);
        }
        index_list_buffer.flip();

        modelr.vbo_index_handle = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, modelr.vbo_index_handle);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, index_list_buffer, GL_STATIC_DRAW);

        modelData.setModelReference(modelr);
    }

    private static void vertexDataToLists(VertexData vertexData) {
        vertex_list.add(vertexData.x);
        vertex_list.add(vertexData.y);
        vertex_list.add(vertexData.z);
        normal_list.add(vertexData.nx);
        normal_list.add(vertexData.ny);
        normal_list.add(vertexData.nz);
        colors_list.add(vertexData.red);
        colors_list.add(vertexData.green);
        colors_list.add(vertexData.blue);
    }

    public static void compileBuffer() {
        int vertices = vertex_list.size();

        // put into buffers
        FloatBuffer vertex_list_buffer = BufferUtils
                .createFloatBuffer(vertices);
        for (Float vertex : vertex_list) {
            vertex_list_buffer.put(vertex);
        }
        vertex_list_buffer.flip();

        FloatBuffer normal_list_buffer = BufferUtils
                .createFloatBuffer(vertices);
        for (Float normal : normal_list) {
            normal_list_buffer.put(normal);
        }
        normal_list_buffer.flip();

        FloatBuffer colors_list_buffer = BufferUtils
                .createFloatBuffer(vertices);
        for (Float colors : colors_list) {
            colors_list_buffer.put(colors);
        }
        colors_list_buffer.flip();

        // create vbo
        vbo_vertex_handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_vertex_handle);
        glBufferData(GL_ARRAY_BUFFER, vertex_list_buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0l);

        vbo_normal_handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_normal_handle);
        glBufferData(GL_ARRAY_BUFFER, normal_list_buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0l);

        vbo_colors_handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_colors_handle);
        glBufferData(GL_ARRAY_BUFFER, colors_list_buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0l);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static void clearBuffers() {
        vbo_colors_handle = 0;
        vbo_normal_handle = 0;
        vbo_vertex_handle = 0;

        vertex_list.clear();
        colors_list.clear();
        normal_list.clear();
    }

    public static void loadBlankModel() {
        int vertex_start = vertex_list.size();

        blank = new ModelReference();
        blank.draw_mode = GL_QUADS;

        vertex_list.addAll(Arrays.asList(new Float[]{
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f,
        }));

        normal_list.addAll(Arrays.asList(new Float[]{
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f
        }));
        colors_list.addAll(Arrays.asList(new Float[]{
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f
        }));


        int vertex_count = vertex_list.size();
        blank.vertices = vertex_list.size() - vertex_start;

        ShortBuffer index_list_buffer = BufferUtils
                .createShortBuffer(blank.vertices / vertex_size);
        for (short i = (short) (vertex_start / vertex_size); i < vertex_count
                / vertex_size; i++) {
            index_list_buffer.put(i);
        }
        index_list_buffer.flip();

        blank.vbo_index_handle = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, blank.vbo_index_handle);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, index_list_buffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static ModelReference getBlankModel() {
        return blank;
    }

    private int draw_mode = GL_TRIANGLES;
    private int vertices;
    private int vbo_index_handle;

    private ModelReference() {
    }

    public void draw() {
        // glPolygonMode(GL_BACK, GL_FILL);

        // glFrontFace(GL_CW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo_index_handle);

        glDrawElements(draw_mode, vertices, GL_UNSIGNED_SHORT, 0);
    }
}
