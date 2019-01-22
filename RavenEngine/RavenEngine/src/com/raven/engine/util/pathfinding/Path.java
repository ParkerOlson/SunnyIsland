package com.raven.engine.util.pathfinding;

import java.util.ArrayList;

public class Path<N extends PathNode<N>> extends ArrayList<PathAdjacentNode<N>> {

    public int getCost() {
        return stream().mapToInt(PathAdjacentNode::getCost).sum();
    }

    public PathAdjacentNode<N> getLast() {
        return get(size() - 1);
    }
}
