/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.assets;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ville
 */
public class Box extends Field {

    public Box(Grid grid, int id) {
        super(grid, id);
    }

    public boolean hasNumber(int number, int pos) {
        for (int i = 0; i < getSquares().size(); i++) {
            if (getSquares().get(i).getNumber() == number && getSquareIds().get(i) != pos) {
                return true;
            }
        }
        return false;
    }

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

    public List<Integer> getSquareIds() {
        List<Integer> squareIds = new ArrayList<>();

        // First row
        for (Square square : getSquares()) {
            squareIds.add(square.getUniqueId());
          //  int i = getId() + (getId() - 1) * 2 + 9 * (square.getRow().getId() - 1);
        }

        return squareIds;
    }

}
