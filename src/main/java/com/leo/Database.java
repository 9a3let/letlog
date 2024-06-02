package com.leo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;

public class Database {

    private String dbPath = "jdbc:sqlite:" + Config.getDbPath();

    private final String createColumns = "ID integer primary key, DATE_ON integer, TIME_ON integer, CALLSIGN string, SENT integer, RCVD integer";
    private final String columns = "DATE_ON, TIME_ON, CALLSIGN, SENT, RCVD";

    public void createdb() throws SQLException {

        final String sql = "CREATE TABLE IF NOT EXISTS log(" + createColumns + ")";

        try (Connection conn = DriverManager.getConnection(dbPath);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void importRecords(Optional<Adif3> adif) throws Exception {

        final String sql = "INSERT INTO log(" + columns + ") VALUES(?, ?, ?, ?, ?)";
        int batchSize = 1000;
        int recordCount = adif.get().getRecords().size();
        List<Adif3Record> records = adif.get().getRecords();

        try (Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
            conn.setAutoCommit(false);

            for (int i = 0; i < recordCount; i++) {

                pstmt.setString(1, records.get(i).getQsoDate().format(dateFormatter)); // DATE ON

                pstmt.setString(2, records.get(i).getTimeOn().format(timeFormatter)); // TIME ON

                pstmt.setString(3, records.get(i).getCall()); // CALLSIGN

                pstmt.setString(4, records.get(i).getRstSent()); // SENT RST
                pstmt.setString(5, records.get(i).getRstRcvd()); // RCVD RST

                pstmt.addBatch();

                if (i % batchSize == 0 && i != 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }

            }
            pstmt.executeBatch();
            conn.commit();
        }
    }
}
