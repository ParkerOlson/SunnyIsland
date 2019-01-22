package com.raven.engine.graphics3d.shader;

import com.raven.engine.GameProperties;
import com.raven.engine.util.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

/**
 * Created by cookedbird on 11/22/17.
 */
public class WaterReflectionShader extends Shader {
    public static final int
            COLOR = getNextTexture(),
            NORMAL =getNextTexture(),
            DEPTH = getNextTexture();

    private int framebuffer_handel, color_texture, normal_texture, depth_texture;

    private IntBuffer buffers;

    public WaterReflectionShader() {
        super("water_reflection_vertex.glsl", "world_fragment.glsl");

        int bfs[] = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1 };
        buffers = BufferUtils.createIntBuffer(bfs.length);
        for (int i = 0; i < bfs.length; i++)
            buffers.put(bfs[i]);
        buffers.flip();

        glBindAttribLocation(getProgramHandel(), 0, "vertex_pos");
        glBindAttribLocation(getProgramHandel(), 1, "vertex_color");
        glBindAttribLocation(getProgramHandel(), 2, "vertex_normal");

        int blockIndex = glGetUniformBlockIndex(getProgramHandel(), "DirectionalLight");
        glUniformBlockBinding(getProgramHandel(), blockIndex, LIGHT);

        blockIndex = glGetUniformBlockIndex(getProgramHandel(), "Matrices");
        glUniformBlockBinding(getProgramHandel(), blockIndex, MATRICES);

        // FBO
        framebuffer_handel = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

        // FBO Textures
        // Color
        color_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + COLOR);
        glBindTexture(GL_TEXTURE_2D, color_texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                GameProperties.getScreenWidth() / GameProperties.getWaterQuality(),
                GameProperties.getScreenHeight() / GameProperties.getWaterQuality(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, color_texture, 0);
        glActiveTexture(GL_TEXTURE0);

        // Normal
        normal_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + NORMAL);
        glBindTexture(GL_TEXTURE_2D, normal_texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,  GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,  GL_CLAMP_TO_EDGE);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, normal_texture, 0);

        // Draw buffers
        buffers.rewind();
        glDrawBuffers(buffers);

        // Depth
        depth_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + DEPTH);
        glBindTexture(GL_TEXTURE_2D, depth_texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT,
                GameProperties.getScreenWidth() / GameProperties.getWaterQuality(),
                GameProperties.getScreenHeight() / GameProperties.getWaterQuality(),
                0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depth_texture, 0);
        glActiveTexture(GL_TEXTURE0);

        // clear to the default reflection colors
        glClearColor(0.6f, 0.7f, 1f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Errors
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("Water Reflection Shader Failed: 0x"
                    + Integer.toHexString(glCheckFramebufferStatus(GL_FRAMEBUFFER)));
        }
    }

    @Override
    public void useProgram() {
        super.useProgram();

        glUseProgram(getProgramHandel());

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

        glViewport(0, 0,
                GameProperties.getScreenWidth() / GameProperties.getWaterQuality(),
                GameProperties.getScreenHeight() / GameProperties.getWaterQuality());

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Enable the custom mode attribute
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glEnable(GL_CLIP_DISTANCE0);
        glCullFace(GL_FRONT);
    }

    @Override
    public void endProgram() {
        // Disable the custom mode attribute
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

        glDisable(GL_CLIP_DISTANCE0);
        glCullFace(GL_BACK);
    }

    public int getColorTexture() {
        return color_texture;
    }

    public int getDepthTexture() {
        return depth_texture;
    }
}
