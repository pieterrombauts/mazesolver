package maze;

import algorithms.StdDraw;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Maze {

    BufferedImage image;
    private int width;
    private int height;
    private List<Node> nodes;
    private List<Node> nodesAbove;
    private List<Edge> backtrackedEdges = new ArrayList<>();
    private List<Edge> correctEdges = new ArrayList<>();
    private Node source;
    private Node finish;
    private boolean isVisual;

    public Maze (int width, int height, boolean isVisual) {
        this.width = width;
        this.height = height;
        this.isVisual = isVisual;
    }

    public boolean importImage(String mazeFile) throws IOException {
        nodes = new ArrayList<Node>();
        nodesAbove = new ArrayList<Node>();

        File f;

        try {
            f = new File(mazeFile);
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
            image = ImageIO.read(f);

            createGraph(image);
            return true;
        } catch (IOException e) {
            System.err.print("Error: " + e);
            return false;
        }
    }

    public BufferedImage createImageCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public void generateOutputImage(List<Edge> correctPath) {
        List<Edge> otherEdges;
        File outputFile = new File("D:/Documents/Programming/Maze Solver/Mazes/Solved/output.png");
        BufferedImage imageCopy = createImageCopy(image);
        if (backtrackedEdges.isEmpty()) {
            otherEdges = correctPath;
            correctPath = correctEdges;
        } else {
            otherEdges = backtrackedEdges;
        }
        for (Edge e : otherEdges) {
            if (e.getStart().getX() == e.getEnd().getX()) {
                if (e.getStart().getY() <= e.getEnd().getY()) {
                    for (int i = e.getStart().getY(); i <= e.getEnd().getY(); i++) {
                        imageCopy.setRGB(e.getStart().getX(), i, 0xFFFFC800);
                    }
                } else if (e.getStart().getY() >= e.getEnd().getY()) {
                    for (int i = e.getStart().getY(); i >= e.getEnd().getY(); i--) {
                        imageCopy.setRGB(e.getStart().getX(), i, 0xFFFFC800);
                    }
                }
            } else if (e.getStart().getY() == e.getEnd().getY()) {
                if (e.getStart().getX() <= e.getEnd().getX()) {
                    for (int i = e.getStart().getX(); i <= e.getEnd().getX(); i++) {
                        imageCopy.setRGB(i, e.getStart().getY(), 0xFFFFC800);
                    }
                } else if (e.getStart().getX() >= e.getEnd().getX()) {
                    for (int i = e.getStart().getX(); i >= e.getEnd().getX(); i--) {
                        imageCopy.setRGB(i, e.getStart().getY(), 0xFFFFC800);
                    }
                }
            }
        }
        for (Edge e : correctPath) {
            if (e.getStart().getX() == e.getEnd().getX()) {
                if (e.getStart().getY() <= e.getEnd().getY()) {
                    for (int i = e.getStart().getY(); i <= e.getEnd().getY(); i++) {
                        imageCopy.setRGB(e.getStart().getX(), i, 0xFFFF0000);
                    }
                } else if (e.getStart().getY() >= e.getEnd().getY()) {
                    for (int i = e.getStart().getY(); i >= e.getEnd().getY(); i--) {
                        imageCopy.setRGB(e.getStart().getX(), i, 0xFFFF0000);
                    }
                }
            } else if (e.getStart().getY() == e.getEnd().getY()) {
                if (e.getStart().getX() <= e.getEnd().getX()) {
                    for (int i = e.getStart().getX(); i <= e.getEnd().getX(); i++) {
                        imageCopy.setRGB(i, e.getStart().getY(), 0xFFFF0000);
                    }
                } else if (e.getStart().getX() >= e.getEnd().getX()) {
                    for (int i = e.getStart().getX(); i >= e.getEnd().getX(); i--) {
                        imageCopy.setRGB(i, e.getStart().getY(), 0xFFFF0000);
                    }
                }
            }
        }
        try {
            ImageIO.write(imageCopy, "PNG", outputFile);
        } catch (IOException e) {
            System.err.print("Error generating solved maze image");
        }
    }

    private void createGraph(BufferedImage image) {
        int numberExits;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                numberExits = 0;

                if (x == 0 && y == 1) {
                    Node newNode = new Node(x, y);
                    source = newNode;
                    nodes.add(newNode);
                    newNode.setDistance(calculateDistance(newNode));
                    drawNode(x, y);
                    continue;
                }
                if (x == width - 1 && y == height - 2) {
                    Node neighbourNode = nodes.get(nodes.size() - 1);
                    Node newNode = new Node(x, y);
                    newNode.setLeft(new Edge(newNode, neighbourNode, calculateWeight(newNode, neighbourNode)));
                    finish = newNode;
                    nodes.add(newNode);
                    neighbourNode.setRight(new Edge(neighbourNode, newNode, calculateWeight(newNode, neighbourNode)));
                    newNode.setDistance(calculateDistance(newNode));
                    drawNode(x, y);
                    drawEdge(x, y + 1, neighbourNode.getX(), neighbourNode.getY());
                    continue;
                }
                if ((image.getRGB(x, y) & 0x00FFFFFF) == 0) {
                    drawWall(x, y);
                } else {
                    if ((x - 1 >= 0) && (y - 1 >= 0) && (x + 1 < width) && (y + 1 < height)) {
                        if ((image.getRGB((x - 1), y) & 0x00FFFFFF) != 0) {
                            numberExits++;
                        }
                        if ((image.getRGB(x, (y - 1)) & 0x00FFFFFF) != 0) {
                            numberExits++;
                        }
                        if ((image.getRGB((x + 1), y) & 0x00FFFFFF) != 0) {
                            numberExits++;
                        }
                        if ((image.getRGB(x, (y + 1)) & 0x00FFFFFF) != 0) {
                            numberExits++;
                        }
                    }
                    switch (numberExits) {
                        case 1:
                            handleOneExit(image, x, y);
                            break;
                        case 2:
                            handleTwoExits(image, x, y);
                            break;
                        case 3:
                            handleThreeExits(image, x, y);
                            break;
                        case 4:
                            handleFourExits(image, x, y);
                            break;
                        default:
                            continue;
                    }
                }
            }
        }
    }

    /**
     * Function to handle node creation for pixels with 3 walls and 1 exit
     * @param image BufferedImage of maze, used to get pixel data
     * @param x x-coordinate of pixel being checked
     * @param y y-coordinate of pixel being checked
     */
    private void handleOneExit(BufferedImage image, int x, int y) {
        Node newNode = null;
        Node neighbourNode;

        if (((image.getRGB((x-1),y) & 0x00FFFFFF) == 0) && ((image.getRGB(x,(y-1)) & 0x00FFFFFF) == 0) && ((image.getRGB((x+1),y) & 0x00FFFFFF) == 0)) {             //If left, top, and right are walls
            newNode = new Node(x,y);
            nodes.add(newNode);
            nodesAbove.add(newNode);
            drawNode(x, y);
            newNode.setDistance(calculateDistance(newNode));
        } else if (((image.getRGB(x,(y-1)) & 0x00FFFFFF) == 0) && ((image.getRGB((x+1),y) & 0x00FFFFFF) == 0) && ((image.getRGB(x,(y+1)) & 0x00FFFFFF) == 0)) {      //If top, right, and bottom are walls
            if (nodes != null && !nodes.isEmpty()) {
                neighbourNode = nodes.get(nodes.size() - 1);
                newNode = new Node(x, y);
                newNode.setLeft(new Edge(newNode, neighbourNode, calculateWeight(newNode, neighbourNode)));
                neighbourNode.setRight(new Edge(neighbourNode, newNode, calculateWeight(newNode, neighbourNode)));
                nodes.add(newNode);
                drawNode(x, y);
                drawEdge(x, y + 1, neighbourNode.getX(), neighbourNode.getY());
                newNode.setDistance(calculateDistance(newNode));
            }
        } else if (((image.getRGB((x+1),y) & 0x00FFFFFF) == 0) && ((image.getRGB(x,(y+1)) & 0x00FFFFFF) == 0) && ((image.getRGB((x-1),y) & 0x00FFFFFF) == 0)) {      //If right, bottom, and left are walls
            if (nodesAbove != null && !nodesAbove.isEmpty() && searchAbove(x) != null) {
                neighbourNode = searchAbove(x);
                newNode = new Node(x, y);
                newNode.setUp(new Edge(newNode, neighbourNode, calculateWeight(newNode, neighbourNode)));
                neighbourNode.setDown(new Edge(neighbourNode, newNode, calculateWeight(newNode, neighbourNode)));
                nodesAbove.remove(neighbourNode);
                nodes.add(newNode);
                drawNode(x, y);
                drawEdge(x, y + 1, neighbourNode.getX(), neighbourNode.getY());
                newNode.setDistance(calculateDistance(newNode));
            }
        } else if (((image.getRGB(x,(y+1)) & 0x00FFFFFF) == 0) && ((image.getRGB(x,(y-1)) & 0x00FFFFFF) == 0) && ((image.getRGB(x,(y-1)) & 0x00FFFFFF) == 0)) {      //If bottom, left, and top are walls
            newNode = new Node(x,y);
            nodes.add(newNode);
            drawNode(x, y);
            newNode.setDistance(calculateDistance(newNode));
        }
    }

    /**
     * Function to handle node creation for pixels with 2 walls and 2 exits (ignoring straight corridors)
     * @param image BufferedImage of maze, used to get pixel data
     * @param x x-coordinate of pixel being checked
     * @param y y-coordinate of pixel being checked
     */
    private void handleTwoExits(BufferedImage image, int x, int y) {
        Node newNode = null;
        Node neighbourNode;
        Node neighbourNodeTwo;

        if (((image.getRGB((x-1),y) & 0x00FFFFFF) == 0) && ((image.getRGB(x,(y-1)) & 0x00FFFFFF) == 0)) {             //If left and top are walls
            newNode = new Node(x,y);
            nodes.add(newNode);
            nodesAbove.add(newNode);
            drawNode(x, y);
            newNode.setDistance(calculateDistance(newNode));
        } else if (((image.getRGB(x,(y-1)) & 0x00FFFFFF) == 0) && ((image.getRGB((x+1),y) & 0x00FFFFFF) == 0)) {      //If top and right are walls
            if (nodes != null && !nodes.isEmpty()) {
                neighbourNode = nodes.get(nodes.size() - 1);
                newNode = new Node(x,y);
                newNode.setLeft(new Edge(newNode, neighbourNode, calculateWeight(newNode, neighbourNode)));
                neighbourNode.setRight(new Edge(neighbourNode, newNode, calculateWeight(newNode, neighbourNode)));
                nodes.add(newNode);
                nodesAbove.add(newNode);
                drawNode(x, y);
                drawEdge(x, y + 1, neighbourNode.getX(), neighbourNode.getY());
                newNode.setDistance(calculateDistance(newNode));
            }
        } else if (((image.getRGB((x+1),y) & 0x00FFFFFF) == 0) && ((image.getRGB(x,(y+1)) & 0x00FFFFFF) == 0)) {      //If right and bottom are walls
            if (nodes != null && !nodes.isEmpty() && nodesAbove != null && !nodesAbove.isEmpty() && searchAbove(x) != null) {
                neighbourNode = nodes.get(nodes.size() - 1);
                neighbourNodeTwo = searchAbove(x);
                newNode = new Node(x, y);
                newNode.setLeft(new Edge(newNode, neighbourNode, calculateWeight(newNode, neighbourNode)));
                newNode.setUp(new Edge(newNode, neighbourNodeTwo, calculateWeight(newNode, neighbourNodeTwo)));
                neighbourNode.setRight(new Edge(neighbourNode, newNode, calculateWeight(newNode, neighbourNode)));
                neighbourNodeTwo.setDown(new Edge(neighbourNodeTwo, newNode, calculateWeight(newNode, neighbourNodeTwo)));
                nodesAbove.remove(neighbourNodeTwo);
                nodes.add(newNode);
                drawNode(x, y);
                drawEdge(x, y + 1, neighbourNode.getX(), neighbourNode.getY());
                drawEdge(x, y + 1, neighbourNodeTwo.getX(), neighbourNodeTwo.getY());
                newNode.setDistance(calculateDistance(newNode));
            }
        } else if (((image.getRGB(x,(y+1)) & 0x00FFFFFF) == 0) && ((image.getRGB((x-1),y) & 0x00FFFFFF) == 0)) {      //If bottom and left are walls
            if (nodesAbove != null && !nodesAbove.isEmpty() && searchAbove(x) != null) {
                neighbourNode = searchAbove(x);
                newNode = new Node(x, y);
                newNode.setUp(new Edge(newNode, neighbourNode, calculateWeight(newNode, neighbourNode)));
                neighbourNode.setDown(new Edge(neighbourNode, newNode, calculateWeight(newNode, neighbourNode)));
                nodesAbove.remove(neighbourNode);
                nodes.add(newNode);
                drawNode(x, y);
                drawEdge(x, y + 1, neighbourNode.getX(), neighbourNode.getY());
                newNode.setDistance(calculateDistance(newNode));
            }
        }
    }

    /**
     * Function to handle node creation for pixels with 1 wall and 3 exits
     * @param image BufferedImage of maze, used to get pixel data
     * @param x x-coordinate of pixel being checked
     * @param y y-coordinate of pixel being checked
     */
    private void handleThreeExits(BufferedImage image, int x, int y) {
        Node newNode = null;
        Node neighbourNode;
        Node neighbourNodeTwo;

        if ((image.getRGB((x-1),y) & 0x00FFFFFF) == 0) {
            if (nodesAbove != null && !nodesAbove.isEmpty() && searchAbove(x) != null) {
                neighbourNode = searchAbove(x);
                newNode = new Node(x, y);
                newNode.setUp(new Edge(newNode, neighbourNode, calculateWeight(newNode, neighbourNode)));
                neighbourNode.setDown(new Edge(neighbourNode, newNode, calculateWeight(newNode, neighbourNode)));
                nodesAbove.remove(neighbourNode);
                nodes.add(newNode);
                nodesAbove.add(newNode);
                drawNode(x, y);
                drawEdge(x, y + 1, neighbourNode.getX(), neighbourNode.getY());
                newNode.setDistance(calculateDistance(newNode));
            }
        } else if ((image.getRGB(x,(y-1)) & 0x00FFFFFF) == 0) {
            if (nodes != null && !nodes.isEmpty()) {
                neighbourNode = nodes.get(nodes.size() - 1);
                newNode = new Node(x, y);
                newNode.setLeft(new Edge(newNode, neighbourNode, calculateWeight(newNode, neighbourNode)));
                neighbourNode.setRight(new Edge(neighbourNode, newNode, calculateWeight(newNode, neighbourNode)));
                nodes.add(newNode);
                nodesAbove.add(newNode);
                drawNode(x, y);
                drawEdge(x, y + 1, neighbourNode.getX(), neighbourNode.getY());
                newNode.setDistance(calculateDistance(newNode));
            }
        } else if ((image.getRGB((x+1),y) & 0x00FFFFFF) == 0) {
            if (nodes != null && !nodes.isEmpty() && nodesAbove != null && !nodesAbove.isEmpty() && searchAbove(x) != null) {
                neighbourNode = nodes.get(nodes.size() - 1);
                neighbourNodeTwo = searchAbove(x);
                newNode = new Node(x, y);
                newNode.setLeft(new Edge(newNode, neighbourNode, calculateWeight(newNode, neighbourNode)));
                newNode.setUp(new Edge(newNode, neighbourNodeTwo, calculateWeight(newNode, neighbourNodeTwo)));
                neighbourNode.setRight(new Edge(neighbourNode, newNode, calculateWeight(newNode, neighbourNode)));
                neighbourNodeTwo.setDown(new Edge(neighbourNodeTwo, newNode, calculateWeight(newNode, neighbourNodeTwo)));
                nodesAbove.remove(neighbourNodeTwo);
                nodes.add(newNode);
                nodesAbove.add(newNode);
                drawNode(x, y);
                drawEdge(x, y + 1, neighbourNode.getX(), neighbourNode.getY());
                drawEdge(x, y + 1, neighbourNodeTwo.getX(), neighbourNodeTwo.getY());
                newNode.setDistance(calculateDistance(newNode));
            }
        } else if ((image.getRGB(x,(y+1)) & 0x00FFFFFF) == 0) {
            if (nodes != null && !nodes.isEmpty() && nodesAbove != null && !nodesAbove.isEmpty() && searchAbove(x) != null) {
                neighbourNode = nodes.get(nodes.size() - 1);
                neighbourNodeTwo = searchAbove(x);
                newNode = new Node(x, y);
                newNode.setLeft(new Edge(newNode, neighbourNode, calculateWeight(newNode, neighbourNode)));
                newNode.setUp(new Edge(newNode, neighbourNodeTwo, calculateWeight(newNode, neighbourNodeTwo)));
                neighbourNode.setRight(new Edge(neighbourNode, newNode, calculateWeight(newNode, neighbourNode)));
                neighbourNodeTwo.setDown(new Edge(neighbourNodeTwo, newNode, calculateWeight(newNode, neighbourNodeTwo)));
                nodesAbove.remove(neighbourNodeTwo);
                nodes.add(newNode);
                drawNode(x, y);
                drawEdge(x, y + 1, neighbourNode.getX(), neighbourNode.getY());
                drawEdge(x, y + 1, neighbourNodeTwo.getX(), neighbourNodeTwo.getY());
                newNode.setDistance(calculateDistance(newNode));
            }
        }

    }

    /**
     * Function to handle node creation for pixels with no walls and 4 exits
     * @param image BufferedImage of maze, used to get pixel data
     * @param x x-coordinate of pixel being checked
     * @param y y-coordinate of pixel being checked
     */
    private void handleFourExits(BufferedImage image, int x, int y) {
        Node newNode = null;
        Node neighbourNode;
        Node neighbourNodeTwo;

        if (nodes != null && !nodes.isEmpty() && nodesAbove != null && !nodesAbove.isEmpty() && searchAbove(x) != null) {
            neighbourNode = nodes.get(nodes.size() - 1);
            neighbourNodeTwo = searchAbove(x);
            newNode = new Node(x, y);
            newNode.setLeft(new Edge(newNode, neighbourNode, calculateWeight(newNode, neighbourNode)));
            newNode.setUp(new Edge(newNode, neighbourNodeTwo, calculateWeight(newNode, neighbourNodeTwo)));
            neighbourNode.setRight(new Edge(neighbourNode, newNode, calculateWeight(newNode, neighbourNode)));
            neighbourNodeTwo.setDown(new Edge(neighbourNodeTwo, newNode, calculateWeight(newNode, neighbourNodeTwo)));
            nodesAbove.remove(neighbourNodeTwo);
            nodes.add(newNode);
            nodesAbove.add(newNode);
            drawNode(x, y);
            drawEdge(x, y + 1, neighbourNode.getX(), neighbourNode.getY());
            drawEdge(x, y + 1, neighbourNodeTwo.getX(), neighbourNodeTwo.getY());
            newNode.setDistance(calculateDistance(newNode));
        }

    }

    /**
     * Function to find the node directly above the node being created to link up the edge for vertical paths
     * @param x x-coordinate of the node being searched for
     * @return the node which is directly above the node being created
     */
    private Node searchAbove(int x) {
        for (Node node : nodesAbove) {
            if (node.getX() == x) {
                return node;
            }
        }
        return null;
    }

    private int calculateWeight(Node start, Node end) {
        if (start.getX() == end.getX()) {
            return Math.abs(start.getY() - end.getY());
        } else if (start.getY() == end.getY()) {
            return Math.abs(start.getX() - end.getX());
        }
        return 0;
    }

    private double calculateDistance(Node node) {
        return (width - 1 - node.getX()) * (width - 1 - node.getX())  + (height - 2 - node.getY()) * (height - 2 - node.getY());
    }

    public void drawGrid() {
        if (!isVisual) return;

        StdDraw.setCanvasSize(1000, 1000);
        StdDraw.setXscale(-1, width + 1);
        StdDraw.setYscale(-1, height + 1);

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius();
        /*for (int r = height; r > 0; r--) {
            for (int c = 0; c < width; c++) {
                algorithms.StdDraw.line(c+1, r, c+1, r-1);
                algorithms.StdDraw.line(c, r-1, c+1, r-1);
                algorithms.StdDraw.line(c, r, c, r-1);
                algorithms.StdDraw.line(c, r, c+1, r);
            }
        }*/
    }

    public void drawWall(int x, int y) {
        if (!isVisual) return;

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius();
        StdDraw.filledRectangle(x + 0.5, (height - 1 - y) + 0.5, 0.5, 0.5);
    }

    public void drawNode(int x, int y) {
        if (!isVisual) return;

        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
        StdDraw.setPenRadius();
        StdDraw.filledCircle(x + 0.5, (height - 1 - y) + 0.5, 0.25);
    }

    public void drawEdge(int x, int y, int x2, int y2) {
        if (!isVisual) return;

        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
        StdDraw.setPenRadius();
        StdDraw.line(x + 0.5, (height - y) + 0.5, x2 + 0.5, (height - 1 - y2) + 0.5);
    }

    public void drawPath(int x, int y, Color color) {
        if (!isVisual) return;

        StdDraw.setPenColor(color);
        StdDraw.setPenRadius();
        StdDraw.filledRectangle(x + 0.5, (height - 1 - y) + 0.5, 0.5, 0.5);

        /*try {
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (InterruptedException ex) {
            // do nothing
        }*/
    }

    public void drawPathBetweenNodes (Node start, Node end, Color color) {
        if (!isVisual) return;
        if (start.getY() == end.getY()) {
            if (start.getX() < end.getX()) {
                for (int i = start.getX(); i <= end.getX(); i++) {
                    drawPath(i, start.getY(), color);
                }
            } else if (start.getX() > end.getX()) {
                for (int i = start.getX(); i >= end.getX(); i--) {
                    drawPath(i, start.getY(), color);
                }
            }
        } else if (start.getX() ==  end.getX()) {
            if (height - start.getY() > height - end.getY()) {
                for (int i = start.getY(); i <= end.getY(); i++) {
                    drawPath(start.getX(), i, color);
                }
            } else if (height - start.getY() < height - end.getY()) {
                for (int i = start.getY(); i >= end.getY(); i--) {
                    drawPath(start.getX(), i, color);
                }
            }
        }
    }

    public void drawBacktrack(int x, int y, Color color) {
        if (!isVisual) return;

        StdDraw.setPenColor(color);
        StdDraw.setPenRadius();
        StdDraw.filledRectangle(x + 0.5, (height - 1 - y) + 0.5, 0.5, 0.5);

        /*try {
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (InterruptedException ex) {
            // do nothing
        }*/
    }

    public List<Edge> drawBacktrackControllerBreadth (List<Edge> allPaths) {
        int indexToRemove = allPaths.size() - 1;
        Edge edgeToCheck = allPaths.get(indexToRemove);

        while(true) {
            if (correctEdges.isEmpty() || edgeToCheck.getEnd().equals(correctEdges.get(correctEdges.size() - 1).getStart())) {
                if (!edgeToCheck.getStart().equals(getSource())) {
                    drawBacktrackBetweenNodes(edgeToCheck, false, StdDraw.RED);
                    allPaths.remove(edgeToCheck);
                    correctEdges.add(edgeToCheck);
                } else {
                    drawBacktrackBetweenNodes(edgeToCheck, false, StdDraw.RED);
                    allPaths.remove(edgeToCheck);
                    correctEdges.add(edgeToCheck);
                    return correctEdges;
                }
                indexToRemove--;
                edgeToCheck = allPaths.get(indexToRemove);
            } else {
                indexToRemove--;
                edgeToCheck = allPaths.get(indexToRemove);
            }

        }
    }

    public List<Edge> drawBacktrackControllerDepth (List<Edge> correctPath, Edge nextEdge) {
        Edge edgeToCheck = correctPath.get(correctPath.size() - 1);
        while(true) {
            if (!edgeToCheck.getStart().equals(nextEdge.getStart())) {
                drawBacktrackBetweenNodes(edgeToCheck, false, StdDraw.ORANGE);
                correctPath.remove(edgeToCheck);
                backtrackedEdges.add(edgeToCheck);
            } else {
                drawBacktrackBetweenNodes(edgeToCheck, true, StdDraw.ORANGE);
                drawPathBetweenNodes(nextEdge.getStart(), nextEdge.getEnd(), StdDraw.RED);
                correctPath.remove(edgeToCheck);
                backtrackedEdges.add(edgeToCheck);
                return correctPath;
            }
            edgeToCheck = correctPath.get(correctPath.size() - 1);
        }
    }

    public void drawBacktrackBetweenNodes (Edge edgeToCheck, boolean lastEdge, Color color) {
        if (!isVisual) return;
        int lastEdgeOffset = 0;
        if (lastEdge) {
            lastEdgeOffset = 1;
        }
        if (edgeToCheck.getStart().getY() == edgeToCheck.getEnd().getY()) {
            if (edgeToCheck.getStart().getX() < edgeToCheck.getEnd().getX()) {
                for (int i = edgeToCheck.getEnd().getX(); i >= edgeToCheck.getStart().getX() + lastEdgeOffset; i--) {
                    drawBacktrack(i, edgeToCheck.getStart().getY(), color);
                }
            } else if (edgeToCheck.getStart().getX() > edgeToCheck.getEnd().getX()) {
                for (int i = edgeToCheck.getEnd().getX(); i <= edgeToCheck.getStart().getX() - lastEdgeOffset; i++) {
                    drawBacktrack(i, edgeToCheck.getStart().getY(), color);
                }
            }
        } else if (edgeToCheck.getStart().getX() == edgeToCheck.getEnd().getX()) {
            if (height - edgeToCheck.getStart().getY() > height - edgeToCheck.getEnd().getY()) {
                for (int i = edgeToCheck.getEnd().getY(); i >= edgeToCheck.getStart().getY() + lastEdgeOffset; i--) {
                    drawBacktrack(edgeToCheck.getStart().getX(), i, color);
                }
            } else if (height - edgeToCheck.getStart().getY() < height - edgeToCheck.getEnd().getY()) {
                for (int i = edgeToCheck.getEnd().getY(); i <= edgeToCheck.getStart().getY() - lastEdgeOffset; i++) {
                    drawBacktrack(edgeToCheck.getStart().getX(), i, color);
                }
            }
        }
    }

    public Node getSource() {
        return source;
    }

    public Node getFinish() {
        return finish;
    }
}

