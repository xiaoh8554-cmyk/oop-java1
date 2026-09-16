package common.ux;

import common.ui.DatePicker;
import javax.swing.*;
import java.awt.*;

public class RegisterUX extends JFrame {
    protected final JTextField emailField = new JTextField(18);
    protected final JPasswordField passwordField = new JPasswordField(18);
    protected final JPasswordField confirmPasswordField = new JPasswordField(18);
    protected final JTextField fullNameField = new JTextField(18);
    protected final JTextField phoneField = new JTextField(18);
    protected final DatePicker dobPicker = new DatePicker();
    protected final JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female"});
    protected final JComboBox<String> bloodGroupBox = new JComboBox<>(new String[]{"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"});
    protected final JTextField emergencyContactField = new JTextField(18);
    protected final JTextArea medicalHistoryArea = new JTextArea(3, 18);

    protected final JButton registerButton = new JButton("Create Patient Account");
    protected final JButton backButton = new JButton("Back to Login");

    public RegisterUX() {
        setTitle("Patient Registration - APU Medical Centre");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 660);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("New Patient Registration", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormRow(form, g, row++, "Gmail / Email Address:", emailField);
        addFormRow(form, g, row++, "Password (min 6 chars):", passwordField);
        addFormRow(form, g, row++, "Confirm Password:", confirmPasswordField);
        addFormRow(form, g, row++, "Full Name:", fullNameField);
        addFormRow(form, g, row++, "Phone Number:", phoneField);
        addFormRow(form, g, row++, "Date of Birth (Birthday):", dobPicker);
        addFormRow(form, g, row++, "Gender:", genderBox);
        addFormRow(form, g, row++, "Blood Group:", bloodGroupBox);
        addFormRow(form, g, row++, "Emergency Contact:", emergencyContactField);

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

    private void addFormRow(JPanel panel, GridBagConstraints g, int row, String label, JComponent comp) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.35; g.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 0.65;
        panel.add(comp, g);
    }
}
