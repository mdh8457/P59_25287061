/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;

/**
 *
 * @author TOSHIBA
 */
public class CombatSystem {
    Character player;
    AICharacter enemy;
    int comboCounter = 0;

    public CombatSystem(Character player, AICharacter enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    public void processTurn(Move pMove, Move eMove) {

        if (player.isStunned()) {
            System.out.println(player.name + " is stunned!");
            player.reduceStun();
            return;
        }

        if (enemy.isStunned()) {
            System.out.println(enemy.name + " is stunned!");
            enemy.reduceStun();
            return;
        }

        if (pMove.speed >= eMove.speed) {
            resolve(player, enemy, pMove, eMove);
        } else {
            resolve(enemy, player, eMove, pMove);
        }
    }

    private void resolve(Character attacker, Character defender, Move atk, Move def) {

        MoveResult result = calculateResult(atk, def);

        if (result.parried) {
            System.out.println(defender.name + " parried!");

            defender.hp -= result.damage;
            attacker.hp -= result.reflectedDamage;

            System.out.println("Reflected damage: " + result.reflectedDamage);
            return;
        }

        if (result.blocked) {
            System.out.println(defender.name + " blocked!");
        }

        if (result.hit) {
            int finalDamage = applyComboScaling(result.damage);
            defender.hp -= finalDamage;

            if (atk.stun > 0) {
                defender.stunTurns = atk.stun;
                System.out.println(defender.name + " is stunned for " + atk.stun + " turn(s)!");
            }

            comboCounter++;
            System.out.println(attacker.name + " deals " + finalDamage);
        } else {
            comboCounter = 0;
        }
    }

    private MoveResult calculateResult(Move atk, Move def) {

        if (atk.type.equals("ATTACK")) {

            int reducedDamage = (int)(atk.damage * (1 - def.damageReduction));

            if (def.type.equals("PARRY")) {
                int reflected = (int)(reducedDamage * def.reflectPercent);
                return new MoveResult(true, false, true, reducedDamage, reflected);
            }

            if (def.type.equals("BLOCK")) {
                return new MoveResult(true, true, false, reducedDamage, 0);
            }

            return new MoveResult(true, false, false, atk.damage, 0);
        }

        return new MoveResult(false, false, false, 0, 0);
    }

    private int applyComboScaling(int damage) {
        return (int)(damage * (1.0 - (comboCounter * 0.1)));
    }

    public boolean isGameOver() {
        return player.hp <= 0 || enemy.hp <= 0;
    }

    
}