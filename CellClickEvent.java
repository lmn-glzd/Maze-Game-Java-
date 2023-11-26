package LEVEL3_4;

import java.util.EventObject;

public class CellClickEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private int row;
    private int col;

    public CellClickEvent(Object source, int row, int col) {
        super(source);
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }
}