package com.leo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Optional;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.Adif3;

public class Main {

    public static void main(String[] args) {

        // reads config file
        try {
            Config.readConfigFile();
        } catch (Exception e) {
            // TODO Exceptions...
            JOptionPane.showMessageDialog(MainWindow.mainFrame, "Unable to read configuration file\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        new MainWindow();

        // checks if databese exists, if not then creates new one
        boolean dbExists = new File(Config.getDbPath()).exists();
        if (!dbExists) {
            try {
                Database.createdb();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(MainWindow.mainFrame, "Unable to create database\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        // loads records into table to be displayed
        try {
            Database.loadRecordsIntoTable();
            System.gc();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(MainWindow.mainFrame,
                    "Unable to insert records into table\n" + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // opens FileChooser to select ADI file and imports it into database
    public static void importAdif() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        AdiReader adiReader = new AdiReader();

        try (Reader adiFileReader = new FileReader(fileChooser.getSelectedFile());
                BufferedReader buffInput = new BufferedReader(adiFileReader)) {

            Optional<Adif3> adif = adiReader.read(buffInput);

            Database.importRecords(adif);

            MainWindow.statusLabel
                    .setText("ADIF Import finished: processed " + adif.get().getRecords().size() + " records.");
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(MainWindow.mainFrame, "Unable to import ADIF\n" + e1.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        MainWindow.mainTableModel.setRowCount(0);
        try {
            Database.loadRecordsIntoTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(MainWindow.mainFrame,
                    "Unable to insert records into table\n" + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        System.gc();
    }
}