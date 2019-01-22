package com.raven.engine.graphics3d.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RavImporter {

    static public ModelData Import(File file, ModelData model) {
        try {
            if (model == null) {
                model = new ModelData();
            }

            BufferedReader br;
            br = new BufferedReader(new FileReader(file));

            // check that the first line is ply
            String line = br.readLine();
            if (!line.equals("rav")) {
                System.err.printf(String.format(
                        "File %s doesn't contain magic rav%n",
                        file.getName()));
                return null;
            }

            // verteces and faces
            int vertex_count = 0;
            int face_count = 0;

            line = br.readLine();
            String[] lineData = line.split(" ");
            vertex_count = Integer.parseInt(lineData[0]);
            face_count = Integer.parseInt(lineData[1]);

            // load the vertices data
            List<VertexData> vertices = new ArrayList<>();
            for (int i = 0; i < vertex_count; i++) {
                line = br.readLine();
                lineData = line.split(" ");

                vertices.add(new VertexData(lineData, VertexData.Type.RAV));
            }

            // load the faces
            for (int i = 0; i < face_count; i++) {
                line = br.readLine();
                lineData = line.split(" ");

                Stream.of(
                        Integer.parseInt(lineData[0]),
                        Integer.parseInt(lineData[1]),
                        Integer.parseInt(lineData[2])
                ).map(vertices::get)
                        .forEach(model::addVertex);
            }



            return model;
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }
}
