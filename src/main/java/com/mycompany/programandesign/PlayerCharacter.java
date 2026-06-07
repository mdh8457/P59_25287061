/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;

public class PlayerCharacter extends Character {

    public PlayerCharacter(String name, int hp) {
        super(name, hp);
    }

    @Override
    public Move chooseMove(Character opponent) {
        return null; // handled externally via keyboard input
    }
}
