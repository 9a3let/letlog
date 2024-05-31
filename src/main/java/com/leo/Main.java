package com.leo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.xml.crypto.Data;

import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.Adif3;

public class Main {

    static JFrame mainFrame = new JFrame();

    public static void main(String[] args) {
        /*
        Database db = new Database();
        try {
            db.createdb();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }*/

        JMenuBar mainMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileMenuImportAdif = new JMenuItem("Import ADIF");
        JMenuItem fileMenuExit = new JMenuItem("Exit");

        fileMenu.add(fileMenuImportAdif);
        fileMenu.add(fileMenuExit);
        mainMenuBar.add(fileMenu);
        mainFrame.setJMenuBar(mainMenuBar);

        mainFrame.setSize(Config.MainFrame.getSizeX(), Config.MainFrame.getSizeY());
        mainFrame.setTitle("LetLog");
        mainFrame.setLayout(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

        // IMPORTING ADIF ###############################################################

        fileMenuImportAdif.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

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

            }
        });

        fileMenuExit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
            
        });

    }

    static void exit() {
       mainFrame.dispose();
    }

}