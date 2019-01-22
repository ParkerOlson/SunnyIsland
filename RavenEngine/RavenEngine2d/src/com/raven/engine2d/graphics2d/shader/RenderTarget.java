package com.raven.engine2d.graphics2d.shader;

import com.raven.engine2d.GameProperties;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

public class RenderTarget {

    private int framebuffer_handle;
    private int color_texture, id_texture, depth_texture;

    public RenderTarget(int color, int id, int depth) {
        // fbo
        framebuffer_handle = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handle);

        // Color
        color_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + color);
        glBindTexture(GL_TEXTURE_2D, color_texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                GameProperties.getDisplayWidth(),
                GameProperties.getDisplayHeight(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, color_texture, 0);

        // ID
        id_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 +  id);
        glBindTexture(GL_TEXTURE_2D, id_texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,
                GameProperties.getDisplayWidth(),
                GameProperties.getDisplayHeight(),
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, id_texture, 0);

        // Depth
        depth_texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0 +  depth);
        glBindTexture(GL_TEXTURE_2D, depth_texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT,
                GameProperties.getDisplayWidth(),
                GameProperties.getDisplayHeight(),
                0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depth_texture, 0);

        // Errors
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("Render Target Failed: 0x"
                    + Integer.toHexString(glCheckFramebufferStatus(GL_FRAMEBUFFER)));
        }
    }

    public int getFramebufferHandle() {
        return framebuffer_handle;
    }

    public int getColorTexture() {
        return color_texture;
    }

    public int getIdTexture() {
        return id_texture;
    }

    public int getDepthTexture() {
        return depth_texture;
    }

    public void release() {
        glDeleteTextures(color_texture);
        glDeleteTextures(id_texture);
        glDeleteTextures(depth_texture);
        glDeleteFramebuffers(framebuffer_handle);
    }
}
