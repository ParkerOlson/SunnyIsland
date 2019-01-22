package com.raven.engine.graphics3d.shader;

import com.raven.engine.GameEngine;
import com.raven.engine.GameProperties;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

/**
 * Created by cookedbird on 11/17/17.
 */
public class IDShader extends Shader {

    private int texture_id_location, coord_location, framebuffer_handel, id_texture;

    public IDShader() {
        super("vertex2.glsl", "id_fragment.glsl");

        glBindAttribLocation(getProgramHandel(), 0, "vertex_pos");

        texture_id_location = glGetUniformLocation(getProgramHandel(), "idTexture");

        coord_location = glGetUniformLocation(getProgramHandel(), "coord");

        glLinkProgram(getProgramHandel());

        // framebuffer
        framebuffer_handel = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

        // ID
        id_texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id_texture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,
                1,
                1,
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, id_texture, 0);

        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        // Errors
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("ID Shader Failed: 0x"
                    + Integer.toHexString(glCheckFramebufferStatus(GL_FRAMEBUFFER)));
        }
    }

    @Override
    public void useProgram() {
        super.useProgram();

        glUseProgram(getProgramHandel());

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);

        glViewport(0, 0,
                1, 1);

        glClearColor( 0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        // Enable the custom mode attribute (not sure if correct)
        glEnableVertexAttribArray(0);

        // Bind the id
        glUniform1i(texture_id_location, WorldMSShader.ID);

        // Bind the mouse
        glUniform2i(coord_location,
                (int)GameEngine.getEngine().getMouse().getX(),
                GameProperties.getScreenHeight() - (int)GameEngine.getEngine().getMouse().getY());
    }

    @Override
    public void endProgram() {
        // Disable the custom mode attribute
        glDisableVertexAttribArray(0);
    }

    private IntBuffer pixelReadBuffer = BufferUtils.createIntBuffer(1);
    public int getWorldObjectID() {
//        glFlush();
//        glFinish();

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer_handel);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glReadBuffer(GL_COLOR_ATTACHMENT0);
        glReadPixels(0, 0, 1, 1,
                GL_RGB, GL_UNSIGNED_BYTE, pixelReadBuffer);

        int id = pixelReadBuffer.get();

        pixelReadBuffer.flip();

        return id;
    }
}
