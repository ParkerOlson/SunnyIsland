package com.raven.engine2d.ui;

import com.raven.engine2d.GameEngine;
import com.raven.engine2d.graphics2d.shader.Shader;
import com.raven.engine2d.graphics2d.shader.ShaderTexture;
import com.raven.engine2d.scene.Scene;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class UITexture
        extends ShaderTexture {

    private int width, height, texture;

    public UITexture(GameEngine engine, int width, int height) {
        super(engine);

        this.width = width;
        this.height = height;
    }

    private boolean loaded = false;

    @Override
    public void load(Scene scene) {
        if (!loaded) {
            // Set Texture

            glActiveTexture(GL_TEXTURE0);

            if (texture == 0) {
                texture = glGenTextures();
//                System.out.println("Gen UITexture: " + texture);
            }

            glBindTexture(GL_TEXTURE_2D, texture);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8,
                    width, height,
                    0, GL_RGBA, GL_UNSIGNED_BYTE, 0);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        }

        scene.addLoadedShaderTexture(this);
        loaded = true;
    }

    @Override
    public int getTexture() {
        return texture;
    }

    @Override
    public final int getWidth() {
        return width;
    }

    @Override
    public final int getHeight() {
        return height;
    }

    @Override
    public void release() {
        // TODO make sure this is correct
        loaded = false;
        if (texture != 0) {
            glDeleteTextures(texture);
//            System.out.println("Del UITexture: " + texture);
            texture = 0;
        }
    }
}
