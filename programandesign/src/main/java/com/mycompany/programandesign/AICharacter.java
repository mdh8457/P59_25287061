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

public class AICharacter extends Character {
    Random rand = new Random();

    public AICharacter(String name, int hp) {
        super(name, hp);
    }

    public Move chooseMove(Character opponent) {
        List<Move> available = new ArrayList<>();

        for (Move m : moves) {
            if (canUseMove(m)) available.add(m);
        }

        if (available.isEmpty()) return moves.get(0);

        return available.get(rand.nextInt(available.size()));
    }
}