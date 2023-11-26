package LEVEL3_4;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;
import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class Maze extends JPanel {
    private static final long serialVersionUID = 1L;
    public int size;
    public Cell[][] grid;
    private ArrayList<Integer> shortestPath;
    private CellClickListener clickListener;

    public Maze(int size) {
        this.size = size;
        this.grid = new Cell[size][size];

        this.setLayout(new GridLayout(size, size));
        this.setBackground(Color.BLACK);

        this.setSize(700, 700);

        this.initCells();

    }

    public int getMazeSize() {
        return this.size;
    }

    private void initCells() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                this.grid[i][j] = new Cell(i, j, this.size);
                int row = i;
                int col = j;
                this.grid[i][j].addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        CellClickEvent ev = new CellClickEvent(this, row, col);
                        if (clickListener != null)
                            clickListener.cellClickEventOccurred(ev);
                    }
                });

                this.add(this.grid[i][j]);
            }
        }
    }

    // BackTracing Algorithm
    public void generate() {
        ArrayList<Cell> stack = new ArrayList<Cell>();

        Cell curCell = this.grid[0][0];
        curCell.setVisited();
        stack.add(curCell);

        while (curCell != null || !stack.isEmpty()) {
            ArrayList<Cell> unvisitedNeighbours = this.getCellUnvisitedNeighbours(curCell.row, curCell.col);
            if (unvisitedNeighbours.isEmpty()) {
                if (!stack.isEmpty())
                    curCell = stack.remove(stack.size() - 1);
                else
                    curCell = null;
            } else {
                Random random = new Random();
                // Picking random unvisited neighbour
                int index = random.nextInt(unvisitedNeighbours.size());
                Cell next = unvisitedNeighbours.get(index);

                removeWallBetweenCells(curCell, next);

                curCell = next;
                curCell.setVisited();
                stack.add(curCell);
            }
        }
    }

    private void displayPath(int x0, int y0) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (this.shortestPath.contains(i * this.size + j)) {
                    if (i == y0 && j == x0)
                        this.grid[i][j].resetBgc(1);
                    else if (i == this.size - 1 && j == this.size - 1)
                        this.grid[i][j].resetBgc(2);
                    else
                        this.grid[i][j].resetBgc(3);
                } else
                    this.grid[i][j].resetBgc(0);
            }
        }
    }

    public void setAsPlayerPos(int r0, int c0, int rf, int cf) {
        this.grid[r0][c0].resetBgc(5); // Player Path
        this.grid[rf][cf].resetBgc(4); // Player
    }

    public void generateSecond() {
        // Making borders
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                this.grid[i][j].unsetWalls();
                if (j == 0)
                    this.grid[i][j].setWall(0);
                if (i == 0)
                    this.grid[i][j].setWall(1);
                if (j == this.size - 1)
                    this.grid[i][j].setWall(2);
                if (i == this.size - 1)
                    this.grid[i][j].setWall(3);
            }
        }

        this.divisionRecursive(0, 0, this.size, this.size);
    }

    // Recursive Division Algorithm
    public void divisionRecursive(int row, int col, int width, int height) {
        // Base Case
        if (width < 2 || height < 2)
            return;

        // Making horizontal line if field is taller than it is wide
        boolean isHorizontal = false;
        if (height > width)
            isHorizontal = true;

        Random random = new Random();

        int lineIndex = random.nextInt((isHorizontal ? height : width) - 1);
        // offset
        lineIndex += isHorizontal ? row : col;

        int passageIndex = random.nextInt(isHorizontal ? width : height);
        // offset
        passageIndex += isHorizontal ? col : row;

        // Adding line
        if (isHorizontal) {
            for (int i = col; i < width + col; i++) {
                if (i == passageIndex)
                    continue;
                this.grid[lineIndex][i].setWall(3);
                this.grid[lineIndex + 1][i].setWall(1);

            }

        } else {
            for (int i = row; i < height + row; i++) {
                if (i == passageIndex)
                    continue;
                this.grid[i][lineIndex].setWall(2);
                this.grid[i][lineIndex + 1].setWall(0);

            }
        }

        // Recursive callss
        if (isHorizontal) {

            divisionRecursive(row, col, width, lineIndex - row + 1);
            divisionRecursive(lineIndex + 1, col, width, row + height - lineIndex - 1);

        } else {
            divisionRecursive(row, col, lineIndex - col + 1, height);
            divisionRecursive(row, lineIndex + 1, width + col - lineIndex - 1, height);

        }
    }

    private ArrayList<Cell> getCellUnvisitedNeighbours(int row, int col) {
        ArrayList<Cell> neighbours = getCellNeighbours(row, col);
        ArrayList<Cell> unvisitedNeighbours = new ArrayList<Cell>();

        for (int i = 0; i < neighbours.size(); i++) {
            if (!neighbours.get(i).isVisited())
                unvisitedNeighbours.add(neighbours.get(i));
        }
        return unvisitedNeighbours;
    }

    private ArrayList<Cell> getCellAccessableNeighbours(int row, int col) {
        ArrayList<Cell> neighbours = new ArrayList<Cell>();

        if (row + 1 < this.size && !this.grid[row][col].isWall(3)) // Bottom wall
            neighbours.add(this.grid[row + 1][col]);
        if (row > 0 && !this.grid[row][col].isWall(1)) // Top wall
            neighbours.add(this.grid[row - 1][col]);
        if (col > 0 && !this.grid[row][col].isWall(0)) // Left Wall
            neighbours.add(this.grid[row][col - 1]);
        if (col + 1 < this.size && !this.grid[row][col].isWall(2)) // Right Wall
            neighbours.add(this.grid[row][col + 1]);

        return neighbours;
    }

    private ArrayList<Cell> getCellNeighbours(int row, int col) {
        ArrayList<Cell> neighbours = new ArrayList<Cell>();

        if (row + 1 < this.size)
            neighbours.add(this.grid[row + 1][col]);
        if (row > 0)
            neighbours.add(this.grid[row - 1][col]);
        if (col > 0)
            neighbours.add(this.grid[row][col - 1]);
        if (col + 1 < this.size)
            neighbours.add(this.grid[row][col + 1]);

        return neighbours;
    }

    private void removeWallBetweenCells(Cell firstCell, Cell secondCell) {
        int rowDiff = firstCell.row - secondCell.row;

        if (rowDiff == -1) {
            firstCell.removeWall(3); // Bottom Wall
            secondCell.removeWall(1);// Top Wall
        } else if (rowDiff == 1) {
            firstCell.removeWall(1); // Top Wall;
            secondCell.removeWall(3); // Bottom Wall;
        } else {
            int colDiff = firstCell.col - secondCell.col;

            if (colDiff == -1) {
                firstCell.removeWall(2); // Right Wall
                secondCell.removeWall(0);// Left Wall
            } else if (colDiff == 1) {
                firstCell.removeWall(0); // Left Wall
                secondCell.removeWall(2);// Right Wall
            }
        }
    }

    public void setCellClickListener(CellClickListener listener) {
        this.clickListener = listener;
    }

    public void findGeneralShortestPath() {
        this.findShortestPath(0, 0, this.size - 1, this.size - 1, true);
    }

    public void findShortestPath(int startRow, int startCol, int endRow, int endCol, boolean showPath) {
        ArrayList<ArrayList<Integer>> adjList = new ArrayList<ArrayList<Integer>>(this.size * this.size);

        // Preparing adjacency list
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                adjList.add(new ArrayList<Integer>());
                ArrayList<Cell> neighbours = this.getCellAccessableNeighbours(i, j);
                																																																																																																																																																																																																																																					
                // Converting two dimensional indexes into one dimensional
                int curIndex = i * this.size + j;
                for (int k = 0; k < neighbours.size(); k++) {
                    Cell neighbour = neighbours.get(k);
                    adjList.get(curIndex).add(neighbour.row * this.size + neighbour.col);
                }
            }
        }

        // Predecessors
        int pred[] = new int[this.size * this.size];

        // Distances
        int dist[] = new int[this.size * this.size];

        // Breadth First Search
        this.BFS(adjList, pred, dist, startRow, startCol, endRow, endCol);

        // Stores shortest path to the destination
        ArrayList<Integer> path = new ArrayList<Integer>();

        int current = endRow * this.size + endCol;

        path.add(current);

        // Traversing from destination back to the initial position
        while (pred[current] != -1) {
            path.add(pred[current]);
            // Asssigning current to its predecessor
            current = pred[current];
        }
        this.shortestPath = path;

        if (showPath)
            this.displayPath(startCol, startRow);
    }

    public int getNextMove() {
        return this.shortestPath.get(this.shortestPath.size() - 2);
    }

    // Breadth First Search which stores predecessors
    private void BFS(ArrayList<ArrayList<Integer>> adjList, int pred[], int dist[], int y0, int x0, int yf, int xf) {
        // Queue for BFS
        ArrayList<Integer> queue = new ArrayList<Integer>();

        // Shows if certain cell was visited or not
        boolean visited[] = new boolean[this.size * this.size];

        // Initializing
        for (int i = 0; i < this.size * this.size; i++) {
            visited[i] = false; // No cell was visited
            dist[i] = Integer.MAX_VALUE;
            pred[i] = -1;
        }

        // Assigning initial cell as visited
        visited[x0 + y0 * this.size] = true;

        // Assigning the distance to zero
        dist[x0 + y0 * this.size] = 0;

        // Enqueueing to the queue
        queue.add(x0 + y0 * this.size);

        while (!queue.isEmpty()) {
            int current = queue.remove(0);

            // Looping through accessable cells for current cell
            for (int i = 0; i < adjList.get(current).size(); i++) {
                int neighbour = adjList.get(current).get(i);

                // Skiping if already visited
                if (visited[neighbour])
                    continue;

                // Marking as visited
                visited[neighbour] = true;

                // Increasing the distance
                dist[neighbour] = dist[current] + 1;

                // Assigning predecessor
                pred[neighbour] = current;

                // Enqueueing to the queue;
                queue.add(neighbour);

                // Checking if destination is reached
                if (neighbour == xf + yf * this.size)
                    return;
            }
        }
    }

    public boolean isCellVisited(int row, int col) {
        return this.grid[row][col].isVisited();
    }

    public void reset() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (i == 0 && j == 0)
                    this.grid[i][j].resetBgc(1);
                else if (i == this.size - 1 && j == this.size - 1)
                    this.grid[i][j].resetBgc(2);
                else
                    this.grid[i][j].resetBgc(0);
            }
        }
    }
}