package com.leo;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class PromptPanel extends JPanel {
    @SuppressWarnings("deprecation")
    public PromptPanel() {

        // **************** CALLSIGN TEXTBOX ****************
        JPanel callPanel = new JPanel(new BorderLayout());
        JLabel callLabel = new JLabel("CALL");
        JTextField callField = new JTextField();
        callField.setColumns(10);
        callField.setFont(new Font("Areal", Font.BOLD, 20));
        // Set the custom DocumentFilter to the JTextField
        ((AbstractDocument) callField.getDocument()).setDocumentFilter(new UppercaseDocumentFilter());
        callPanel.add(callLabel, BorderLayout.PAGE_START);
        callPanel.add(callField, BorderLayout.PAGE_END);
        
        // **************** SENT RST TEXTBOX ****************
        JPanel sentPanel = new JPanel(new BorderLayout());
        JLabel sentLabel = new JLabel("TX RST");
        JTextField sentField = new JTextField();
        sentField.setColumns(4);
        sentField.setFont(new Font("Areal", Font.BOLD, 20));
        sentPanel.add(sentLabel, BorderLayout.PAGE_START);
        sentPanel.add(sentField, BorderLayout.PAGE_END);

        // **************** RCVD RST TEXTBOX ****************
        JPanel rcvdPanel = new JPanel(new BorderLayout());
        JLabel rcvdLabel = new JLabel("TX RST");
        JTextField rcvdField = new JTextField();
        rcvdField.setColumns(4);
        rcvdField.setFont(new Font("Areal", Font.BOLD, 20));
        rcvdField.setNextFocusableComponent(callField);
        rcvdPanel.add(rcvdLabel, BorderLayout.PAGE_START);
        rcvdPanel.add(rcvdField, BorderLayout.PAGE_END);
        
        add(callPanel);
        add(sentPanel);
        add(rcvdPanel);

    }

    // Custom DocumentFilter to convert input to uppercase
    static class UppercaseDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
            if (text != null) {
                text = text.toUpperCase();
            }
            super.insertString(fb, offset, text, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null) {
                text = text.toUpperCase();
            }
            super.replace(fb, offset, length, text, attrs);
        }
    }
    
}
