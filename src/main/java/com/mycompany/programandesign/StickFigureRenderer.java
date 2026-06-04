/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;

/**
 *
 * @author TOSHIBA
 */
import java.awt.*;
import java.awt.geom.*;
 
/**
 * StickFigureRenderer - Draws animated sprite-style stick figures using Java2D.
 * Each MovementState has distinct pose frames drawn with geometric primitives.
 */
public class StickFigureRenderer {

    
  

    private final Color bodyColor;
    private final Color accentColor;
    private final Color shadowColor;
    
    
    public StickFigureRenderer(Color bodyColor, Color accentColor) {
        this.bodyColor   = bodyColor;
        this.accentColor = accentColor;
        this.shadowColor = new Color(0, 0, 0, 60);
    }
    void draw(Graphics2D g2, int x, int y, MovementSystem.MovementState state, int animFrame, boolean facingRight) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
 
    
 
    
}