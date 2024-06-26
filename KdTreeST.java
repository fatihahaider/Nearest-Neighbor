import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;

public class KdTreeST<Value> {
    // root of entire bst
    private Node root;
    // size of bst
    private int size;

    // construct an empty symbol table of points
    public KdTreeST() {
        size = 0;
        root = null;
    }

    private class Node {
        private Point2D key; // key of node
        private Value val; // value of node
        private Node lb; // left child of node
        private Node rt; // right child of node
        private RectHV rect; // bounding box of each point

        // initializes new node with key and val
        public Node(Point2D x, Value val, RectHV rect) {
            key = x;
            this.val = val;
            lb = null;
            rt = null;
            this.rect = rect;
        }
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return (size == 0);
    }

    // number of points
    public int size() {
        return size;
    }


    // associate the value val with point p
    public void put(Point2D p, Value val) {
        if (p == null || val == null) throw new IllegalArgumentException(
                "null argument");

        int level = 0;
        Node parent = null;
        boolean left = false;
        boolean right = false;
        boolean x = false;
        boolean y = false;

        if (root == null) {
            root = new Node(p, val, new RectHV(Double.NEGATIVE_INFINITY,
                                               Double.NEGATIVE_INFINITY,
                                               Double.POSITIVE_INFINITY,
                                               Double.POSITIVE_INFINITY));
            size++;
            return;
        }
        Node current = root;

        while (current != null) {

            left = false;
            right = false;

            parent = current;
            if (current.key.equals(p)) {
                current.val = val;
                return;
            }

            if (level % 2 == 0) {
                y = false;
                if (p.x() < current.key.x()) {
                    current = current.lb;
                    left = true;
                    x = true;
                }
                else if (p.x() >= current.key.x()) {
                    current = current.rt;
                    x = true;
                    right = true;
                }

            }
            else {
                x = false;
                if (p.y() < current.key.y()) {
                    current = current.lb;
                    left = true;
                    y = true;
                }
                else if (p.y() >= current.key.y()) {
                    current = current.rt;
                    right = true;
                    y = true;
                }
            }
            level++;
        }

        if (left && x) {
            RectHV rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(),
                                     parent.key.x(), parent.rect.ymax());
            parent.lb = new Node(p, val, rect);

        }
        else if (right && x) {
            RectHV rect = new RectHV(parent.key.x(), parent.rect.ymin(),
                                     parent.rect.xmax(), parent.rect.ymax());
            parent.rt = new Node(p, val, rect);
        }
        else if (left && y) {
            RectHV rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(),
                                     parent.rect.xmax(), parent.key.y());
            parent.lb = new Node(p, val, rect);
        }
        else {
            RectHV rect = new RectHV(parent.rect.xmin(), parent.key.y(),
                                     parent.rect.xmax(), parent.rect.ymax());
            parent.rt = new Node(p, val, rect);
        }
        size++;
    }

    // value associated with point p
    public Value get(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null value");

        int level = 0;
        Node current = root;
        while (current != null) {
            if (p.equals(current.key)) {
                return current.val;
            }
            if (level % 2 == 0) {

                if (p.x() < current.key.x()) {
                    current = current.lb;
                }
                else {
                    current = current.rt;
                }
            }
            else {

                if (p.y() < current.key.y()) {
                    current = current.lb;
                }
                else {
                    current = current.rt;
                }
            }
            level++;
        }
        return null;
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null value");
        int level = 0;
        Node current = root;
        while (current != null) {
            if (p.equals(current.key)) {
                return true;
            }
            if (level % 2 == 0) {

                if (p.x() < current.key.x()) {
                    current = current.lb;
                }
                else {
                    current = current.rt;
                }
            }
            else {

                if (p.y() < current.key.y()) {
                    current = current.lb;
                }
                else {
                    current = current.rt;
                }
            }
            level++;
        }
        return false;
    }

    // all points in the symbol table
    public Iterable<Point2D> points() {
        Queue<Node> queue = new Queue<Node>();
        Queue<Point2D> levelOrder = new Queue<Point2D>();

        Node current;

        if (root != null) {
            current = root;
        }
        else {
            return levelOrder;
        }

        queue.enqueue(current);
        while (!queue.isEmpty()) {
            current = queue.dequeue();

            if (current != null) {
                levelOrder.enqueue(current.key);
                queue.enqueue(current.lb);
                queue.enqueue(current.rt);
            }
        }
        return levelOrder;
    }

    // calls range of points in rectangle query
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("null value");
        Queue<Point2D> rangeQueue = new Queue<Point2D>();
        recursiveRange(rect, rangeQueue, root);
        return rangeQueue;
    }

    // recursively checks if a point is in its range
    private void recursiveRange(RectHV rect, Queue<Point2D> queue, Node current) {
        if (current == null) return;

        if (rect.contains(current.key)) {
            queue.enqueue(current.key);
        }

        if (current.lb != null) {
            if (rect.intersects(current.lb.rect)) {
                recursiveRange(rect, queue, current.lb);
            }
        }
        if (current.rt != null) {
            if (rect.intersects(current.rt.rect)) {
                recursiveRange(rect, queue, current.rt);
            }
        }
    }

    // private method to recursively search through tree for closest point
    private Point2D recursiveNearest(Node current, Point2D p, int level, Point2D
            closest) {
        if (current == null) return closest;

        // The distance from the query to the champion is less than the distance
        // from the query to the current nodes rectangle
        if (closest.distanceSquaredTo(p) <= current.rect.distanceSquaredTo(p)) {
            return closest;
        }

        if (current.key.distanceSquaredTo(p) < closest.distanceSquaredTo(
                p)) {
            closest = current.key;
        }

        Node leftNode = current.lb;
        Node rightNode = current.rt;

        if (level % 2 == 0 && p.x() >= current.key.x() ||
                level % 2 == 1 && p.y() >= current.key.y()) {
            leftNode = current.rt;
            rightNode = current.lb;
        }

        closest = recursiveNearest(leftNode, p, level + 1, closest);
        closest = recursiveNearest(rightNode, p, level + 1, closest);

        return closest;
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("null argument");
        if (isEmpty()) return null;
        return recursiveNearest(root, p, 0, root.key);
    }

    // unit testing (required)
    public static void main(String[] args) {
        KdTreeST<String> obj = new KdTreeST<String>();

        Point2D a = new Point2D(0.372, 0.497);
        Point2D b = new Point2D(0.564, 0.413);
        Point2D c = new Point2D(0.226, 0.577);
        Point2D d = new Point2D(0.144, 0.179);

        obj.put(a, "A");
        obj.put(b, "B");
        obj.put(c, "C");
        obj.put(d, "D");

        System.out.println("all points: " + obj.points());
        System.out.println("is Empty: " + obj.isEmpty());
        System.out.println("size: " + obj.size());
        System.out.println("get C : " + obj.get(c));
        System.out.println("contains new x  : " + obj.contains(
                new Point2D(8, 9)));
        System.out.println(
                "points in rect:  " + obj.range(new RectHV(0.125, 0.625, 0.375,
                                                           0.625)));
        System.out.println("nearest  : " + obj.nearest(new Point2D(0.57,
                                                                   0.73)));


    }
}
