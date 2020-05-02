/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.assets;

import com.company.assets.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ville
 */
public class Column extends Field{

    public Column(Grid grid, int id) {
        super(grid, id);
    }

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
