package com.raven.engine.util;

public abstract class Factory<T> {
    public abstract T getInstance();
    public abstract void clear();
}
