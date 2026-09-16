package common.ui;

import common.AuthService;
import common.Navigation;
import common.Session;
import common.model.User;
import common.ux.LoginUX;

import javax.swing.*;

public class LoginUI extends LoginUX {
    public LoginUI() {
        super();
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> {
            new RegisterUI().setVisible(true);
            dispose();
        });
        passwordField.addActionListener(e -> login());
        emailField.addActionListener(e -> login());
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        AuthService.AuthStatus status = AuthService.getInstance().authenticate(email, password);

        if (status != AuthService.AuthStatus.SUCCESS) {
            JOptionPane.showMessageDialog(this, status.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = Session.getCurrentUser();
        dispose();
        Navigation.openDashboard(user);
    }
}
