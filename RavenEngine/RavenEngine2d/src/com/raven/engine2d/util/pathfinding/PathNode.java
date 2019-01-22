package com.raven.engine2d.util.pathfinding;

import java.util.EnumSet;
import java.util.List;

public interface PathNode<N extends PathNode, E extends Enum<E>> {
    List<PathAdjacentNode<N>> getAdjacentNodes(EnumSet<E> flags);
    EnumSet<E> getEmptyNodeEnumSet();
}
