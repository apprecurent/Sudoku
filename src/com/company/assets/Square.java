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

    private boolean highlighted, pressed, error, locked;

    private JLabel numberShower;

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

        numberShower = new JLabel();
        grid.getPanel().add(numberShower);
        numberShower.setLocation(new Point((col - 1) * 50, (row - 1) * 50));
        numberShower.setSize(new Dimension(50, 50));
        numberShower.setFont(new Font("Arial", Font.PLAIN, 30));
        numberShower.setVerticalAlignment(SwingConstants.CENTER);
        numberShower.setHorizontalAlignment(SwingConstants.CENTER);

        if (number != 0) {
            numberShower.setText(String.valueOf(number));
        }
    }

    public void setNumber(int number, boolean locked) {
        
        this.number = number;
        this.locked = locked;
        this.error = false;
        
        numberShower.setText(String.valueOf(number));

        if (number == 0) {
            this.locked = false;
            numberShower.setText("");
        }

        // loop through all the fields (row, column, box) and do these checks instead (good for highlighting associated squares aswell)
        if (this.id.getRow().hasNumber(number, getColumn().getId()) || this.id.getColumn().hasNumber(number, getRow().getId()) || getBox().hasNumber(number, getUniqueId())) {
            error = true;
        }

        if (error) {
            numberShower.setForeground(Color.RED);
        } else if (locked) {
            numberShower.setForeground(Color.BLACK);
        } else {
            numberShower.setForeground(new Color(25, 110, 140));
        }

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
        if (isHighlighted()) {
            g.setColor(new Color(160, 200, 215));
        }
        if (isPressed()) {
            g.setColor(new Color(120, 190, 210));
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

    public String toString() {
        return "Column: " + getColumn().getId() + " | Row: " + getRow().getId();
    }

}
