package common.ui;

import common.service.RegistrationService;
import common.service.RegistrationService.RegistrationResult;
import common.ux.RegisterUX;

import javax.swing.*;

public class RegisterUI extends RegisterUX {
    public RegisterUI() {
        super();
        backButton.addActionListener(e -> back());
        registerButton.addActionListener(e -> register());
    }

    private void back() {
        dispose();
        new LoginUI().setVisible(true);
    }

    private void register() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String dob = dobPicker.getDateString();
        String gender = (String) genderBox.getSelectedItem();
        String bloodGroup = (String) bloodGroupBox.getSelectedItem();
        String emergencyContact = emergencyContactField.getText().trim();
        String history = medicalHistoryArea.getText().trim();

        RegistrationResult result = RegistrationService.getInstance().registerPatient(
                email,
                password,
                confirmPassword,
                fullName,
                phone,
                dob,
                gender,
                bloodGroup,
                emergencyContact,
                history
        );

        if (!result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Patient account created successfully!\nAssigned Patient ID: " + result.getPatient().getId() + "\nBirthday: " + dob + "\nYou may now log in with your Gmail.",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);

        back();
    }
}
