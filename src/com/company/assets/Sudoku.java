package com.company.assets;

import java.awt.*;

public class Sudoku {

    private Grid grid;

    public Sudoku(Panel panel) {
        // generateGrid();
    }

    public void paint(Graphics g) {
        grid.paint(g);
    }

    public Grid getGrid() {
        return this.grid;
    }


}
