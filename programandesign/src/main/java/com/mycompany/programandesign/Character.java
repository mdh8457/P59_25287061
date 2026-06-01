/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;

/**
 *
 * @author TOSHIBA
 */
import java.util.*;

public class Character {
    String name;
    int hp;
    List<Move> moves = new ArrayList<>();
    Queue<String> lastMoves = new LinkedList<>();
    int stunTurns = 0;

    public Character(String name, int hp) {
        this.name = name;
        this.hp = hp;
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    public boolean canUseMove(Move move) {
        int count = 0;
        for (String m : lastMoves) {
            if (m.equals(move.name)) count++;
        }
        return count < 2;
    }

    public void recordMove(Move move) {
        if (lastMoves.size() >= 2) lastMoves.poll();
        lastMoves.add(move.name);
    }

    public boolean isStunned() {
        return stunTurns > 0;
    }

    public void reduceStun() {
        if (stunTurns > 0) stunTurns--;
    }
}