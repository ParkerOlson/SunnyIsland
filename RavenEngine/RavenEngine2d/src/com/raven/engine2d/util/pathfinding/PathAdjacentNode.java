package com.raven.engine2d.util.pathfinding;

public class PathAdjacentNode<N extends PathNode> {
    private N node;
    private int cost;

    public PathAdjacentNode(N node, int cost) {
        this.node = node;
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    public N getNode() {
        return node;
    }
}
