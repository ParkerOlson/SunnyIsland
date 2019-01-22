package com.raven.engine.graphics3d.shader;

import com.raven.engine.GameProperties;
import com.raven.engine.scene.light.GlobalDirectionalLight;
import com.raven.engine.util.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

/**
 * Created by cookedbird on 12/5/17.
 */
public class BasicShader extends Shader {

    private int model_matrix_location, view_matrix_location, projection_matrix_location,
            light_color_location, light_intensity_location, light_direction_location;

    public BasicShader() {
        super("basic_vertex.glsl", "basic_fragment.glsl");

        projection_matrix_location = glGetUniformLocation(getProgramHandel(), "P");
        view_matrix_location = glGetUniformLocation(getProgramHandel(), "V");
        model_matrix_location = glGetUniformLocation(getProgramHandel(), "M");

        light_color_location = glGetUniformLocation(getProgramHandel(), "light_color");
        light_intensity_location = glGetUniformLocation(getProgramHandel(), "light_intensity");
        light_direction_location = glGetUniformLocation(getProgramHandel(), "light_direction");
    }

    @Override
    public void useProgram() {
        super.useProgram();

        glUseProgram(getProgramHandel());

         glViewport(0, 0,
                GameProperties.getScreenWidth(),
                GameProperties.getScreenHeight());

        glClearColor(0.6f, 0.7f, 1f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

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

    public void setUnifromProjectionMatrix(Matrix4f unifromProjectionMatrix) {
        glUniformMatrix4fv(projection_matrix_location, false, unifromProjectionMatrix.toBuffer());
    }

    public void setUnifromVertexMatrix(Matrix4f unifromVertexMatrix) {
        glUniformMatrix4fv(view_matrix_location, false, unifromVertexMatrix.toBuffer());
    }

    public void setUnifromModelMatrix(Matrix4f unifromModelMatrix) {
        glUniformMatrix4fv(model_matrix_location, false, unifromModelMatrix.toBuffer());
    }

    public void setUniformLight(GlobalDirectionalLight uniformLight) {
        glUniform3fv(light_color_location, uniformLight.color.toBuffer());
        glUniform1f(light_intensity_location, uniformLight.intensity);
        glUniform3fv(light_direction_location, uniformLight.getDirection().toBuffer());
    }
}
