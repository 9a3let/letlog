package com.leo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.marsik.ham.adif.Adif3;

public class Database {
    
    private String dbPath = "jdbc:sqlite:" + Config.Log.getdbPath();

    private String createColumns = "ID integer primary key, DATE_ON integer, TIME_ON integer, CALLSIGN string, SENT integer, RCVD integer";
    private String columns = "DATE_ON, TIME_ON, CALLSIGN, SENT, RCVD";

    public void createdb() throws SQLException {

        Connection conn = DriverManager.getConnection(dbPath);  

        String sql = "CREATE TABLE IF NOT EXISTS log(" + createColumns + ")";
 
        Statement stmt = conn.createStatement();  
        stmt.execute(sql);
        conn.close();

    }

    public void importRecords(Optional<Adif3> adif) throws Exception {

        String sql = "INSERT INTO log(" + columns + ") VALUES(?, ?, ?, ?, ?)";
        int batchSize = 1000;
        int recordCount = adif.get().getRecords().size();
    
        Connection conn = DriverManager.getConnection(dbPath);  
        PreparedStatement pstmt = conn.prepareStatement(sql);
        DateTimeFormatter formatter;

        conn.setAutoCommit(false);
    
        for(int i = 0; i<recordCount; i++) {

            formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            pstmt.setString(1, adif.get().getRecords().get(i).getQsoDate().format(formatter)); // DATE ON 

            formatter = DateTimeFormatter.ofPattern("HHmmss");
            pstmt.setString(2, adif.get().getRecords().get(i).getTimeOn().format(formatter)); // TIME ON

            pstmt.setString(3, adif.get().getRecords().get(i).getCall()); // CALLSIGN

            pstmt.setString(4, adif.get().getRecords().get(i).getRstSent()); // SENT RST
            pstmt.setString(5, adif.get().getRecords().get(i).getRstRcvd()); // RCVD RST

            pstmt.addBatch();

            if (i % batchSize == 0 && i != 0) {
                pstmt.executeBatch();
                pstmt.clearBatch();
            }

        }
        pstmt.executeBatch();
        conn.commit();
        conn.close();
    }
}
