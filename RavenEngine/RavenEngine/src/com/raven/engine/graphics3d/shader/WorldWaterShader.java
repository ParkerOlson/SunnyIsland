package com.raven.engine.graphics3d.shader;

import com.raven.engine.GameEngine;
import com.raven.engine.GameProperties;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * Created by cookedbird on 11/21/17.
 */
public class WorldWaterShader extends Shader {

    private int texture_refract_color_location, texture_refract_depth_location,
            texture_reflect_color_location, texture_reflect_depth_location,
            time_location;

    private WorldMSShader worldMSShader;

    public WorldWaterShader(WorldMSShader worldMSShader) {
        super("world_water_vertex.glsl", "water_fragment.glsl");

        this.worldMSShader = worldMSShader;

        glBindAttribLocation(getProgramHandel(), 0, "vertex_pos");
        glBindAttribLocation(getProgramHandel(), 1, "vertex_color");
        glBindAttribLocation(getProgramHandel(), 2, "vertex_normal");

        texture_refract_color_location = glGetUniformLocation(getProgramHandel(), "refractColorTexture");
        texture_refract_depth_location = glGetUniformLocation(getProgramHandel(), "refractDepthTexture");

        texture_reflect_color_location = glGetUniformLocation(getProgramHandel(), "reflectColorTexture");
        texture_reflect_depth_location = glGetUniformLocation(getProgramHandel(), "reflectDepthTexture");

        time_location = glGetUniformLocation(getProgramHandel(), "time");

        int blockIndex = glGetUniformBlockIndex(getProgramHandel(), "DirectionalLight");
        glUniformBlockBinding(getProgramHandel(), blockIndex, LIGHT);

        blockIndex = glGetUniformBlockIndex(getProgramHandel(), "Matrices");
        glUniformBlockBinding(getProgramHandel(), blockIndex, MATRICES);
    }

    public void useProgram() {
        super.useProgram();

        glUseProgram(getProgramHandel());

        glBindFramebuffer(GL_FRAMEBUFFER, worldMSShader.getMSFramebuffer());

        glViewport(0, 0,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight());

        glUniform1i(texture_refract_color_location, WaterRefractionShader.COLOR);
        glUniform1i(texture_refract_depth_location, WaterRefractionShader.DEPTH);

        glUniform1i(texture_reflect_color_location, WaterReflectionShader.COLOR);
        glUniform1i(texture_reflect_depth_location, WaterReflectionShader.DEPTH);

        glUniform1f(time_location, GameEngine.getEngine().getSystemTime() / 400000f);

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
    }
}
