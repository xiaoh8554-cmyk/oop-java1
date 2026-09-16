package common.ux;

import javax.swing.*;
import java.awt.*;

public class LoginUX extends JFrame {
    protected final JTextField emailField = new JTextField(18);
    protected final JPasswordField passwordField = new JPasswordField(18);
    protected final JButton loginButton = new JButton("Login");
    protected final JButton registerButton = new JButton("Register New Patient");

    public LoginUX() {
        setTitle("APU Medical Centre - HMS Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 360);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel title = new JLabel("Hospital Management System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Gmail / Email:"), gbc);
        gbc.gridx = 1;
        emailField.setToolTipText("Enter your Gmail address (or 'admin' for administrator)");
        form.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        form.add(passwordField, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2, 1, 8, 8));
        actions.add(loginButton);
        actions.add(registerButton);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
