/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.asset;

/**
 *
 * @author ville
 */
public class Id {

    private Column column;
    private Row row;

    /**
     * Constructor
     * @param column
     * @param row
     */
    public Id(Column column, Row row) {
        this.column = column;
        this.row = row;
    }

    /**
     * Gets the column id
     * @return the column id
     */
    public int getColumnId() {
        return this.column.getId();
    }

    /**
     * Gets the row id
     * @return the row id
     */
    public int getRowId() {
        return this.row.getId();
    }

    /**
     * Gets the column
     * @return the column
     */
    public Column getColumn() {
        return this.column;
    }

    /**
     * Gets the row
     * @return the row
     */
    public Row getRow() {
        return this.row;
    }

    @Override
    public String toString() {
        return "Id{" +
                "column=" + column +
                ", row=" + row +
                '}';
    }

}
