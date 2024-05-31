package com.leo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Database {

    private String dbPath = "jdbc:sqlite:"+Config.Log.getdbPath();

    public void createdb() throws SQLException {

        Connection conn = DriverManager.getConnection(dbPath);  

        String sql = "CREATE TABLE IF NOT EXISTS log(ID integer primary key, CALLSIGN)";
 
        Statement stmt = conn.createStatement();  
        stmt.execute(sql);

    }

    public void insertData(ArrayList<String> calls) throws SQLException {
        
        String sql = "INSERT INTO log(CALLSIGN) VALUES(?)";
        int batchSize = 1000;

        Connection conn = DriverManager.getConnection(dbPath);  
        PreparedStatement pstmt = conn.prepareStatement(sql);

        conn.setAutoCommit(false);

        System.err.println("Importing " + calls.size() + " records");
        for(int i = 0; i<calls.size(); i++) {

            pstmt.setString(1, calls.get(i));
            pstmt.addBatch();

            if (i % batchSize == 0 && i != 0) {
                pstmt.executeBatch();
                pstmt.clearBatch();
            }

        }
        pstmt.executeBatch();
        conn.commit();
        System.err.println("Finished");
        
    }
}
