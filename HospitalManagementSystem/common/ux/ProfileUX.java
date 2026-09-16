package common.ux;

import javax.swing.*;
import java.awt.*;

public class ProfileUX extends JFrame {
    protected final JLabel titleLabel = new JLabel("My Profile", SwingConstants.CENTER);
    protected final JTextField userIdField = new JTextField(16);
    protected final JTextField roleField = new JTextField(16);
    protected final JTextField emailField = new JTextField(16);
    protected final JTextField fullNameField = new JTextField(16);
    protected final JTextField phoneField = new JTextField(16);
    protected final JPasswordField passwordField = new JPasswordField(16);
    protected final JCheckBox showPasswordCheck = new JCheckBox("Show Password");

    // Dynamic Extra Fields
    protected final JTextField extra1Field = new JTextField(16);
    protected final JTextField extra2Field = new JTextField(16);
    protected final JTextField extra3Field = new JTextField(16);
    protected final JTextField extra4Field = new JTextField(16);
    protected final JTextArea extraArea = new JTextArea(3, 16);

    protected final JLabel extra1Label = new JLabel("Extra 1:");
    protected final JLabel extra2Label = new JLabel("Extra 2:");
    protected final JLabel extra3Label = new JLabel("Extra 3:");
    protected final JLabel extra4Label = new JLabel("Extra 4:");
    protected final JLabel extraAreaLabel = new JLabel("Notes / History:");

    protected final JPanel extraPanel = new JPanel(new GridBagLayout());

    protected final JButton saveButton = new JButton("Save Changes");
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");

    public ProfileUX() {
        setTitle("My Profile");
        setSize(560, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        root.add(titleLabel, BorderLayout.NORTH);

        userIdField.setEditable(false);
        userIdField.setBackground(new Color(240, 240, 240));
        roleField.setEditable(false);
        roleField.setBackground(new Color(240, 240, 240));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormRow(formPanel, gbc, row++, "User ID:", userIdField);
        addFormRow(formPanel, gbc, row++, "Role:", roleField);
        addFormRow(formPanel, gbc, row++, "Gmail / Email Address:", emailField);
        addFormRow(formPanel, gbc, row++, "Full Name:", fullNameField);
        addFormRow(formPanel, gbc, row++, "Phone Number:", phoneField);
        addFormRow(formPanel, gbc, row++, "Password:", passwordField);

        gbc.gridx = 1; gbc.gridy = row++;
        formPanel.add(showPasswordCheck, gbc);

        // Extra dynamic fields container
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        formPanel.add(extraPanel, gbc);

        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(backButton);
        buttonPanel.add(saveButton);
        root.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void addFormRow(JPanel panel, GridBagConstraints g, int row, String label, JComponent comp) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.35; g.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 0.65;
        panel.add(comp, g);
    }
}
