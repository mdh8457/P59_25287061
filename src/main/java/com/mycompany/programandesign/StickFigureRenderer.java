/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.programandesign;

import java.awt.*;

public class StickFigureRenderer {

    private static final int HEAD_R   = 14;
    private static final int UPPER_ARM = 20;
    private static final int UPPER_LEG = 24;
    private static final int LOWER_LEG = 22;

    private final Color bodyColor;
    private final Color accentColor;
    private final Color shadowColor;

    public StickFigureRenderer(Color bodyColor, Color accentColor) {
        this.bodyColor   = bodyColor;
        this.accentColor = accentColor;
        this.shadowColor = new Color(0, 0, 0, 60);
    }

    public void draw(Graphics2D g2, int x, int y, MovementSystem.MovementState state,
                     int frame, boolean facingRight) {
        Graphics2D g = (Graphics2D) g2.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (!facingRight) {
            g.translate(x, y);
            g.scale(-1, 1);
            g.translate(-x, -y);
        }

        drawShadow(g, x, y);

        switch (state) {
            case IDLE                                -> drawIdle(g, x, y, frame);
            case WALKING_FORWARD, WALKING_BACKWARD  -> drawWalk(g, x, y, frame);
            case JUMPING                             -> drawJump(g, x, y, frame);
            case ROLLING                             -> drawRoll(g, x, y, frame);
            case CROUCHING                           -> drawCrouch(g, x, y, frame);
            case ATTACKING                           -> drawAttack(g, x, y, frame);
            default                                  -> drawIdle(g, x, y, frame);
        }
        g.dispose();
    }

    private void drawShadow(Graphics2D g, int x, int y) {
        g.setColor(shadowColor);
        g.fillOval(x - 20, y + 2, 40, 10);
    }

    private void drawIdle(Graphics2D g, int x, int y, int frame) {
        int bob = (frame == 0) ? 0 : -2;
        drawHead(g, x, y - 80 + bob);
        drawLine(g, x, y - 65 + bob, x, y - 35);
        drawArm(g, x, y - 60 + bob, -25, 15, -5, 30);
        drawArm(g, x, y - 60 + bob,  25, 15,  5, 30);
        drawLeg(g, x, y - 35, -12, 0, -15, 22);
        drawLeg(g, x, y - 35,  12, 0,  15, 22);
    }

    private void drawWalk(Graphics2D g, int x, int y, int frame) {
        int[][] legAngles = {
            {-20, 20, 15, -15},
            {-10, 10, 25, -25},
            { 20,-20,-15,  15},
            { 10,-10,-25,  25}
        };
        int[][] armAngles = {
            { 15,-15},
            { 25,-25},
            {-15, 15},
            {-25, 25}
        };
        int[] la = legAngles[frame % 4];
        int[] aa = armAngles[frame % 4];
        int bob = (frame % 2 == 0) ? 0 : -3;

        drawHead(g, x, y - 80 + bob);
        drawLine(g, x, y - 65 + bob, x, y - 35);

        int ax1 = (int)(Math.sin(Math.toRadians(aa[0])) * UPPER_ARM);
        int ay1 = (int)(Math.cos(Math.toRadians(aa[0])) * UPPER_ARM);
        drawArm(g, x, y - 60 + bob, -ax1, ay1, -ax1 - 5, ay1 + 15);

        int ax2 = (int)(Math.sin(Math.toRadians(aa[1])) * UPPER_ARM);
        int ay2 = (int)(Math.cos(Math.toRadians(aa[1])) * UPPER_ARM);
        drawArm(g, x, y - 60 + bob, ax2, ay2, ax2 + 5, ay2 + 15);

        drawLeg(g, x, y - 35, la[0], 0, la[2], 22);
        drawLeg(g, x, y - 35, la[1], 0, la[3], 22);
    }

    private void drawJump(Graphics2D g, int x, int y, int frame) {
        switch (frame % 3) {
            case 0 -> {
                drawHead(g, x, y - 82);
                drawLine(g, x, y - 67, x, y - 37);
                drawArm(g, x, y - 62, -30, -10, -20, 10);
                drawArm(g, x, y - 62,  30, -10,  20, 10);
                drawLeg(g, x, y - 37, -15, 15, -8, 30);
                drawLeg(g, x, y - 37,  15, 15,  8, 30);
            }
            case 1 -> {
                drawHead(g, x, y - 85);
                drawLine(g, x, y - 70, x, y - 40);
                drawArm(g, x, y - 65, -35, -20, -15, -10);
                drawArm(g, x, y - 65,  35, -20,  15, -10);
                drawLeg(g, x, y - 40, -20, 15, -5, 8);
                drawLeg(g, x, y - 40,  20, 15,  5, 8);
            }
            case 2 -> {
                drawHead(g, x, y - 80);
                drawLine(g, x, y - 65, x, y - 35);
                drawArm(g, x, y - 60, -35, 0, -30, 20);
                drawArm(g, x, y - 60,  35, 0,  30, 20);
                drawLeg(g, x, y - 35, -10, 10, -12, 28);
                drawLeg(g, x, y - 35,  10, 10,  12, 28);
            }
        }
    }
    private void drawRoll(Graphics2D g, int x, int y, int frame) {
        int angle = frame * 90;
        Graphics2D gr = (Graphics2D) g.create();
        gr.translate(x, y - 20);
        gr.rotate(Math.toRadians(angle));

        gr.setColor(bodyColor);
        gr.fillOval(-18, -18, 36, 36);
        gr.setColor(accentColor);
        gr.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        gr.drawOval(-18, -18, 36, 36);

        gr.setColor(bodyColor);
        gr.fillOval(-8, -24, 16, 16);
        gr.setColor(accentColor);
        gr.drawOval(-8, -24, 16, 16);

        gr.setColor(accentColor);
        gr.drawLine(-15, -5, -10, 12);
        gr.drawLine( 15, -5,  10, 12);
        gr.drawLine(-10, 12,   0, 16);
        gr.drawLine( 10, 12,   0, 16);
        gr.dispose();
    }

    private void drawCrouch(Graphics2D g, int x, int y, int frame) {
        int bob = (frame == 0) ? 0 : -1;
        int cy = y - 15 + bob;
        drawHead(g, x, cy - 55);
        drawLine(g, x, cy - 40, x, cy - 18);
        drawArm(g, x, cy - 35, -28, -8, -35, 10);
        drawArm(g, x, cy - 35,  28, -8,  35, 10);
        drawLeg(g, x, cy - 18, -20, 12, -22, 18);
        drawLeg(g, x, cy - 18,  20, 12,  22, 18);
    }

    private void drawAttack(Graphics2D g, int x, int y, int frame) {
        drawHead(g, x, y - 80);
        drawLine(g, x, y - 65, x, y - 35);
        drawLeg(g, x, y - 35, -12, 0, -15, 22);
        drawLeg(g, x, y - 35,  12, 0,  15, 22);

        switch (frame % 3) {
            case 0 -> {
                drawArm(g, x, y - 60, -30,  5, -20, 20);
                drawArm(g, x, y - 60, -15,-10, -35,-15);
            }
            case 1 -> {
                drawArm(g, x, y - 60, -30,  5, -20, 20);
                drawArm(g, x, y - 60,  40, -5,  65, -5);
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.drawString("★", x + 58, y - 18);
                g.setColor(accentColor);
            }
            case 2 -> {
                drawArm(g, x, y - 60, -30, 5, -20, 20);
                drawArm(g, x, y - 60,  30,-2,  45,  5);
            }
        }
    }

    private void drawHead(Graphics2D g, int cx, int cy) {
        g.setColor(bodyColor);
        g.fillOval(cx - HEAD_R, cy - HEAD_R, HEAD_R * 2, HEAD_R * 2);
        g.setColor(accentColor);
        g.drawOval(cx - HEAD_R, cy - HEAD_R, HEAD_R * 2, HEAD_R * 2);
        g.fillOval(cx + 3, cy - 5, 4, 4);
        g.fillOval(cx - 7, cy - 5, 4, 4);
    }

    private void drawLine(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.setColor(accentColor);
        g.drawLine(x1, y1, x2, y2);
    }

    private void drawArm(Graphics2D g, int sx, int sy, int ex1, int ey1, int ex2, int ey2) {
        g.setColor(accentColor);
        g.drawLine(sx, sy, sx + ex1, sy + ey1);
        g.drawLine(sx + ex1, sy + ey1, sx + ex2, sy + ey2);
    }

    private void drawLeg(Graphics2D g, int sx, int sy, int ex1, int ey1, int ex2, int ey2) {
        g.setColor(accentColor);
        g.drawLine(sx, sy, sx + ex1, sy + ey1);
        g.drawLine(sx + ex1, sy + ey1, sx + ex2, sy + ey2);
        g.drawLine(sx + ex2, sy + ey2, sx + ex2 + 8, sy + ey2);
    }
}