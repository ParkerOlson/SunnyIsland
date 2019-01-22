package com.raven.engine.graphics3d.shader;

import com.raven.engine.GameEngine;
import com.raven.engine.GameProperties;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;
import static org.lwjgl.opengl.GL45.glNamedFramebufferDrawBuffers;

/**
 * Created by cookedbird on 5/29/17.
 */
public class WorldMSShader extends Shader {

    public static final int
            COLOR = getNextTexture(),
            NORMAL = getNextTexture(),
            ID = getNextTexture(),
            COMPLEX = getNextTexture(),
            NONMS_COMPLEX = getNextTexture(),
            DEPTH = getNextTexture();

    private int id_location;

    private int ms_framebuffer_handel,
            ms_color_texture,
            ms_normal_texture,
            ms_id_texture,
            ms_complex_texture,
            ms_depth_texture;

    private int framebuffer_handel, complex_texture;

    private IntBuffer buffers;

    public WorldMSShader() {
        super("world_vertex.glsl", "world_ms_fragment.glsl");

        glBindAttribLocation(getProgramHandel(), 0, "vertex_pos");
        glBindAttribLocation(getProgramHandel(), 1, "vertex_color");
        glBindAttribLocation(getProgramHandel(), 2, "vertex_normal");

        id_location = glGetUniformLocation(getProgramHandel(), "id");

        int blockIndex = glGetUniformBlockIndex(getProgramHandel(), "DirectionalLight");
        glUniformBlockBinding(getProgramHandel(), blockIndex, LIGHT);

        blockIndex = glGetUniformBlockIndex(getProgramHandel(), "Matrices");
        glUniformBlockBinding(getProgramHandel(), blockIndex, MATRICES);

        int bfs[] = {
                GL_COLOR_ATTACHMENT0, // Color
                GL_COLOR_ATTACHMENT1, // Normal
                GL_COLOR_ATTACHMENT2, // ID
                GL_COLOR_ATTACHMENT3, // Complex
        };

        buffers = BufferUtils.createIntBuffer(bfs.length);
        for (int i = 0; i < bfs.length; i++)
            buffers.put(bfs[i]);
        buffers.flip();

        int ms_count = GameEngine.getEngine().getWindow().getMultisampleCount();

        ms_framebuffer_handel = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, ms_framebuffer_handel);

        // MS FBO Textures
        // Color
        ms_color_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + COLOR);
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, ms_color_texture);

        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE,
                ms_count, GL_RGBA,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                true);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, ms_color_texture, 0);
        glActiveTexture(GL_TEXTURE0);

        // Normal
        ms_normal_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + NORMAL);
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, ms_normal_texture);

        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE,
                ms_count, GL_RGBA8,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                true);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D_MULTISAMPLE, ms_normal_texture, 0);
        glActiveTexture(GL_TEXTURE0);

        // ID
        ms_id_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + ID);
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, ms_id_texture);

        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE,
                ms_count, GL_RGB,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                true);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D_MULTISAMPLE, ms_id_texture, 0);
        glActiveTexture(GL_TEXTURE0);

        // Complex
        ms_complex_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + COMPLEX);
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, ms_complex_texture);

        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE,
                ms_count, GL_RED,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                true);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D_MULTISAMPLE, ms_complex_texture, 0);
        glActiveTexture(GL_TEXTURE0);

        // Depth Texture
        ms_depth_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + DEPTH);
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, ms_depth_texture);

        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE,
                ms_count, GL_DEPTH_COMPONENT32,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                true);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D_MULTISAMPLE, ms_depth_texture, 0);
        glActiveTexture(GL_TEXTURE0);

        // Draw buffers
        buffers.rewind();
        glDrawBuffers(buffers);

        // Errors
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("FBO_MS Failed: 0x"
                    + Integer.toHexString(glCheckFramebufferStatus(GL_FRAMEBUFFER)));
        }

        // resolve ms
        framebuffer_handel = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

        // FBO Textures
        // Color
        complex_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + NONMS_COMPLEX);
        glBindTexture(GL_TEXTURE_2D, complex_texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                0, GL_RED, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, complex_texture, 0);
        glActiveTexture(GL_TEXTURE0);

        // Draw buffers
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        // Errors
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("World Shader Failed: 0x"
                    + Integer.toHexString(glCheckFramebufferStatus(GL_FRAMEBUFFER)));
        }
    }

    @Override
    public void useProgram() {
        super.useProgram();

        glUseProgram(getProgramHandel());

        glBindFramebuffer(GL_FRAMEBUFFER, ms_framebuffer_handel);

        glViewport(0, 0,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight());

        glClearColor(0.6f, 0.7f, 1f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // make sure the id buffer isn't colored
        glClearBufferfv(GL_COLOR, 2,
                new float[]{ 0f, 0f, 0f, 0f });


        // Enable the custom mode attribute
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

//        buffers.rewind();
//        glDrawBuffers(buffers);
    }

    @Override
    public void endProgram() {
        // Disable the custom mode attribute
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

        glDisable(GL_CLIP_DISTANCE0);
    }

    public void setWorldObjectID(int id) {
        if (GameEngine.getEngine().getWindow().getActiveShader() == this)
            if (id != 0) {
                int r = (id & 0x000000FF) >> 0;
                int g = (id & 0x0000FF00) >> 8;
                int b = (id & 0x00FF0000) >> 16;

                glUniform3f(id_location, r / 255.0f, g / 255.0f, b / 255.0f);
            }
    }

    public int getMSFramebuffer() {
        return ms_framebuffer_handel;
    }

    public void blitComplexValue() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, framebuffer_handel);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        glBindFramebuffer(GL_READ_FRAMEBUFFER, ms_framebuffer_handel);
        glReadBuffer(GL_COLOR_ATTACHMENT3);

        glBlitFramebuffer(
                0, 0,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                0, 0,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                GL_COLOR_BUFFER_BIT, GL_NEAREST);
    }
}
