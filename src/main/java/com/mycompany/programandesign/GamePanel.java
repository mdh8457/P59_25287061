/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
 
/**
 * GamePanel - Main Swing game panel.
 * Handles the game loop (60fps via Timer), rendering, and arrow key bindings.
 *
 * Controls (Player 1 - Arrow Keys):
 *   LEFT  = move left
 *   RIGHT = move right
 *   UP    = jump
 *   DOWN  = crouch
 *   SHIFT + direction = roll
 *
 * Combat keys (existing system):
 *   A/W/D = jab attacks
 *   J/I/K = kick attacks
 *   1-5   = blocks
 *   6-0   = parries
 */
public class GamePanel extends JPanel implements ActionListener {
 
    // --- Game dimensions ---
    public static final int WIDTH  = 800;
    public static final int HEIGHT = 500;
    
    // --- Database tracking ---
    private int turnCount    = 0;
    private int matchId      = -1;
    private boolean resultSaved = false;
 
    // --- Game loop ---
    private final Timer gameTimer;
    private static final int FPS = 60;
 
    // --- Game objects ---
    private final PlayerCharacter playerChar;
    private final AICharacter aiChar;
    private final CombatSystem combatSystem;
    private final MovementSystem playerMovement;
    private final MovementSystem aiMovement;
 
    // --- Renderers ---
    private final StickFigureRenderer playerRenderer;
    private final StickFigureRenderer aiRenderer;
 
    private boolean shiftHeld = false;
 
    // --- Combat state ---
    private Move pendingPlayerMove = null;
    private String lastCombatMessage = "";
    private int messageTimer = 0;
    private static final int MESSAGE_DURATION = 90; // frames
 
    // --- Move list (same as Main.java) ---
    private Move quickJab, jab, heavyJab;
    private Move quickKick, kick, heavyKick;
    private Move block1, block2, block3, block4, block5;
    private Move parry1, parry2, parry3, parry4, fullParry;
 
    // --- Background ---
    private final Color skyTop    = new Color(15, 10, 35);
    private final Color skyBottom = new Color(40, 20, 80);
    private final Color groundColor = new Color(30, 60, 30);
    private final Color groundLine  = new Color(50, 120, 50);
 
    // --- Fonts ---
    private Font hpFont;
    private Font controlFont;
    private Font messageFont;
 
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
 
        // Setup characters
        playerChar = new PlayerCharacter("Player", 100);
        aiChar     = new AICharacter("AI", 100);
        setupMoves();
 
        combatSystem  = new CombatSystem(playerChar, aiChar);
        playerMovement = new MovementSystem(200, true);
        aiMovement     = new MovementSystem(600, false);
 
        // Player renderer: blue theme
        playerRenderer = new StickFigureRenderer(
            new Color(180, 220, 255),
            new Color(80, 160, 255)
        );
        // AI renderer: red theme
        aiRenderer = new StickFigureRenderer(
            new Color(255, 200, 190),
            new Color(255, 80, 80)
        );
 
        setupFonts();
        setupKeyBindings();
        
 
        gameTimer = new Timer(1000 / FPS, this);
        gameTimer.start();
        matchId = DatabaseManager.getInstance().saveMatchResult(
            "In Progress", "In Progress", 0, 100, 100);
    }
 
    // ===================== Game Loop =====================
 
    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }
 
    private void update() {
        if (combatSystem.isGameOver()) return;
 
        playerMovement.update();
        int px = playerMovement.getX();
        int ax = aiMovement.getX();
        if (Math.abs(px - ax) > 120) {
            aiMovement.setMovingRight(px > ax);
            aiMovement.setMovingLeft(px < ax);
        } else {
            aiMovement.setMovingRight(false);
            aiMovement.setMovingLeft(false);
        }
        
        
        aiMovement.update();
        
        if(pendingPlayerMove != null){
            
            if(playerChar.canUseMove(pendingPlayerMove)){
                // Check reach — distance between fighters
                int dist = Math.abs(playerMovement.getX() - aiMovement.getX());
                boolean inRange = pendingPlayerMove.reach == 0 || dist <= pendingPlayerMove.reach;
                if(!inRange){
                    lastCombatMessage = "Too far away! Move closer to use " + pendingPlayerMove.name;
                    messageTimer = MESSAGE_DURATION;
                }
                else{
                    Move aiMove = aiChar.chooseMove(playerChar);
                    playerChar.recordMove(pendingPlayerMove);
                    aiChar.recordMove(aiMove);

                    int playerHpBefore = playerChar.hp;
                    int aiHpBefore     = aiChar.hp;

                    combatSystem.processTurn(pendingPlayerMove, aiMove);

                    int playerDmg = playerHpBefore - playerChar.hp;
                    int aiDmg     = aiHpBefore     - aiChar.hp;

                    

                    lastCombatMessage = buildCombatMessage(pendingPlayerMove, aiMove, playerDmg, aiDmg);
                    messageTimer = MESSAGE_DURATION;
                    playerMovement.setState(MovementSystem.MovementState.ATTACKING);
                }
            }
            pendingPlayerMove = null;
        }

 
        if (messageTimer > 0) messageTimer--;
        
        

    }
 
    private String buildCombatMessage(Move p, Move a, int pDmg, int aDmg) {
        StringBuilder sb = new StringBuilder();
        sb.append("You: ").append(p.name);
        if (aDmg > 0) sb.append(" → AI -").append(aDmg).append("HP");
        sb.append("  |  AI: ").append(a.name);
        if (pDmg > 0) sb.append(" → You -").append(pDmg).append("HP");
        return sb.toString();
    }
 
    // ===================== Rendering =====================
 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
        drawBackground(g2);
        drawGround(g2);
        drawHPBars(g2);
        drawStickFigures(g2);
        drawCombatMessage(g2);
        drawControls(g2);
 
        if (combatSystem.isGameOver()) drawGameOver(g2);
    }
 
    private void drawBackground(Graphics2D g2) {
        GradientPaint sky = new GradientPaint(0, 0, skyTop, 0, HEIGHT, skyBottom);
        g2.setPaint(sky);
        g2.fillRect(0, 0, WIDTH, HEIGHT);
 
        // Stars
        g2.setColor(new Color(255, 255, 255, 120));
        int[][] stars = {{50,30},{150,60},{250,20},{400,45},{500,25},{650,55},{730,35},
                         {100,80},{300,70},{550,90},{700,75},{180,40},{420,15}};
        for (int[] s : stars) {
            g2.fillOval(s[0], s[1], 2, 2);
        }
 
        // Moon
        g2.setColor(new Color(255, 240, 200, 200));
        g2.fillOval(680, 30, 45, 45);
        g2.setColor(skyTop);
        g2.fillOval(692, 25, 45, 45); // crescent cutout
    }
 
    private void drawGround(Graphics2D g2) {
        int groundY = playerMovement.getGroundY() + 5;
 
        // Ground fill
        g2.setColor(groundColor);
        g2.fillRect(0, groundY, WIDTH, HEIGHT - groundY);
 
        // Ground line
        g2.setColor(groundLine);
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(0, groundY, WIDTH, groundY);
 
        // Grid lines on ground for depth
        g2.setColor(new Color(50, 100, 50, 80));
        g2.setStroke(new BasicStroke(1f));
        for (int i = 0; i < WIDTH; i += 60) {
            g2.drawLine(i, groundY, i, HEIGHT);
        }
    }
 
    private void drawHPBars(Graphics2D g2) {
        int barW = 280, barH = 22;
        int py = 20;
 
        // Player HP bar (left)
        drawHPBar(g2, 30, py, barW, barH, playerChar.hp, 100,
                  new Color(80, 160, 255), "PLAYER", true);
 
        // AI HP bar (right)
        drawHPBar(g2, WIDTH - 30 - barW, py, barW, barH, aiChar.hp, 100,
                  new Color(255, 80, 80), "AI", false);
 
        // Round indicator
        g2.setColor(new Color(255, 220, 100));
        g2.setFont(hpFont);
        String vs = "VS";
        int vsW = g2.getFontMetrics().stringWidth(vs);
        g2.drawString(vs, (WIDTH - vsW) / 2, py + 18);
    }
 
    private void drawHPBar(Graphics2D g2, int x, int y, int w, int h,
                           int hp, int maxHp, Color barColor, String label, boolean leftAlign) {
        float ratio = Math.max(0, (float) hp / maxHp);
 
        // Background
        g2.setColor(new Color(20, 20, 20, 200));
        g2.fillRoundRect(x, y, w, h, 8, 8);
 
        // HP fill
        Color depleted = new Color(100, 30, 30);
        g2.setColor(depleted);
        g2.fillRoundRect(x + 1, y + 1, w - 2, h - 2, 7, 7);
 
        int fillW = (int)((w - 2) * ratio);
        g2.setColor(barColor);
        g2.fillRoundRect(x + 1, y + 1, fillW, h - 2, 7, 7);
 
        // Border
        g2.setColor(new Color(200, 200, 200, 180));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, w, h, 8, 8);
 
        // Label
        g2.setFont(controlFont);
        g2.setColor(Color.WHITE);
        String hpText = label + "  " + hp + " / " + maxHp;
        if (leftAlign) {
            g2.drawString(hpText, x + 6, y + h - 5);
        } else {
            int tw = g2.getFontMetrics().stringWidth(hpText);
            g2.drawString(hpText, x + w - tw - 6, y + h - 5);
        }
    }
 
    private void drawStickFigures(Graphics2D g2) {
        playerRenderer.draw(g2, playerMovement.getX(), playerMovement.getY(),
            playerMovement.getState(), playerMovement.getAnimFrame(), playerMovement.isFacingRight());
        aiRenderer.draw(g2, aiMovement.getX(), aiMovement.getY(),
            aiMovement.getState(), aiMovement.getAnimFrame(), aiMovement.isFacingRight());
    }
 
    private void drawCombatMessage(Graphics2D g2) {
        if (messageTimer <= 0 || lastCombatMessage.isEmpty()) return;
 
        float alpha = Math.min(1f, messageTimer / 30f);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setFont(messageFont);
        g2.setColor(new Color(255, 230, 100));
 
        int tw = g2.getFontMetrics().stringWidth(lastCombatMessage);
        int tx = (WIDTH - tw) / 2;
        int ty = 80;
 
        // Shadow
        g2.setColor(new Color(0, 0, 0, (int)(180 * alpha)));
        g2.drawString(lastCombatMessage, tx + 2, ty + 2);
 
        g2.setColor(new Color(255, 230, 100, (int)(255 * alpha)));
        g2.drawString(lastCombatMessage, tx, ty);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
 
    private void drawControls(Graphics2D g2) {
        g2.setFont(controlFont);
        
        g2.setColor(new Color(255, 255, 255, 160));
 
        String[] lines = {
            "MOVE: ← →   JUMP: ↑   CROUCH: ↓   ROLL: SHIFT+←/→",
            "ATTACK: A/W/D (jabs)  J/I/K (kicks)  1-5 (block)  6-0 (parry)"
        };
 
        int y = HEIGHT - 36;
        for (String line : lines) {
            int tw = g2.getFontMetrics().stringWidth(line);
            g2.drawString(line, (WIDTH - tw) / 2, y);
            y += 16;
        }
    }
 
    private void drawGameOver(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, WIDTH, HEIGHT);
 
        g2.setFont(hpFont.deriveFont(Font.BOLD, 48f));
        String msg = playerChar.hp <= 0 ? "YOU LOSE" : "YOU WIN!";
        Color c = playerChar.hp <= 0 ? new Color(255, 80, 80) : new Color(100, 255, 100);
        g2.setColor(c);
        int tw = g2.getFontMetrics().stringWidth(msg);
        g2.drawString(msg, (WIDTH - tw) / 2, HEIGHT / 2);
 
        g2.setFont(controlFont);
        g2.setColor(Color.WHITE);
        String sub = "Close and relaunch to play again";
        int sw = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, (WIDTH - sw) / 2, HEIGHT / 2 + 40);
    }
 
    // ===================== Key Bindings =====================
 
    private void setupKeyBindings() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        bindKey(im, am, KeyEvent.VK_LEFT,  false, "left_press",    () -> {
            if (shiftHeld) playerMovement.setRollPressed(true);
            else playerMovement.setMovingLeft(true);
        });
        bindKey(im, am, KeyEvent.VK_RIGHT, false, "right_press",   () -> {
            if (shiftHeld) playerMovement.setRollPressed(true);
            else playerMovement.setMovingRight(true);
        });
        bindKey(im, am, KeyEvent.VK_UP,    false, "up_press",      () -> playerMovement.setJumpPressed(true));
        bindKey(im, am, KeyEvent.VK_DOWN,  false, "down_press",    () -> playerMovement.setCrouchPressed(true));
        bindKey(im, am, KeyEvent.VK_LEFT,  true,  "left_release",  () -> playerMovement.setMovingLeft(false));
        bindKey(im, am, KeyEvent.VK_RIGHT, true,  "right_release", () -> playerMovement.setMovingRight(false));
        bindKey(im, am, KeyEvent.VK_DOWN,  true,  "down_release",  () -> playerMovement.setCrouchPressed(false));
        bindKey(im, am, KeyEvent.VK_SHIFT, false, "shift_press",   () -> shiftHeld = true);
        bindKey(im, am, KeyEvent.VK_SHIFT, true,  "shift_release", () -> shiftHeld = false);

        bindCombat(im, am, KeyEvent.VK_A, quickJab);
        bindCombat(im, am, KeyEvent.VK_W, jab);
        bindCombat(im, am, KeyEvent.VK_D, heavyJab);
        bindCombat(im, am, KeyEvent.VK_J, quickKick);
        bindCombat(im, am, KeyEvent.VK_I, kick);
        bindCombat(im, am, KeyEvent.VK_K, heavyKick);
        bindCombat(im, am, KeyEvent.VK_1, block1);
        bindCombat(im, am, KeyEvent.VK_2, block2);
        bindCombat(im, am, KeyEvent.VK_3, block3);
        bindCombat(im, am, KeyEvent.VK_4, block4);
        bindCombat(im, am, KeyEvent.VK_5, block5);
        bindCombat(im, am, KeyEvent.VK_6, parry1);
        bindCombat(im, am, KeyEvent.VK_7, parry2);
        bindCombat(im, am, KeyEvent.VK_8, parry3);
        bindCombat(im, am, KeyEvent.VK_9, parry4);
        bindCombat(im, am, KeyEvent.VK_0, fullParry);
    }
    
    
    private void bindKey(InputMap im, ActionMap am, int keyCode, boolean onRelease,
                         String name, Runnable action) {
        im.put(KeyStroke.getKeyStroke(keyCode, 0, onRelease), name);
        am.put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { action.run(); }
        });
    }

    private void bindCombat(InputMap im, ActionMap am, int keyCode, Move move) {
        im.put(KeyStroke.getKeyStroke(keyCode, 0, false), "combat_" + keyCode);
        am.put("combat_" + keyCode, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                pendingPlayerMove = move;
            }
        });
    }
    
 
    // ===================== Setup =====================
 
    private void setupMoves() {
        quickJab  = new Move("Quick Jab",  10, 5, 0, "ATTACK", 0,   0,   80);
        jab       = new Move("Jab",        15, 3, 0, "ATTACK", 0,   0,   90);
        heavyJab  = new Move("Heavy Jab",  25, 1, 0, "ATTACK", 0,   0,   100);
        quickKick = new Move("Quick Kick",  5, 6, 1, "ATTACK", 0,   0,   110);
        kick      = new Move("Kick",       10, 3, 1, "ATTACK", 0,   0,   120);
        heavyKick = new Move("Heavy Kick", 20, 1, 1, "ATTACK", 0,   0,   130);
        block1    = new Move("Block 1",     0, 5, 0, "BLOCK",  0.2, 0,   0);
        block2    = new Move("Block 2",     0, 4, 0, "BLOCK",  0.4, 0,   0);
        block3    = new Move("Block 3",     0, 3, 0, "BLOCK",  0.6, 0,   0);
        block4    = new Move("Block 4",     0, 2, 0, "BLOCK",  0.8, 0,   0);
        block5    = new Move("Block 5",     0, 1, 0, "BLOCK",  1.0, 0,   0);
        parry1    = new Move("Parry 1",     0, 5, 0, "PARRY",  0.1, 0.2, 0);
        parry2    = new Move("Parry 2",     0, 4, 0, "PARRY",  0.2, 0.4, 0);
        parry3    = new Move("Parry 3",     0, 3, 0, "PARRY",  0.3, 0.6, 0);
        parry4    = new Move("Parry 4",     0, 2, 0, "PARRY",  0.4, 0.8, 0);
        fullParry = new Move("Full Parry",  0, 1, 0, "PARRY",  0.5, 1.0, 0);

        // Player gets the shared move references
        Move[] all = {quickJab, jab, heavyJab, quickKick, kick, heavyKick,
                                block1, block2, block3, block4, block5,
                                parry1, parry2, parry3, parry4, fullParry};
        for (Move m : all) {
            playerChar.addMove(m);
            aiChar.addMove(m);
        }

        
    }
 
    private void setupFonts() {
        hpFont      = new Font("Monospaced", Font.BOLD, 16);
        controlFont = new Font("Monospaced", Font.PLAIN, 12);
        messageFont = new Font("Monospaced", Font.BOLD, 14);
    }
}