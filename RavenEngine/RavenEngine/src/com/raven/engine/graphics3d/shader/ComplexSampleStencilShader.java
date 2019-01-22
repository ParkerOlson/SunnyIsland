package com.raven.engine.graphics3d.shader;

import com.raven.engine.GameProperties;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

/**
 * Created by cookedbird on 12/5/17.
 */
public class ComplexSampleStencilShader extends Shader {

    public static final int LIGHT = getNextTexture();

    private int texture_complex_location, texture_normal_location, texture_depth_location;
    private int framebuffer_handel;
    private int light_texture;

    public ComplexSampleStencilShader() {
        super("vertex2.glsl", "cmplx_stencil_fragment.glsl");

        glBindAttribLocation(getProgramHandel(), 0, "vertex_pos");

        texture_complex_location = glGetUniformLocation(getProgramHandel(), "complexTexture");
//        texture_normal_location = glGetUniformLocation(getProgramHandel(), "normalTexture");
//        texture_depth_location = glGetUniformLocation(getProgramHandel(), "depthTexture");

        framebuffer_handel = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

        light_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + LIGHT);
        glBindTexture(GL_TEXTURE_2D, light_texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, light_texture, 0);
        glActiveTexture(GL_TEXTURE0);
    }

    public void useProgram() {
        super.useProgram();

        glUseProgram(getProgramHandel());

        // Use for the alpha value
        glUniform1i(texture_complex_location, WorldMSShader.NONMS_COMPLEX);
//        glUniform1i(texture_normal_location, WorldMSShader.NORMAL);
//        glUniform1i(texture_depth_location, WorldMSShader.DEPTH);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glViewport(0, 0,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight());

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glDisable(GL_DEPTH_TEST);

        glEnable(GL_STENCIL_TEST);
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        glStencilFunc(GL_ALWAYS, 1, 0xFF);
        glStencilMask(0xFF);

        glClear(GL_STENCIL_BUFFER_BIT);
    }

    @Override
    public void endProgram() {
//        glEnable(GL_DEPTH_TEST);
//        glDisable(GL_STENCIL_TEST);
    }

    public int getFramebuffer() {
        return framebuffer_handel;
    }
}
