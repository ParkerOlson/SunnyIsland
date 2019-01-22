package com.raven.engine.graphics3d.model;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class ModelReference {

    private static ModelReference blank;

    private final static int vertex_size = 3;
    private final static int normal_size = 3;
    private final static int colors_size = 3;
    private final static int texture_size = 2;
    private final static int bone_size = 4;

    private static List<Float> vertex_list = new LinkedList<>();
    private static List<Float> normal_list = new LinkedList<>();
    private static List<Float> colors_list = new LinkedList<>();
    private static List<Float> texture_list = new LinkedList<>();
    private static List<Integer> bone_list = new LinkedList<>();
    private static List<Float> weight_list = new LinkedList<>();

    // Don't forget about the shader when adding more
    private static int
            vbo_vertex_handle,
            vbo_normal_handle,
            vbo_texture_handle,
            vbo_colors_handle,
            vbo_bone_handle,
            vbo_weight_handle;

    public static void load(ModelData modelData) {
        // make sure all the data is loaded into the modelData
        modelData.load();

        ModelReference modelr = new ModelReference();

        int vertex_start = vertex_list.size();

        modelData.getVertexData()
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
        texture_list.add(vertexData.s);
        texture_list.add(vertexData.t);
        colors_list.add(vertexData.red);
        colors_list.add(vertexData.green);
        colors_list.add(vertexData.blue);
    }

    public static void compileBuffer() {
        int size = vertex_list.size() / vertex_size;

        // put into buffers
        FloatBuffer vertex_list_buffer =
                BufferUtils.createFloatBuffer(size * vertex_size);
        vertex_list.forEach(vertex_list_buffer::put);
        vertex_list_buffer.flip();

        FloatBuffer normal_list_buffer =
                BufferUtils.createFloatBuffer(size * normal_size);
        normal_list.forEach(normal_list_buffer::put);
        normal_list_buffer.flip();

        FloatBuffer texture_list_buffer =
                BufferUtils.createFloatBuffer(size * texture_size);
        texture_list.forEach(texture_list_buffer::put);
        texture_list_buffer.flip();

        FloatBuffer colors_list_buffer =
                BufferUtils.createFloatBuffer(size * colors_size);
        colors_list.forEach(colors_list_buffer::put);
        colors_list_buffer.flip();

        IntBuffer bone_list_buffer =
                BufferUtils.createIntBuffer(size * bone_size);
        bone_list.forEach(bone_list_buffer::put);
        bone_list_buffer.flip();

        FloatBuffer weight_list_buffer =
                BufferUtils.createFloatBuffer(size * bone_size);
        weight_list.forEach(weight_list_buffer::put);
        weight_list_buffer.flip();

        // create vbo
        vbo_vertex_handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_vertex_handle);
        glBufferData(GL_ARRAY_BUFFER, vertex_list_buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L);

        vbo_normal_handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_normal_handle);
        glBufferData(GL_ARRAY_BUFFER, normal_list_buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0L);

        vbo_texture_handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_texture_handle);
        glBufferData(GL_ARRAY_BUFFER, texture_list_buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0L);

        vbo_colors_handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_colors_handle);
        glBufferData(GL_ARRAY_BUFFER, colors_list_buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0L);

        vbo_bone_handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_bone_handle);
        glBufferData(GL_ARRAY_BUFFER, bone_list_buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(4, bone_size, GL_FLOAT, false, 0, 0L);

        vbo_weight_handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_weight_handle);
        glBufferData(GL_ARRAY_BUFFER, weight_list_buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(5, bone_size, GL_FLOAT, false, 0, 0L);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static void clearBuffers() {
        glDeleteBuffers(vbo_vertex_handle);
        glDeleteBuffers(vbo_normal_handle);
        glDeleteBuffers(vbo_texture_handle);
        glDeleteBuffers(vbo_colors_handle);
        glDeleteBuffers(vbo_bone_handle);
        glDeleteBuffers(vbo_weight_handle);

        vbo_vertex_handle = 0;
        vbo_normal_handle = 0;
        vbo_texture_handle = 0;
        vbo_colors_handle = 0;
        vbo_bone_handle = 0;
        vbo_weight_handle = 0;

        vertex_list.clear();
        normal_list.clear();
        texture_list.clear();
        colors_list.clear();
        bone_list.clear();
        weight_list.clear();
    }

    public static void loadBlankModel() {
        int vertex_start = vertex_list.size();

        blank = new ModelReference();
        blank.draw_mode = GL_QUADS;

        vertex_list.addAll(Arrays.asList(
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f));
        normal_list.addAll(Arrays.asList(
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f));
        texture_list.addAll(Arrays.asList(
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.0f));
        colors_list.addAll(Arrays.asList(
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f));
        bone_list.addAll(Arrays.asList(
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0));
        weight_list.addAll(Arrays.asList(
                1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.0f));

        int vertex_count = vertex_list.size();
        blank.vertices = vertex_list.size() - vertex_start;

        ShortBuffer index_list_buffer = BufferUtils
                .createShortBuffer(blank.vertices / vertex_size);
        for (short i = (short) (vertex_start / vertex_size);
             i < vertex_count / vertex_size;
             i++) {
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

    public void release() {
        glDeleteBuffers(vbo_index_handle);
    }
}
