package com.company.assets;

import java.util.*;
import java.awt.*;
import java.util.List;

public class Sudoku {

    private Grid grid;

    public Sudoku(Panel panel) {
        // generateGrid();
    }

    public void solve() {
        for (List<Square> innerList : grid.getSplitSquares()) {
            for (Square square : innerList) {

            }
        }
    }

    public void paint(Graphics g) {
        grid.paint(g);
    }

    public Grid getGrid() {
        return this.grid;
    }


}
