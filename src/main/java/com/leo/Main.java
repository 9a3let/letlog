package com.leo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Optional;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.Adif3;

public class Main {

    private static JFrame mainFrame = new JFrame();
    private static JLabel statusLabel = new JLabel();

    public static void main(String[] args) {

        try {
            Config.readConfigFile();
        } catch (Exception e) {
            // TODO Exceptions...
            JOptionPane.showMessageDialog(mainFrame, "Unable to read configuration file\n" + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        boolean dbExists = new File(Config.getDbPath()).exists();
        if (!dbExists) {
            Database db = new Database();
            try {
                db.createdb();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(mainFrame, "Unable to create database\n" + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        initializeMainFrame();
        createMenuBar();
        createStatusPanel();
        createCenterPanel();

        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        mainFrame.setVisible(true);
    }

    private static void initializeMainFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }

        mainFrame.setSize(Config.getMainFrameSizeX(), Config.getMainFrameSizeY());
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setTitle("LetLog");
    }

    private static void createCenterPanel() {
        JPanel centerJPanel = new JPanel();
        centerJPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        JButton jb1 = new JButton("Test");
        jb1.setFocusPainted(false);
        centerJPanel.add(jb1);
        mainFrame.add(centerJPanel, BorderLayout.CENTER);
    }

    private static void createMenuBar() {
        JMenuBar mainMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileMenuImportAdif = new JMenuItem("Import ADIF");
        JMenuItem fileMenuExit = new JMenuItem("Exit");

        fileMenu.add(fileMenuImportAdif);
        fileMenu.add(fileMenuExit);
        mainMenuBar.add(fileMenu);
        mainFrame.setJMenuBar(mainMenuBar);

        fileMenuImportAdif.addActionListener(e -> importAdif(e));
        fileMenuExit.addActionListener(e -> exit());
    }

    private static void createStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setPreferredSize(new Dimension(mainFrame.getWidth(), 18));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        mainFrame.add(statusPanel, BorderLayout.SOUTH);

        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
    }

    private static void importAdif(ActionEvent e) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        if (fileChooser.showOpenDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        AdiReader adiReader = new AdiReader();

        try (Reader adiFileReader = new FileReader(fileChooser.getSelectedFile());
                BufferedReader buffInput = new BufferedReader(adiFileReader)) {

            Optional<Adif3> adif = adiReader.read(buffInput);

            Database db = new Database();

            db.importRecords(adif);

            statusLabel.setText("ADIF Import finished: processed " + adif.get().getRecords().size() + " records.");
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(mainFrame, "Unable to import ADIF\n" + e1.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    static void exit() {
        Config.setMainFrameSizeX(mainFrame.getWidth());
        Config.setMainFrameSizeY(mainFrame.getHeight());

        try {
            Config.writeConfigFile();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, "Unable to write configuration file\n" + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);

        }

        mainFrame.dispose();
        System.exit(0);
    }

}