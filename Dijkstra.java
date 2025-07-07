import java.util.*;

public class Dijkstra {

    public static class Result {
        double distance;
        List<Long> path;

        Result(double distance, List<Long> path) {
            this.distance = distance;
            this.path = path;
        }
    }

    public static Result findShortestPath(Map<Long, List<GraphBuilder.Edge>> graph, long source, long target) {
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> prev = new HashMap<>();
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));

        for (Long node : graph.keySet()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }

        distances.put(source, 0.0);
        pq.add(new long[]{source, 0});

        while (!pq.isEmpty()) {
            long[] current = pq.poll();
            long currentNode = current[0];
            double currentDistance = current[1];

            if (currentNode == target) break;

            if (currentDistance > distances.get(currentNode)) continue;

            for (GraphBuilder.Edge edge : graph.getOrDefault(currentNode, new ArrayList<>())) {
                double newDist = distances.get(currentNode) + edge.distance;
                if (newDist < distances.get(edge.to)) {
                    distances.put(edge.to, newDist);
                    prev.put(edge.to, currentNode);
                    pq.add(new long[]{edge.to, (long) newDist});
                }
            }
        }

        if (!distances.containsKey(target) || distances.get(target) == Double.POSITIVE_INFINITY) {
            return new Result(Double.POSITIVE_INFINITY, new ArrayList<>());
        }

        List<Long> path = new ArrayList<>();
        for (Long at = target; at != null; at = prev.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return new Result(distances.get(target), path);
    }
}
