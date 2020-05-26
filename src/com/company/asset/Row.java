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
public class Row extends Field{
    
    public Row(Grid grid, int id) {
        super(grid, id);
    }

    public List<Square> getSquares() {
         List<Square> squares = new ArrayList<>();
        
        /*
        switch to correct field type, do not check all at the same time
        */
        
        for (Square square : getGrid().getSquares()) {
            if (square.getRow().getId() == getId()) {
                squares.add(square);
            }
        }
        
        return squares;
    }
    
    
    
}
