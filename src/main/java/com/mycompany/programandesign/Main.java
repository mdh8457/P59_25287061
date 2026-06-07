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
//import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        //DatabaseManager db = new DatabaseManager();
        //try {
            //db.connect();
        //} catch (SQLException e) {
            //System.out.println("DB error: " + e.getMessage());
        //}

        Character player = new PlayerCharacter("Player", 100);
        AICharacter ai = new AICharacter("AI", 100);

        // ATTACKS                name          dmg spd stun  type    dmgRed reflect reach cooldown
        Move quickJab  = new Move("Quick Jab",  10, 5,  0, "ATTACK", 0,     0,      80);
        Move jab       = new Move("Jab",        15, 3,  0, "ATTACK", 0,     0,      90);
        Move heavyJab  = new Move("Heavy Jab",  25, 1,  0, "ATTACK", 0,     0,      100);
        Move quickKick = new Move("Quick Kick",  5, 6,  1, "ATTACK", 0,     0,      110);
        Move kick      = new Move("Kick",       10, 3,  1, "ATTACK", 0,     0,      120);
        Move heavyKick = new Move("Heavy Kick", 20, 1,  1, "ATTACK", 0,     0,      130);

        // BLOCKS
        Move block1 = new Move("Block 1", 0, 5, 0, "BLOCK", 0.2, 0, 0);
        Move block2 = new Move("Block 2", 0, 4, 0, "BLOCK", 0.4, 0, 0);
        Move block3 = new Move("Block 3", 0, 3, 0, "BLOCK", 0.6, 0, 0);
        Move block4 = new Move("Block 4", 0, 2, 0, "BLOCK", 0.8, 0, 0);
        Move block5 = new Move("Block 5", 0, 1, 0, "BLOCK", 1.0, 0, 0);

        // PARRIES
        Move parry1    = new Move("Parry 1",    0, 5, 0, "PARRY", 0.1, 0.2, 0);
        Move parry2    = new Move("Parry 2",    0, 4, 0, "PARRY", 0.2, 0.4, 0);
        Move parry3    = new Move("Parry 3",    0, 3, 0, "PARRY", 0.3, 0.6, 0);
        Move parry4    = new Move("Parry 4",    0, 2, 0, "PARRY", 0.4, 0.8, 0);
        Move fullParry = new Move("Full Parry", 0, 1, 0, "PARRY", 0.5, 1.0, 0);

        

        // ADD ALL MOVES
        Move[] allMoves = {
            quickJab, jab, heavyJab,
            quickKick, kick, heavyKick,
            block1, block2, block3, block4, block5,
            parry1, parry2, parry3, parry4, fullParry
        };

        for (Move m : allMoves) {
            player.addMove(m);
            ai.addMove(m);
        }

        CombatSystem combat = new CombatSystem(player, ai);

        while (!combat.isGameOver()) {

            System.out.println("\nHP: Player=" + player.hp + " AI=" + ai.hp);

            System.out.println("a: Quick Jab | w: Jab | d: Heavy Jab");
            System.out.println("j: Quick Kick | i: Kick | k: Heavy Kick");
            System.out.println("1-5: Blocks | 6-0: Parries");

            String input = sc.next();
            Move playerMove = null;

            switch (input) {
                case "a": playerMove = quickJab; break;
                case "w": playerMove = jab; break;
                case "d": playerMove = heavyJab; break;
                case "j": playerMove = quickKick; break;
                case "i": playerMove = kick; break;
                case "k": playerMove = heavyKick; break;

                case "1": playerMove = block1; break;
                case "2": playerMove = block2; break;
                case "3": playerMove = block3; break;
                case "4": playerMove = block4; break;
                case "5": playerMove = block5; break;

                case "6": playerMove = parry1; break;
                case "7": playerMove = parry2; break;
                case "8": playerMove = parry3; break;
                case "9": playerMove = parry4; break;
                case "0": playerMove = fullParry; break;

                default:
                    System.out.println("Invalid input!");
                    continue;
            }

            if (!player.canUseMove(playerMove)) {
                System.out.println("Move disabled due to spam!");
                continue;
            }

            Move aiMove = ai.chooseMove(player);

            player.recordMove(playerMove);
            ai.recordMove(aiMove);

            combat.processTurn(playerMove, aiMove);
        }

        System.out.println("Game Over!");
    }
}