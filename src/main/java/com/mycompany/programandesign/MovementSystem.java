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
 
    // --- Movement State Enum ---
    public enum MovementState {
        IDLE, WALKING_FORWARD, WALKING_BACKWARD, JUMPING, ROLLING, CROUCHING, ATTACKING
    }
 
    // --- Physics Constants ---
    private static final int GROUND_Y = 380;
    private static final int GRAVITY = 2;
    private static final int JUMP_FORCE = -22;
    private static final int MOVE_SPEED = 5;
    private static final int ROLL_SPEED = 8;
    private static final int ROLL_DURATION = 20; // frames
    private static final int ARENA_LEFT = 50;
    private static final int ARENA_RIGHT = 750;
 
    // --- Position & Velocity ---
    private int x;
    private int y;
    private int velocityX;
    private int velocityY;
 
    // --- State ---
    private MovementState state;
    private boolean facingRight;
    private boolean onGround;
    private int rollTimer;
    private int rollDirection;
 
    // --- Animation ---
    private int animFrame;
    private int animTimer;
    private static final int ANIM_SPEED = 6; // frames per animation tick
 
    // --- Input flags ---
    private boolean movingLeft;
    private boolean movingRight;
    private boolean jumpPressed;
    private boolean rollPressed;
    private boolean crouchPressed;
 
    public MovementSystem(int startX, boolean facingRight) {
        this.x = startX;
        this.y = GROUND_Y;
        this.facingRight = facingRight;
        this.state = MovementState.IDLE;
        this.onGround = true;
        this.animFrame = 0;
        this.animTimer = 0;
    }
 
    // --- Called every game tick ---
    public void update() {
        handleInput();
        applyPhysics();
        updateAnimation();
        clampPosition();
    }
 
    private void handleInput() {
        // Don't interrupt a roll
        if (state == MovementState.ROLLING) {
            rollTimer--;
            x += rollDirection * ROLL_SPEED;
            if (rollTimer <= 0) {
                state = MovementState.IDLE;
                velocityX = 0;
            }
            return;
        }
 
        // Jump
        if (jumpPressed && onGround) {
            velocityY = JUMP_FORCE;
            onGround = false;
            state = MovementState.JUMPING;
            jumpPressed = false;
        }
 
        // Roll
        if (rollPressed && onGround) {
            state = MovementState.ROLLING;
            rollTimer = ROLL_DURATION;
            rollDirection = facingRight ? 1 : -1;
            rollPressed = false;
            animFrame = 0;
            return;
        }
 
        // Crouch
        if (crouchPressed && onGround) {
            state = MovementState.CROUCHING;
            velocityX = 0;
            return;
        }
 
        // Horizontal movement
        if (movingRight && !movingLeft) {
            velocityX = MOVE_SPEED;
            facingRight = true;
            if (onGround) state = MovementState.WALKING_FORWARD;
        } else if (movingLeft && !movingRight) {
            velocityX = -MOVE_SPEED;
            facingRight = false;
            if (onGround) state = MovementState.WALKING_BACKWARD;
        } else {
            velocityX = 0;
            if (onGround && state != MovementState.CROUCHING) {
                state = MovementState.IDLE;
            }
        }
 
        crouchPressed = false;
    }
 
    private void applyPhysics() {
        // Gravity
        if (!onGround) {
            velocityY += GRAVITY;
            y += velocityY;
            if (y >= GROUND_Y) {
                y = GROUND_Y;
                velocityY = 0;
                onGround = true;
                if (state == MovementState.JUMPING) state = MovementState.IDLE;
            }
        }
 
        // Apply horizontal velocity (only if not rolling, rolling handles its own x)
        if (state != MovementState.ROLLING) {
            x += velocityX;
        }
    }
 
    private void updateAnimation() {
        animTimer++;
        if (animTimer >= ANIM_SPEED) {
            animTimer = 0;
            int frameCount = getFrameCount();
            animFrame = (animFrame + 1) % frameCount;
        }
    }
 
    private int getFrameCount() {
        return switch (state) {
            case WALKING_FORWARD, WALKING_BACKWARD -> 4;
            case JUMPING -> 3;
            case ROLLING -> 4;
            case CROUCHING -> 2;
            case ATTACKING -> 3;
            default -> 2; // IDLE breathe cycle
        };
    }
 
    private void clampPosition() {
        if (x < ARENA_LEFT) x = ARENA_LEFT;
        if (x > ARENA_RIGHT) x = ARENA_RIGHT;
    }
 
    // --- Input setters (called by key bindings) ---
    public void setMovingLeft(boolean v) { this.movingLeft = v; }
    public void setMovingRight(boolean v) { this.movingRight = v; }
    public void setJumpPressed(boolean v) { this.jumpPressed = v; }
    public void setRollPressed(boolean v) { this.rollPressed = v; }
    public void setCrouchPressed(boolean v) { this.crouchPressed = v; }
 
    // --- Getters ---
    public int getX() { return x; }
    public int getY() { return y; }
    public int getAnimFrame() { return animFrame; }
    public MovementState getState() { return state; }
    public boolean isFacingRight() { return facingRight; }
    public boolean isOnGround() { return onGround; }
    public int getGroundY() { return GROUND_Y; }
 
    public void setState(MovementState state) { this.state = state; }
}
 