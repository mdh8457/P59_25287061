/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;

/**
 *
 * @author TOSHIBA
 */
public class MovementSystem {

    // =========================
    // MOVEMENT STATES
    // =========================

    public enum MovementState {
        IDLE,
        WALKING_FORWARD,
        WALKING_BACKWARD,
        JUMPING,
        CROUCHING,
        ROLLING,
        ATTACKING
    }

    // =========================
    // CURRENT STATE
    // =========================

    private MovementState state;

    // =========================
    // INPUT FLAGS
    // =========================

    private boolean moveLeftPressed;
    private boolean moveRightPressed;
    private boolean jumpPressed;
    private boolean crouchPressed;
    private boolean rollPressed;
    private boolean attackPressed;

    // =========================
    // POSITION
    // =========================

    private int x;
    private int y;

    // =========================
    // VELOCITY
    // =========================

    private int velocityX;
    private int velocityY;

    // =========================
    // CHARACTER STATUS
    // =========================

    private boolean onGround;
    private boolean facingRight;

    // =========================
    // ROLL SYSTEM
    // =========================

    private int rollTimer;
    private int rollDirection;

    // =========================
    // PHYSICS CONSTANTS
    // =========================

    private static final int GRAVITY = 1;

    private static final int WALK_SPEED = 5;

    private static final int CROUCH_SPEED = 2;

    private static final int JUMP_FORCE = -18;

    private static final int ROLL_SPEED = 12;

    private static final int ROLL_DURATION = 20;

    private static final int GROUND_LEVEL = 500;

    private static final int LEFT_BOUNDARY = 0;

    private static final int RIGHT_BOUNDARY = 1200;

    // =========================
    // CONSTRUCTOR
    // =========================
    
    
    private int animationFrame;
    private int animationTimer;

    private static final int ANIMATION_SPEED = 6;

    public MovementSystem(int startX, boolean facingRight) {

        this.x = startX;
        this.y = GROUND_LEVEL;

        this.facingRight = facingRight;

        this.state = MovementState.IDLE;

        this.onGround = true;

        this.velocityX = 0;
        this.velocityY = 0;

        this.rollTimer = 0;
        this.rollDirection = 1;
        
        // Animation setup
        this.animationFrame = 0;
        this.animationTimer = 0;
    }

    // =========================
    // MAIN UPDATE
    // =========================

    public void update() {

        handleInput();

        applyPhysics();

        updateState();
        updateAnimation();
    }

    // =========================
    // HANDLE INPUT
    // =========================

    private void handleInput() {

        if (state == MovementState.ROLLING) {
            return;
        }

        velocityX = 0;

        // Jump

        if (jumpPressed && onGround) {

            velocityY = JUMP_FORCE;

            onGround = false;
        }

        // Roll

        if (rollPressed && onGround) {

            state = MovementState.ROLLING;

            rollTimer = ROLL_DURATION;

            rollDirection = facingRight ? 1 : -1;

            return;
        }

        // Crouch

        if (crouchPressed && onGround) {

            velocityX = 0;

            return;
        }

        // Movement

        if (moveRightPressed) {

            velocityX = WALK_SPEED;

            facingRight = true;
        }

        if (moveLeftPressed) {

            velocityX = -WALK_SPEED;

            facingRight = false;
        }
    }

    // =========================
    // PHYSICS
    // =========================

    private void applyPhysics() {

        // Roll movement

        if (state == MovementState.ROLLING) {

            velocityX = rollDirection * ROLL_SPEED;

            rollTimer--;

            if (rollTimer <= 0) {

                velocityX = 0;

                state = MovementState.IDLE;
            }
        }

        // Gravity

        if (!onGround) {

            velocityY += GRAVITY;
        }

        // Apply movement

        x += velocityX;
        y += velocityY;

        // Ground collision

        if (y >= GROUND_LEVEL) {

            y = GROUND_LEVEL;

            velocityY = 0;

            onGround = true;
        }

        // Screen boundaries

        if (x < LEFT_BOUNDARY) {

            x = LEFT_BOUNDARY;
        }

        if (x > RIGHT_BOUNDARY) {

            x = RIGHT_BOUNDARY;
        }
    }

    // =========================
    // UPDATE STATE
    // =========================

    private void updateState() {

        if (state == MovementState.ROLLING) {
            return;
        }

        if (attackPressed) {

            state = MovementState.ATTACKING;
            return;
        }

        if (!onGround) {

            state = MovementState.JUMPING;
            return;
        }

        if (crouchPressed) {

            state = MovementState.CROUCHING;
            return;
        }

        if (moveRightPressed) {

            state = MovementState.WALKING_FORWARD;
            return;
        }

        if (moveLeftPressed) {

            state = MovementState.WALKING_BACKWARD;
            return;
        }

        state = MovementState.IDLE;
    }
    
    private void updateAnimation() {

        animationTimer++;

        if (animationTimer >= ANIMATION_SPEED) {

            animationTimer = 0;

            animationFrame++;
        }
    }

    // =========================
    // GETTERS
    // =========================

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getVelocityX() {
        return velocityX;
    }

    public int getVelocityY() {
        return velocityY;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public MovementState getState() {
        return state;
    }
    
    public int getAnimationFrame() {
        return animationFrame;
    }

    public int getAnimationTimer() {
        return animationTimer;
    }

    // =========================
    // SETTERS
    // =========================

    public void setMoveLeftPressed(boolean moveLeftPressed) {
        this.moveLeftPressed = moveLeftPressed;
    }

    public void setMoveRightPressed(boolean moveRightPressed) {
        this.moveRightPressed = moveRightPressed;
    }

    public void setJumpPressed(boolean jumpPressed) {
        this.jumpPressed = jumpPressed;
    }

    public void setCrouchPressed(boolean crouchPressed) {
        this.crouchPressed = crouchPressed;
    }

    public void setRollPressed(boolean rollPressed) {
        this.rollPressed = rollPressed;
    }

    public void setAttackPressed(boolean attackPressed) {
        this.attackPressed = attackPressed;
    }
}
