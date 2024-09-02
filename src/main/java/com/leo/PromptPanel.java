package com.leo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;

import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Mode;

public class PromptPanel extends JPanel {

    private JSpinner createSpinner(int width, int height) {
        JSpinner spinner = new JSpinner();
        spinner.setFocusable(false);
        Dimension d = new Dimension(width, height);
        spinner.setPreferredSize(d);
        return spinner;
    }

    private JCheckBox creatCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFocusable(false);
        return checkBox;
    }

    private JTextField createTextEntry(int cols, Font font, DocumentFilter documentFilter) {
        JTextField textEntry = new JTextField();
        textEntry.setColumns(cols);
        textEntry.setFont(font);
        ((AbstractDocument) textEntry.getDocument()).setDocumentFilter(documentFilter);
        return textEntry;
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        return comboBox;
    }

    private JPanel createEntryPanel(String labelText, JComponent textEntry) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.PAGE_START);
        panel.add(textEntry, BorderLayout.PAGE_END);
        return panel;
    }

    public JComboBox<Mode> modeComboBox;
    private JCheckBox realtimeCheckBox;
    public JSpinner freqEntry;
    public JTextField dateEntry;
    private JTextField timeEntry;
    public JTextField callEntry;
    private JTextField sentEntry;
    private JTextField rcvdEntry;
    private JTextField nameEntry;

    @SuppressWarnings("deprecation")
    public PromptPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // REAL TIME CHECK BOX
        realtimeCheckBox = creatCheckBox("Real Time");
        realtimeCheckBox.setSelected(true);

        // DATE TEXTBOX
        dateEntry = createTextEntry(6, new Font("Areal", Font.PLAIN, 13), null);
        dateEntry.setEnabled(false);

        // TIME TEXTBOX
        timeEntry = createTextEntry(4, new Font("Areal", Font.PLAIN, 13), null);
        timeEntry.setEnabled(false);
        
        // FREQUENCY SPINNER
        freqEntry = createSpinner(100, 25);
        freqEntry.setValue(3675);

        // MODE COMBOBOX
        modeComboBox = new JComboBox<>(Mode.values());
        modeComboBox.setEditable(false);
        modeComboBox.setMinimumSize(new Dimension(modeComboBox.getWidth(), 25));
        
        // CALLSIGN TEXTBOX
        callEntry = createTextEntry(10, new Font("Areal", Font.BOLD, 20), new CustomDocumentFilters.UcWsFilter());

        // SENT RST TEXTBOX
        sentEntry = createTextEntry(4, new Font("Areal", Font.PLAIN, 20), new CustomDocumentFilters.NrFilter());

        // RCVD RST TEXTBOX
        rcvdEntry = createTextEntry(4, new Font("Areal", Font.PLAIN, 20), new CustomDocumentFilters.NrFilter());

        // NAME TEXTBOX
        nameEntry = createTextEntry(10, new Font("Areal", Font.PLAIN, 20), null);
        nameEntry.setNextFocusableComponent(callEntry);

        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.add(createEntryPanel("\n", realtimeCheckBox));
        line1.add(createEntryPanel("Date", dateEntry));
        line1.add(createEntryPanel("Time", timeEntry));
        line1.add(createEntryPanel("Frequency", freqEntry));
        line1.add(createEntryPanel("Mode", modeComboBox));

        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line2.add(createEntryPanel("Callsign", callEntry));
        line2.add(createEntryPanel("TX RST", sentEntry));
        line2.add(createEntryPanel("RX RST", rcvdEntry));
        line2.add(createEntryPanel("Name", nameEntry));

        add(line2);
        add(line1, BoxLayout.Y_AXIS);

        KeyListener keyListener1 = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        logQso();
                        break;

                    case KeyEvent.VK_SPACE:
                        sentEntry.setText("59");
                        rcvdEntry.setText("59");

                        nameEntry.grabFocus();
                        break;

                    case (KeyEvent.VK_ESCAPE):
                        clearPrompt();;
                        callEntry.grabFocus();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        };

        KeyListener keyListener2 = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        logQso();
                        break;

                    case (KeyEvent.VK_ESCAPE):
                        clearPrompt();;
                        callEntry.grabFocus();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        };

        realtimeCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == 1) {
                    dateEntry.setEnabled(false);
                    timeEntry.setEnabled(false);
                } else {
                    dateEntry.setEnabled(true);
                    timeEntry.setEnabled(true);
                }
            }
        });

        dateEntry.addKeyListener(keyListener2);
        timeEntry.addKeyListener(keyListener2);
        callEntry.addKeyListener(keyListener1);
        sentEntry.addKeyListener(keyListener2);
        rcvdEntry.addKeyListener(keyListener2);
        nameEntry.addKeyListener(keyListener2);

    }

    void logQso() {

        if (callEntry.getText().contentEquals("WIPELOG")) {
            /*JOptionPane.showMessageDialog(MainWindow.mainFrame, "wipelog", "Warning",
                    JOptionPane.WARNING_MESSAGE);*/
            try {
                Database.wipeLog();
                MainWindow.mainTableModel.setRowCount(0);
                clearPrompt();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(MainWindow.mainFrame, "Unable to wipe the log: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        if (callEntry.getText().isBlank()) {
            JOptionPane.showMessageDialog(MainWindow.mainFrame, "Callsign filed cannot be empty!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (sentEntry.getText().isBlank() || rcvdEntry.getText().isBlank()) {
            JOptionPane.showMessageDialog(MainWindow.mainFrame, "Reports cannot be empty!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!realtimeCheckBox.isSelected() && (dateEntry.getText().isBlank() || timeEntry.getText().isBlank())) {
            JOptionPane.showMessageDialog(MainWindow.mainFrame, "Date and/or time cannot be empty!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (freqEntry.getValue().equals(0)) {
            JOptionPane.showMessageDialog(MainWindow.mainFrame, "Frequency filed cannot be zero!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Adif3Record record = new Adif3Record();

        LocalDate date;
        LocalTime time;

        if (realtimeCheckBox.isSelected()) {
            date = LocalDate.now(ZoneOffset.UTC);
            time = LocalTime.now(ZoneOffset.UTC);
        } else {
            try {
                date = LocalDate.parse(dateEntry.getText(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                time = LocalTime.parse(timeEntry.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(MainWindow.mainFrame, "Date/time error\n" + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        record.setQsoDate(date);
        record.setTimeOn(time);
        record.setCall(callEntry.getText());
        record.setRstSent(sentEntry.getText());
        record.setRstRcvd(rcvdEntry.getText());
        record.setMode(Mode.valueOf(modeComboBox.getSelectedItem().toString()));
        record.setFreq((double)(int)freqEntry.getValue()/1000d); 
        record.setName(nameEntry.getText());

        try {
            Database.saveRecord(record);
        } catch (Exception e1) {
            System.err.println(e1);
        }

        MainWindow.mainTableModel.addRow(new Object[] {
                        date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                        time.format(DateTimeFormatter.ofPattern("HH:mm")),
                        record.getCall(),
                        record.getRstSent(),
                        record.getRstRcvd(),
                        freqEntry.getValue().toString(),
                        record.getMode(),
                        record.getName(),
                });
        MainWindow.mainTableScrollToBottom();

        clearPrompt();;
        callEntry.grabFocus();
    }

    void clearPrompt() {
        callEntry.setText("");
        sentEntry.setText("");
        rcvdEntry.setText("");
        nameEntry.setText("");
    }
}
