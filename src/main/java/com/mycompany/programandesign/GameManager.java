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
         // Print DB contents on startup to verify everything is working
        printDatabaseContents();
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
    private void printDatabaseContents() {
        System.out.println("\n========== DATABASE CHECK ==========");

        DatabaseManager db = DatabaseManager.getInstance();

        int total = db.getTotalMatches();
        int wins  = db.getPlayerWins();
        System.out.println("Total matches: " + total);
        System.out.println("Player wins:   " + wins);
        System.out.println("AI wins:       " + (total - wins));

        System.out.println("\n--- Match History ---");
        var results = db.getAllMatchResults();
        if (results.isEmpty()) {
            System.out.println("No matches recorded yet.");
        } 
        else {
            for (var r : results) {
                System.out.println(r.toString());
            }
        }

        System.out.println("====================================\n");
    }
}
