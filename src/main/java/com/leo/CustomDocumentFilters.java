package com.leo;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class CustomDocumentFilters {

    // Custom DocumentFilter to convert input to uppercase and block white spaces
    static class UcWsFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
            if (text != null) {
                text = text.toUpperCase().replaceAll("\\s", "");
            }
            super.insertString(fb, offset, text, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null) {
                text = text.toUpperCase().replaceAll("\\s", "");
            }
            super.replace(fb, offset, length, text, attrs);
        }
    }

    // Custom DocumentFilter to allow only numbers
    static class NrFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
            if (text != null) {
                // Only keep integer values
                text = text.replaceAll("[^\\d+-]", ""); // Allow digits and '-' (negative sign)
            }
            super.insertString(fb, offset, text, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null) {
                // Only keep integer values
                text = text.replaceAll("[^\\d+-]", ""); // Allow digits and '-' (negative sign)
            }
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
