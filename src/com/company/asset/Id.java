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

    public Id(Column column, Row row) {
        this.column = column;
        this.row = row;
    }

    public int getColumnId() {
        return this.column.getId();
    }

    public int getRowId() {
        return this.row.getId();
    }

    public Column getColumn() {
        return this.column;
    }

    public Row getRow() {
        return this.row;
    }

}
