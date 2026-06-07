/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;

import java.util.*;

public abstract class Character implements Combatant {

    protected String name;
    protected int hp;
    protected List<Move> moves = new ArrayList<>();
    protected Queue<String> lastMoves = new LinkedList<>();
    protected int stunTurns = 0;

    public Character(String name, int hp) {
        this.name = name;
        this.hp   = hp;
    }

    public abstract Move chooseMove(Character opponent);

    @Override public String getName()  { return name; }
    @Override public int getHp()       { return hp; }
    @Override public void setHp(int hp){ this.hp = hp; }
    @Override public boolean isDefeated() { return hp <= 0; }
    @Override public boolean isStunned()  { return stunTurns > 0; }
    @Override public void reduceStun() { if (stunTurns > 0) stunTurns--; }
    @Override public void addMove(Move move) { moves.add(move); }

    @Override
    public boolean canUseMove(Move move) {
        int count = 0;
        for (String m : lastMoves) {
            if (m.equals(move.name)) count++;
        }
        return count < 2;
    }

    @Override
    public void recordMove(Move move) {
        if (lastMoves.size() >= 2) lastMoves.poll();
        lastMoves.add(move.name);
    }
}