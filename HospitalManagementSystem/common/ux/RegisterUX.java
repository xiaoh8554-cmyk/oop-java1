package common.ux;

import common.ui.DatePicker;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class RegisterUX extends JFrame {
    public static final String[] COUNTRY_CODES = new String[]{
        "+60", "+65", "+62", "+66", "+63", "+84", "+86", "+852", "+886", "+81", "+82", "+91", "+1", "+44", "+61", "+64", "+971"
    };

    public static final String[] RELATIONSHIP_OPTIONS = new String[]{
        "parents", "mate", "son/daughter", "guardian", "siblings", "relatives", "friends"
    };

    protected final JTextField emailField = new JTextField(18);
    protected final JPasswordField passwordField = new JPasswordField(18);
    protected final JPasswordField confirmPasswordField = new JPasswordField(18);
    protected final JTextField fullNameField = new JTextField(18);

    protected final JComboBox<String> phoneCountryCodeBox = new JComboBox<>(COUNTRY_CODES);
    protected final JTextField phoneField = new JTextField(12);

    protected final DatePicker dobPicker = new DatePicker();
    protected final JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female"});
    protected final JComboBox<String> bloodGroupBox = new JComboBox<>(new String[]{"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"});

    protected final JComboBox<String> emergencyCountryCodeBox = new JComboBox<>(COUNTRY_CODES);
    protected final JTextField emergencyContactField = new JTextField(12);
    protected final JComboBox<String> relationshipBox = new JComboBox<>(RELATIONSHIP_OPTIONS);

    protected final JTextArea medicalHistoryArea = new JTextArea(3, 18);

    protected final JButton registerButton = new JButton("Create Patient Account");
    protected final JButton backButton = new JButton("Back to Login");

    public RegisterUX() {
        setTitle("Patient Registration - APU Medical Centre");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);

        setNumericOnly(phoneField);
        setNumericOnly(emergencyContactField);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("New Patient Registration", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        JPanel phonePanel = new JPanel(new BorderLayout(5, 0));
        phoneCountryCodeBox.setPreferredSize(new Dimension(75, 26));
        phonePanel.add(phoneCountryCodeBox, BorderLayout.WEST);
        phonePanel.add(phoneField, BorderLayout.CENTER);

        JPanel emergencyPhonePanel = new JPanel(new BorderLayout(5, 0));
        emergencyCountryCodeBox.setPreferredSize(new Dimension(75, 26));
        emergencyPhonePanel.add(emergencyCountryCodeBox, BorderLayout.WEST);
        emergencyPhonePanel.add(emergencyContactField, BorderLayout.CENTER);

        int row = 0;
        addFormRow(form, g, row++, "Gmail / Email Address:", emailField);
        addFormRow(form, g, row++, "Password (min 6 chars):", passwordField);
        addFormRow(form, g, row++, "Confirm Password:", confirmPasswordField);
        addFormRow(form, g, row++, "Full Name:", fullNameField);
        addFormRow(form, g, row++, "Phone Number:", phonePanel);
        addFormRow(form, g, row++, "Date of Birth (Birthday):", dobPicker);
        addFormRow(form, g, row++, "Gender:", genderBox);
        addFormRow(form, g, row++, "Blood Group:", bloodGroupBox);
        addFormRow(form, g, row++, "Emergency Contact:", emergencyPhonePanel);
        addFormRow(form, g, row++, "Emergency Relationship:", relationshipBox);

        g.gridx = 0; g.gridy = row; g.weightx = 0.35; g.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Medical History / Allergies:"), g);
        g.gridx = 1; g.weightx = 0.65;
        medicalHistoryArea.setLineWrap(true);
        medicalHistoryArea.setWrapStyleWord(true);
        form.add(new JScrollPane(medicalHistoryArea), g);

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        root.add(scrollPane, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 10));
        actions.add(backButton);
        actions.add(registerButton);
        root.add(actions, BorderLayout.SOUTH);

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

    private void addFormRow(JPanel panel, GridBagConstraints g, int row, String label, JComponent comp) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.35; g.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 0.65;
        panel.add(comp, g);
    }
}
