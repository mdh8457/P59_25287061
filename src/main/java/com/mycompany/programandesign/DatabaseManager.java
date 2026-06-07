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
    
    
    // ===================== DAO Methods — Save =====================

    /**
     * Saves the result of a completed match.
     * Uses PreparedStatement to prevent SQL injection.
     * @param winner
     * @param loser
     * @param turns
     * @param playerHp
     * @param aiHp
     * @return the generated match ID, or -1 if failed
     */
    public int saveMatchResult(String winner, String loser, int turns,
                                int playerHp, int aiHp) {
        String sql = "INSERT INTO MATCH_RESULTS (WINNER, LOSER, TURNS, PLAYER_HP, AI_HP) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, winner);
            ps.setString(2, loser);
            ps.setInt(3, turns);
            ps.setInt(4, playerHp);
            ps.setInt(5, aiHp);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                System.out.println("[DB] Match saved with ID: " + id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error saving match: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Saves a single turn's move history for a match.
     * @param matchId
     * @param turnNumber
     * @param playerMove
     * @param damageDealt
     * @param aiMove
     * @param damageTaken
     */
    public void saveMoveHistory(int matchId, int turnNumber,
                                 String playerMove, String aiMove,
                                 int damageDealt, int damageTaken) {
        String sql = "INSERT INTO MOVE_HISTORY " +
                     "(MATCH_ID, TURN_NUMBER, PLAYER_MOVE, AI_MOVE, DAMAGE_DEALT, DAMAGE_TAKEN) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ps.setInt(2, turnNumber);
            ps.setString(3, playerMove);
            ps.setString(4, aiMove);
            ps.setInt(5, damageDealt);
            ps.setInt(6, damageTaken);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB] Error saving move history: " + e.getMessage());
        }
    }
    
    
    /**
    * Updates an existing match record when the game ends.
    * Replaces the "In Progress" placeholder with real results.
     * @param id
     * @param winner
     * @param loser
     * @param turns
     * @param playerHp
     * @param aiHp
    */
    public void updateMatchResult(int id, String winner, String loser,
                                int turns, int playerHp, int aiHp) {
        String sql = "UPDATE MATCH_RESULTS SET WINNER=?, LOSER=?, TURNS=?, " +
                    "PLAYER_HP=?, AI_HP=? WHERE ID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, winner);
            ps.setString(2, loser);
            ps.setInt(3, turns);
            ps.setInt(4, playerHp);
            ps.setInt(5, aiHp);
            ps.setInt(6, id);
            ps.executeUpdate();
            System.out.println("[DB] Match #" + id + " updated — Winner: " + winner);
        } catch (SQLException e) {
            System.err.println("[DB] Error updating match: " + e.getMessage());
        }
    }
    
    
    
    // ===================== DAO Methods — Retrieve =====================

    /**
     * Returns all match results as a list of MatchResult objects.
     * @return 
     */
    public List<MatchResult> getAllMatchResults() {
        List<MatchResult> results = new ArrayList<>();
        String sql = "SELECT * FROM MATCH_RESULTS ORDER BY PLAYED_AT DESC";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                results.add(new MatchResult(
                    rs.getInt("ID"),
                    rs.getString("WINNER"),
                    rs.getString("LOSER"),
                    rs.getInt("TURNS"),
                    rs.getInt("PLAYER_HP"),
                    rs.getInt("AI_HP"),
                    rs.getTimestamp("PLAYED_AT").toString()
                ));
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error retrieving matches: " + e.getMessage());
        }
        return results;
    }

    /**
     * Returns total number of matches played.
     * @return 
     */
    public int getTotalMatches() {
        String sql = "SELECT COUNT(*) FROM MATCH_RESULTS WHERE WINNER != 'In Progress'";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DB] Error counting matches: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Returns total number of player wins.
     * @return 
     */
    public int getPlayerWins() {
        String sql = "SELECT COUNT(*) FROM MATCH_RESULTS WHERE WINNER = 'Player'";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DB] Error counting wins: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Returns move history for a specific match.
     * @param matchId
     * @return 
     */
    public List<String> getMoveHistory(int matchId) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT * FROM MOVE_HISTORY WHERE MATCH_ID = ? ORDER BY TURN_NUMBER";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                history.add(
                    "Turn " + rs.getInt("TURN_NUMBER") +
                    " | You: " + rs.getString("PLAYER_MOVE") +
                    " | AI: "  + rs.getString("AI_MOVE") +
                    " | Dealt: " + rs.getInt("DAMAGE_DEALT") +
                    " | Taken: " + rs.getInt("DAMAGE_TAKEN")
                );
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error retrieving move history: " + e.getMessage());
        }
        return history;
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
