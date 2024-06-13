package com.leo;

import java.awt.BorderLayout;
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

    private JSpinner createSpinner() {
        JSpinner spinner = new JSpinner();
        spinner.setValue(7023);
        return spinner;
    }

    private JCheckBox creatCheckBox() {
        JCheckBox checkBox = new JCheckBox("Real Time");
        checkBox.setFocusable(false);
        return checkBox;
    }

    private JTextField createTextField(int cols, Font font, DocumentFilter documentFilter) {
        JTextField textField = new JTextField();
        textField.setColumns(cols);
        textField.setFont(font);
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(documentFilter);
        return textField;
    }

    private JPanel createFieldPanel(String labelText, JComponent textField) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.PAGE_START);
        panel.add(textField, BorderLayout.PAGE_END);
        return panel;
    }

    private JCheckBox realtimeCheckBox;
    private JTextField dateField;
    private JTextField timeField;
    private JTextField callField;
    private JTextField sentField;
    private JTextField rcvdField;
    private JTextField nameField;

    @SuppressWarnings("deprecation")
    public PromptPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // REAL TIME CHECK BOX
        realtimeCheckBox = creatCheckBox();
        realtimeCheckBox.setSelected(true);

        // DATE TEXTBOX
        dateField = createTextField(6, new Font("Areal", Font.PLAIN, 13), null);
        dateField.setEnabled(false);

        // TIME TEXTBOX
        timeField = createTextField(4, new Font("Areal", Font.PLAIN, 13), null);
        timeField.setEnabled(false);

        // CALLSIGN TEXTBOX
        callField = createTextField(10, new Font("Areal", Font.BOLD, 20), new CustomDocumentFilters.UcWsFilter());

        // SENT RST TEXTBOX
        sentField = createTextField(4, new Font("Areal", Font.PLAIN, 20), new CustomDocumentFilters.NrFilter());

        // RCVD RST TEXTBOX
        rcvdField = createTextField(4, new Font("Areal", Font.PLAIN, 20), new CustomDocumentFilters.NrFilter());

        // NAME TEXTBOX
        nameField = createTextField(10, new Font("Areal", Font.PLAIN, 20), null);
        nameField.setNextFocusableComponent(callField);

        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.add(createFieldPanel("\n", realtimeCheckBox));
        line1.add(createFieldPanel("Date", dateField));
        line1.add(createFieldPanel("Time", timeField));

        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line2.add(createFieldPanel("Callsign", callField));
        line2.add(createFieldPanel("TX RST", sentField));
        line2.add(createFieldPanel("RX RST", rcvdField));
        line2.add(createFieldPanel("Name", nameField));

        add(line1);
        add(line2, BoxLayout.Y_AXIS);

        KeyListener keyListener1 = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        logQso();
                        break;

                    case KeyEvent.VK_SPACE:
                        sentField.setText("59");
                        rcvdField.setText("59");

                        nameField.grabFocus();
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
                    dateField.setEnabled(false);
                    timeField.setEnabled(false);
                } else {
                    dateField.setEnabled(true);
                    timeField.setEnabled(true);
                }
            }
        });

        dateField.addKeyListener(keyListener2);
        timeField.addKeyListener(keyListener2);
        callField.addKeyListener(keyListener1);
        sentField.addKeyListener(keyListener2);
        rcvdField.addKeyListener(keyListener2);
        nameField.addKeyListener(keyListener2);

    }

    void logQso() {

        if (callField.getText().isBlank()) {
            JOptionPane.showMessageDialog(MainWindow.mainFrame, "Callsign filed cannot be empty!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (sentField.getText().isBlank() || rcvdField.getText().isBlank()) {
            JOptionPane.showMessageDialog(MainWindow.mainFrame, "Reports cannot be empty!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!realtimeCheckBox.isSelected() && (dateField.getText().isBlank() || timeField.getText().isBlank())) {
            JOptionPane.showMessageDialog(MainWindow.mainFrame, "Date and/or time cannot be empty!", "Warning",
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
                date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                time = LocalTime.parse(timeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(MainWindow.mainFrame, "Date/time error\n" + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        record.setQsoDate(date);
        record.setTimeOn(time);
        record.setCall(callField.getText());
        record.setRstSent(sentField.getText());
        record.setRstRcvd(rcvdField.getText());
        record.setMode(Mode.CW);
        record.setFreq(7.023);
        record.setName(nameField.getText());

        try {
            Database.saveRecord(record);
        } catch (Exception e1) {
            System.err.println(e1);
        }
        MainWindow.mainTableModel.setRowCount(0);
        try {
            Database.loadRecordsIntoTable();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        wipe();
        callField.grabFocus();
    }

    void wipe() {
        dateField.setText("");
        timeField.setText("");
        callField.setText("");
        sentField.setText("");
        rcvdField.setText("");
        nameField.setText("");
    }
}
