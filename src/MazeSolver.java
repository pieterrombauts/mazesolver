import algorithms.*;
import maze.Maze;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Maze Solver main class
 * @author Pieter Rombauts
 */
public class MazeSolver {

    /**
     * Program name for use in error messages
     */
    protected static final String progName = "MazeSolver";

    /**
     *
     * @param progName name of the program
     */
    public static void usage(String progName) {
        System.err.print(progName + "[-v] <maze width> <maze height> <maze image file location> <algorithm to use>");
        System.err.print(progName + "<algorithm to use> = breadth | depth | dijkstra | A*");
        System.exit(1);
    }

    /**
     *
     * @param algorithmName name of the algorithm to be used for searching maze
     * @return instance of specified algorithm's class
     */
    public static Algorithm constructAlgorithm(String algorithmName) {
        switch(algorithmName) {
            case "breadth":
                return new BreadthFirstSearch();
            case "depth":
                return new DepthFirstSearch();
            case "dijkstra":
                return new DijkstraSearch();
            case "A*":
                return new AStarSearch();
            default:
                return null;
        }
    }

    /**
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int argOffset = 0;
        boolean isVisual = false;
        List<Double> times = new ArrayList<>();
        long startTime;
        long endTime;

        // Check there are the minimum number of command line arguments
        if (args.length < 4) {
            System.err.print("Not enough arguments");
            usage(progName);
        }

        // Check if maze needs to be rendered with a GUI component
        if ("-v".equalsIgnoreCase(args[argOffset])) {
            isVisual = true;
            argOffset++;
        }

        if (args.length == argOffset + 4) {
            Algorithm alg = constructAlgorithm(args[argOffset + 3]);
            if (alg == null) {
                System.err.print("Incorrect argument value for algorithm to use");
                usage(progName);
            } else {
                Maze maze = new Maze(Integer.parseInt(args[argOffset]), Integer.parseInt(args[argOffset + 1]), isVisual);
                maze.drawGrid();
                startTime = System.nanoTime();
                try {
                    if (!maze.importImage(args[argOffset + 2])) {
                        System.err.print("Creation of maze failed");
                        usage(progName);
                    }
                } catch (IOException e) {
                    System.err.print("Maze file not found");
                    usage(progName);
                } catch (NumberFormatException e) {
                    System.err.print("Maze dimensions must be integer values representing pixel width and height of maze image");
                    usage(progName);
                }
                endTime = System.nanoTime();

                times.add((double) (endTime - startTime)/1000000000.0);

                startTime = System.nanoTime();
                alg.initialiseAlgorithm(maze);

                maze.generateOutputImage(alg.solveMaze());
                endTime = System.nanoTime();

                times.add((double) (endTime - startTime)/1000000000.0);

                System.out.println("Maze generation took " + times.get(0) + " seconds");
                System.out.println("Maze solution was calculated in " + times.get(1) + " seconds using " + args[argOffset + 3]);

            }
        } else {
            System.err.print("Not enough arguments");
            usage(progName);
        }
    }


}
