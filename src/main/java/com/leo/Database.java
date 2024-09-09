package com.leo;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.marsik.ham.adif.AdiWriter;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.AdifHeader;
import org.marsik.ham.adif.enums.Mode;
import org.marsik.ham.adif.enums.Propagation;

public class Database {

    private static String dbPath = "jdbc:sqlite:" + Config.getDbPath();

    private static final String createColumns = "ID integer primary key, DATE_ON text, TIME_ON text, CALLSIGN text, SENT text, RCVD text, "
            + "MODE text, FREQ bigint, GRIDSQUARE text, NAME text, CONTEST_ID text, COMMENT text, PROP_MODE text, "
            + "STATE text";

    private final static String columns = "DATE_ON, TIME_ON, CALLSIGN, SENT, RCVD, MODE, FREQ, GRIDSQUARE, NAME, CONTEST_ID, COMMENT, PROP_MODE, STATE";

    public static void createdb() throws SQLException {

        final String sql = "CREATE TABLE IF NOT EXISTS log(" + createColumns + ")";

        try (Connection conn = DriverManager.getConnection(dbPath);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    // imports records into database
    public static void importRecordsFromAdif(Optional<Adif3> adif) throws Exception {

        final String sql = "INSERT INTO log(" + columns + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            final int batchSize = 1000;
            int recordCount = adif.get().getRecords().size();
            List<Adif3Record> records = adif.get().getRecords();

            Adif3Record record;
            Propagation prop;

            for (int i = 0; i < recordCount; i++) {

                record = records.get(i);
                pstmt.setString(1, record.getQsoDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))); // DATE ON
                pstmt.setString(2, record.getTimeOn().format(DateTimeFormatter.ofPattern("HHmmss"))); // TIME ON
                pstmt.setString(3, record.getCall()); // CALLSIGN
                pstmt.setString(4, record.getRstSent()); // SENT RST
                pstmt.setString(5, record.getRstRcvd()); // RCVD RST
                pstmt.setString(6, record.getMode().toString()); // MODE
                if (record.getFreq() != null) {
                    pstmt.setLong(7, (long) (record.getFreq() * 1000000)); // FREQUENCY in Hz
                } else {
                    pstmt.setLong(7, 0);
                }
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

                // wirtes to database every 1000 records
                if (i % batchSize == 0 && i != 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                }
            }
            // writes to database
            pstmt.executeBatch();
            conn.commit();
        }
    }

    public static void exportRecordsToAdif(String adifPath) throws Exception {

        final String sql = "SELECT " + columns + " FROM log";

        AdiWriter writer = new AdiWriter();
        AdifHeader header = new AdifHeader();

        try (Connection conn = DriverManager.getConnection(dbPath);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            header.setProgramId("LETLOG");
            header.setProgramVersion("alpha");
            
            Adif3Record record;
            
            int numberOfRecords = 0;
            while (rs.next()) {
                record = new Adif3Record();

                record.setQsoDate(LocalDate.parse(rs.getString("DATE_ON"), DateTimeFormatter.ofPattern("yyyyMMdd")));
                record.setTimeOn(LocalTime.parse(rs.getString("TIME_ON"), DateTimeFormatter.ofPattern("HHmmss")));
                record.setCall(rs.getString("CALLSIGN"));
                record.setRstSent(rs.getString("SENT"));
                record.setRstRcvd(rs.getString("RCVD"));
                record.setMode(Mode.valueOf(rs.getString("MODE")));
                record.setFreq(rs.getLong("FREQ") / 1000000d);
                record.setGridsquare(rs.getString("GRIDSQUARE"));
                record.setName(rs.getString("NAME"));
                record.setComment(rs.getString("COMMENT"));
                writer.append(record);
                numberOfRecords++;
            }
            File adiFile = new File(adifPath + ".adi");
            adiFile.createNewFile();
            FileWriter adifWriter = new FileWriter(adiFile);
            adifWriter.write(writer.toString());
            adifWriter.close();
            MainWindow.statusLabel.setText("ADIF Export finished: exported " + numberOfRecords + " records");
        }
    }

    public static void saveRecord(Adif3Record record) throws Exception {
        final String sql = "INSERT INTO log(" + columns + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbPath);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");

            Propagation prop;
            
            pstmt.setString(1, record.getQsoDate().format(dateFormatter)); // DATE ON
            pstmt.setString(2, record.getTimeOn().format(timeFormatter)); // TIME ON */
            pstmt.setString(3, record.getCall()); // CALLSIGN
            pstmt.setString(4, record.getRstSent()); // SENT RST
            pstmt.setString(5, record.getRstRcvd()); // RCVD RST
            pstmt.setString(6, record.getMode().toString()); // MODE
            pstmt.setLong(7, (long) (record.getFreq() * 1000000)); // FREQUENCY in Hz
            pstmt.setString(8, record.getGridsquare()); // GRID
            pstmt.setString(9, record.getName()); // NAME
            pstmt.setString(10, record.getContestId()); // CONTEST ID
            pstmt.setString(11, record.getComment()); // COMMENT
            prop = record.getPropMode();
            if (prop != null) {
                pstmt.setString(12, prop.adifCode()); // PROPAGATION MODE
            }
            pstmt.setString(13, record.getState());

            // writes to database
            pstmt.executeUpdate();
        }
    }

    // loads records into table(model)
    public static void loadRecordsIntoTable() throws Exception {
        final String sql = "SELECT " + columns + " FROM log";

        try (Connection conn = DriverManager.getConnection(dbPath);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            DateTimeFormatter inputDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter outputDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDate date;
            String outputDate;

            DateTimeFormatter inputTimeFormatter = DateTimeFormatter.ofPattern("HHmmss");
            DateTimeFormatter outputTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime time;
            String outputTime;

            double freq;

            while (rs.next()) {

                date = LocalDate.parse(rs.getString("DATE_ON"), inputDateFormatter);
                outputDate = date.format(outputDateFormatter);

                time = LocalTime.parse(rs.getString("TIME_ON"), inputTimeFormatter);
                outputTime = time.format(outputTimeFormatter);

                freq = rs.getLong("FREQ") / 1000d;

                MainWindow.mainTableModel.addRow(new Object[] {
                        outputDate,
                        outputTime,
                        rs.getString("CALLSIGN"),
                        rs.getString("SENT"),
                        rs.getString("RCVD"),
                        freq,
                        rs.getString("MODE"),
                        rs.getString("NAME"),
                        rs.getString("COMMENT")
                });
            }
            MainWindow.mainTableScrollToBottom();
        }
    }

    public static void wipeLog() throws Exception {
        final String sql = "DELETE FROM log";

        try (Connection conn = DriverManager.getConnection(dbPath);
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}
