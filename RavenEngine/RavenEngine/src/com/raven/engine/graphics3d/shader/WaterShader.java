package com.raven.engine.graphics3d.shader;

import com.raven.engine.GameEngine;
import com.raven.engine.GameProperties;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import static org.lwjgl.opengl.GL43.glCopyImageSubData;

/**
 * Created by cookedbird on 12/11/17.
 */
public class WaterShader extends Shader {

    public static final int
            COLOR = getNextTexture(),
            DEPTH = getNextTexture();

    private WorldShader worldShader;

    private int
            framebuffer_handel,
            color_texture,
            depth_texture,
            stencil_renderbuffer;

    private int
            texture_terrain_location,
            texture_reflect_location,
            texture_depth_location,
            time_location;

    public WaterShader(WorldShader worldShader) {
        super("water_vertex.glsl", "water_fragment.glsl");

        this.worldShader = worldShader;

        glBindAttribLocation(getProgramHandel(), 0, "vertex_pos");
        glBindAttribLocation(getProgramHandel(), 1, "vertex_color");
        glBindAttribLocation(getProgramHandel(), 2, "vertex_normal");

        texture_terrain_location = glGetUniformLocation(getProgramHandel(), "terrainTexture");
        texture_reflect_location = glGetUniformLocation(getProgramHandel(), "reflectTexture");
        texture_depth_location = glGetUniformLocation(getProgramHandel(), "depthTexture");

        time_location = glGetUniformLocation(getProgramHandel(), "time");

        int blockIndex = glGetUniformBlockIndex(getProgramHandel(), "DirectionalLight");
        glUniformBlockBinding(getProgramHandel(), blockIndex, LIGHT);

        blockIndex = glGetUniformBlockIndex(getProgramHandel(), "Matrices");
        glUniformBlockBinding(getProgramHandel(), blockIndex, MATRICES);

        // fbo
        framebuffer_handel = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

        // FBO Textures
        // Color
        color_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + COLOR);
        glBindTexture(GL_TEXTURE_2D, color_texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, color_texture, 0);

        // Depth
        depth_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + DEPTH);
        glBindTexture(GL_TEXTURE_2D, depth_texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, depth_texture, 0);
        glActiveTexture(GL_TEXTURE0);


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

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

        glViewport(0, 0,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight());

        glUniform1i(texture_terrain_location, LightShader.LIGHT);
        glUniform1i(texture_reflect_location, WaterReflectionShader.COLOR);
        glUniform1i(texture_depth_location, WorldShader.DEPTH);

        float time = GameEngine.getEngine().getSystemTime() / 400000f;
        glUniform1f(time_location, time);

        // Copy the world depth buffer - TODO try Blit and none
        glCopyImageSubData(
                worldShader.getDepthTexture(), GL_TEXTURE_2D, 0, 0, 0, 0,
                depth_texture, GL_TEXTURE_2D, 0, 0, 0, 0,
                GameProperties.getScreenWidth(), GameProperties.getScreenHeight(), 1);


        glEnable(GL_DEPTH_TEST);

        glEnable(GL_STENCIL_TEST);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glStencilFunc(GL_ALWAYS, 1, 0xFF);
        glStencilMask(0xFF);

        glClearStencil(0);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        // Enable the custom mode attribute
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void endProgram() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

//        glDisable(GL_STENCIL_TEST);
    }

    public int getFramebufferHandel() {
        return framebuffer_handel;
    }
}
