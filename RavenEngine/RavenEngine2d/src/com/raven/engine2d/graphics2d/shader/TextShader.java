package com.raven.engine2d.graphics2d.shader;

import com.raven.engine2d.GameEngine;
import com.raven.engine2d.GameProperties;
import com.raven.engine2d.graphics2d.GameWindow;
import com.raven.engine2d.graphics2d.sprite.SpriteSheet;
import com.raven.engine2d.ui.UITexture;
import com.raven.engine2d.util.math.Vector2i;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import static org.lwjgl.opengl.GL40.glBlendFuncSeparatei;

public class TextShader extends Shader {

    public static final int
            TEXTURE = Shader.getNextTextureID("Text Texture");

    private GameWindow window;

    private int framebuffer_handle;
    private int sprite_sheet_location, rect_location;

    private ShaderTexture desTextrue;

    public TextShader(GameEngine engine, GameWindow window) {
        super("vertex.glsl", "text_fragment.glsl", engine);

        this.window = window;

        sprite_sheet_location = glGetUniformLocation(getProgramHandel(), "spriteSheet");
        rect_location = glGetUniformLocation(getProgramHandel(), "rect");

        // Smallest shader ever?
        // fbo
        framebuffer_handle = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handle);

        // Draw buffers
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        // Errors
        int error = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (error != GL_FRAMEBUFFER_COMPLETE && error != GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
            System.out.println("Text Shader Failed: 0x"
                    + Integer.toHexString(glCheckFramebufferStatus(GL_FRAMEBUFFER)));
            // Ignore FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT (0x8cd7)
            // The image is attached later
        }
    }

    @Override
    public void useProgram() {
        super.useProgram();

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handle);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFuncSeparatei(0, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
    }

    @Override
    public void endProgram() {

    }

    public void setWriteDestination(ShaderTexture desImage) {
        desTextrue = desImage;
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, desImage.getTexture(), 0);
    }

    public void drawImage(SpriteSheet img) {
        glActiveTexture(GL_TEXTURE0 + TEXTURE);
        glBindTexture(GL_TEXTURE_2D, img.getTexture());
        glActiveTexture(GL_TEXTURE0);
        glUniform1i(sprite_sheet_location, TEXTURE);
        glUniform4f(rect_location,
                0,
                0,
                1,
                1);
        window.drawQuad();
    }

    public void clear() {
        if (desTextrue != null) {
            glViewport(0, 0,
                    desTextrue.getHeight(),
                    desTextrue.getWidth());

            glClearColor(0, 0, 0, 0f);
            glClear(GL_COLOR_BUFFER_BIT);
        }
    }

    public void write(Vector2i size, Vector2i src, Vector2i des, ShaderTexture srcImage) {
        window.printErrors("rat");
        glViewport(des.x, des.y, size.x, size.y);
        window.printErrors("cat");

        glActiveTexture(GL_TEXTURE0 + TEXTURE);
        window.printErrors("cata");
        glBindTexture(GL_TEXTURE_2D, srcImage.getTexture());
        window.printErrors("catb");
        glActiveTexture(GL_TEXTURE0);
        window.printErrors("catc");

        glUniform1i(sprite_sheet_location, TEXTURE);
        window.printErrors("dog");

        glUniform4f(rect_location,
                (float) src.x / (float) srcImage.getWidth(),
                (float) (src.y + size.y) / (float) srcImage.getHeight(),
                (float) size.x / (float) srcImage.getWidth(),
                (float) (-size.y) / (float) srcImage.getHeight());
        window.printErrors("mouse");

        window.drawQuad();
        window.printErrors("pig");

    }
}
