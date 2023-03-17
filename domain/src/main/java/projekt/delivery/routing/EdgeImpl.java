package projekt.delivery.routing;

import org.jetbrains.annotations.NotNull;
import projekt.base.Location;

import java.util.Comparator;
import java.util.Objects;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * Represents a weighted edge in a graph.
 */
@SuppressWarnings("ClassCanBeRecord")
class EdgeImpl implements Region.Edge {

    private final Region region;
    private final String name;
    private final Location locationA;
    private final Location locationB;
    private final long duration;

    /**
     * Creates a new {@link EdgeImpl} instance.
     *
     * @param region    The {@link Region} this {@link EdgeImpl} belongs to.
     * @param name      The name of this {@link EdgeImpl}.
     * @param locationA The start of this {@link EdgeImpl}.
     * @param locationB The end of this {@link EdgeImpl}.
     * @param duration  The length of this {@link EdgeImpl}.
     */
    EdgeImpl(
        Region region,
        String name,
        Location locationA,
        Location locationB,
        long duration
    ) {
        this.region = region;
        this.name = name;
        // locations must be in ascending order
        if (locationA.compareTo(locationB) > 0) {
            throw new IllegalArgumentException(String.format("locationA %s must be <= locationB %s", locationA, locationB));
        }
        this.locationA = locationA;
        this.locationB = locationB;
        this.duration = duration;
    }

    /**
     * Returns the start of this {@link EdgeImpl}.
     *
     * @return The start of this {@link EdgeImpl}.
     */
    public Location getLocationA() {
        return locationA;
    }

    /**
     * Returns the end of this {@link EdgeImpl}.
     *
     * @return The end of this {@link EdgeImpl}.
     */
    public Location getLocationB() {
        return locationB;
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
    public long getDuration() {
        return duration;
    }

    @Override
    public Region.Node getNodeA() { // TODO: H4.1 - remove if implemented
        return region.getNode(this.locationA);
    }


    @Override
    public Region.Node getNodeB() { // TODO: H4.1 - remove if implemented
        return region.getNode(this.locationB);
    }

    @Override
    public int compareTo(Region.@NotNull Edge o) { // TODO: H4.2 - remove if implemented
        Comparator<Region.Edge> compareNodeA = Comparator.comparing(e -> e.getNodeA());
        Comparator<Region.Edge> compareNodeB = Comparator.comparing(e -> e.getNodeB());

        int result = compareNodeA.compare(this, o);
        if (result == 0) {
            result = compareNodeB.compare(this, o);
        }

        return result;
    }

    @Override
    public boolean equals(Object o) { // TODO: H4.3 - remove if implemented
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof EdgeImpl)) {
            return false;
        }
        EdgeImpl other = (EdgeImpl) o;
        return Objects.equals(this.name, other.name)
            && Objects.equals(this.locationA, other.locationA)
            && Objects.equals(this.locationB, other.locationB)
            && Objects.equals(this.duration, other.duration);
    }

    @Override
    public int hashCode() { // TODO: H4.4 - remove if implemented
        return Objects.hash(name, locationA, locationB, duration);
    }

    @Override
    public String toString() { // TODO: H4.5 - remove if implemented
        return "EdgeImpl(name='" + name + "', locationA='" + locationA + "', locationB='" + locationB + "', duration='" + duration + "')";

    }
}
