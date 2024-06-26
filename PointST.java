import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;

import java.util.TreeMap;

public class PointST<Value> {
    // tree map of all the points in symbol table
    private TreeMap<Point2D, Value> allPoints;

    // construct an empty symbol table of points
    public PointST() {
        allPoints = new TreeMap<>();
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return allPoints.isEmpty();
    }

    // number of points
    public int size() {
        return allPoints.size();
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        if (p == null || val == null) throw new IllegalArgumentException(
                "null value");
        allPoints.put(p, val);
    }

    // value associated with point p
    public Value get(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null value");
        return allPoints.get(p);
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null value");
        return allPoints.containsKey(p);
    }

    // all points in the symbol table
    public Iterable<Point2D> points() {
        return allPoints.keySet();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("null rectangle");
        Queue<Point2D> queue = new Queue<>();
        for (Point2D x : allPoints.keySet()) {
            if (rect.contains(x)) {
                queue.enqueue(x);
            }
        }
        return queue;
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        double min = Double.POSITIVE_INFINITY;
        Point2D nearest = null;

        if (isEmpty()) return null;
        if (p == null) throw new IllegalArgumentException("null value");

        for (Point2D x : allPoints.keySet()) {
            if (x.distanceSquaredTo(p) < min) {
                min = x.distanceSquaredTo(p);
                nearest = x;
            }
        }
        return nearest;
    }

    // unit testing
    public static void main(String[] args) {

        PointST<String> obj = new PointST<String>();
        Point2D x = new Point2D(0, 0);
        Point2D y = new Point2D(5, 5);
        Point2D z = new Point2D(-1, 2);

        System.out.println(obj.isEmpty());

        obj.put(x, "one");
        obj.put(y, "two");
        obj.put(z, "three");

        System.out.println(obj.size());
        System.out.println(obj.get(y));
        System.out.println(obj.contains(new Point2D(4, 3)));
        System.out.println(obj.points());
        System.out.println(obj.range(new RectHV(0, 0, 6, 6)));
        System.out.println(obj.nearest(new Point2D(3, 3)));
    }
}
