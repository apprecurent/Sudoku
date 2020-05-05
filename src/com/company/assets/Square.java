/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.assets;

import com.company.GamePanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @author ville
 */
public class Square {

    private int number;
    private Id id;

    private int col;
    private int row;

    private boolean highlighted, pressed, error, locked, marked, hovered;

    private JLabel lblNumber;

    private List<JLabel> notes;

    private Grid grid;

    /**
     * Constructor of a Square, assign var data (make separate method init?)
     *
     * @param grid The grid which this Square belong sto
     * @param id   The identifier of the Square (row and column)
     */
    public Square(Grid grid, Id id) {
        this.id = id;
        this.number = 0;
        this.grid = grid;

        col = id.getColumnId();
        row = id.getRowId();

        notes = new ArrayList<>();

        /*
        Initialize lblNumber, containing the number of each Square
         */
        lblNumber = new JLabel();
        grid.getPanel().add(lblNumber);
        lblNumber.setLocation(new Point((col - 1) * 50, (row - 1) * 50));
        lblNumber.setSize(new Dimension(50, 50));
        lblNumber.setFont(new Font("Arial", Font.PLAIN, 30));
        lblNumber.setVerticalAlignment(SwingConstants.CENTER);
        lblNumber.setHorizontalAlignment(SwingConstants.CENTER);

        /*
        Initialize lblNote, containing the notes of each Square
         */
        for (int i = 0; i < 9; i++) {
            int vertical = 0, horizontal = 0;
            JLabel lblNote = new JLabel();
            grid.getPanel().add(lblNote);
            lblNote.setText(String.valueOf(i + 1));
            lblNote.setLocation(new Point((col - 1) * 50 + 5, (row - 1) * 50 + 5));
            lblNote.setSize(new Dimension(40, 40));
            lblNote.setFont(new Font("Arial", Font.PLAIN, 10));
            lblNote.setVisible(false);

            // Assign correct position corresponding to the number
            switch (i) {
                case 0:
                    vertical = SwingConstants.TOP;
                    horizontal = SwingConstants.LEFT;
                    break;
                case 1:
                    vertical = SwingConstants.TOP;
                    horizontal = SwingConstants.CENTER;
                    break;
                case 2:
                    vertical = SwingConstants.TOP;
                    horizontal = SwingConstants.RIGHT;
                    break;
                case 3:
                    vertical = SwingConstants.CENTER;
                    horizontal = SwingConstants.LEFT;
                    break;
                case 4:
                    vertical = SwingConstants.CENTER;
                    horizontal = SwingConstants.CENTER;
                    break;
                case 5:
                    vertical = SwingConstants.CENTER;
                    horizontal = SwingConstants.RIGHT;
                    break;
                case 6:
                    vertical = SwingConstants.BOTTOM;
                    horizontal = SwingConstants.LEFT;
                    break;
                case 7:
                    vertical = SwingConstants.BOTTOM;
                    horizontal = SwingConstants.CENTER;
                    break;
                case 8:
                    vertical = SwingConstants.BOTTOM;
                    horizontal = SwingConstants.RIGHT;
                    break;
            }

            // Set correct alignments
            lblNote.setVerticalAlignment(vertical);
            lblNote.setHorizontalAlignment(horizontal);

            // Add the new note to the list
            notes.add(lblNote);
        }
    }


    public void setNumber(int number, boolean locked) {

        /*
         Clear all notes when setting a new number (the number should always override the notes)
         Must do this before setting the number or the clearNotes() will think that there is a number and note remove the notes
         */
        clearNotes();

        // Set vars
        // temp
        int previousNumber = this.number;
        this.number = number;
        this.locked = locked;
        this.error = false;

        // Set the text of the label
        lblNumber.setText(String.valueOf(number));

        // 0 equals an empty square
        if (number == 0) {
            this.locked = false;
            lblNumber.setText("");
        }

        // loop through all the fields (row, column, box) and do these checks instead (good for highlighting associated squares aswell)
        /*
        FINALLY
         */

        /*


        for (Square square : getAssociatedSquares()) {
            if (square.hasDuplicateValue()) {
                square.setError(true);
            }
            if (square.getNumber() == number && number != 0) {
                this.error = true;
                square.setError(true);
            }
            else square.setError(false);
        }
         */


        /*
        HOLY SHIT, NEVER AGAIN
         */
        int usedNumber = number == 0 ? previousNumber : number;
        for (Square square : grid.getSquares(usedNumber)) {
            square.setError(square.hasDuplicateValue() && square.getNumber() != 0);
        }


        /*

        for (Field field : getFields()) {
            for (Square square : field.getSquares()) {
                square.setError(field.hasNumber(square.getNumber(), square.getUniqueId()));
            }
        }
         */
    }

    public void setNumber(int number) {
        clearNotes();
        this.number = number;

        lblNumber.setText(String.valueOf(number));
        if (number == 0) {
            lblNumber.setText("");
        }
        for (Square square : grid.getSquares()) {
            square.setError(square.hasDuplicateValue() && square.getNumber() != 0);
        }
    }

    public boolean hasDuplicateValue() {
        for (Square square : getAssociatedSquares()) {
            if (square.getNumber() == number) return true;
        }
        return false;
    }

    public Set<Square> getAssociatedSquares() {
        Set<Square> associated = new HashSet<>();
        associated.add(this);
        for (Field field : getFields()) {
            associated.addAll(field.getSquares());
        }
        associated.remove(this);
        return associated;
    }

    public void color() {
        if (locked)
            lblNumber.setForeground(Color.BLACK);
        else if (error)
            lblNumber.setForeground(Color.RED);
        else
            lblNumber.setForeground(new Color(25, 110, 140));

    }

    private Color color;

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean hasColor() {
        return color != null;
    }

    public void setError(boolean error) {
        this.error = error;
        color();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        color();
    }


    public void setNote(int note) {

        // Only set notes if the Square is empty, else makes it invisible
        if (!hasNumber()) notes.get(note - 1).setVisible(!isVisible(note));
    }

    public void clearNotes() {
        // Set all notes that are currently active, making them go invisible again
        for (int i = 1; i < 10; i++) {
            if (isVisible(i)) {
                setNote(i);
            }
        }
    }

    public boolean isVisible(int note) {
        return notes.get(note - 1).isVisible();
    }

    public int getNumber() {
        return this.number;
    }

    public boolean hasNumber() {
        return number != 0;
    }

    public int getUniqueId() {
        return col + (row - 1) * 9 - 1;
    }

    public Column getColumn() {
        return this.id.getColumn();
    }

    public Row getRow() {
        return this.id.getRow();
    }

    public Box getBox() {
        return grid.getBox(getColumn(), getRow());
    }

    public List<Field> getFields() {
        List<Field> fields = new ArrayList<>();

        fields.add(getColumn());
        fields.add(getRow());
        fields.add(getBox());

        return fields;
    }


    public void paint(Graphics g) {

        /*
        g.drawLine((col - 1) * 50, (row - 1) * 50, col * 50, (row - 1) * 50);
        g.drawLine(col * 50, (row - 1) * 50, col * 50, (row + 1) * 50);
        g.drawLine(col * 50, row * 50, col - 1 * 50, row * 50);
        g.drawLine((col - 1) * 50, row* 50, (col - 1) * 50, (row - 1) * 50);
        
         */

        // If no condition is true, keep the square white
        g.setColor(Color.WHITE);

        // Lower = high prio (that color will take presidence over other) reversed for higher up
        if (isHighlighted()) g.setColor(new Color(220, 220, 230));
        if (isMarked()) g.setColor(new Color(160, 200, 215));
        if (isHovered()) g.setColor(new Color(195, 215, 250));
        if (isPressed()) g.setColor(new Color(145, 180, 230));
        if (hasError()) g.setColor(new Color(255, 190, 180));
        if (hasError() && isHovered()) g.setColor(new Color(255, 140, 120));
        if (hasColor()) g.setColor(color);

        g.fillRect((col - 1) * 50, (row - 1) * 50, 50, 50);

        // Draw the outlinings with black
        g.setColor(Color.BLACK);

        g.drawRect((col - 1) * 50, (row - 1) * 50, 50, 50);

        if (row == 4 || row == 7) {
            g.fillRect((col - 1) * 50, (row - 1) * 50, 50, 3);
        }

        if (col == 4 || col == 7) {
            g.fillRect((col - 1) * 50, (row - 1) * 50, 3, 50);
        }
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isHighlighted() {
        return this.highlighted;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isHovered() {
        return this.hovered;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public boolean isMarked() {
        return this.marked;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public boolean hasError() {
        return this.error;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public boolean isHit(Point p) {
        return p.x > (col - 1) * 50 && p.x < col * 50 && p.y > (row - 1) * 50 && p.y < row * 50;
    }

    public boolean isEmpty() {
        return this.getNumber() == 0;
    }

    public String toString() {
        return "Column: " + getColumn().getId() + " | Row: " + getRow().getId() + " | ID: " + getUniqueId();
    }

}
