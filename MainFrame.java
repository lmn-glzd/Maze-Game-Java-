	package LEVEL3_4;

import javax.swing.*;


import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private Maze maze;
    private boolean isGameFinished;
    private Player player;
    private boolean isSelectingCell;
    private int[] hint;

    public MainFrame() {
        super("Maze");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.height - 50, screenSize.height - 50);

        setJMenuBar(this.menu());

        // Default
        this.newGameFirst(10);

        this.setUnfinished();
        this.player = new Player();
        this.updateScore();
        this.isSelectingCell = false;
        this.hint = new int[]{-1, -1};

        // Setting our player on start
        this.maze.setAsPlayerPos(0, 0, 0, 0);

        this.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                movePlayer(e.getKeyCode());
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
            }
        });
    }

    private void movePlayer(int keyCode) {
        if (isFinished())
            return;
        int dx = 0;
        int dy = 0;
        switch (keyCode) {
            case 37:
                dx = -1;
                break;
            case 38:
                dy = -1;
                break;
            case 39:
                dx = 1;
                break;
            case 40:
                dy = 1;
                break;
            default:
                break;
        }

        if (isValidMove(dy, dx)) {
            if (isHintMarked())
                unsetHint();

            maze.setAsPlayerPos(player.getRow(), player.getCol(), player.getRow() + dy, player.getCol() + dx);
            player.move(dy, dx);

            updateScore();
            if (isWinner()) {
                setFinished();
                congratulate();
            }
        }
    }

    private boolean isHintMarked() {
        return this.hint[0] != -1;
    }

    private void setHint(int row, int col) {
        this.hint = new int[]{row, col};
    }

    private void unsetHint() {
        if (this.isHintMarked())
            this.maze.grid[this.hint[0]][this.hint[1]].resetBgc(0);
        this.hint = new int[]{-1, -1};
    }

    private void setFinished() {
        this.isGameFinished = true;
    }

    private void congratulate() {
        int action = JOptionPane.showConfirmDialog(this,
                "Congratulations! You have won. Your final score is: " + player.getScore() + ". Want to play again?",
                "Congratulations", JOptionPane.OK_CANCEL_OPTION);
        if (action == JOptionPane.OK_OPTION) {
            this.newGameFirst(this.maze.size);
            this.reset();
        }
    }

    private void setUnfinished() {
        this.isGameFinished = false;
    }

    private boolean isFinished() {
        return this.isGameFinished;
    }

    private void updateScore() {
        this.setTitle("Score: " + this.player.getScore());
    }

    private boolean isValidMove(int dy, int dx) {
        if (dx == 0 && dy == 0)
            return false;
        if (dx == -1)
            if (player.getCol() == 0 || maze.grid[player.getRow()][player.getCol()].isWall(0))
                return false;
        if (dx == 1)
            if (player.getCol() == maze.getMazeSize() - 1 || maze.grid[player.getRow()][player.getCol()].isWall(2))
                return false;
        if (dy == -1)
            if (player.getRow() == 0 || maze.grid[player.getRow()][player.getCol()].isWall(1))
                return false;
        if (dy == 1)
            if (player.getRow() == maze.getMazeSize() - 1 || maze.grid[player.getRow()][player.getCol()].isWall(3))
                return false;

        return true;
    }

    private void selectCell() {
        maze.setCellClickListener(new CellClickListener() {
            public void cellClickEventOccurred(CellClickEvent e) {
                if (isSelectingCell) {
                    int row = e.getRow();
                    int col = e.getCol();

                    isSelectingCell = false;
                    maze.findShortestPath(row, col, maze.getMazeSize() - 1, maze.getMazeSize() - 1, true);
                }
            }
        });
    }

    private boolean isWinner() {
        return this.player.getRow() == this.maze.getMazeSize() - 1
                && this.player.getCol() == this.maze.getMazeSize() - 1;
    }

    private void getHint() {
        if (this.isFinished())
            return;

        this.maze.findShortestPath(this.player.getRow(), this.player.getCol(), this.maze.getMazeSize() - 1,
                this.maze.getMazeSize() - 1, false);

        int hintCellIndex = this.maze.getNextMove();
        int hintRow = hintCellIndex / this.maze.getMazeSize();
        int hintCol = hintCellIndex % this.maze.getMazeSize();

        this.setHint(hintRow, hintCol);
        this.maze.grid[hintRow][hintCol].resetBgc(6); // Mark as hint
        this.player.penalty();
        this.updateScore();
    }

    private void reDraw() {
        this.getContentPane().removeAll();
        this.add(maze);
        this.repaint();
    }

    private void newGameFirst(int size) {
        this.maze = new Maze(size);
        this.reDraw();
        this.maze.generate();
    }

    private void newGameSecond(int size) {
        this.maze = new Maze(size);
        this.reDraw();
        this.maze.generateSecond();
    }

    private void reset() {
        this.player.reset();
        this.setUnfinished();
        this.updateScore();
        this.maze.setAsPlayerPos(0, 0, 0, 0);
    }

    private JMenuBar menu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem newGame1 = new JMenuItem("New Maze with Backtracking");
        JMenuItem newGame2 = new JMenuItem("New Maze with Recursive Division");
        JMenuItem exit = new JMenuItem("Exit");

        // Adding events
        newGame1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String value = JOptionPane.showInputDialog(MainFrame.this, "Enter size of the maze",
                        maze.getMazeSize());
                if (value == null)
                    return;
                newGameFirst(Integer.parseInt(value));
                reset();
            }
        });

        newGame2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String value = JOptionPane.showInputDialog(MainFrame.this, "Enter size of the maze",
                        maze.getMazeSize());
                if (value == null)
                    return;
                newGameSecond(Integer.parseInt(value));
                reset();
            }
        });

        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int action = JOptionPane.showConfirmDialog(MainFrame.this, "Do you really want to exit?",
                        "Confirm Exit", JOptionPane.OK_CANCEL_OPTION);
                if (action == JOptionPane.OK_OPTION)
                    System.exit(0);
            }
        });

        // Adding an accelerator
        newGame1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newGame2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));

        fileMenu.add(newGame1);
        fileMenu.add(newGame2);
        fileMenu.add(exit);

        JMenu movement = new JMenu("Move");

        JMenuItem left = new JMenuItem("Left");
        JMenuItem top = new JMenuItem("Top");
        JMenuItem right = new JMenuItem("Right");
        JMenuItem bottom = new JMenuItem("Bottom");

        // Adding Events
        left.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                movePlayer(37); // Left arrow key code
            }
        });

        top.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                movePlayer(38); // Top arrow key code
            }
        });

        right.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                movePlayer(39); // Right arrow key code
            }
        });

        bottom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                movePlayer(40); // Bottom arrow key code
            }
        });

        movement.add(left);
        movement.add(top);
        movement.add(right);
        movement.add(bottom);

        JMenu toolsMenu = new JMenu("Tools");

        JMenuItem solveFromStart = new JMenuItem("Solve from entry");
        JMenuItem solveFromGivenPoint = new JMenuItem("Solve from selected");
        JMenuItem reset = new JMenuItem("Reset Maze");
        JMenuItem hint = new JMenuItem("Hint");

        // Adding Events
        solveFromStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFinished())
                    return;
                int action = JOptionPane.showConfirmDialog(MainFrame.this, "Do you really want to see the solution?",
                        "Confirm Solving", JOptionPane.OK_CANCEL_OPTION);
                if (action == JOptionPane.OK_OPTION) {
                    maze.findGeneralShortestPath();
                    setFinished();
                }
            }
        });

        solveFromGivenPoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFinished())
                    return;
                isSelectingCell = true;
                setFinished();
                selectCell();
            }
        });

        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int action = JOptionPane.showConfirmDialog(MainFrame.this, "Do you really want to reset the maze?",
                        "Confirm Reset", JOptionPane.OK_CANCEL_OPTION);
                if (action == JOptionPane.OK_OPTION) {
                    maze.reset();
                    reset();
                }
            }
        });

        hint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getHint();
            }
        });

        // Adding an accelerator
        solveFromStart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        solveFromGivenPoint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        reset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        hint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));

        toolsMenu.add(solveFromStart);
        toolsMenu.add(solveFromGivenPoint);
        toolsMenu.add(reset);
        toolsMenu.add(hint);

        menuBar.add(fileMenu);
        menuBar.add(movement);
        menuBar.add(toolsMenu);

        return menuBar;
    }
}