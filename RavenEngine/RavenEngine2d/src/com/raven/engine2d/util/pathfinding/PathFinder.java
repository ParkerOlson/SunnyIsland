package com.raven.engine2d.util.pathfinding;

import java.util.*;

public class PathFinder<N extends PathNode<N, E>, E extends Enum<E>> {

    public HashMap<N, Path<N>> findDistance(N start, int dist) {
        return findDistance(start, dist, start.getEmptyNodeEnumSet());
    }

    public HashMap<N, Path<N>> findDistance(N start, int dist, EnumSet<E> flags) {
//        dist += 1; // take the starting cost into account

        HashMap<N, Path<N>> nodeMap = new HashMap<>();
        HashMap<N, Path<N>> unresolvedPaths = new HashMap<>();

        List<PathAdjacentNode<N>> neighbors = start.getAdjacentNodes(flags);

        for (PathAdjacentNode<N> n : neighbors) {
            Path<N> path = new Path<>();
            path.add(new PathAdjacentNode<>(start, 0));
            path.add(n);

            if (path.getCost() == dist) {
                nodeMap.put(n.getNode(), path);
            } else if (path.getCost() < dist) {
                nodeMap.put(n.getNode(), path);
                unresolvedPaths.put(n.getNode(), path);
            }
        }

        findPaths(nodeMap, unresolvedPaths, dist, flags);

        return nodeMap;
    }

    private void findPaths(HashMap<N, Path<N>> nodeMap, HashMap<N, Path<N>> unresolvedPaths, int dist, EnumSet<E> flags) {

        HashMap<N, Path<N>> nextUnresolvedPaths = new HashMap<>();

        for (Path<N> unresolvedPath : unresolvedPaths.values()) {
            List<PathAdjacentNode<N>> neighbors = unresolvedPath.getLast().getNode().getAdjacentNodes(flags);

            for (PathAdjacentNode<N> n : neighbors) {
                Path<N> oldPath = nodeMap.get(n.getNode());

                Path<N> path = new Path<>();
                path.addAll(unresolvedPath);
                path.add(n);

                int cost = path.getCost();

                if (oldPath == null || oldPath.getCost() > cost) {

                    if (cost == dist) {
                        nodeMap.put(n.getNode(), path);
                    } else if (cost < dist) {
                        nodeMap.put(n.getNode(), path);
                        nextUnresolvedPaths.put(n.getNode(), path);
                    }
                }
            }
        }

        if (nextUnresolvedPaths.size() > 0) {
            findPaths(nodeMap, nextUnresolvedPaths, dist, flags);
        }
    }

    public Path<N> findTarget(N start, N target) {
        return findTarget(start, target, start.getEmptyNodeEnumSet());
    }

    public Path<N> findTarget(N start, N target, E flag) {
        return findTarget(start, target, EnumSet.of(flag));
    }

    // TODO fix this shitty code - but does it work?
    public Path<N> findTarget(N start, N target, EnumSet<E> flags) {
        HashMap<N, Path<N>> catMap = findDistance(start, 100, flags);

        Path<N> cat = catMap.get(target);

        if (cat == null) {
            Optional<Path<N>> maybeCat = target.getAdjacentNodes(flags).stream()
                    .map(an -> catMap.get(an.getNode()))
                    .filter(Objects::nonNull)
                    .min(Comparator.comparingInt(Path::getCost));

            if (maybeCat.isPresent())
                cat = maybeCat.get();
        }

        return cat;
    }
}
