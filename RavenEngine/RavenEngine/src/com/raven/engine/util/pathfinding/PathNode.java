package com.raven.engine.util.pathfinding;

import java.util.List;

public interface PathNode<N extends PathNode> {
    List<PathAdjacentNode<N>> getAdjacentNodes();
}
