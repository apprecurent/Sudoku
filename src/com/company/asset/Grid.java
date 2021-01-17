/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.asset;

import com.company.exception.SudokuGeneratingException;
import com.company.util.Util;

import java.awt.Graphics;
import java.util.*;
import javax.swing.*;

/**
 * @author ville
 */
public class Grid {

    private List<Square> squares;
    private List<Column> columns;
    private List<Row> rows;
    private List<Box> boxes;
    private List<Integer> solution = new ArrayList();

    private JPanel panel;

    private Random random;

    private long startTime;

    /**
     * Constructor
     *
     * @param panel the panel of this grid, could potentially be static as the grid only belongs GamePanel at all times but whatever
     */
    public Grid(JPanel panel) {
        this.panel = panel;

        init();

    }

    /**
     * Init vars and create squares
     */
    private void init() {
        this.random = new Random();
        this.squares = new ArrayList<>();
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
        this.boxes = new ArrayList<>();

        for (int i = 1; i <= 9; i++) {
            columns.add(new Column(this, i));
            rows.add(new Row(this, i));
            boxes.add(new Box(this, i));
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                squares.add(new Square(this, new Id(columns.get(j), rows.get(i))));
            }
        }
    }

    /**
     * Gets the starttime
     *
     * @return a Long representing the starttime
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * Generates a Sudoku with a specific difficulty
     *
     * @param difficulty The difficulty
     * @throws SudokuGeneratingException If could not generate in time
     */
    public void generateSudoku(Difficulty difficulty) throws SudokuGeneratingException {
        startTime = System.nanoTime();
        generateRandomSolve(true);

        for (Square square : getSquares()) {
            solution.add(square.getNumber());
        }

        List<Integer> defaultValues = new ArrayList<>();

        int remove = 0;

        /*
        There is only one more clue separating these which would make the difficulty difference not that substantial. 
        You'd need to develop the way of which the program generates the Sudokus in order to fix this considering that it could take forever to generate a puzzle which would have more than 50 empty squares.
        For illustration purposes I have choosen to include different difficulties although the difference is indeed not very big.
        */

        switch (difficulty) {
            case EASY:
                remove = 42;
                break;
            case MEDIUM:
                remove = 43;
                break;
            case HARD:
                remove = 44;
                break;
        }

        // If timeout, run again
        for (Square square : squares) {
            defaultValues.add(square.getNumber());
        }

        // Put code in here
        do {
            empty(Type.ALL);
            for (int j = 0; j < defaultValues.size(); j++) {
                squares.get(j).setNumber(defaultValues.get(j), true);
            }
            // Optimize this
            remove(remove, remove + 1);

        } while (!isUnique());

        empty(Type.INPUTS);

    }

    public List<Square> getAvailableSquares() {
        List<Square> availableSquares = new ArrayList<>();

        for (Square square : squares) {
            if (!square.hasNumber()) {
                availableSquares.add(square);
            }
        }
        return availableSquares;
    }

    /**
     * Gets the squares of this grid
     * @return List of squares
     */
    public List<Square> getSquares() {
        return this.squares;
    }

    /**
     * Gets the squares with a specific number
     *
     * @param number The number
     * @return List of squares
     */
    public List<Square> getSquares(int number) {
        List<Square> list = new ArrayList<>();
        for (Square square : getSquares()) {
            if (square.getNumber() == number) {
                list.add(square);
            }
        }

        return list;
    }

    /**
     * Gets all the empty squares
     * @return List of empty squares
     */
    public List<Integer> getEmptySquares() {
        List<Integer> emptySquares = new ArrayList<>();
        for (Square square : squares) {
            if (square.isEmpty()) {
                emptySquares.add(square.getUniqueId());
            }
        }
        return emptySquares;
    }

    /**
     * Loop through and set all squares to locked or unlocked
     *
     * @param locked locked if true
     */
    public void setLocked(boolean locked) {
        for (Square square : getSquares()) {
            square.setNumber(square.getNumber(), locked);
        }
    }

    /**
     * Check if the grid has any errors
     *
     * @return true if has errors
     */
    public boolean hasErrors() {
        for (Square square : squares) {
            if (square.hasError()) {
                return true;
            }
        }
        return false;
    }




    /**
     * Generates a completely random solution
     * @param lock locked if true
     */
    public void generateRandomSolve(boolean lock) {
        /*
        Loop while board is not full
        For loop while i is smaller than the amount of empty squares
        The selected square is equal to the position it has of the empty squares
        For loop nine times (future check how many available numbers each square has)
        Start with placing in a random number in the square, put that number in HashMap along with the squareId
        If no error, procceed to next square
        If has error, set square to 0 and go bac to previous square and increase it as long as the value is not equal to the value in the hashmap
         */
        Map<Square, Integer> squareStartNumberMap = new HashMap<>();
        List<Square> availableSquares = new ArrayList<>();

        for (Square square : getAvailableSquares()) {
            squareStartNumberMap.put(square, random.nextInt(9) + 1);
            availableSquares.add(square);
        }

        int start = 0;

        outer:
        while (!isFilled()) {

            // Algorithm went back to the first square and could still not insert a number without error
            inner:
            for (int i = start; i < availableSquares.size(); i++) {
                Square square = availableSquares.get(i);
                int number = squareStartNumberMap.get(square);
                for (int j = 0; j < 9; j++) {

                    // Check to see if all numbers have been tested given the starting number
                    if (number == 1) {
                        if (square.getNumber() == 9) {
                            break;
                        }
                    } else {
                        if (square.getNumber() == number - 1) {
                            break;
                        }
                    }

                    // Set the correct number depending on if the square is empty or not
                    if (square.isEmpty()) {
                        square.setNumber(number, false);
                    } else {
                        square.setNumber(square.getNumber() + 1, false);
                    }
                    // If the current square is listed in the map

                    // If the starting number is not 1, and the current number is 10, set the number back to 1 to loop back around
                    if (number != 1) {
                        if (square.getNumber() >= 10) {
                            square.setNumber(1, false);
                        }
                    }

                    // If the current square does not have error, proceed to next square
                    if (!square.hasError()) {
                        continue inner;
                    }
                }
                square.setNumber(0, false);
                start = --i;
                continue outer;
            }
        }

        // Replace all numbers in locked mode to get rid of errors
        if (lock) {
            for (Square square : availableSquares) {
                square.setNumber(square.getNumber(), true);
            }
        }

    }

    /**
     * Gets the solution to this grid
     * @return List of numbers to the correct solution
     */
    public List<Integer> getSolution() {
        return solution;
    }

    /**
     * Checks if the Sudoku is unique
     * @return true if unique
     * @throws SudokuGeneratingException when taking to long / timeout
     */
    public boolean isUnique() throws SudokuGeneratingException {
        List<Integer> emptySquares = new ArrayList<>(getEmptySquares());
        selectedSolve(1, false);

        List<Integer> firstSolve, compareSolve;
        firstSolve = new ArrayList<>();
        compareSolve = new ArrayList<>();

        for (int i = 0; i < emptySquares.size(); i++) {
            firstSolve.add(getSquares().get(emptySquares.get(i)).getNumber());
        }

        for (int i = 2; i < 10; i++) {

            empty(Type.INPUTS);

            selectedSolve(i, false);
            for (int j = 0; j < emptySquares.size(); j++) {
                compareSolve.add(getSquares().get(emptySquares.get(j)).getNumber());
            }

            for (int j = 0; j < emptySquares.size(); j++) {
                if (!(firstSolve.get(j).equals(compareSolve.get(j)))) {
                    empty(Type.INPUTS);
                    return false;
                }
            }

            compareSolve.clear();

        }
        return true;
    }

    /**
     * Remove a certain amount of numbers from the squares
     * @param min minimum amount of squares removed
     * @param max maximum amount of squares removed
     */
    public void remove(int min, int max) {
        int removed = 0;
        outer:
        do {
            for (int i = 0; i < squares.size(); i++) {
                int rand = random.nextInt(80);
                if (squares.get(rand).hasNumber()) {
                    squares.get(rand).setNumber(0, false);
                    removed++;
                    if (removed > max) {
                        break outer;
                    }
                }
            }
        } while (removed < min);
    }

    /**
     * Solves the Sudoku starting with a specific number
     * @param number The number to start the solve with
     * @param lock locked if true
     * @throws SudokuGeneratingException when taking to long / timeout
     */
    public void selectedSolve(int number, boolean lock) throws SudokuGeneratingException {

        int row = 0, pos = 0;

        all:
        while (!isFilled()) {
            for (int i = row; i < 9; i++) {
                inner:
                for (int j = pos; j < 9; j++) {
                    Square square = rows.get(i).getSquares().get(j);
                    if (!square.hasNumber() || !square.isLocked()) {
                        // Try to place every number in Square to check for errors.
                        for (int k = 0; k < 9; k++) {
                            // Starts to count from 1, normally

                            if (System.nanoTime() - startTime > 5000000000L) {
                                throw new SudokuGeneratingException();
                            }

                            if (number == 1) {
                                if (square.getNumber() == 9) {
                                    break;
                                }
                            } else {
                                if (square.getNumber() == number - 1) {
                                    break;
                                }
                            }

                            if (square.isEmpty()) {
                                square.setNumber(number, false);
                            } else {
                                square.setNumber(square.getNumber() + 1, false);
                            }
                            if (number != 1) {
                                if (square.getNumber() == 10) {
                                    square.setNumber(1, false);
                                }
                            }

                            if (!square.hasError()) {
                                continue inner;
                            }
                        }
                        square.setNumber(0, false);
                        // Every number was tested for a cell and all yieled errors.
                        // go back to last inputed cell and replace

                        for (int l = i; l >= 0; l--) {
                            // Set to 8 every time l (row) decreases
                            for (int p = j - 1; p >= 0; p--) {
                                // Do check to prevent get(-1)
                                Square lastSquare = rows.get(l).getSquares().get(p);
                                if (lastSquare.isLocked()) {
                                    continue;
                                }

                                row = l;
                                pos = p;
                                continue all;

                                // continue
                            }
                            j = 9;
                        }
                    }
                }
                pos = 0;
            }
        }

        // Make all Squares locked
        if (lock) {
            for (Square square : squares) {
                square.setNumber(square.getNumber(), true);
            }
        }

    }

    /**
     * Performs a normal solve (starting with 1)
     * @param locked
     */
    public void normalSolve(boolean locked) {

        int row = 0, pos = 0;

        all:
        while (!isFilled()) {
            for (int i = row; i < 9; i++) {
                inner:
                for (int j = pos; j < 9; j++) {
                    Square square = rows.get(i).getSquares().get(j);
                    if (!square.hasNumber() || !square.isLocked()) {
                        // Try to place every number in Square to check for errors.
                        for (int k = 0; k < 9; k++) {
                            // Starts to count from 1, normally
                            if (square.getNumber() == 9) {
                                break;
                            }

                            square.setNumber(square.getNumber() + 1, false);

                            if (!square.hasError()) {
                                continue inner;
                            }
                        }
                        square.setNumber(0, false);
                        // Every number was tested for a cell and all yieled errors.
                        // go back to last inputed cell and replace

                        for (int l = i; l >= 0; l--) {
                            // Set to 8 every time l (row) decreases
                            for (int p = j - 1; p >= 0; p--) {
                                // Do check to prevent get(-1)
                                Square lastSquare = rows.get(l).getSquares().get(p);
                                if (lastSquare.isLocked()) {
                                    continue;
                                }

                                row = l;
                                pos = p;
                                continue all;

                                // continue
                            }
                            j = 9;
                        }
                    }
                }
                pos = 0;
            }
        }

        // Make all Squares locked
        for (Square square : squares) {
            square.setNumber(square.getNumber(), locked);
        }

    }

    /**
     * Checks if no square pressed
     * @return true if no square is pressed
     */
    public boolean noPressed() {
        for (Square square : getSquares()) {
            if (square.isPressed()) {
                return false;
            }
        }
        return true;
    }


    /**
     * Checks if this grid is filled
     * @return true if filled
     */
    public boolean isFilled() {
        for (Square square : getSquares()) {
            if (!square.hasNumber()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Clears different types of numbers from the grid
     * @param type The type to clear
     */
    public void empty(Type type) {
        // ALL, INPUTS, ERROR
        switch (type) {
            case ALL:
                for (Square square : getSquares()) {
                    square.setNumber(0, false);
                }
                break;
            case INPUTS:
                for (Square square : getSquares()) {
                    if (!square.isLocked()) {
                        square.setNumber(0, false);
                    }
                }
                break;
        }
    }

    /**
     * Paints this grid
     * @param g
     */
    public void paint(Graphics g) {
        for (Square square : squares) {
            square.paint(g);
        }
    }

    /**
     * Gets the panel of this grid
     * @return
     */
    public JPanel getPanel() {
        return this.panel;
    }

    /**
     * Get the box for a certain column and row
     * @param column the column
     * @param row the row
     * @return the box
     */
    public Box getBox(Column column, Row row) {
        for (int i = 1; i <= 9; ) {
            for (int j = 1; j <= 9; ) {
                if (column.getId() >= j && column.getId() <= j + 2 && row.getId() >= i && row.getId() <= i + 2) {
                    int id = -1;
                    switch (i) {
                        case 1:
                            id = j - 1;
                            break;
                        case 4:
                            id = j;
                            break;
                        case 7:
                            id = j + 1;
                            break;
                    }
                    return boxes.get(id);
                }
                j += 3;
            }
            i += 3;
        }
        return null;
    }

    /**
     * Shows a representation of this grid
     * @return A string of this grid
     */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Square square : getSquares()) {
            stringBuilder.append("Pos: " + square.getUniqueId() + " Value: " + square.getNumber() + " | ");
        }

        return stringBuilder.toString();

    }





    // This method removes square for square to check if it is still solvable, probably the way to achieve harder puzzles
/*
public void remove() {
        Map<Square, Integer> map = new HashMap<>();
        for (Square square : squares) {
            map.put(square, square.getNumber());
        }


        Try random number
        If not worked, remove that number from occurring again



        If the size is the same 10 times in a row, (if it timeouts, do better measurements for that) reset the last two squares with the correct numbers to try again.
        This is because the current branch is most likely dead and will never find a correct solution.

        Possibly switch branch much quicker, as early difficulties the program has could  hint at future problems

        When removing a number from a square, check if that square can take any other value than the one it already had. If not, as the number has to be the same, it is still uniquely solvable

        int removed = 0;
        outer:
        while (true) {
            System.out.println(getTakenSquares().size());
            List<Square> availableIds = new ArrayList<>(getTakenSquares());
            while (removed < 3) {

                // If size == 0, tried every square, nothing worked, roll back to square numbers and try again
                System.out.println("SIZE: " + availableIds.size());
                int rand = random.nextInt(availableIds.size());
                Square square = availableIds.get(rand);
                int squareNumber = square.getNumber();
                availableIds.remove(square);
                square.setNumber(0, false);

                for (int i = 0; i < 9; i++) {
                    if (square.getNumber() >= 9) {
                        removed++;
                        System.out.println(removed);
                        continue outer;
                    }
                    square.setNumber(square.getNumber() + 1, false);
                    if (square.getNumber() == squareNumber) {
                        square.setNumber(square.getNumber() + 1, false);
                    }
                    if (square.hasError()) {
                        break;
                    }

                }
                empty(Type.INPUTS);

                if (continuousSolve()) {
                    removed++;
                    System.out.println(removed);
                    continue outer;

                } else {
                    square.setNumber(map.get(square), true);
                }
            }
            break;

        }

    }
 */
}
