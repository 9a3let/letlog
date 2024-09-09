package com.leo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatDarkLaf;

public class MainWindow {

    public static DefaultTableModel mainTableModel = new DefaultTableModel();
    public static JFrame mainFrame;
    public static JLabel statusLabel;
    public static PromptPanel promptPanel;
    private static JScrollPane scrollPane;

    public MainWindow() {

        FlatDarkLaf.setup();

        mainFrame = new JFrame();

        mainFrame.setSize(Config.getMainFrameSizeX(), Config.getMainFrameSizeY());
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setTitle("LetLog");
        ImageIcon ii = new ImageIcon("./images/icon.png");
        mainFrame.setIconImage(ii.getImage());

        createMenuBar();
        createStatusPanel();
        createCenterPanel();

        mainTableModel.addColumn("DATE");
        mainTableModel.addColumn("TIME");
        mainTableModel.addColumn("CALLSIGN");
        mainTableModel.addColumn("RST TX");
        mainTableModel.addColumn("RST RX");
        mainTableModel.addColumn("FREQ");
        mainTableModel.addColumn("MODE");
        mainTableModel.addColumn("NAME");
        mainTableModel.addColumn("COMMENT");

        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        mainFrame.setVisible(true);
    }

    private static void createMenuBar() {
        JMenuBar mainMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem fileMenuImportAdif = new JMenuItem("Import ADIF");
        JMenuItem fileMenuExportAdif = new JMenuItem("Export ADIF");
        JMenuItem fileMenuExit = new JMenuItem("Exit");

        fileMenu.add(fileMenuImportAdif);
        fileMenu.add(fileMenuExportAdif);
        fileMenu.addSeparator();
        fileMenu.add(fileMenuExit);

        mainMenuBar.add(fileMenu);
        mainFrame.setJMenuBar(mainMenuBar);

        fileMenuImportAdif.addActionListener(e -> Main.importAdif());
        fileMenuExportAdif.addActionListener(e -> Main.exportAdif());
        fileMenuExit.addActionListener(e -> exit());
    }

    private static void createStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setPreferredSize(new Dimension(mainFrame.getWidth(), 22));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.add(statusLabel);

        mainFrame.add(statusPanel, BorderLayout.SOUTH);
    }

    private static void createCenterPanel() {
        JPanel centerPanel = new JPanel();
        JTable table = new JTable(mainTableModel);
        scrollPane = new JScrollPane(table);

        promptPanel = new PromptPanel();

        centerPanel.setLayout(new BorderLayout());
        
        // QSO TABLE
        table.setRowHeight(25);
        table.setFont(table.getFont().deriveFont(16f));
        table.getTableHeader().setFont(table.getFont().deriveFont(16f));
        table.setRowSelectionAllowed(false);
        table.setShowHorizontalLines(true);

        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        defaults.putIfAbsent("Table.alternateRowColor", Color.DARK_GRAY); // sets every other row color

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer); // centers text in cells

        table.setFocusable(false);

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(promptPanel, BorderLayout.SOUTH);

        mainFrame.add(centerPanel, BorderLayout.CENTER);
    }

    // updates Config and writes to config file before closing
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

    // scrolls table to bottom
    public static void mainTableScrollToBottom() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
            }
        });
    }
}
