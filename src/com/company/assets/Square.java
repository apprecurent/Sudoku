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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author ville
 */
public class Square {

    private int number;
    private Id id;

    private int col;
    private int row;

    private boolean highlighted, pressed, error, locked, marked, hovered;

    private JLabel lblNumber, lblNote;

    private List<JLabel> notes;

    private Grid grid;

    public Square(Grid grid, Id id) {
        this(grid, id, 0);
    }

    /**
     * REPLACE ID WITH ROW AND COLUMN
     *
     * @param grid
     * @param id
     * @param number
     */
    public Square(Grid grid, Id id, int number) {
        this.id = id;
        this.number = number;
        this.grid = grid;

        col = id.getColumnId();
        row = id.getRowId();

        lblNumber = new JLabel();
        grid.getPanel().add(lblNumber);
        lblNumber.setLocation(new Point((col - 1) * 50, (row - 1) * 50));
        lblNumber.setSize(new Dimension(50, 50));
        lblNumber.setFont(new Font("Arial", Font.PLAIN, 30));
        lblNumber.setVerticalAlignment(SwingConstants.CENTER);
        lblNumber.setHorizontalAlignment(SwingConstants.CENTER);

        if (number != 0) {
            lblNumber.setText(String.valueOf(number));
        }
        
        notes = new ArrayList<>();
        
        for (int i = 0; i < 9; i++) {
            int vertical = 0, horizontal = 0;
            notes.add(new JLabel(String.valueOf(i+1)));
            notes.get(i).setLocation(new Point((col - 1) * 50, (row - 1) * 50));
            notes.get(i).setSize(new Dimension(50, 50));
            notes.get(i).setFont(new Font("Arial", Font.PLAIN, 10));

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
            notes.get(i).setVerticalAlignment(vertical);
            notes.get(i).setHorizontalAlignment(horizontal);
        }
        
        
    }

    public void setNumber(int number, boolean locked) {
        
        this.number = number;
        this.locked = locked;
        this.error = false;
        
        lblNumber.setText(String.valueOf(number));

        if (number == 0) {
            this.locked = false;
            lblNumber.setText("");
        }

        // loop through all the fields (row, column, box) and do these checks instead (good for highlighting associated squares aswell)
        if (this.id.getRow().hasNumber(number, getColumn().getId()) || this.id.getColumn().hasNumber(number, getRow().getId()) || getBox().hasNumber(number, getUniqueId())) {
            error = true;
        }

        if (error) {
            lblNumber.setForeground(Color.RED);
        } else if (locked) {
            lblNumber.setForeground(Color.BLACK);
        } else {
            lblNumber.setForeground(new Color(25, 110, 140));
        }

    }
    
    public void setNote(int note) {
        notes.get(note).setVisible(!isVisible(note));
    }
    
    public boolean isVisible(int note) {
        return notes.get(note).isVisible();
    }
    
    public int getNumber() {
        return this.number;
    }

    public boolean hasNumber() {
        return number != 0;
    }

    public int getUniqueId() {
        return col + (row - 1) * 9;
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
        g.setColor(Color.WHITE);
        if (isMarked()) {
            g.setColor(new Color(160, 200, 215));
        }
        if (isHighlighted()) {
            g.setColor(new Color(220, 220, 230));
        }
        if (isHovered()) {
            g.setColor(new Color(195, 215, 250));
        }
        if (isPressed()) {
            g.setColor(new Color(145, 180, 230));
            // g.setColor(new Color(120, 190, 210));
        }
        g.fillRect((col - 1) * 50, (row - 1) * 50, 50, 50);
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

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean hasError() {
        return this.error;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
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
        return "Column: " + getColumn().getId() + " | Row: " + getRow().getId();
    }

}
