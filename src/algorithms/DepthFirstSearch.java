package algorithms;

import maze.Edge;
import maze.Maze;
import maze.Node;

import java.util.*;

public class DepthFirstSearch implements Algorithm {

    Stack<Edge> searchEdges = new Stack<>();
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
        List<Edge> correctPath = new ArrayList<>();

        currentEdge = maze.getSource().getRight();
        searchEdges.push(currentEdge);
        maze.getSource().setVisited(true);

        while(!searchEdges.isEmpty()) {
            currentEdge = searchEdges.pop();
            if (firstNode) {
                currentNode = currentEdge.getStart();
                firstNode = false;
            } else {
                currentNode = currentEdge.getEnd();
            }
            correctPath.add(currentEdge);

            if (currentNode.equals(maze.getFinish())) {
                return correctPath;
            }

            if (!addChildNodes(currentNode)) {
                correctPath = maze.drawBacktrackControllerDepth(correctPath, searchEdges.peek());
            } else {
                maze.drawPathBetweenNodes(currentNode, searchEdges.peek().getEnd(), StdDraw.RED);
            }
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


        Collections.shuffle(neighbours);
        //Collections.sort(neighbours, new Edge.SortReverseByDistance());

        for (Edge e : neighbours) {
            e.getEnd().setVisited(true);
            searchEdges.push(e);
        }

        return true;
    }




}
