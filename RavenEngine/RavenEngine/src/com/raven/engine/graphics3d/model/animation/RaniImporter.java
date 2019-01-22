package com.raven.engine.graphics3d.model.animation;

import com.raven.engine.util.math.Quaternion;
import com.raven.engine.util.math.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RaniImporter {

    static public Animation Import(File file) {
        try {
            BufferedReader br;
            br = new BufferedReader(new FileReader(file));

            Animation animation = new Animation(file.getPath());

            String line;

            boolean hasAction = true;
            while (hasAction) {
                // action name
                line = br.readLine();
                AnimatedAction action = new AnimatedAction(line.toLowerCase());

                // keyframes
                line = br.readLine();
                String[] frames = line.split(" ");
                int[] keyframes = new int[frames.length];

                for (int i = 0; i < frames.length; i++) {
                    keyframes[i] = Integer.parseInt(frames[i]);
                }
                action.setKeyframes(keyframes);

                // bones
                line = br.readLine();
                boolean hasBone = true;
                while (hasBone) {
                    // bone name
                    Bone bone = new Bone(line);

                    bone.setKeyframesLength(keyframes.length);

                    // parent
                    line = br.readLine();
                    if (line.equals("") || line.equals("None")) {
                        line = null;
                    }
                    bone.setParentName(line);

                    // location
                    line = br.readLine();
                    String[] vals = line.split(" ");
                    Vector3f[] location = new Vector3f[keyframes.length];
                    for (int i = 0; i < keyframes.length; i++) {
                        location[i] = new Vector3f(
                                Float.parseFloat(vals[i * 3]),
                                Float.parseFloat(vals[i * 3 + 1]),
                                Float.parseFloat(vals[i * 3 + 2])
                        );
                    }
                    bone.setLocation(location);

                    // rotation
                    line = br.readLine();
                    vals = line.split(" ");
                    Quaternion[] rotation = new Quaternion[keyframes.length];
                    for (int i = 0; i < keyframes.length; i++) {
                        rotation[i] = new Quaternion(
                                Float.parseFloat(vals[i * 4 ]),
                                Float.parseFloat(vals[i * 4 + 2]),
                                Float.parseFloat(vals[i * 4 + 1]),
                                Float.parseFloat(vals[i * 4 + 3])
                        );
//                        rotation[i] = new Quaternion(
//                                Float.parseFloat(vals[i * 4 ]),
//                                Float.parseFloat(vals[i * 4 + 3]),
//                                Float.parseFloat(vals[i * 4 + 1]),
//                                Float.parseFloat(vals[i * 4 + 2])
//                        );
                        System.out.println(rotation[i]);
                    }
                    bone.setRotation(rotation);

                    // scale
                    line = br.readLine();
                    vals = line.split(" ");
                    Vector3f[] scale = new Vector3f[keyframes.length];
                    for (int i = 0; i < keyframes.length; i++) {
                        scale[i] = new Vector3f(
                                Float.parseFloat(vals[i * 3]),
                                Float.parseFloat(vals[i * 3 + 1]),
                                Float.parseFloat(vals[i * 3 + 2])
                        );
                    }
                    bone.setScale(scale);

                    // vector
                    line = br.readLine();
                    vals = line.split(" ");
                    Vector3f[] vector = new Vector3f[keyframes.length];
                    for (int i = 0; i < keyframes.length; i++) {
                        vector[i] = new Vector3f(
                                Float.parseFloat(vals[i * 3]),
                                Float.parseFloat(vals[i * 3 + 1]),
                                Float.parseFloat(vals[i * 3 + 2])
                        );
                    }
                    bone.setVector(vector);

                    // head
                    line = br.readLine();
                    vals = line.split(" ");
                    Vector3f[] head = new Vector3f[keyframes.length];
                    for (int i = 0; i < keyframes.length; i++) {
                        head[i] = new Vector3f(
                                Float.parseFloat(vals[i * 3]),
                                Float.parseFloat(vals[i * 3 + 2]),
                                Float.parseFloat(vals[i * 3 + 1])
                        );
                    }
                    bone.setHead(head);

                    // tail
                    line = br.readLine();
                    vals = line.split(" ");
                    Vector3f[] tail = new Vector3f[keyframes.length];
                    for (int i = 0; i < keyframes.length; i++) {
                        tail[i] = new Vector3f(
                                Float.parseFloat(vals[i * 3]),
                                Float.parseFloat(vals[i * 3 + 2]),
                                Float.parseFloat(vals[i * 3 + 1])
                        );
                    }
                    bone.setTail(tail);

                    // bone or action?
                    line = br.readLine();
                    if (line == null) {
                        hasBone = false;
                        hasAction = false;
                    } else if (line.trim().equals("")) {
                        hasBone = false;
                    }

                    action.addBone(bone);
                }

                action.structureBones();
                animation.addAction(action);
            }

            return animation;
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }
}
