package com.raven.engine.graphics3d.shader;

import com.raven.engine.GameProperties;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
 * Created by cookedbird on 12/8/17.
 */
public class FXAAShader extends Shader {

    private int texture_buf_location, frameBufSize_location;

    public FXAAShader() {
        super("vertex2.glsl", "fxaa_fragment.glsl");

        glBindAttribLocation(getProgramHandel(), 0, "vertex_pos");

        texture_buf_location = glGetUniformLocation(getProgramHandel(), "buf0");
        frameBufSize_location = glGetUniformLocation(getProgramHandel(), "frameBufSize");
    }

    public void useProgram() {
        super.useProgram();

        glUseProgram(getProgramHandel());

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

//        glUniform1i(texture_buf_location, ShadowShader.DEPTH);
        glUniform1i(texture_buf_location, WaterShader.COLOR);

        glUniform2f(frameBufSize_location,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight());

        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
    }

    @Override
    public void endProgram() {
        glEnable(GL_DEPTH_TEST);
    }
}
