/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;


import javax.swing.*;
import java.awt.*;
/**
 *
 * @author TOSHIBA
 */
public class GameManager extends JFrame {
 
    public GameManager() {
        setTitle("Combat Fighter — P59_25287061");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
 
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
 
        pack();
        setLocationRelativeTo(null); // centre on screen
        setVisible(true);
 
        gamePanel.requestFocusInWindow();
        
        // Shut down Derby cleanly when window is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                DatabaseManager.getInstance().shutdown();
            }
        });
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameManager::new);
    }
}
