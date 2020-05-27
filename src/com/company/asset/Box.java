/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.asset;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ville
 */
public class Box extends Field {

    // Super constructor
    public Box(Grid grid, int id) {
        super(grid, id);
    }

    /**
     * Gets the squares of this box
     * @return List of squares
     */
    public List<Square> getSquares() {
        List<Square> squares = new ArrayList<>();

        /*
        switch to correct field type, do not check all at the same time
         */
        for (Square square : getGrid().getSquares()) {
            if (square.getBox().getId() == getId()) {
                squares.add(square);
            }
        }

        return squares;
    }


}
