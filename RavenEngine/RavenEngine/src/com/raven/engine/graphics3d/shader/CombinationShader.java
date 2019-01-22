package com.raven.engine.graphics3d.shader;

import com.raven.engine.GameProperties;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.glStencilMask;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
 * Created by cookedbird on 12/12/17.
 */
public class CombinationShader extends Shader {

    private WaterShader waterShader;

    private int texture_color_location;

    public CombinationShader(WaterShader waterShader) {
        super("vertex2.glsl", "combination_fragment.glsl");

        this.waterShader = waterShader;

        texture_color_location = glGetUniformLocation(getProgramHandel(), "colorTexture");
    }

    public void useProgram() {
        super.useProgram();

//        glBindFramebuffer(GL_FRAMEBUFFER, waterShader.getFramebufferHandel());

        glViewport(0, 0,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight());

        glUniform1i(texture_color_location, LightShader.LIGHT);

        glEnable(GL_STENCIL_TEST);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        glStencilFunc(GL_EQUAL, 0, 0xFF);
        glStencilMask(0x00);
    }

    @Override
    public void endProgram() {
        glDisable(GL_STENCIL_TEST);
    }
}
