package com.raven.engine.graphics3d.shader;

import com.raven.engine.GameProperties;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import static org.lwjgl.opengl.GL45.glNamedFramebufferDrawBuffers;

/**
 * Created by cookedbird on 5/29/17.
 */
public class BloomShader extends Shader {

    public static final int
            GLOW = getNextTexture();

    private int texture_glow_location, bloom_step_location, screen_size_location;
    private int framebuffer_handel, renderbuffer_handel, bloom_texture;

    public BloomShader() {
        super("vertex2.glsl", "bloomf.glsl");

        texture_glow_location = glGetUniformLocation(getProgramHandel(), "glowTexture");
        bloom_step_location = glGetUniformLocation(getProgramHandel(), "bloomStep");

        screen_size_location = glGetUniformLocation(getProgramHandel(), "screen_size");

        glLinkProgram(getProgramHandel());

        framebuffer_handel = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

        // FBO Textures
        // Color
        bloom_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + GLOW);
        glBindTexture(GL_TEXTURE_2D, bloom_texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight(),
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, bloom_texture, 0);
        glActiveTexture(GL_TEXTURE0);

        // Draw buffers
        IntBuffer fboBuffers = BufferUtils.createIntBuffer(1);
        int bfs[] = { GL_COLOR_ATTACHMENT0 };
        for (int i = 0; i < bfs.length; i++)
            fboBuffers.put(bfs[i]);
        fboBuffers.flip();

        glNamedFramebufferDrawBuffers(framebuffer_handel, fboBuffers);

        // Depth
        renderbuffer_handel = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderbuffer_handel);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight());
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
                GL_RENDERBUFFER, renderbuffer_handel);

        // Errors
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("FBOHOR Failed: 0x"
                    + Integer.toHexString(glCheckFramebufferStatus(GL_FRAMEBUFFER)));
        }
    }

    @Override
    public void useProgram() {
        super.useProgram();

        glUseProgram(getProgramHandel());

        // Bind the glow
        // set the texture
        glUniform1i(texture_glow_location, WorldMSShader.NORMAL);

        glUniform2f(bloom_step_location,
                1f / GameProperties.getScreenWidth(), 0f);

        glUniform2i(screen_size_location,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight());

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

        glViewport(0, 0,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight());

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void endProgram() {

    }

    public int getBloomTexture() {
        return bloom_texture;
    }
}
