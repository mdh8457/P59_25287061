/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager - Singleton class responsible for managing the Apache Derby
 * embedded database connection. Implements the Singleton design pattern to
 * ensure only one database connection exists throughout the application.
 *
 * Works automatically with no manual configuration — Derby creates the
 * database folder (CombatDB) inside the project directory on first run.
 */
public class DatabaseManager {

    // --- Singleton instance ---
    private static DatabaseManager instance;

    // --- Derby embedded URL ---
    private static final String DB_URL       = "jdbc:derby:CombatDB;create=true";
    private static final String SHUTDOWN_URL = "jdbc:derby:CombatDB;shutdown=true";
    

    private Connection connection;

    // --- Private constructor (Singleton pattern) ---
    DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("[DB] Connected to CombatDB");
            createTables();
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
        }
    }

    /**
     * Returns the single instance of DatabaseManager.
     * Creates it on first call (lazy initialisation).
     * @return 
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    // ===================== Table Creation =====================

    private void createTables() throws SQLException {
        createMatchResultsTable();
        createMoveHistoryTable();
    }

    private void createMatchResultsTable() {
        String sql =
            "CREATE TABLE MATCH_RESULTS (" +
            "  ID          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
            "  WINNER      VARCHAR(30)  NOT NULL," +
            "  LOSER       VARCHAR(30)  NOT NULL," +
            "  TURNS       INT          NOT NULL," +
            "  PLAYER_HP   INT          NOT NULL," +
            "  AI_HP       INT          NOT NULL," +
            "  PLAYED_AT   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP" +
            ")";
        executeCreate(sql, "MATCH_RESULTS");
    }

    private void createMoveHistoryTable() {
        String sql =
            "CREATE TABLE MOVE_HISTORY (" +
            "  ID           INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
            "  MATCH_ID     INT          NOT NULL," +
            "  TURN_NUMBER  INT          NOT NULL," +
            "  PLAYER_MOVE  VARCHAR(50)  NOT NULL," +
            "  AI_MOVE      VARCHAR(50)  NOT NULL," +
            "  DAMAGE_DEALT INT          NOT NULL," +
            "  DAMAGE_TAKEN INT          NOT NULL" +
            ")";
        executeCreate(sql, "MOVE_HISTORY");
    }

    private void executeCreate(String sql, String tableName) {
        try (Statement st = connection.createStatement()) {
            st.execute(sql);
            System.out.println("[DB] Table created: " + tableName);
        } catch (SQLException e) {
            if (e.getSQLState().equals("X0Y32")) {
                System.out.println("[DB] Table already exists: " + tableName);
            } else {
                System.err.println("[DB] Error creating " + tableName + ": " + e.getMessage());
            }
        }
    }
    // ===================== Shutdown =====================

    /**
     * Cleanly shuts down the Derby database.
     * Must be called when the application closes.
     */
    
    public void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            DriverManager.getConnection(SHUTDOWN_URL);
        } catch (SQLException e) {
            // Derby always throws SQLState 08006 on successful shutdown — this is normal
            if ("08006".equals(e.getSQLState())) {
                System.out.println("[DB] Derby shut down cleanly.");
            } else {
                System.err.println("[DB] Shutdown error: " + e.getMessage());
            }
        }
    }
    // ===================== Inner DTO class =====================

    /**
     * MatchResult - Data Transfer Object for match results retrieved from DB.
     */
    public static class MatchResult {
        public final int id;
        public final String winner;
        public final String loser;
        public final int turns;
        public final int playerHp;
        public final int aiHp;
        public final String playedAt;

        public MatchResult(int id, String winner, String loser,
                           int turns, int playerHp, int aiHp, String playedAt) {
            this.id       = id;
            this.winner   = winner;
            this.loser    = loser;
            this.turns    = turns;
            this.playerHp = playerHp;
            this.aiHp     = aiHp;
            this.playedAt = playedAt;
        }

        @Override
        public String toString() {
            return String.format(
                "Match #%d | Winner: %s | Turns: %d | Player HP: %d | AI HP: %d | %s",
                id, winner, turns, playerHp, aiHp, playedAt);
        }
    }
}
