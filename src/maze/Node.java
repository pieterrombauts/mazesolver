package maze;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private int x;
    private int y;
    private double distance;
    private List<Edge> edges;
    private boolean visited;

    Node (int x, int y) {
        edges = new ArrayList<Edge>();
        this.edges.add(0, null);
        this.edges.add(1, null);
        this.edges.add(2, null);
        this.edges.add(3, null);
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object object) {
        boolean isSame = false;
        if (object != null && object instanceof Node) {
            Node other = (Node) object;
            isSame = (this.x == other.x && this.y == other.y);
        }
        return isSame;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Edge getLeft() { return edges.get(0); }

    public Edge getUp() { return edges.get(1); }

    public Edge getRight() { return edges.get(2); }

    public Edge getDown() {
        return edges.get(3);
    }

    public List<Edge> getEdges() { return edges; }

    public void setLeft(Edge left) {
        this.edges.set(0, left);
    }

    public void setUp(Edge up) {
        this.edges.set(1, up);
    }

    public void setRight(Edge right) {
        this.edges.set(2, right);
    }

    public void setDown(Edge down) {
        this.edges.set(3, down);
    }

    public double getDistance() { return distance; }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
