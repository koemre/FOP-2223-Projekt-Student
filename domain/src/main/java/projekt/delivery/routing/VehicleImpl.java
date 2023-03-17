package projekt.delivery.routing;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;


class VehicleImpl implements Vehicle {

    private final int id;
    private final double capacity;
    private final List<ConfirmedOrder> orders = new ArrayList<>();
    private final VehicleManagerImpl vehicleManager;
    private final Deque<PathImpl> moveQueue = new LinkedList<>();
    private final VehicleManager.OccupiedRestaurant startingNode;
    private AbstractOccupied<?> occupied;

    public VehicleImpl(
        int id,
        double capacity,
        VehicleManagerImpl vehicleManager,
        VehicleManager.OccupiedRestaurant startingNode) {
        this.id = id;
        this.capacity = capacity;
        this.occupied = (AbstractOccupied<?>) startingNode;
        this.vehicleManager = vehicleManager;
        this.startingNode = startingNode;
    }

    @Override
    public VehicleManager.Occupied<?> getOccupied() {
        return occupied;
    }

    @Override
    public @Nullable VehicleManager.Occupied<?> getPreviousOccupied() {
        AbstractOccupied.VehicleStats stats = occupied.vehicles.get(this);
        return stats == null ? null : stats.previous;
    }

    @Override
    public List<? extends Path> getPaths() {
        return new LinkedList<>(moveQueue);
    }

    void setOccupied(AbstractOccupied<?> occupied) {
        this.occupied = occupied;
    }

    @Override
    public void moveDirect(Region.Node node, BiConsumer<? super Vehicle, Long> arrivalAction) { // H5.4
        if (node == occupied.getComponent()) {
            throw new IllegalArgumentException("Cannot move to own node");
        }
        // check if vehicle is currently on a node
        if (occupied instanceof OccupiedEdgeImpl) {
            Region.Edge currentEdge = ((OccupiedEdgeImpl) occupied).getComponent();
            Region.Node nodeA = currentEdge.getNodeA();
            Region.Node nodeB = currentEdge.getNodeB();
            Region.Node nextInPath = moveQueue.getFirst().nodes.getFirst();
            if (nodeA.equals(nextInPath)) {
                moveQueue.clear();
                moveQueue.add(new PathImpl(vehicleManager.getPathCalculator().getPath(nodeB, nodeA), null));
            } else {
                moveQueue.clear();
                moveQueue.add(new PathImpl(vehicleManager.getPathCalculator().getPath(nodeA, nodeB), null));
            }
            // add the remaining path to the old destination to the moveQueue
        } else {
            moveQueue.clear();
        }
        moveQueued(node, arrivalAction);
    }

    @Override
    public void moveQueued(Region.Node node, BiConsumer<? super Vehicle, Long> arrivalAction) {  //H5.3
        checkMoveToNode(node);

        PathImpl lastPath = moveQueue.peek();
        Region.Node lastNode = lastPath != null ? moveQueue.getLast().nodes.getLast() : (NodeImpl) occupied.getComponent();
        PathImpl newPath = new PathImpl(vehicleManager.getPathCalculator().getPath(lastNode, node), arrivalAction);
        moveQueue.add(newPath);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public double getCapacity() {
        return capacity;
    }

    @Override
    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    @Override
    public VehicleManager.Occupied<? extends Region.Node> getStartingNode() {
        return startingNode;
    }

    @Override
    public Collection<ConfirmedOrder> getOrders() {
        return orders;
    }

    @Override
    public void reset() {
        occupied = (AbstractOccupied<?>) startingNode;
        moveQueue.clear();
        orders.clear();
    }

    private void checkMoveToNode(Region.Node node) {
        if (occupied.component.equals(node) && moveQueue.isEmpty()) {
            throw new IllegalArgumentException("Vehicle " + getId() + " cannot move to own node " + node);
        }
    }

    void move(long currentTick) {
        final Region region = vehicleManager.getRegion();
        if (moveQueue.isEmpty()) {
            return;
        }
        final PathImpl path = moveQueue.peek();
        if (path.nodes().isEmpty()) {
            moveQueue.pop();
            final @Nullable BiConsumer<? super Vehicle, Long> action = path.arrivalAction();
            if (action == null) {
                move(currentTick);
            } else {
                action.accept(this, currentTick);
            }
        } else {
            Region.Node next = path.nodes().peek();
            if (occupied instanceof OccupiedNodeImpl) {
                vehicleManager.getOccupied(region.getEdge(((OccupiedNodeImpl<?>) occupied).getComponent(), next)).addVehicle(this, currentTick);
            } else if (occupied instanceof OccupiedEdgeImpl) {
                vehicleManager.getOccupied(next).addVehicle(this, currentTick);
                path.nodes().pop();
            } else {
                throw new AssertionError("Component must be either node or component");
            }
        }
    }

    void loadOrder(ConfirmedOrder order) { //H5.2
        if (order.getWeight() + getCurrentWeight() > capacity) {
            throw new VehicleOverloadedException(this, order.getWeight() + getCurrentWeight());
        }
        orders.add(order);

    }

    void unloadOrder(ConfirmedOrder order) { //H5.2
        if (orders.remove(order)) {
            orders.remove(order);
        }
    }

    @Override
    public int compareTo(Vehicle o) {
        return Integer.compare(getId(), o.getId());
    }

    @Override
    public String toString() {
        return "VehicleImpl("
            + "id=" + id
            + ", capacity=" + capacity
            + ", orders=" + orders
            + ", component=" + occupied.component
            + ')';
    }

    private record PathImpl(Deque<Region.Node> nodes, BiConsumer<? super Vehicle, Long> arrivalAction) implements Path {

    }
}
