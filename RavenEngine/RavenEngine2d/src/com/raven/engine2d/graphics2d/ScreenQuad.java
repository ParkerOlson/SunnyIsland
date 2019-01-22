package com.raven.engine2d.graphics2d;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class ScreenQuad {
    private static ScreenQuad blank;

    private final static int vertex_size = 3, texture_size = 2;

    private static List<Float> vertex_list = new LinkedList<>();
    private static List<Float> texture_list = new LinkedList<>();

    private static int vbo_vertex_handle, vbo_texture_handle, vao_handel;

    public static void compileBuffer(GameWindow window) {
        int size = vertex_list.size() / vertex_size;

        // put into buffers
        FloatBuffer vertex_list_buffer =
                BufferUtils.createFloatBuffer(size * vertex_size);
        vertex_list.forEach(vertex_list_buffer::put);
        vertex_list_buffer.flip();

        FloatBuffer texture_list_buffer =
                BufferUtils.createFloatBuffer(size * texture_size);
        texture_list.forEach(texture_list_buffer::put);
        texture_list_buffer.flip();

        vao_handel = glGenVertexArrays();
        glBindVertexArray(vao_handel);

        // create vbo
        vbo_vertex_handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_vertex_handle);
        glBufferData(GL_ARRAY_BUFFER, vertex_list_buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, vertex_size, GL_FLOAT, false, 0, 0L);

        vbo_texture_handle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_texture_handle);
        glBufferData(GL_ARRAY_BUFFER, texture_list_buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, texture_size, GL_FLOAT, false, 0, 0L);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static void clearBuffers() {
        glDeleteVertexArrays(vao_handel);
        glDeleteBuffers(vbo_vertex_handle);
        glDeleteBuffers(vbo_texture_handle);

        vao_handel = 0;
        vbo_vertex_handle = 0;
        vbo_texture_handle = 0;

        vertex_list.clear();
        texture_list.clear();
    }

    public static void loadQuad() {
        int vertex_start = vertex_list.size();

        blank = new ScreenQuad();
//        blank.draw_mode = GL_QUADS; // -- Was it a bug that it worked?

        vertex_list.addAll(Arrays.asList(
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f));

        texture_list.addAll(Arrays.asList(
                0.0f, 1f,
                1.0f, 1f,
                1.0f, 0f,
                0.0f, 0f,
                0.0f, 1f,
                1.0f, 0f));

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

    public static ScreenQuad getBlankModel() {
        return blank;
    }

    private int draw_mode = GL_TRIANGLES;
    private int vertices;
    private int vbo_index_handle;

    private ScreenQuad() {
    }

    public void draw(GameWindow window) {
        // glPolygonMode(GL_BACK, GL_FILL);

        // glFrontFace(GL_CW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo_index_handle);

        glDrawElements(draw_mode, vertices, GL_UNSIGNED_SHORT, 0);
    }

    public void release() {
        glDeleteBuffers(vbo_index_handle);
    }
}
