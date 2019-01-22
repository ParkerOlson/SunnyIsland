package com.raven.engine.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Range implements Iterable<Integer> {

    private int start, end, dir;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
        this.dir = end < start ? -1 : 1;
    }

    @Override
    public Iterator<Integer> iterator() {
        final int max = end;

        return new Iterator<Integer>() {

            private int current = start;

            @Override
            public boolean hasNext() {
                if (dir > 0) {
                    return current <= max;
                }
                return current >= max;
            }

            @Override
            public Integer next() {
                if (hasNext()) {
                    int r = current;
                    current += dir;
                    return r;
                } else {
                    throw new NoSuchElementException("Range reached the end");
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Can't remove values from a Range");
            }
        };
    }
}