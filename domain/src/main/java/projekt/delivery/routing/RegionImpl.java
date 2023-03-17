package projekt.delivery.routing;

import org.jetbrains.annotations.Nullable;
import projekt.base.DistanceCalculator;
import projekt.base.EuclideanDistanceCalculator;
import projekt.base.Location;

import java.util.*;

import static org.tudalgo.algoutils.student.Student.crash;

class RegionImpl implements Region {

    private final Map<Location, NodeImpl> nodes = new HashMap<>();
    private final Map<Location, Map<Location, EdgeImpl>> edges = new HashMap<>();
    private final List<EdgeImpl> allEdges = new ArrayList<>();
    private final DistanceCalculator distanceCalculator;

    /**
     * Creates a new, empty {@link RegionImpl} instance using a {@link EuclideanDistanceCalculator}.
     */
    public RegionImpl() {
        this(new EuclideanDistanceCalculator());
    }

    /**
     * Creates a new, empty {@link RegionImpl} instance using the given {@link DistanceCalculator}.
     */
    public RegionImpl(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public @Nullable Node getNode(Location location) { // TODO: H2.1 - remove if implemented
        return nodes.get(location);
    }

    @Override
    public @Nullable Edge getEdge(Location locationA, Location locationB) { // TODO: H2.3 - remove if implemented
        EdgeImpl edges = null;// Alle ausgehenden Kanten von locationA
        EdgeImpl edges2 = null; // Alle ausgehenden Kanten von locationA

        if (this.edges.get(locationA) != null) {
            edges = this.edges.get(locationA).get(locationB);
        }
        if (this.edges.get(locationB) != null) {
            edges2 = this.edges.get(locationB).get(locationA);
        }
        if (edges != null) {
            return edges;
        } else if (edges2 != null) {
            return edges2;
        }

        return null;

    }

    @Override
    public Collection<Node> getNodes() { // TODO: H2.5 - remove if implemented
        return Collections.unmodifiableCollection(this.nodes.values());
    }

    @Override
    public Collection<Edge> getEdges() { // TODO: H2.5 - remove if implemented
        return Collections.unmodifiableCollection(this.allEdges);
    }

    @Override
    public DistanceCalculator getDistanceCalculator() {
        return distanceCalculator;
    }

    /**
     * Adds the given {@link NodeImpl} to this {@link RegionImpl}.
     *
     * @param node the {@link NodeImpl} to add.
     */
    void putNode(NodeImpl node) { // TODO: H2.2 - remove if implemented
        if (this != node.region) {
            throw new IllegalArgumentException("Node " + node.toString() + " has incorrect region");
        }
        nodes.put(node.getLocation(), node);
    }


    /**
     * Adds the given {@link EdgeImpl} to this {@link RegionImpl}.
     *
     * @param edge the {@link EdgeImpl} to add.
     */
    void putEdge(EdgeImpl edge) { // TODO: H2.4 - remove if implemented
        // Überprüfen, ob die Kante selbst in der Region liegt
        if (edge.getRegion() != this) {
            throw new IllegalArgumentException("Edge " + edge + " has incorrect region");
        }
        if (edge.getNodeA() == null) {
            throw new IllegalArgumentException("NodeA " + edge.getLocationA() + " is not part of the region");
        }
        if (edge.getNodeB() == null) {
            throw new IllegalArgumentException("NodeB " + edge.getLocationB() + " is not part of the region");
        }
        // Überprüfen, ob die Knoten der Kante in der Region liegen
        if (this != edge.getNodeA().getRegion()) {
            throw new IllegalArgumentException("NodeA " + edge.getNodeA() + " is not part of the region");
        }
        if (this != edge.getNodeB().getRegion()) {
            throw new IllegalArgumentException("NodeB " + edge.getNodeB() + " is not part of the region");
        }
        Node nodeA = edge.getNodeA();
        Node nodeB = edge.getNodeB();
        // Kante in Map und Liste einfügen
        this.edges.computeIfAbsent(nodeA.getLocation(), k -> new HashMap<>()).put(nodeB.getLocation(), edge);
        Map<Location,EdgeImpl> map = this.edges.get(nodeA.getLocation());
        map.put(nodeB.getLocation(),edge);
        this.edges.put(nodeA.getLocation(),map);
        this.allEdges.add(edge);
    }

    @Override
    public boolean equals(Object o) { // TODO: H2.6 - remove if implemented
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegionImpl region = (RegionImpl) o;
        return Objects.equals(nodes, region.nodes) && Objects.equals(edges, region.edges);
    }

    @Override
    public int hashCode() {  // TODO: H2.7 - remove if implemented
        return Objects.hash(this.nodes, this.allEdges);
    }
}
