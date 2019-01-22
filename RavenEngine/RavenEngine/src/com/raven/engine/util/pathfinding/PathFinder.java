package com.raven.engine.util.pathfinding;

import com.raven.engine.Game;
import com.raven.engine.GameEngine;

import java.util.*;
import java.util.stream.Collectors;

public class PathFinder<N extends PathNode<N>> {

    public HashMap<N, Path<N>> findDistance(N start, int dist) {
//        dist += 1; // take the starting cost into account

        HashMap<N, Path<N>> nodeMap = new HashMap<>();
        HashMap<N, Path<N>> unresolvedPaths = new HashMap<>();

        List<PathAdjacentNode<N>> neighbors = start.getAdjacentNodes();

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

        findPaths(nodeMap, unresolvedPaths, dist);

        return nodeMap;
    }

    private void findPaths(HashMap<N, Path<N>> nodeMap, HashMap<N, Path<N>> unresolvedPaths, int dist) {

        HashMap<N, Path<N>> nextUnresolvedPaths = new HashMap<>();

        for (Path<N> unresolvedPath : unresolvedPaths.values()) {
            List<PathAdjacentNode<N>> neighbors = unresolvedPath.getLast().getNode().getAdjacentNodes();

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
            findPaths(nodeMap, nextUnresolvedPaths, dist);
        }
    }

    // TODO fix this shitty code
    public Path<N> findTarget(N start, N target) {
        HashMap<N, Path<N>> catMap = findDistance(start, 100);

        Path<N> cat = catMap.get(target);

        if (cat == null) {
            Optional<Path<N>> maybeCat = target.getAdjacentNodes().stream()
                    .map(an -> catMap.get(an.getNode()))
                    .filter(Objects::nonNull)
                    .min(Comparator.comparingInt(Path::getCost));

            if (maybeCat.isPresent())
                cat = maybeCat.get();
        }

        return cat;
    }
}
