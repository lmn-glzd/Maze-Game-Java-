package LEVEL3_4;


public class Player {
    private int row;
    private int col;
    private int score;

    public Player() {
        this.row = 0;
        this.col = 0;
        this.score = 0;
    }

    public void move(int drow, int dcol) {
        this.row += drow;
        this.col += dcol;
        this.score++;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public int getScore() {
        return this.score;
    }

    public void penalty() {
        this.score++;
    }

    public void reset() {
        this.row = 0;
        this.col = 0;
        this.score = 0;
    }
}