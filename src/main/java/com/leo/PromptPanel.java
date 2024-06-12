package com.leo;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
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

    private JTextField createTextField(int cols, Font font, DocumentFilter documentFilter) {
        JTextField textField = new JTextField();
        textField.setColumns(cols);
        textField.setFont(font);
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(documentFilter);
        return textField;
    }

    private JPanel createFieldPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.PAGE_START);
        panel.add(textField, BorderLayout.PAGE_END);
        return panel;
    }

    private JTextField dateField;
    private JTextField timeField;
    private JTextField callField;
    private JTextField sentField;
    private JTextField rcvdField;
    private JTextField nameField;

    @SuppressWarnings("deprecation")
    public PromptPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // DATE TEXTBOX
        dateField = createTextField(6, new Font("Areal", Font.PLAIN, 13), null);

        // TIME TEXTBOX
        timeField = createTextField(4, new Font("Areal", Font.PLAIN, 13), null); 

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
                        wipe();
                        break;
    
                    case KeyEvent.VK_SPACE:
                        sentField.setText("59");
                        rcvdField.setText("59");

                        dateField.setText(LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
                        timeField.setText(LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("HH:mm")));

                        nameField.grabFocus();
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
        };

        KeyListener keyListener2 = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        logQso();
                        wipe();
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
        };

        dateField.addKeyListener(keyListener2);
        timeField.addKeyListener(keyListener2);
        callField.addKeyListener(keyListener1);
        sentField.addKeyListener(keyListener2);
        rcvdField.addKeyListener(keyListener2);
        nameField.addKeyListener(keyListener2);

    }

    void logQso() {
        Adif3Record record = new Adif3Record();

        record.setQsoDate(LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        record.setTimeOn(LocalTime.parse(timeField.getText(), DateTimeFormatter.ofPattern("HH:mm")));
        record.setCall(callField.getText());
        record.setRstSent(sentField.getText());
        record.setRstRcvd(rcvdField.getText());
        record.setMode(Mode.CW);
        record.setFreq(7.023);
        record.setName(nameField.getText());

        try {
            Database.importRecord(record);
        } catch (Exception e1) {
            System.err.println(e1);
        }
        MainWindow.mainTableModel.setRowCount(0);
        try {
            Database.loadRecordsIntoTable();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
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
