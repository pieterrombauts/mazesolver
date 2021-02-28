package algorithms;

import maze.Edge;
import maze.Maze;

import java.util.List;

public interface Algorithm {

    public void initialiseAlgorithm(Maze maze);

    public List<Edge> solveMaze();
}
