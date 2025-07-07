import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.*;

public class OSMParser {

    public static void main(String[] args) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
                Map<Long, double[]> nodes = new HashMap<>();
                List<long[]> ways = new ArrayList<>();
                boolean isHighway = false;
                List<Long> currentWayNodes = new ArrayList<>();

                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    if (qName.equals("node")) {
                        long id = Long.parseLong(attributes.getValue("id"));
                        double lat = Double.parseDouble(attributes.getValue("lat"));
                        double lon = Double.parseDouble(attributes.getValue("lon"));
                        nodes.put(id, new double[]{lat, lon});
                    } else if (qName.equals("way")) {
                        currentWayNodes.clear();
                        isHighway = false;
                    } else if (qName.equals("nd")) {
                        long ref = Long.parseLong(attributes.getValue("ref"));
                        currentWayNodes.add(ref);
                    } else if (qName.equals("tag") && "highway".equals(attributes.getValue("k"))) {
                        isHighway = true;
                    }
                }

                public void endElement(String uri, String localName, String qName) {
                    if (qName.equals("way") && isHighway) {
                        ways.add(currentWayNodes.stream().mapToLong(l -> l).toArray());
                    }
                }

                public void endDocument() {
                    System.out.println("Parsed Nodes: " + nodes.size());
                    System.out.println("Parsed Highways: " + ways.size());

                    Map<Long, List<GraphBuilder.Edge>> graph = GraphBuilder.buildGraph(nodes, ways);
                    System.out.println("Graph built with " + graph.size() + " nodes.");

                    long source = nodes.keySet().iterator().next();
                    long target = new ArrayList<>(nodes.keySet()).get(nodes.size() / 2);

                    Dijkstra.Result result = Dijkstra.findShortestPath(graph, source, target);
                    if (result.distance == Double.POSITIVE_INFINITY) {
                        System.out.println("No path found between " + source + " and " + target);
                    } else {
                        System.out.println("Shortest Distance: " + result.distance + " km");
                        System.out.println("Path:");
                        for (Long nodeId : result.path) {
                            double[] coords = nodes.get(nodeId);
                            String areaName = ReverseGeocoder.getAddress(coords[0], coords[1]);
                            System.out.printf("Node ID: %d | Lat: %.6f | Lon: %.6f | Area Name: %s%n",
                                    nodeId, coords[0], coords[1], areaName);
                            // ✅ Respect API Limit
                                try {
                                    Thread.sleep(1500);  // Respect API rate limit
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // 1.5 second pause per Nominatim API rules
                        }
                    }
                }
            };

            saxParser.parse(new File("mumbai-region.osm"), handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
