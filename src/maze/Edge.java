package maze;

import java.util.Comparator;

public class Edge {
    private Node start;
    private Node end;
    private double weight;

    Edge (Node start, Node end, double weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object object) {
        boolean isSame = false;
        if (object != null && object instanceof Edge) {
            Edge other = (Edge) object;
            isSame = (this.start.equals(other.start) && this.end.equals(other.end));
        }
        return isSame;
    }

    public static class SortReverseByDistance implements Comparator<Edge> {

        @Override
        public int compare(Edge a, Edge b) {
            return (int) (b.getEnd().getDistance() - a.getEnd().getDistance());
        }
    }

    public static class SortByDistance implements Comparator<Edge> {

        @Override
        public int compare(Edge a, Edge b) {
            return (int) (a.getEnd().getDistance() - b.getEnd().getDistance());
        }
    }

    public Node getStart() { return start; }

    public Node getEnd() { return end; }
}
