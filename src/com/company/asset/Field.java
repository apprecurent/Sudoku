/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.asset;

import java.util.*;

/**
 *
 * @author ville
 */
public abstract class Field {
    
    private Grid grid;
    private int id;

    /**
     * This class was not as useful as I had originally thought, only works to prohibit having to call Box, Column, Row because you can instead call all its fields
     * Also no repetition of getId and getGrid methods
     *
     * Constructor
     * @param grid the grid of this field
     * @param id the id of this field
     */
    public Field(Grid grid, int id) {
        this.grid = grid;
        this.id = id;
    }

    public abstract List<Square> getSquares();

    /**
     * Gets the id of this field
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the grid of this field
     * @return the grid
     */
    public Grid getGrid() {
        return this.grid;
    }

    @Override
    public String toString() {
        return "Field{" +
                "grid=" + grid +
                ", id=" + id +
                '}';
    }
}
