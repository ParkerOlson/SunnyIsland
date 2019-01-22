package com.raven.engine.graphics3d.model.animation;

import com.raven.engine.GameEngine;
import org.lwjgl.system.CallbackI;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnimatedAction {

    private String name;
    private int[] keyframes;
    private List<Bone> bones = new ArrayList<>();
    private Bone root;

    public AnimatedAction(String name) {
        this.name = name;
    }

    public void addBone(Bone bone) {
        bones.add(bone);

        if (bone.getParentName() == null)
            root = bone;
    }

    public String getName() {
        return name;
    }

    public void setKeyframes(int[] keyframes) {
        this.keyframes = keyframes;
    }

    public void structureBones() {
        // TODO calculate bone matrices?
        bones.forEach(b -> bones.stream()
                .filter(p -> p.getName().equals(b.getParentName()))
                .findAny()
                .ifPresent(b::setParent));
    }

    public List<Bone> getBones() {
        return bones;
    }


    public void toBuffer(FloatBuffer aBuffer, AnimationState state) {

        float time = state.getTime() * 24f / 1000f;

        // TODO have the % be calculated
        float frame = time % keyframes[keyframes.length - 1];
        float mix = 0f;

        int i = 0;
        for (; i < keyframes.length; i++)
            if (frame < keyframes[i])
                break;

        final int keyframeIndex = i - 1;

        int keyframeIndex2 = i;
        if (keyframeIndex2 != keyframes.length) {
            float keyframe = keyframes[keyframeIndex];
            float keyframe2 = keyframes[keyframeIndex2];

            float len = keyframe2 - keyframe;
            float pos = frame - keyframe;
            mix = pos / len;
        }

        final float finalMix = mix;

        bones.forEach(bone -> {
            bone.toBuffer(aBuffer, keyframeIndex, finalMix);
        });
    }
}
