package com.leo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Main {

    static JFrame mainFrame = new JFrame();

    public static void main(String[] args) {

        JMenuBar mainMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileMenuImportAdif = new JMenuItem("Import ADIF");
        JMenuItem fileMenuExit = new JMenuItem("Exit");

        fileMenuImportAdif.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Database db = new Database();
                try {
                    db.createdb(Config.Log.getdbPath());
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

        fileMenu.add(fileMenuImportAdif);
        fileMenu.add(fileMenuExit);
        mainMenuBar.add(fileMenu);
        mainFrame.setJMenuBar(mainMenuBar);

        mainFrame.setSize(Config.MainFrame.getSizeX(), Config.MainFrame.getSizeY());
        mainFrame.setTitle("LetLog");
        mainFrame.setLayout(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        
    }

    static void exit() {
       mainFrame.dispose();
    }

}