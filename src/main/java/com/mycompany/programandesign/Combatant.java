/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;

public interface Combatant {
    String getName();
    int getHp();
    void setHp(int hp);
    boolean isDefeated();
    boolean isStunned();
    void reduceStun();
    void addMove(Move move);
    boolean canUseMove(Move move);
    void recordMove(Move move);
}
