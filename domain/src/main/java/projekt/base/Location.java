package projekt.base;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * A tuple for the x- and y-coordinates of a point.
 */
@SuppressWarnings("ClassCanBeRecord")
public final class Location implements Comparable<Location> {

    private final static Comparator<Location> COMPARATOR =
        Comparator.comparing(Location::getX).thenComparing(Location::getY);

    private final int x;
    private final int y;

    /**
     * Instantiates a new {@link Location} object using {@code x} and {@code y} as coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate of this location.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of this location.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Adds the coordinates of this location and the other location and returns a new
     * {@link Location} object with the resulting coordinates.
     *
     * @param other the other {@link Location} object to get the second set of coordinates from
     * @return a new {@link Location} object with the sum of coordinates from both locations
     */
    public Location add(Location other) {
        return new Location(x + other.x, y + other.y);
    }

    /**
     * Subtracts the coordinates of this location from the other location and returns a new
     * {@link Location} object with the resulting coordinates.
     *
     * @param other the other {@link Location} object to get the second set of coordinates from
     * @return a new {@link Location} object with the difference of coordinates from both locations
     */
    public Location subtract(Location other) {
        return new Location(x - other.x, y - other.y);
    }

    @Override
    public int compareTo(@NotNull Location o) { // TODO: H1.1 - remove if implemented
        if (this.x == o.x && this.y == o.y) {
            return 0;
        } else if (this.x < o.x || (this.x == o.x && this.y < o.y)) {
            return -1;
        } else {
            return 1;
        }

    }

    @Override
    public int hashCode() { //H1.2
        return x * 100000 + y;
    }

    // Implementieren Sie die Funktion public boolean equals(Object o) der Klasse Location, welche das gegebene Objekt
    // o mit this auf Objektgleichheit überprüft und das Resultat zurückliefert.
    // Zwei Objekte l1, l2 des Typs Location werden als objektgleich bezeichnet,
    // wenn die Koordinaten dieser übereinstimmen. Im Fall, dass o null oder nicht
    // vom Typ Location ist, soll false geliefert werden.
    @Override
    public boolean equals(Object o) { // TODO: H1.3 - remove if implemented
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location other = (Location) o;
        return (this.x == other.x && this.y == other.y);


    }

    @Override
    public String toString() { // TODO: H1.4 - remove if implemented
        return "(" + x + "," + y + ")";
    }
}
