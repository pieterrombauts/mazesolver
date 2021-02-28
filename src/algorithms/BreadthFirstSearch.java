package algorithms;

import maze.Edge;
import maze.Maze;
import maze.Node;

import java.util.*;

public class BreadthFirstSearch implements Algorithm {

    Queue<Edge> searchEdges = new LinkedList<>();
    Maze maze;

    @Override
    public void initialiseAlgorithm(Maze maze) {
        this.maze = maze;
    }

    @Override
    public List<Edge> solveMaze() {
        boolean firstNode = true;
        Node currentNode;
        Edge currentEdge;
        List<Edge> allPaths = new ArrayList<>();
        List<Edge> correctPath = new ArrayList<>();

        currentEdge = maze.getSource().getRight();
        searchEdges.add(currentEdge);
        maze.getSource().setVisited(true);

        while(!searchEdges.isEmpty()) {
            currentEdge = searchEdges.element();
            searchEdges.remove();

            if (firstNode) {
                currentNode = currentEdge.getStart();
                firstNode = false;
            } else {
                currentNode = currentEdge.getEnd();
            }
            allPaths.add(currentEdge);

            if (currentNode.equals(maze.getFinish())) {
                correctPath = maze.drawBacktrackControllerBreadth(allPaths);
                return allPaths;
            }

            addChildNodes(currentNode);
            maze.drawPathBetweenNodes(searchEdges.element().getStart(), searchEdges.element().getEnd(), StdDraw.ORANGE);
        }
        return null;
    }

    private boolean addChildNodes(Node currentNode) {
        List<Edge> neighbours = new ArrayList<>();

        for (Edge e : currentNode.getEdges()) {
            if (e != null && !e.getEnd().isVisited()) {
                neighbours.add(e);
            }
        }

        if (neighbours.size() == 0) {
            return false;
        }

        Collections.sort(neighbours, new Edge.SortByDistance());

        for (Edge e : neighbours) {
            e.getEnd().setVisited(true);
            searchEdges.add(e);
        }

        return true;
    }

}
