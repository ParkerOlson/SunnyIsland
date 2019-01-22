package com.raven.engine2d.graphics2d.sprite;

import com.raven.engine2d.GameEngine;
import com.raven.engine2d.graphics2d.shader.ShaderTexture;
import com.raven.engine2d.graphics2d.shader.Shader;
import com.raven.engine2d.scene.Scene;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class SpriteSheet extends ShaderTexture {

    private int textureName;
    private String filePath;
    private int height;
    private int width;
    private int comp;
    private ByteBuffer buffer;
    private boolean loaded = false;

    public SpriteSheet(GameEngine engine, File f) {
        super(engine);

        filePath = f.getPath();

        try {
            int[] w = new int[1], h = new int[1], c = new int[1];

            buffer = STBImage.stbi_load(filePath, w, h, c, STBImage.STBI_rgb_alpha);

            this.width = w[0];
            this.height = h[0];
            this.comp = c[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(Scene scene) {
        if (!loaded) {
            buffer.flip();

            // Set Texture
            glActiveTexture(GL_TEXTURE0);
            textureName = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureName);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8,
                    width, height,
                    0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

            glBindTexture(GL_TEXTURE_2D, 0);
            scene.addLoadedShaderTexture(this);
            loaded = true;
        }
    }

    @Override
    public int getTexture() {
        return textureName;
    }

    // TODO
    public void release() {
        loaded = false;
        if (textureName != 0) {
//            System.out.println("Del SpriteSheet: " + textureName);
            glDeleteTextures(textureName);
            textureName = 0;
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public CharSequence getFilePath() {
        return filePath;
    }
}
