package com.raven.engine.graphics3d.shader;

import com.raven.engine.GameProperties;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

/**
 * Created by cookedbird on 12/8/17.
 */
public abstract class LightShader extends Shader {
    public static int LIGHT = getNextTexture();

    protected static int framebuffer_handel = 0;

    private static int light_texture;

    public LightShader(String fragment_shader) {
        super("vertex2.glsl", fragment_shader);

        if (framebuffer_handel == 0) {
            framebuffer_handel = glGenFramebuffers();
            glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

            // FBO Textures
            // Color
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

            // Draw Buffers
            glDrawBuffer(GL_COLOR_ATTACHMENT0);

            // Errors
            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
                System.out.println("Light Shader Failed: 0x"
                        + Integer.toHexString(glCheckFramebufferStatus(GL_FRAMEBUFFER)));
            }

            System.out.println("Lighted " + framebuffer_handel);
        }
    }

    public void useProgram() {
        super.useProgram();

        glUseProgram(getProgramHandel());

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        glBlendEquation(GL_FUNC_ADD);

        glDisable(GL_DEPTH_TEST);
    }

    public static void clear() {
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);
    }
}
