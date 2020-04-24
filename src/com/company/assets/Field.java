/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.assets;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ville
 */
public abstract class Field {
    
    private Grid grid;
    private int id;
    
    public Field(Grid grid, int id) {
        this.grid = grid;
        this.id = id;
    }
    
    public abstract boolean hasNumber(int number, int pos);
    
    public abstract List<Square> getSquares();
    
    public int getId() {
        return this.id;
    }
    
    public Grid getGrid() {
        return this.grid;
    }
    
    
    
}
