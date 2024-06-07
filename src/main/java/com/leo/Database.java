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
import org.marsik.ham.adif.enums.Propagation;

public class Database {

    private String dbPath = "jdbc:sqlite:" + Config.getDbPath();

    private final String createColumns = "ID integer primary key, DATE_ON integer, TIME_ON integer, CALLSIGN string, SENT integer, RCVD integer, "
                                        + "MODE string, FREQ integer, GRIDSQUARE string, NAME string, CONTEST_ID string, COMMENT string, PROP_MODE string, "
                                        + "STATE string";
            
    private final String columns = "DATE_ON, TIME_ON, CALLSIGN, SENT, RCVD, MODE, FREQ, GRIDSQUARE, NAME, CONTEST_ID, COMMENT, PROP_MODE, STATE";

    public void createdb() throws SQLException {

        final String sql = "CREATE TABLE IF NOT EXISTS log(" + createColumns + ")";

        try (Connection conn = DriverManager.getConnection(dbPath);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void importRecords(Optional<Adif3> adif) throws Exception {

        final String sql = "INSERT INTO log(" + columns + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(dbPath);
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
            conn.setAutoCommit(false);
            
            int batchSize = 1000;
            int recordCount = adif.get().getRecords().size();
            List<Adif3Record> records = adif.get().getRecords();

            Adif3Record record;
            Propagation prop;

            for (int i = 0; i < recordCount; i++) {

                record = records.get(i);
                pstmt.setString(1, record.getQsoDate().format(dateFormatter)); // DATE ON
                pstmt.setString(2, record.getTimeOn().format(timeFormatter)); // TIME ON
                pstmt.setString(3, record.getCall()); // CALLSIGN
                pstmt.setString(4, record.getRstSent()); // SENT RST
                pstmt.setString(5, record.getRstRcvd()); // RCVD RST
                pstmt.setString(6, record.getMode().toString()); // MODE
                pstmt.setLong(7, (long) (record.getFreq() * 1000000)); // FREQUENCY
                pstmt.setString(8, record.getGridsquare()); // GRID
                pstmt.setString(9, record.getName()); // NAME
                pstmt.setString(10, record.getContestId()); // CONTEST ID
                pstmt.setString(11, record.getComment()); // COMMENT
                prop = record.getPropMode();
                if (prop != null) {
                    pstmt.setString(12, prop.adifCode()); // PROPAGATION MODE
                }
                pstmt.setString(13, record.getState());

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
