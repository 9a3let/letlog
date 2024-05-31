package com.leo;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {

        JFrame mainFrame = new JFrame();

        mainFrame.setSize(LogConfig.MainFrame.sizeX, LogConfig.MainFrame.sizeY);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
    }
}