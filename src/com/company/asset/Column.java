/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.asset;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ville
 */
public class Column extends Field{

    /**
     * Super constructor
     * @param grid the grid
     * @param id the id
     */
    public Column(Grid grid, int id) {
        super(grid, id);
    }

    /**
     * Gets the squares of this column
     * @return List of squares
     */
    public List<Square> getSquares() {
        List<Square> squares = new ArrayList<>();
        
        /*
        switch to correct field type, do not check all at the same time
        */
        
        for (Square square : getGrid().getSquares()) {
            if (square.getColumn().getId() == getId()) {
                squares.add(square);
            }
        }
        
        return squares;
    }

    
}
