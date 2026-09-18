package common.ux;

import common.model.User;
import common.ui.DatePicker;
import patients.Patient;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class ProfileUX extends JFrame {
    protected final JLabel titleLabel = new JLabel("My Profile", SwingConstants.CENTER);
    protected final JTextField emailField = new JTextField(18);
    protected final JPasswordField passwordField = new JPasswordField(18);
    protected final JCheckBox showPasswordCheck = new JCheckBox("Show Password");
    protected final JTextField fullNameField = new JTextField(18);

    protected final JComboBox<String> phoneCountryCodeBox = new JComboBox<>(User.COUNTRY_CODES);
    protected final JTextField phoneField = new JTextField(12);

    // Patient Fields
    protected final DatePicker dobPicker = new DatePicker();
    protected final JComboBox<String> genderBox = new JComboBox<>(Patient.GENDER_OPTIONS);
    protected final JComboBox<String> bloodGroupBox = new JComboBox<>(Patient.BLOOD_GROUP_OPTIONS);
    protected final JComboBox<String> emergencyCountryCodeBox = new JComboBox<>(User.COUNTRY_CODES);
    protected final JTextField emergencyContactField = new JTextField(12);
    protected final JComboBox<String> emergencyRelationshipBox = new JComboBox<>(Patient.RELATIONSHIP_OPTIONS);
    protected final JTextArea medicalHistoryArea = new JTextArea(3, 18);

    // Dynamic Extra Fields for Staff/Doctor/Manager
    protected final JTextField extra1Field = new JTextField(18);
    protected final JTextField extra2Field = new JTextField(18);
    protected final JTextField extra3Field = new JTextField(18);
    protected final JLabel extra1Label = new JLabel("Extra 1:");
    protected final JLabel extra2Label = new JLabel("Extra 2:");
    protected final JLabel extra3Label = new JLabel("Extra 3:");

    protected final JPanel extraPanel = new JPanel(new GridBagLayout());

    protected final JButton saveButton = new JButton("Save Changes");
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");

    public ProfileUX() {
        setTitle("My Profile");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setNumericOnly(phoneField);
        setNumericOnly(emergencyContactField);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel phonePanel = new JPanel(new BorderLayout(5, 0));
        phoneCountryCodeBox.setPreferredSize(new Dimension(75, 26));
        phonePanel.add(phoneCountryCodeBox, BorderLayout.WEST);
        phonePanel.add(phoneField, BorderLayout.CENTER);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Gmail / Email Address:", emailField);
        addFormRow(formPanel, gbc, row++, "Password:", passwordField);

        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.65; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(showPasswordCheck, gbc);

        addFormRow(formPanel, gbc, row++, "Full Name:", fullNameField);
        addFormRow(formPanel, gbc, row++, "Phone Number:", phonePanel);

        // Extra dynamic fields container
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; gbc.weightx = 1.0;
        formPanel.add(extraPanel, gbc);

        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(backButton);
        buttonPanel.add(saveButton);
        root.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void setNumericOnly(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if (string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if (text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    protected void addFormRow(JPanel panel, GridBagConstraints g, int row, String label, JComponent comp) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1; g.weightx = 0.35; g.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 0.65;
        panel.add(comp, g);
    }
}
