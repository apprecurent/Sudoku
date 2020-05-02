/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.assets;

import com.company.util.Util;

import java.awt.Graphics;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;

/**
 * @author ville
 */
public class Grid {

    private List<Square> squares;
    private List<Column> columns;
    private List<Row> rows;
    private List<Box> boxes;

    private JPanel panel;

    private Random random;

    /*
    Make Grid a class just containing Squares, not displaying anything automatically.
    The Grid can check the square which it has for errors without displaying
    Then have Game class (Sudoku?) that does the displaying and which takes a Grid with the appropriate Squares.
     */


    public Grid(JPanel panel) {
        this.panel = panel;
        this.random = new Random();

        init();

    }

    private void init() {
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

    public void fill() {


        // solve();

        // remove(50);


        /* Gen first 8 rows, fill the last (if possible)
        Remove random numbers
        Do checks starting with every number, and reversed.
        If any of the solutions are the same, loop again till all are the same
        Voila, solvable Sudoku
         */


        // generateRandomSolution();

        /*

         */
        long startTime = System.nanoTime();
        generateRandomSolve();
        removeTest();
        System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + " ms.");
    }

    public List<Square> getTakenSquares() {
        List<Square> takenSquares = new ArrayList<>();
        for (Square square : squares) {
            if (square.hasNumber()) takenSquares.add(square);
        }
        return takenSquares;
    }

    public boolean continuousSolve() {
        List<Square> emptySquares = new ArrayList<>(getAvailableSquares());
        List<List<Integer>> values = new ArrayList<>();

        int startNumber = 1;
        int start = 0;
        boolean success = true;

        all:
        while (startNumber < 10) {
            outer:
            while (!isFilled()) {
                inner:
                for (int i = start; i < emptySquares.size(); i++) {
                    if (i == -1) {
                        success = false;
                        break all;
                    }
                    Square square = emptySquares.get(i);
                    for (int j = 0; j < 9; j++) {
                        if (startNumber == 1) {
                            if (square.getNumber() == 9) {
                                break;
                            }
                        } else {
                            if (square.getNumber() == startNumber - 1) {
                                break;
                            }
                        }

                        if (square.isEmpty()) square.setNumber(startNumber, false);
                        else square.setNumber(square.getNumber() + 1, false);

                        if (startNumber != 1) {
                            if (square.getNumber() == 10) {
                                square.setNumber(1, false);
                            }
                        }

                        if (!square.hasError()) {
                            continue inner;
                        }
                    }
                    square.setNumber(0, false);
                    start = i - 1;
                    continue outer;
                }
            }
            List<Integer> theseValues = new ArrayList<>();

            for (Square square : emptySquares) theseValues.add(square.getNumber());

            values.add(theseValues);

            if (startNumber > 1) {
                for (int i = 0; i < emptySquares.size(); i++) {
                    if (!values.get(startNumber - 2).get(i).equals(values.get(startNumber - 1).get(i))) {
                        success = false;
                        break all;
                    }
                }
            }
            empty(Type.INPUTS);
            startNumber++;
        }
        empty(Type.INPUTS);
        return success;
    }

    public List<Square> getSquares(int number) {
        List<Square> list = new ArrayList<>();
        for (Square square : getSquares()) {
            if (square.getNumber() == number) list.add(square);
        }

        return list;
    }

    public void removeTest() {
        Map<Square, Integer> map = new HashMap<>();
        for (Square square : squares) {
            map.put(square, square.getNumber());
        }

        /*
        Try random number
        If not worked, remove that number from occurring again
         */

        /*
        If the size is the same 10 times in a row, (if it timeouts, do better measurements for that) reset the last two squares with the correct numbers to try again.
        This is because the current branch is most likely dead and will never find a correct solution.

        Possibly switch branch much quicker, as early difficulties the program has could  hint at future problems

        When removing a number from a square, check if that square can take any other value than the one it already had. If not, as the number has to be the same, it is still uniquely solvable
         */
            int removed = 0;
            outer:
            while (true) {
                List<Square> availableIds = new ArrayList<>(getTakenSquares());
                while (removed < 40) {
                    if (availableIds.size() == 0) System.out.println("TEST");

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
                        if (square.getNumber() == squareNumber) square.setNumber(square.getNumber() + 1, false);
                        if (square.hasError()) break;

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

    public boolean generateRandomSolve() {
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
        int counter = 0;

        outer:
        while (!isFilled()) {
            counter++;
            if (counter > 500 || start == -1) return false;

            // Algorithm went back to the first square and could still not insert a number without error
            inner:
            for (int i = start; i < availableSquares.size(); i++) {
                Square square = availableSquares.get(i);
                int number = squareStartNumberMap.get(square);
                for (int j = 0; j < 9; j++) {

                    // Check to see if all numbers have been tested given the starting number
                    if (number == 1) {
                        if (square.getNumber() == 9) break;
                    } else {
                        if (square.getNumber() == number - 1) break;
                    }

                    // Set the correct number depending on if the square is empty or not
                    if (square.isEmpty()) square.setNumber(number, false);
                    else square.setNumber(square.getNumber() + 1, false);
                    // If the current square is listed in the map

                    // If the starting number is not 1, and the current number is 10, set the number back to 1 to loop back around
                    if (number != 1) {
                        if (square.getNumber() >= 10) square.setNumber(1, false);
                    }

                    // If the current square does not have error, proceed to next square
                    if (!square.hasError()) continue inner;
                }
                square.setNumber(0, false);
                start = --i;
                continue outer;
            }
        }

        // Replace all numbers in locked mode to get rid of errors
        for (Square square : availableSquares) {
            square.setNumber(square.getNumber(), true);
        }
        return true;

    }

    // Exception for when id is too large or too small
    public Square getSquare(int id) {
        for (Square square : squares) {
            if (square.getUniqueId() == id) return square;
        }

        // Exception here
        return null;
    }

    public void generateSudoku(Difficulty difficulty) {

        int min = 0, max = 0;
        int counter = 0;

        List<Integer> defaultValues = new ArrayList<>();

        // If timeout, run again
        generateRandomSolve();

        for (Square square : squares) {
            defaultValues.add(square.getNumber());
        }

        switch (difficulty) {
            case EASY:
                min = 39;
                max = 40;
                break;
            case MEDIUM:
                min = 47;
                max = 48;
                break;
            case HARD:
                min = 53;
                max = 54;
                break;
        }


        // Put code in here
        do {
            counter++;
            System.out.println("ITERATION: " + counter);
            empty(Type.ALL);
            for (int j = 0; j < defaultValues.size(); j++) {
                squares.get(j).setNumber(defaultValues.get(j), true);
            }
            // Optimize this
            remove(min, max);
        } while (!isUnique());
        empty(Type.INPUTS);


        getPanel().repaint();



        /*
        int counter = 0;
        do {
            counter++;
            System.out.println("ITERATION: " + counter);
            empty(Type.ALL);
            for (int j = 0; j < defaultValues.size(); j++) {
                squares.get(j).setNumber(defaultValues.get(j), true);
            }
            // Optimize this
            remove(min, max);
        } while (!isUnique());
        empty(Type.INPUTS);
         */

    }


    public void solve(boolean locked) {
        /*
        Make a HashMap of Squares and a list of possible numbers that can go in that Square, this Map should be sorted in order of size of the possible numbers list. (if the size of the list is zero for every number, the puzzle is unsolvable)
        Make continuous loop that executes as long as the Grid is not full
        For each Square in the HashMap, loop through the list of all possible numbers and check if they are possible
        (None of the fields of that Square contain the same value AND that number is not stored in the HashMap for a Square that belongs to the same Field as the one compared)

        If Store the number for that Square together with the Square's id in another HashMap
        Continue to next Square

        If all the numbers for a Square results in errors, go back to the next possible (editable) Square and change its value to the next possible value in it's HashMap's value list
         */

        Map<Square, List<Integer>> squareValuesMap = new LinkedHashMap<>();
        for (Square availableSquare : getAvailableSquares()) {
            Set<Integer> takenNumbersSet = new HashSet<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
            for (Square fieldSquare : availableSquare.getAssociatedSquares()) {
                if (!fieldSquare.isEmpty()) takenNumbersSet.remove(fieldSquare.getNumber());
            }
            List<Integer> values = new ArrayList<>(takenNumbersSet);
            squareValuesMap.put(availableSquare, values);
        }

        LinkedHashMap<Square, List<Integer>> sortedMap = Util.sortMapByValue(squareValuesMap);
        List<Square> squareList = new ArrayList<>(sortedMap.keySet());


        int index = 0;
        outer:
        while (!isFilled()) {
            inner:
            for (int i = index; i < squareList.size(); ) {
                Square square = squareList.get(i);
                List<Integer> valueList = sortedMap.get(square);
                int currentSquareIndex = valueList.indexOf(square.getNumber());
                if (currentSquareIndex > valueList.size()) currentSquareIndex = -1;
                for (int j = currentSquareIndex; j < valueList.size(); j++) {
                    if (square.getNumber() == valueList.get(valueList.size() - 1)) break;
                    // Set it to one above of what it currently is
                    square.setNumber(valueList.get(j + 1), false);

                    if (!square.hasError()) {
                        i++;
                        continue inner;
                    }
                }
                square.setNumber(0, false);

                index = --i;
                continue outer;
            }
        }

    }

    public void solved() {
        List<Square> shuffledSquares = new ArrayList<>(getAvailableSquares());
        Collections.shuffle(shuffledSquares);

        Thread thread = new Thread(() -> {
            all:
            while (!isFilled()) {
                inner:
                for (int i = 0; i < shuffledSquares.size(); i++) {
                    Square square = shuffledSquares.get(i);
                    for (int j = square.getNumber(); j < 9; j++) {
                        if (square.getNumber() == 9) break;
                        square.setNumber(square.getNumber() + 1, false);
                        if (!square.hasError())
                            continue inner;

                    }
                    square.setNumber(0, false);
                    --i;
                    continue all;
                }
            }
        });
        thread.start();


    }

    public void solve() {


        Thread thread = new Thread(() -> {
            int row = 0, pos = 0;


            all:
            // Make boolean,  If checked all numbers and no solution found, return false
            while (!isFilled()) {
                for (int i = row; i < 9; i++) {
                    inner:
                    for (int j = pos; j < 9; j++) {
                        Square square = rows.get(i).getSquares().get(j);
                        if (!square.hasNumber() || !square.isLocked()) {
                            // Try to place every number in Square to check for errors.
                            for (int k = 0; k < 9; k++) {

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
                                // Set to 8 every time l decreases
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
                }
            }
            for (Square square : squares) {
                square.setNumber(square.getNumber(), true);
            }
        });
        thread.start();

    }

    public boolean isUnique() {
        List<Integer> emptySquares = new ArrayList<>(getEmptySquares());
        selectedSolve(1, false);

        List<Integer> firstSolve, compareSolve;
        firstSolve = new ArrayList<>();
        compareSolve = new ArrayList<>();

        // Only add unsolved squares and only check them, faster!!!!!!!

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

    public void remove(int min, int max) {
        int removed = 0;
        outer:
        do {
            for (int i = 0; i < squares.size(); i++) {
                int rand = random.nextInt(80);
                if (squares.get(rand).hasNumber()) {
                    squares.get(rand).setNumber(0, false);
                    removed++;
                    if (removed > max) break outer;
                }
            }
        } while (removed < min);
    }

    public void generateStart() {
        StringBuilder numbers = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            outer:
            while (true) {
                numbers.setLength(0);
                numbers.append("123456789");
                for (int j = 0; j < rows.size(); j++) {
                    int rand = random.nextInt(numbers.length());
                    Square square = rows.get(i).getSquares().get(j);
                    square.setNumber(Integer.parseInt(String.valueOf(numbers.charAt(rand))), true);
                    numbers.deleteCharAt(rand);

                    if (square.hasError()) {
                        for (int k = 0; k < j + 1; k++) {
                            rows.get(i).getSquares().get(k).setNumber(0, false);
                        }
                        continue outer;
                    }
                }
                break;
            }
        }
        /*

        int counter = 0;
        for (Square square : columns.get(1).getSquares()) {
            numbers.deleteCharAt(square.getNumber() - 1);
            counter++;
        }
         */
        for (int i = 3; i < 9; ) {
            numbers.setLength(0);
            numbers.append("123456789");
            int rand = random.nextInt(numbers.length());
            Square square = columns.get(0).getSquares().get(i);
            int integer = Integer.parseInt(String.valueOf(numbers.charAt(rand)));
            square.setNumber(integer, true);
            numbers.deleteCharAt(rand);

            if (square.hasError()) {
                for (int j = 3; j < i + 1; j++) {
                    columns.get(0).getSquares().get(j).setNumber(0, false);
                }
                i = 3;
            } else i++;
        }


    }

    public boolean generateRandomRows(int rowAmount) {
        StringBuilder numbers = new StringBuilder();
        int counter = 0;

        for (int i = 0; i < rowAmount; i++) {
            outer:
            while (true) {
                numbers.setLength(0);
                numbers.append("123456789");
                for (int j = 0; j < rows.size(); j++) {
                    int rand = random.nextInt(numbers.length());
                    Square square = rows.get(i).getSquares().get(j);
                    square.setNumber(Integer.parseInt(String.valueOf(numbers.charAt(rand))), true);
                    numbers.deleteCharAt(rand);

                    counter++;

                    // If it is has done 1 million loops without success
                    if (counter >= rowAmount * 100000) return false;
                    if (square.hasError()) {
                        for (int k = 0; k < j + 1; k++) {
                            rows.get(i).getSquares().get(k).setNumber(0, false);
                        }
                        continue outer;
                    }
                }
                break;
            }
        }
        return true;
    }


    public void reverseSolve() {
        int row, pos;
        row = 0;
        pos = 0;

        all:
        while (!isFilled()) {
            outer:
            for (int i = row; i < 9; i++) {
                inner:
                for (int j = pos; j < 9; j++) {
                    Square square = rows.get(i).getSquares().get(j);
                    if (!square.hasNumber() || !square.isLocked()) {
                        // Try to place every number in Square to check for errors.
                        for (int k = 0; k < 9; k++) {
                            if (square.getNumber() == 1) {
                                break;
                            }
                            if (square.getNumber() == 0) {
                                square.setNumber(9, false);
                            } else {

                                square.setNumber(square.getNumber() - 1, false);
                            }
                            if (!square.hasError()) {
                                continue inner;
                            }
                        }
                        square.setNumber(0, false);
                        // Every number was tested for a cell and all yieled errors.
                        // go back to last inputed cell and replace 

                        for (int l = i; l >= 0; l--) {
                            // Set to 8 every time l decreases
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
    }


    public void randomSolve() {

        int row, pos;
        row = 0;
        pos = 0;

        int rand = random.nextInt(9) + 1;
        System.out.println(rand);

        all:
        while (!isFilled()) {
            outer:
            for (int i = row; i < 9; i++) {
                inner:
                for (int j = pos; j < 9; j++) {
                    Square square = rows.get(i).getSquares().get(j);
                    if (!square.hasNumber() || !square.isLocked()) {
                        // Try to place every number in Square to check for errors.
                        for (int k = 0; k < 9; k++) {
                            // Starts to count from 1, normally
                            if (rand == 1) {
                                if (square.getNumber() == 9) {
                                    break;
                                }
                            } else {
                                if (square.getNumber() == rand - 1) {
                                    break;
                                }
                            }
                            // Same rand every time
                            if (square.isEmpty()) square.setNumber(rand, false);
                            else square.setNumber(square.getNumber() + 1, false);
                            if (rand != 1) {
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
        for (Square square : getSquares()) {
            square.setNumber(square.getNumber(), true);
        }

    }

    public void selectedSolve(int number, boolean lock) {

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
                            if (number == 1) {
                                if (square.getNumber() == 9) {
                                    break;
                                }
                            } else {
                                if (square.getNumber() == number - 1) {
                                    break;
                                }
                            }

                            if (square.isEmpty()) square.setNumber(number, false);
                            else square.setNumber(square.getNumber() + 1, false);
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

    public void selectedSolveV2(int number, boolean lock) {

        Long startTime = System.nanoTime();
        // Put the squareId and the amount of possible square values for that square in Map
        Map<Integer, Integer> possibleNumberMap = new HashMap<>();
        Set<Integer> takenNumbers = new HashSet<>();
        for (Square square : getAvailableSquares()) {
            for (Square innerSquare : square.getAssociatedSquares()) {
                if (innerSquare.getNumber() != 0) takenNumbers.add(innerSquare.getNumber());
            }
            possibleNumberMap.put(square.getUniqueId(), 9 - takenNumbers.size());
            takenNumbers.clear();
        }

        // Sort these from lowest to highest amount of square values, put the corresponding squares into list for later use
        LinkedHashMap<Integer, Integer> sortedMap = Util.sortByValue(possibleNumberMap);

        List<Square> finalSquares = new ArrayList<>();

        for (Integer i : sortedMap.keySet()) finalSquares.add(getSquare(i));

        System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));

        int start = 0;

        all:
        while (!isFilled()) {
            inner:
            for (int i = start; i < finalSquares.size(); i++) {
                Square square = finalSquares.get(i);
                // Try to place every number in Square to check for errors. (improve with first storing the possible numbers by looking at the already locked values that exist)
                for (int k = 0; k < 9; k++) {
                    // Starts to count from 1, normally
                    if (number == 1) {
                        if (square.getNumber() == 9) {
                            break;
                        }
                    } else {
                        if (square.getNumber() == number - 1) {
                            break;
                        }
                    }

                    if (square.isEmpty()) square.setNumber(number, false);
                    else square.setNumber(square.getNumber() + 1, false);
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

                for (int j = i - 1; j >= 0; j--) {
                    // Set to 8 every time l (row) decreases
                    // Do check to prevent get(-1)
                    Square lastSquare = finalSquares.get(j);
                    if (lastSquare.isLocked()) {
                        continue;
                    }

                    start = j;

                    continue all;
                }
            }
        }


        // Make all Squares locked
        if (lock) {
            for (Square square : squares) {
                square.setNumber(square.getNumber(), true);
            }
        }
    }

    public List<Integer> getEmptySquares() {
        List<Integer> emptySquares = new ArrayList<>();
        for (Square square : squares) {
            if (square.isEmpty()) emptySquares.add(square.getUniqueId());
        }
        return emptySquares;
    }


    public boolean noPressed() {
        for (Square square : getSquares()) {
            if (square.isPressed()) return false;
        }
        return true;
    }


    /*
    
    private int counter = 0;
    
    public boolean hasSolution(int row, int pos) {

        if (pos == 9) {
            row++;
            pos = 0;
        }
        for (int i = row; i < rows.size(); i++) {
            for (int j = pos; j < 9; j++) {
                Square square = rows.get(i).getSquares().get(j);
                counter++;
                System.out.println("I: " + i + "(" + row + " ROW) | J: " + j + "(" + pos + " POS) | VALUE: " + square.getNumber());
                if (!square.hasNumber() || !square.isLocked()) {
                    // Try to place every number in Square to check for errors.
                    for (int k = 0; k < 9; k++) {
                        if (square.getNumber() == 9) {
                            break;
                        }
                        square.setNumber(square.getNumber() + 1, false);

                        if (!square.hasError()) {
                            hasSolution(i, j + 1);
                        }
                    }

                    square.setNumber(0, false);
                    // Every number was tested for a cell and all yieled errors.
                    // go back to last inputed cell and replace 

                    for (int l = i; l >= 0; l--) {
                        for (int p = j - 1; p >= 0; p--) {

                            if (p == -1) {
                                l--;
                                p = 8;
                            }
                            square = rows.get(l).getSquares().get(p);
                            if (square.isLocked()) {
                                continue;
                            }

                            hasSolution(l, p);
                        }
                    }
                }
            }
        }

        return true;
    }
     */
    public boolean isFilled() {
        for (Square square : getSquares()) {
            if (!square.hasNumber()) {
                return false;
            }
        }

        return true;
    }

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
                    if (!square.isLocked()) square.setNumber(0, false);
                }
                break;
        }
    }

    public List<Square> getAvailableSquares(Field field, int number) {
        List<Square> squares = new ArrayList<>();

        for (Square square : field.getSquares()) {
            if (!square.hasNumber()) {
                square.setNumber(number, false);
                if (!square.hasError()) {
                    squares.add(square);
                }
                square.setNumber(0, false);
            }
        }
        return squares;
    }

    public List<Square> getAvailableSquares() {
        List<Square> availableSquares = new ArrayList<>();

        for (Square square : squares) {
            if (!square.hasNumber()) availableSquares.add(square);
        }
        return availableSquares;
    }

    public List<Square> getSquares() {
        return this.squares;
    }

    public void paint(Graphics g) {
        for (Square square : squares) {
            square.paint(g);
        }
    }

    public JPanel getPanel() {
        return this.panel;
    }

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

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Square square : getSquares()) {
            stringBuilder.append("Pos: " + square.getUniqueId() + " Value: " + square.getNumber() + " | ");
        }

        return stringBuilder.toString();

    }

       /*
          StringBuilder numbers = new StringBuilder();
        numbers.append("123456789");
        List<Integer> pattern = new ArrayList<>();

        // Generate first random row, use this later to create the other ones
        for (int j = 0; j < 9; j++) {
            int rand = random.nextInt(numbers.length());
            pattern.add(Integer.parseInt(String.valueOf(numbers.charAt(rand))));
            numbers.deleteCharAt(rand);

        }

            for (Square square : getSquares()) {
                for (int i = 0; i < 9; i++) {
                    int randNumber = pattern.get(i);
                    if (!square.getRow().hasNumber(randNumber, square.getColumn().getId())
                            && !square.getColumn().hasNumber(randNumber, square.getRow().getId())
                            && !square.getBox().hasNumber(randNumber, square.getUniqueId())) {
                        square.setNumber(randNumber, true);
                    }
                }

        }
         */
    /*
     * Generate complete puzzle
     * Remove a random number in every iteration
     * Check if the puzzle is still solvable
     * When it is no longer solvable, add back the last removed number
     */
 /*
        StringBuilder numbers = new StringBuilder();
        numbers.append("123456789");

        // Generate first random row, use this later to create the other ones
        for (int j = 0; j < rows.size(); j++) {
            int rand = random.nextInt(numbers.length());
            rows.get(0).getSquares().get(j).setNumber(Integer.parseInt(String.valueOf(numbers.charAt(rand))), true);
            numbers.deleteCharAt(rand);

        }

        List<Integer> numberPattern = new ArrayList<>();

        // Put all numbers in pattern list
        for (Square square : rows.get(0).getSquares()) {
            numberPattern.add(square.getNumber());
        }

        for (int i = 1; i < rows.size(); i++) {
            // Only move by 1 if its the 4th or 7th row
            if (i == 3 || i == 6) {

                // Remove first number and place it at the back of the list
                int number = numberPattern.remove(0);
                numberPattern.add(number);

                // Move by 3 blocks to the left
            } else {

                for (int j = 0; j < 3; j++) {
                    int number = numberPattern.remove(0);
                    numberPattern.add(number);
                }
            }

            // Set all numbers into Grid
            for (int j = 0; j < numberPattern.size(); j++) {
                rows.get(i).getSquares().get(j).setNumber(numberPattern.get(j), true);
            }
        }

        // Input condition for minimum and maximum amount of left squares. Determines difficulty (more squares, eaiser) while () {
        int removedSquares = 0;

        // Adjust this number up for higher difficulty (longer time to generate) and down for lower difficulty (less time to generate)
        int emptySquares = 10;
        int counter = 1;
        while (removedSquares < emptySquares) {
            System.out.println(counter);
            removedSquares = 0;
            for (int i = 0; i < getSquares().size(); i++) {
                int rand = random.nextInt(80);
                if (getSquares().get(rand).hasNumber()) {
                    getSquares().get(rand).setNumber(0, false);
                    removedSquares++;
                }
                if (!isSolvable()) {
                    break;
                }
            }

            counter++;

        }
         */
    // Randomize rows, not working
        /*
         for (int i = 0; i < 10; i++) {
            int rand = random.nextInt(9);

            System.out.println(rand);

            List<Integer> switchRowIntegers, firstRowIntegers;
            switchRowIntegers = new ArrayList<>();
            firstRowIntegers = new ArrayList<>();

            for (int j = 0; j < rows.size(); j++) {
                System.out.println("SEPERATOR");
                switchRowIntegers.add(rows.get(rand).getSquares().get(j).getNumber());
                System.out.println(rows.get(rand).getSquares().get(j).getNumber());
                firstRowIntegers.add(rows.get(0).getSquares().get(j).getNumber());
                System.out.println(rows.get(0).getSquares().get(j).getNumber());
            }

            List<Integer> tempIntegers = new ArrayList<>(switchRowIntegers);
            switchRowIntegers.clear();
            switchRowIntegers.addAll(firstRowIntegers);
            firstRowIntegers.clear();
            firstRowIntegers.addAll(tempIntegers);

            for (int j = 0; j < rows.size(); j++) {
                rows.get(rand
               ).getSquares().get(j).setNumber(firstRowIntegers.get(j), true);
                rows.get(0).getSquares().get(j).setNumber(switchRowIntegers.get(j), true);
            }

        }
         */
 /*

        StringBuilder ints = new StringBuilder();
        ints.append("123456789");
        String pattern = "";
        for (int i = 0; i < 9; i++) {
            int rand = random.nextInt(ints.length());
            pattern += ints.charAt(rand);
            ints.deleteCharAt(rand);
        }

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < columns.size(); j++) {

            }
        }
         */
 /*

        getSquares().get(0).setNumber(5, true);
        getSquares().get(1).setNumber(6, true);
        getSquares().get(3).setNumber(8, true);
        getSquares().get(4).setNumber(4, true);
        getSquares().get(5).setNumber(7, true);
        getSquares().get(9).setNumber(3, true);
        getSquares().get(11).setNumber(9, true);
        getSquares().get(15).setNumber(6, true);
        getSquares().get(20).setNumber(8, true);
        getSquares().get(28).setNumber(1, true);
        getSquares().get(31).setNumber(8, true);
        getSquares().get(34).setNumber(4, true);
        getSquares().get(36).setNumber(7, true);
        getSquares().get(37).setNumber(9, true);
        getSquares().get(39).setNumber(6, true);
        getSquares().get(41).setNumber(2, true);
        getSquares().get(43).setNumber(1, true);
        getSquares().get(44).setNumber(8, true);
        getSquares().get(46).setNumber(5, true);
        getSquares().get(49).setNumber(3, true);
        getSquares().get(52).setNumber(9, true);
        getSquares().get(60).setNumber(2, true);
        getSquares().get(65).setNumber(6, true);
        getSquares().get(69).setNumber(8, true);
        getSquares().get(71).setNumber(7, true);
        getSquares().get(75).setNumber(3, true);
        getSquares().get(76).setNumber(1, true);
        getSquares().get(77).setNumber(6, true);
        getSquares().get(79).setNumber(5, true);
        getSquares().get(80).setNumber(9, true);
         */
 /*
        Easy Sudoku.com
        getSquares().get(0).setNumber(5, true);
        getSquares().get(1).setNumber(2, true);
        getSquares().get(5).setNumber(7, true);
        getSquares().get(6).setNumber(3, true);
        getSquares().get(7).setNumber(8, true);
        getSquares().get(11).setNumber(7, true);
        getSquares().get(13).setNumber(4, true);
        getSquares().get(14).setNumber(2, true);
        getSquares().get(16).setNumber(1, true);
        getSquares().get(18).setNumber(1, true);
        getSquares().get(19).setNumber(9, true);
        getSquares().get(20).setNumber(3, true);
        getSquares().get(21).setNumber(5, true);
        getSquares().get(23).setNumber(8, true);
        getSquares().get(24).setNumber(7, true);
        getSquares().get(28).setNumber(7, true);
        getSquares().get(30).setNumber(8, true);
        getSquares().get(32).setNumber(1, true);
        getSquares().get(34).setNumber(4, true);
        getSquares().get(37).setNumber(3, true);
        getSquares().get(40).setNumber(2, true);
        getSquares().get(43).setNumber(7, true);
        getSquares().get(45).setNumber(9, true);
        getSquares().get(49).setNumber(7, true);
        getSquares().get(50).setNumber(3, true);
        getSquares().get(51).setNumber(6, true);
        getSquares().get(53).setNumber(2, true);
        getSquares().get(57).setNumber(6, true);
        getSquares().get(58).setNumber(8, true);
        getSquares().get(61).setNumber(3, true);
        getSquares().get(62).setNumber(1, true);
        getSquares().get(64).setNumber(1, true);
        getSquares().get(65).setNumber(2, true);
        getSquares().get(70).setNumber(9, true);
        getSquares().get(73).setNumber(5, true);
        getSquares().get(74).setNumber(8, true);
        getSquares().get(75).setNumber(2, true);
        getSquares().get(76).setNumber(1, true);
        getSquares().get(79).setNumber(6, true);
         */
 /*

        getSquares().get(0).setNumber(8, true);
        getSquares().get(2).setNumber(7, true);
        getSquares().get(3).setNumber(5, true);
        getSquares().get(8).setNumber(1, true);
        getSquares().get(13).setNumber(3, true);
        getSquares().get(20).setNumber(1, true);
        getSquares().get(26).setNumber(8, true);
        getSquares().get(30).setNumber(3, true);
        getSquares().get(35).setNumber(4, true);
        getSquares().get(37).setNumber(9, true);
        getSquares().get(38).setNumber(6, true);
        getSquares().get(46).setNumber(5, true);
        getSquares().get(49).setNumber(8, true);
        getSquares().get(50).setNumber(4, true);
        getSquares().get(51).setNumber(6, true);
        getSquares().get(54).setNumber(9, true);
        getSquares().get(55).setNumber(6, true);
        getSquares().get(66).setNumber(7, true);
        getSquares().get(68).setNumber(3, true);
        getSquares().get(69).setNumber(9, true);
        getSquares().get(76).setNumber(2, true);
        getSquares().get(79).setNumber(5, true);
         */
 /*
         Medium Sudoku.com
         getSquares().get(0).setNumber(2, true);
         getSquares().get(4).setNumber(3, true);
         getSquares().get(8).setNumber(9, true);
         getSquares().get(9).setNumber(9, true);
         getSquares().get(12).setNumber(6, true);
         getSquares().get(18).setNumber(6, true);
         getSquares().get(24).setNumber(8, true);
         getSquares().get(29).setNumber(2, true);
         getSquares().get(30).setNumber(1, true);
         getSquares().get(32).setNumber(7, true);
         getSquares().get(37).setNumber(4, true);
         getSquares().get(43).setNumber(1, true);
         getSquares().get(46).setNumber(9, true);
         getSquares().get(47).setNumber(6, true);
         getSquares().get(48).setNumber(2, true);
         getSquares().get(53).setNumber(3, true);
         getSquares().get(54).setNumber(3, true);
         getSquares().get(56).setNumber(8, true);
         getSquares().get(59).setNumber(5, true);
         getSquares().get(61).setNumber(6, true);
         getSquares().get(62).setNumber(2, true);
         getSquares().get(63).setNumber(4, true);
         getSquares().get(64).setNumber(2, true);
         getSquares().get(66).setNumber(7, true);
         getSquares().get(68).setNumber(3, true);
         getSquares().get(69).setNumber(1, true);
         getSquares().get(75).setNumber(9, true);
         getSquares().get(76).setNumber(2, true);
         getSquares().get(78).setNumber(3, true);
         getSquares().get(79).setNumber(7, true);


         */

 /*
    public boolean isSolvable() {

        int counter = 0;

        List<Integer> squareValues = new ArrayList<>();
        for (Square square : getSquares()) {
            squareValues.add(square.getNumber());
        }

        for (int j = 0; j < 2; j++) {
            for (Square square : getSquares()) {
                if (!square.hasNumber()) {

                    for (int i = 1; i <= 9; i++) {
                        for (Field field : square.getFields()) {
                            for (Square availableSquare : getAvailableSquares(field, i)) {
                                counter++;
                                if (getAvailableSquares(field, i).size() == 1) {
                                    availableSquare.setNumber(i, false);
                                }
                                if (availableSquare.hasError()) {
                                    availableSquare.setNumber(0, false);
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println(counter);

        boolean solvable = false;

        if (isFilled()) {
            solvable = true;
        }

        empty(Type.ALL);

        for (int i = 0; i < getSquares().size(); i++) {
            boolean locked;
            if (squareValues.get(i) == 0) {
                locked = false;
            } else {
                locked = true;
            }
            getSquares().get(i).setNumber(squareValues.get(i), locked);
        }

        return solvable;
    }
     */

}
