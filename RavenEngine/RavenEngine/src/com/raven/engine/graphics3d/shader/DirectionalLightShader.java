package com.raven.engine.graphics3d.shader;

import com.raven.engine.GameProperties;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * Created by cookedbird on 5/29/17.
 */
public class DirectionalLightShader extends LightShader {

    private int texture_color_location,
                texture_normal_location,
                texture_depth_location,
                texture_shadow_location,
                texture_shadow_distance_location;

    public DirectionalLightShader() {
        super("dir_light_fragment.glsl");

        glBindAttribLocation(getProgramHandel(), 0, "vertex_pos");

        texture_color_location = glGetUniformLocation(getProgramHandel(), "colorTexture");
        texture_normal_location = glGetUniformLocation(getProgramHandel(), "normalTexture");
        texture_depth_location = glGetUniformLocation(getProgramHandel(), "depthTexture");

        texture_shadow_location = glGetUniformLocation(getProgramHandel(), "shadowTexture");
        texture_shadow_distance_location = glGetUniformLocation(getProgramHandel(), "shadowDistanceTexture");

        int blockIndex = glGetUniformBlockIndex(getProgramHandel(), "DirectionalLight");
        glUniformBlockBinding(getProgramHandel(), blockIndex, Shader.LIGHT);

        blockIndex = glGetUniformBlockIndex(getProgramHandel(), "Matrices");
        glUniformBlockBinding(getProgramHandel(), blockIndex, MATRICES);
    }

    @Override
    public void  useProgram() {
        super.useProgram();

        // Bind the gbuffer
        glUniform1i(texture_color_location, WorldShader.COLOR);
        glUniform1i(texture_normal_location, WorldShader.NORMAL);
        glUniform1i(texture_depth_location, WorldShader.DEPTH);

        glUniform1i(texture_shadow_location, ShadowShader.DEPTH);

        glViewport(0, 0,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight());

        glDisable(GL_DEPTH_TEST);
    }

    @Override
    public void endProgram() {
        glDisable(GL_BLEND);
    }
}
