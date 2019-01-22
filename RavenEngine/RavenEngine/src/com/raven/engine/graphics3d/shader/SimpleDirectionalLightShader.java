package com.raven.engine.graphics3d.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * Created by cookedbird on 5/29/17.
 */
public class SimpleDirectionalLightShader extends Shader {

    private ComplexSampleStencilShader complexSampleStencilShader;

    private int framebuffer_handel;
    private int texture_color_location,
                texture_normal_location,
                texture_depth_location;

    public SimpleDirectionalLightShader(ComplexSampleStencilShader csss) {
        super("vertex2.glsl", "simple_dir_light_fragment.glsl");

        complexSampleStencilShader = csss;

        glBindAttribLocation(getProgramHandel(), 0, "vertex_pos");

        texture_color_location = glGetUniformLocation(getProgramHandel(), "colorTexture");
        texture_normal_location = glGetUniformLocation(getProgramHandel(), "normalTexture");
        texture_depth_location = glGetUniformLocation(getProgramHandel(), "depthTexture");

        int blockIndex = glGetUniformBlockIndex(getProgramHandel(), "DirectionalLight");
        glUniformBlockBinding(getProgramHandel(), blockIndex, LIGHT);

        blockIndex = glGetUniformBlockIndex(getProgramHandel(), "Matrices");
        glUniformBlockBinding(getProgramHandel(), blockIndex, MATRICES);
    }

    @Override
    public void  useProgram() {
        super.useProgram();

        glUseProgram(getProgramHandel());

        // Bind the gbuffer
        glUniform1i(texture_color_location, WorldMSShader.COLOR);
        glUniform1i(texture_normal_location, WorldMSShader.NORMAL);
        glUniform1i(texture_depth_location, WorldMSShader.DEPTH);

        // should be bound already - but if not
        // glBindFramebuffer(GL_FRAMEBUFFER, complexSampleStencilShader.getFramebuffer());

//        glViewport(0, 0,
//                GameProperties.getScreenWidth(),
//                GameProperties.getScreenHeight());

//        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glEnable(GL_STENCIL_TEST);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        glStencilMask(0x00);
        glStencilFunc(GL_EQUAL, 1, 0xFF);
    }

    @Override
    public void endProgram() {
//        glDisable(GL_STENCIL_TEST);
    }
}
