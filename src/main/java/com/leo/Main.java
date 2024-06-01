package com.leo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
        
        /*Database db = new Database();
        try {
            db.createdb();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }*/

        initializeMainFrame();
        createMenuBar();
        createStatusPanel();
        createCenterPanel();
        
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

    }

    private static void initializeMainFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {};

        mainFrame.setSize(Config.MainFrame.getSizeX(), Config.MainFrame.getSizeY());
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setTitle("LetLog");
    }

    private static void createCenterPanel() {
        JPanel centerJPanel = new JPanel();
        centerJPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        JButton jb1 = new JButton("Test");
        jb1.setLayout(null);
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

        if(fileChooser.showOpenDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        Reader adiFileReader;
        try {
            adiFileReader = new FileReader(fileChooser.getSelectedFile());
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return;
        }

        AdiReader adiReader = new AdiReader();
        BufferedReader buffInput = new BufferedReader(adiFileReader);

        Optional<Adif3> adif;
        try {
            adif = adiReader.read(buffInput);
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        ArrayList<String> calls = new ArrayList<>();

        for (int i = 0; i < (adif.get().getRecords().size()); i++) {
            
            calls.add(adif.get().getRecords().get(i).getCall());

        }

        Database dbc = new Database();
        try {
            dbc.insertData(calls);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        statusLabel.setText("ADIF Import finished: processed " + adif.get().getRecords().size() + " records.");

    }

    static void exit() {
       mainFrame.dispose();
    }

}