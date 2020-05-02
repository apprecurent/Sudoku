/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.assets;

import java.util.*;

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
    
    public boolean hasNumber(int number, int skipId) {
        for (int i = 0; i < getSquares().size(); i++) {
            if (getSquares().get(i).getNumber() == number && getSquareIds().get(i) != skipId) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(int number) {
        for (int i = 0; i < getSquares().size(); i++) {
            if (getSquares().get(i).getNumber() == number) return true;
        }
        return false;
    }

    public List<Square> getSquares(int number) {
        List<Square> squares = new ArrayList<>();
        for (Square square : getSquares()) {
            if (square.getNumber() == number) {
                squares.add(square);
            }
        }
        return squares;
    }

    public Set<Integer> getDuplicates() {
        Set<Integer> set = new HashSet<>();
        Set<Integer> duplicateSet = new HashSet<>();
        for (Square square : getSquares()) {
            if (!set.add(square.getNumber()) && square.getNumber() != 0) duplicateSet.add(square.getUniqueId());
        }
        return duplicateSet;
    }

    public Square getSquare(int id) {
        for (Square square : getSquares()) {
            if (square.getUniqueId() == id) return square;
        }
        // Throw square not found exception
        return null;
    }

    public boolean isDuplicate(int number) {
        return getDuplicates().contains(number);
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

    public abstract List<Square> getSquares();
    
    public int getId() {
        return this.id;
    }
    
    public Grid getGrid() {
        return this.grid;
    }
}
