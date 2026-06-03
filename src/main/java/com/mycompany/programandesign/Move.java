/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;

/**
 *
 * @author TOSHIBA
 */
public class Move {
    String name;
    int damage;
    int speed;
    int stun;
    String type; // ATTACK, BLOCK, PARRY

    double damageReduction;
    double reflectPercent;
    
    // New stats
    int reach;       // how far the move can hit (pixels)
    

    public Move(String name, int damage, int speed, int stun, String type,
                double damageReduction, double reflectPercent, int reach) {
        this.name = name;
        this.damage = damage;
        this.speed = speed;
        this.stun = stun;
        this.type = type;
        this.damageReduction = damageReduction;
        this.reflectPercent = reflectPercent;
        this.reach = reach;
        
    }
    
}