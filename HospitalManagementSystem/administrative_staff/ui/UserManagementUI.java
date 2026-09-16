package administrative_staff.ui;

import administrative_staff.AdministrativeStaff;
import administrative_staff.ux.UserManagementUX;
import common.DataManager;
import common.Session;
import common.model.User;
import doctors.Doctor;
import medical_manager.MedicalManager;
import patients.Patient;

import javax.swing.*;

public class UserManagementUI extends UserManagementUX {
    public UserManagementUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refresh();
        refreshButton.addActionListener(e -> refresh());
        addButton.addActionListener(e -> addUser());
        deleteButton.addActionListener(e -> deleteUser());
    }

    private void refresh() {
        model.setRowCount(0);
        for (User u : DataManager.getInstance().getAllUsers()) {
            model.addRow(new Object[]{u.getId(), u.getEmail(), u.getRole().name(), u.getFullName(), u.getPhoneNumber()});
        }
    }

    private void addUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleBox.getSelectedItem();
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (DataManager.getInstance().isEmailTaken(email)) {
            JOptionPane.showMessageDialog(this, "Email address already exists.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser;
        if ("ADMINISTRATIVE_STAFF".equals(role)) {
            String id = DataManager.getInstance().generateNextId("A", 3);
            newUser = new AdministrativeStaff(id, email, password, fullName, phone, "ADMIN");
        } else if ("MEDICAL_MANAGER".equals(role)) {
            String id = DataManager.getInstance().generateNextId("M", 3);
            newUser = new MedicalManager(id, email, password, fullName, phone, "General", "Main Office");
        } else if ("DOCTOR".equals(role)) {
            String id = DataManager.getInstance().generateNextId("D", 3);
            newUser = new Doctor(id, email, password, fullName, phone, "General", "MBBS", "Room 101");
        } else {
            String id = DataManager.getInstance().generateNextPatientId();
            newUser = new Patient(id, email, password, fullName, phone, "2000-01-01", "Other", "O+", "-", "None");
        }

        DataManager.getInstance().addUser(newUser);
        emailField.setText("");
        passwordField.setText("");
        fullNameField.setText("");
        phoneField.setText("");
        refresh();
        JOptionPane.showMessageDialog(this, "User created successfully with ID: " + newUser.getId());
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user first.");
            return;
        }
        String id = model.getValueAt(row, 0).toString();
        if (Session.getCurrentUser() != null && id.equalsIgnoreCase(Session.getCurrentUser().getId())) {
            JOptionPane.showMessageDialog(this, "You cannot delete your current account.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DataManager.getInstance().deleteUser(id);
            refresh();
        }
    }
}
