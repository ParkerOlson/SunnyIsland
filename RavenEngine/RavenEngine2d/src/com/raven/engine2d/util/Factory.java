package com.raven.engine2d.util;

public abstract class Factory<T> {
    public abstract T getInstance();
    public abstract void clear();
}
