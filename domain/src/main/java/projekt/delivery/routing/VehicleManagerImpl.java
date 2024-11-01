package projekt.delivery.routing;

import projekt.base.Location;
import projekt.delivery.event.Event;
import projekt.delivery.event.EventBus;
import projekt.delivery.event.SpawnEvent;

import java.util.*;

import static org.tudalgo.algoutils.student.Student.crash;

class VehicleManagerImpl implements VehicleManager {

    final Map<Region.Node, OccupiedNodeImpl<? extends Region.Node>> occupiedNodes;
    final Map<Region.Edge, OccupiedEdgeImpl> occupiedEdges;
    private final Region region;
    private final PathCalculator pathCalculator;
    private final List<VehicleImpl> vehiclesToSpawn = new ArrayList<>();
    private final List<VehicleImpl> vehicles = new ArrayList<>();
    private final Collection<Vehicle> unmodifiableVehicles = Collections.unmodifiableCollection(vehicles);
    private final EventBus eventBus = new EventBus();

    VehicleManagerImpl(
        Region region,
        PathCalculator pathCalculator
    ) {
        this.region = region;
        this.pathCalculator = pathCalculator;
        occupiedNodes = toOccupiedNodes(region.getNodes());
        occupiedEdges = toOccupiedEdges(region.getEdges());
    }

    private Map<Region.Node, OccupiedNodeImpl<? extends Region.Node>> toOccupiedNodes(Collection<Region.Node> nodes) { // TODO: H6.1 - remove if implemented
            Map<Region.Node, OccupiedNodeImpl<? extends Region.Node>> occupiedNodes = new HashMap<>();
            for (Region.Node node : nodes) {
                if (node instanceof Region.Restaurant restaurant) {
                    occupiedNodes.put(node,new OccupiedRestaurantImpl(restaurant,this));
                } else if (node instanceof Region.Neighborhood neighborhood) {
                    OccupiedNodeImpl<Region.Neighborhood> occupiedNode = new OccupiedNeighborhoodImpl(neighborhood, this);
                    occupiedNodes.put(node, occupiedNode);
                } else {
                    OccupiedNodeImpl<?> occupiedNode = new OccupiedNodeImpl<>(node, this);
                    occupiedNodes.put(node, occupiedNode);
                }
            }
            return Collections.unmodifiableMap(occupiedNodes);
    }

    private Map<Region.Edge, OccupiedEdgeImpl> toOccupiedEdges(Collection<Region.Edge> edges) { // TODO: H6.1 - remove if implemented
        Map<Region.Edge, OccupiedEdgeImpl> occupiedEdges = new HashMap<>();
        for (Region.Edge edge : edges) {
            OccupiedEdgeImpl occupiedEdge = new OccupiedEdgeImpl(edge, this);
            occupiedEdges.put(edge, occupiedEdge);
        }
        return Collections.unmodifiableMap(occupiedEdges);
    }

    private Set<AbstractOccupied<?>> getAllOccupied() { // TODO: H6.2 - remove if implemented

        Set<AbstractOccupied<?>> set = new HashSet<>();
        set.addAll(occupiedEdges.values());
        set.addAll(occupiedNodes.values());
        return set;

    }

    private OccupiedNodeImpl<? extends Region.Node> getOccupiedNode(Location location) {
        return occupiedNodes.values().stream()
            .filter(node -> node.getComponent().getLocation().equals(location))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Could not find node with given predicate"));
    }

    @Override
    public Region getRegion() {
        return region;
    }

    @Override
    public PathCalculator getPathCalculator() {
        return pathCalculator;
    }

    @Override
    public Collection<Vehicle> getVehicles() {
        return unmodifiableVehicles;
    }

    @Override
    public Collection<Vehicle> getAllVehicles() {
        Collection<Vehicle> allVehicles = new ArrayList<>(getVehicles());
        allVehicles.addAll(vehiclesToSpawn);
        return allVehicles;
    }

    @Override
    public <C extends Region.Component<C>> AbstractOccupied<C> getOccupied(C component) { // TODO: H6.3 - remove if implemented
        if (component == null) {
            throw new NullPointerException("Component is null!");
        }

        if (component instanceof Region.Node) {
            Region.Node node = (Region.Node) component;
            if (occupiedNodes.containsKey(node)) {
                return (AbstractOccupied<C>) occupiedNodes.get(node);
            } else {
                throw new IllegalArgumentException("Could not find occupied node for " + component);
            }
        } else if (component instanceof Region.Edge) {
            Region.Edge edge = (Region.Edge) component;
            if (occupiedEdges.containsKey(edge)) {
                return (AbstractOccupied<C>) occupiedEdges.get(edge);
            } else {
                throw new IllegalArgumentException("Could not find occupied edge for " + component);
            }
        } else {
            throw new IllegalArgumentException("Component is not of recognized subtype: " + component.getClass().getName());
        }
    }

    @Override
    public List<OccupiedRestaurant> getOccupiedRestaurants() {
        return occupiedNodes.values().stream()
            .filter(OccupiedRestaurant.class::isInstance)
            .map(OccupiedRestaurant.class::cast)
            .toList();
    }

    @Override
    public OccupiedRestaurant getOccupiedRestaurant(Region.Node node) { // TODO: H6.4- remove if implemented
        if (node == null) {
            throw new NullPointerException("Node is null!");
        }
        if(occupiedNodes.get(node) instanceof OccupiedRestaurant){
            return (OccupiedRestaurant) occupiedNodes.get(node);
        }
        throw new IllegalArgumentException("Node " + node + " is not a restaurant");
    }

    @Override
    public Collection<OccupiedNeighborhood> getOccupiedNeighborhoods() {
        return occupiedNodes.values().stream()
            .filter(OccupiedNeighborhood.class::isInstance)
            .map(OccupiedNeighborhood.class::cast)
            .toList();
    }

    @Override
    public OccupiedNeighborhood getOccupiedNeighborhood(Region.Node node) { // TODO: H6.4 - remove if implemented
        if (node == null) {
            throw new NullPointerException("Node is null!");
        }
        if(occupiedNodes.get(node) instanceof OccupiedNeighborhood){
            return (OccupiedNeighborhood) occupiedNodes.get(node);
        }
            throw new IllegalArgumentException("Node " + node + " is not a neighborhood");
    }

    @Override
    public Collection<Occupied<? extends Region.Node>> getOccupiedNodes() {
        return Collections.unmodifiableCollection(occupiedNodes.values());
    }

    @Override
    public Collection<Occupied<? extends Region.Edge>> getOccupiedEdges() {
        return Collections.unmodifiableCollection(occupiedEdges.values());
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public List<Event> tick(long currentTick) {
        for (VehicleImpl vehicle : vehiclesToSpawn) {
            spawnVehicle(vehicle, currentTick);
        }
        vehiclesToSpawn.clear();
        // It is important that nodes are ticked before edges
        // This only works because edge ticking is idempotent
        // Otherwise, there may be two state changes in a single tick.
        // For example, a node tick may move a vehicle onto an edge.
        // Ticking this edge afterwards does not move the vehicle further along the edge
        // compared to a vehicle already on the edge.
        occupiedNodes.values().forEach(occupiedNode -> occupiedNode.tick(currentTick));
        occupiedEdges.values().forEach(occupiedEdge -> occupiedEdge.tick(currentTick));
        return eventBus.popEvents(currentTick);
    }

    public void reset() {
        for (AbstractOccupied<?> occupied : getAllOccupied()) {
            occupied.reset();
        }

        for (Vehicle vehicle : getAllVehicles()) {
            vehicle.reset();
        }

        vehiclesToSpawn.addAll(getVehicles().stream()
            .map(VehicleImpl.class::cast)
            .toList());

        vehicles.clear();
    }

    @SuppressWarnings("UnusedReturnValue")
    Vehicle addVehicle(
        Location startingLocation,
        double capacity
    ) {
        OccupiedNodeImpl<? extends Region.Node> occupied = getOccupiedNode(startingLocation);

        if (!(occupied instanceof OccupiedRestaurant)) {
            throw new IllegalArgumentException("Vehicles can only spawn at restaurants!");
        }

        final VehicleImpl vehicle = new VehicleImpl(
            vehicles.size() + vehiclesToSpawn.size(),
            capacity,
            this,
            (OccupiedRestaurant) occupied);
        vehiclesToSpawn.add(vehicle);
        vehicle.setOccupied(occupied);
        return vehicle;
    }

    private void spawnVehicle(VehicleImpl vehicle, long currentTick) {
        vehicles.add(vehicle);
        OccupiedRestaurantImpl warehouse = (OccupiedRestaurantImpl) vehicle.getOccupied();
        warehouse.vehicles.put(vehicle, new AbstractOccupied.VehicleStats(currentTick, null));
        getEventBus().queuePost(SpawnEvent.of(currentTick, vehicle, warehouse.getComponent()));
    }
}
