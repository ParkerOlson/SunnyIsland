package com.raven.breakingsands;

public enum ZLayer {
    TERRAIN(.1f), PAWN(.052f), WEAPON(.01f), DECAL(.05f), EFFECT(.01f);

    private float value;
    ZLayer(float value) {
        this.value = value;
    }
    public float getValue() {
        return value;
    }
}
