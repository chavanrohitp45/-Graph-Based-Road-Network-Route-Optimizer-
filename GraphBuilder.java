import java.util.*;

public class GraphBuilder {

    static class Edge {
        long to;
        double distance;

        Edge(long to, double distance) {
            this.to = to;
            this.distance = distance;
        }
    }

    public static Map<Long, List<Edge>> buildGraph(Map<Long, double[]> nodes, List<long[]> ways) {
        Map<Long, List<Edge>> graph = new HashMap<>();

        for (long[] way : ways) {
            for (int i = 0; i < way.length - 1; i++) {
                long from = way[i];
                long to = way[i + 1];

                double[] fromCoord = nodes.get(from);
                double[] toCoord = nodes.get(to);

                if (fromCoord != null && toCoord != null) {
                    double distance = haversine(fromCoord[0], fromCoord[1], toCoord[0], toCoord[1]);

                    // Add edge from -> to
                    graph.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, distance));

                    // Add edge to -> from (undirected graph, since roads are usually bidirectional)
                    graph.computeIfAbsent(to, k -> new ArrayList<>()).add(new Edge(from, distance));
                }
            }
        }

        return graph;
    }

    // Haversine formula to compute distance between two lat/lon points (in kilometers)
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
