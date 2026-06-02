/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.programandesign;

/**
 *
 * @author TOSHIBA
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL =
        "jdbc:derby:CombatDB;create=true";

    private Connection conn;

    public void connect() throws SQLException {
        conn = DriverManager.getConnection(URL);
        System.out.println("Connected to CombatDB");
        createTables();
    }

    private void createTables() throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute(
                "CREATE TABLE MATCH_RESULTS (" +
                "  ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                "  WINNER VARCHAR(20)," +
                "  LOSER  VARCHAR(20)," +
                "  TURNS  INT" +
                ")"
            );
            System.out.println("Table created.");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) throw e;
        }
    }

    public void saveResult(String winner, String loser, int turns)
            throws SQLException {
        conn.createStatement().execute(
            "INSERT INTO MATCH_RESULTS (WINNER, LOSER, TURNS) VALUES " +
            "('" + winner + "', '" + loser + "', " + turns + ")"
        );
    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
    }
}
