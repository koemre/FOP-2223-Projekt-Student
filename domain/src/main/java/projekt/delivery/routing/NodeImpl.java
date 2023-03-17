package projekt.delivery.routing;

import org.jetbrains.annotations.Nullable;
import projekt.base.Location;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.tudalgo.algoutils.student.Student.crash;

class NodeImpl implements Region.Node {

    protected final Set<Location> connections;
    protected final Region region;
    protected final String name;
    protected final Location location;

    /**
     * Creates a new {@link NodeImpl} instance.
     * @param region The {@link Region} this {@link NodeImpl} belongs to.
     * @param name The name of this {@link NodeImpl}.
     * @param location The {@link Location} of this {@link EdgeImpl}.
     * @param connections All {@link Location}s this {@link NeighborhoodImpl} has an {@link Region.Edge} to.
     */
    NodeImpl(
        Region region,
        String name,
        Location location,
        Set<Location> connections
    ) {
        this.region = region;
        this.name = name;
        this.location = location;
        this.connections = connections;
    }

    @Override
    public Region getRegion() {
        return region;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public Set<Location> getConnections() {
        return connections;
    }

    @Override
    public @Nullable Region.Edge getEdge(Region.Node other) { // TODO: H3.1 - remove if implemented
        return this.region.getEdge(this.location,other.getLocation());
    }

    @Override
    public Set<Region.Node> getAdjacentNodes() { // TODO: H3.2 - remove if implemented
        Set<Region.Node> adjacentNodes = new HashSet<>();
        for (Location location1 : connections) {
            adjacentNodes.add(this.region.getNode(location1));
        }
        //adjacentNodes.add(this); // add current node itself if it has a self-loop edge
        return adjacentNodes;
    }

    @Override
    public Set<Region.Edge> getAdjacentEdges() { // TODO: H3.3 - remove if implemented
        Set<Region.Edge> adjacentEdges = new HashSet<>();
        for (Region.Node node : getAdjacentNodes()) {
            adjacentEdges.add(region.getEdge(this.location,node.getLocation()));
        }
        return adjacentEdges;
    }

    @Override
    public int compareTo(Region.Node o) { // TODO: H3.4 - remove if implemented
         return this.location.compareTo(o.getLocation());
    }

    @Override
    public boolean equals(Object o) { // TODO: H3.5 - remove if implemented
        if (o == null || !(o instanceof NodeImpl)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        NodeImpl other = (NodeImpl) o;
        return Objects.equals(this.name, other.name)
            && Objects.equals(this.location, other.location)
            && Objects.equals(this.connections, other.connections);
        }


    @Override
    public int hashCode() { // TODO: H3.6 - remove if implemented
        return Objects.hash(name, location, connections);
    }

    @Override
    public String toString() { // TODO: H3.7 - remove if implemented
        return String.format("NodeImpl(name='%s', location='%s', connections='%s')",
            name, location, connections);
    }
}
