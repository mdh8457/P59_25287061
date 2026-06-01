/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;

/**
 *
 * @author TOSHIBA
 */
public class MoveResult {
    boolean hit;
    boolean blocked;
    boolean parried;
    int damage;
    int reflectedDamage;

    public MoveResult(boolean hit, boolean blocked, boolean parried, int damage, int reflectedDamage) {
        this.hit = hit;
        this.blocked = blocked;
        this.parried = parried;
        this.damage = damage;
        this.reflectedDamage = reflectedDamage;
    }
}