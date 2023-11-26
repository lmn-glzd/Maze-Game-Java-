package LEVEL3_4;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class Cell extends JPanel {
    private static final long serialVersionUID = 1L;
    public int row;
    public int col;
    public boolean visited;
    public boolean[] walls;

    public Cell(int row, int col, int size) {
        this.row = row;
        this.col = col;
        this.visited = false;
        // The walls on the each side LEFT TOP RIGHT BOTTOM
        this.walls = new boolean[] { true, true, true, true };
        if (row == 0 && col == 0)
            this.resetBgc(1); // Start
        else if (row == size - 1 && col == size - 1)
            this.resetBgc(2); // Finish
        else
            this.resetBgc(0); // Init

    }

    public boolean isVisited() {
        return this.visited;
    }

    public void resetBgc(int i) {
        // 0 - Black, Initial
        // 1 - Red, Start
        // 2 - Green, Finish
        // 3 - Purple, In path
        // 4 - Blue, Player Position
        // 5 - LightBlue, Player Path
        // 6 - Yellow, Hint
        switch (i) {
            case 0:
                this.setBackground(new Color(0, 0, 0));
                break;
            case 1:
                this.setBackground(new Color(255, 0, 0));
                break;
            case 2:
                this.setBackground(new Color(0, 255, 0));
                break;
            case 3:
                this.setBackground(new Color(200, 0, 255));
                break;
            case 4:
                this.setBackground(new Color(0, 0, 255));
                break;
            case 5:
                this.setBackground(new Color(49, 54, 89));
                break;
            case 6:
                this.setBackground(new Color(255, 255, 0));
                break;
            default:
                this.setBackground(new Color(0, 0, 0));
                break;
        }
    }

    public void resetBorders() {
        this.setBorder(new MatteBorder(this.walls[1] ? 1 : 0, this.walls[0] ? 1 : 0, this.walls[3] ? 1 : 0,
                this.walls[2] ? 1 : 0, Color.WHITE));
    }

    public void setVisited() {
        this.visited = true;
    }

    public void removeWall(int index) {
        this.walls[index] = false;
        this.resetBorders();
    }

    public boolean isWall(int index) {
        return this.walls[index];
    }

    public void unsetWalls() {
        this.walls = new boolean[] { false, false, false, false };
        this.resetBorders();
    }

    public void setWall(int index) {
        this.walls[index] = true;
        this.resetBorders();
    }
}