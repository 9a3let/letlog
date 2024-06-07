package com.leo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

public class GUI {
    private static JFrame mainFrame = new JFrame();
    public static JLabel statusLabel = new JLabel();
    public static DefaultTableModel mainTableModel = new DefaultTableModel();

    public GUI() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        
        mainFrame.setSize(Config.getMainFrameSizeX(), Config.getMainFrameSizeY());
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setTitle("LetLog");
        
        createMenuBar();
        createStatusPanel();
        createCenterPanel();

        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        mainFrame.setVisible(true);;
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

        fileMenuImportAdif.addActionListener(e -> Main.importAdif());
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

    private static void createCenterPanel() {
        JPanel centerJPanel = new JPanel();
        JTable table = new JTable(mainTableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        centerJPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        centerJPanel.setLayout(new BorderLayout());

        centerJPanel.add(scrollPane, BorderLayout.CENTER);
        mainFrame.add(centerJPanel, BorderLayout.CENTER);
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
