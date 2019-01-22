package com.raven.engine.graphics3d.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by cookedbird on 11/8/17.
 */
public class PlyImporter {

    static public ModelData Import(File file, ModelData model) {
        try {
            if (model == null) {
                model = new ModelData();
            }

            BufferedReader br;
            br = new BufferedReader(new FileReader(file));

            // check that the first line is ply
            String line = br.readLine();
            if (!line.equals("ply")) {
                System.err.printf(String.format(
                        "File %s doesn't contain magic ply%n",
                        file.getName()));
                return null;
            }

            // check the format (lol)
            line = br.readLine();
            if (!line.equals("format ascii 1.0")) {
                System.err.printf(String.format(
                        "File %s doesn't contain ascii 1.0 format%n",
                        file.getName()));
                return null;
            }

            // skip if comment
            line = br.readLine();
            String[] lineData = line.split(" ");
            if (lineData[0].equals("comment")) {
                line = br.readLine();
                lineData = line.split(" ");
            }

            // verteces and faces
            int vertex_count = 0;
            int face_count = 0;

            // loop for each element
            while (!(line.equals("end_header") || line == null)) {
                if (lineData[0].equals("element")) {
                    if (lineData[1].equals("vertex")) {
                        vertex_count = Integer.parseInt(lineData[2]);
                    } else if (lineData[1].equals("face")) {
                        face_count = Integer.parseInt(lineData[2]);
                    }
                }

                line = br.readLine();
                lineData = line.split(" ");
            }

            // load the vertices data
            List<VertexData> vertices = new ArrayList<>();
            for (int i = 0; i < vertex_count; i++) {
                line = br.readLine();
                lineData = line.split(" ");

                vertices.add(new VertexData(lineData, VertexData.Type.PLY));
            }

            // load the faces
            for (int i = 0; i < face_count; i++) {
                line = br.readLine();
                lineData = line.split(" ");

                Stream.of(
                        Integer.parseInt(lineData[1]),
                        Integer.parseInt(lineData[2]),
                        Integer.parseInt(lineData[3])
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
