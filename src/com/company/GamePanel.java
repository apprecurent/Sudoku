/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company;

import com.company.assets.*;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ville
 */
public class GamePanel extends javax.swing.JPanel {

    /**
     * Creates new form GamePanel
     */
    // private Frame frame;
    
    /*
    public void setFrame(Frame frame) {
        this.frame = frame;
    }
    */
    
    private Grid grid;

    public GamePanel() {
        initComponents();
        grid = new Grid(this);
        
    }
    
    public Grid getGrid() {
        return this.grid;
    }

    public void paintComponent(Graphics g) {

        grid.paint(g);
    }

    public void clearHighlightedSquares() {
        for (Square square : grid.getSquares()) {
            square.setHighlighted(false);
        }

        repaint();
    }

    public void clearPressedSquares() {
        for (Square square : grid.getSquares()) {
            square.setPressed(false);
        }

        repaint();
    }

    public void clearMarkedSquares() {
        for (Square square : grid.getSquares()) {
            square.setMarked(false);
        }
        repaint();
    }

    public void clearHoveredSquares() {
        for (Square square : grid.getSquares()) {
            square.setHovered(false);
        }
        repaint();
    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addComponent(jLabel1)
                .addContainerGap(284, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addComponent(jLabel1)
                .addContainerGap(309, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        // TODO add your handling code here:
        for (Square square : grid.getSquares()) {
            if (square.isHit(new Point(evt.getX(), evt.getY()))) {
                square.setHovered(true);
            } else {
                square.setHovered(false);
            }
        }
        repaint();
    }//GEN-LAST:event_formMouseMoved

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        // TODO add your handling code here:
        clearPressedSquares();
        clearMarkedSquares();
        clearHighlightedSquares();

        for (Square square : grid.getSquares()) {
            if (square.isHit(new Point(evt.getX(), evt.getY()))) {
                square.setPressed(true);
                for (Field field : square.getFields()) {
                    for (Square s : field.getSquares()) {
                        s.setHighlighted(true);
                    }
                }
                for (Square s : grid.getSquares()) {
                    if (s.getNumber() == square.getNumber() && s.getNumber() != 0) {
                        s.setMarked(true);
                    }
                }
                break;
            }
        }
        repaint();
    }//GEN-LAST:event_formMousePressed

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        // TODO add your handling code here:
        clearHoveredSquares();
    }//GEN-LAST:event_formMouseExited


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
