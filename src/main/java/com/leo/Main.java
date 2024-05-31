package com.leo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.Adif3;

public class Main {

    static JFrame mainFrame = new JFrame();

    public static void main(String[] args) {

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

        fileMenuImportAdif.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

                if(fileChooser.showOpenDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File importAdifPath = fileChooser.getSelectedFile();
                System.err.println(importAdifPath.toString());

                AdiReader reader = new AdiReader();
                BufferedReader buffInput;
                try {
                    buffInput = new BufferedReader(new FileReader(importAdifPath));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                    return;
                }

                Optional<Adif3> adif;
                try {
                    adif = reader.read(buffInput);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return;
                }

                System.err.println(adif.get().getRecords());

                /*Database db = new Database();
                try {
                    db.createdb(Config.Log.getdbPath());
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }*/
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