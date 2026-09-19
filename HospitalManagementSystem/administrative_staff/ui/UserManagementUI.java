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
import java.util.regex.Pattern;

public class UserManagementUI extends UserManagementUX {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public UserManagementUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refresh();
        refreshButton.addActionListener(e -> refresh());
        addButton.addActionListener(e -> addUser());
        updateButton.addActionListener(e -> updateUser());
        deleteButton.addActionListener(e -> deleteUser());
        clearButton.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromSelection();
            }
        });
    }

    private void refresh() {
        model.setRowCount(0);
        for (User u : DataManager.getInstance().getAllUsers()) {
            model.addRow(new Object[]{u.getId(), u.getEmail(), u.getRole().name(), u.getFullName(), u.getPhoneNumber()});
        }
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String id = model.getValueAt(row, 0).toString();
            User user = DataManager.getInstance().findById(id);
            if (user != null) {
                emailField.setText(user.getEmail());
                passwordField.setText(user.getPassword());
                roleBox.setSelectedItem(user.getRole().name());
                fullNameField.setText(user.getFullName());
                phoneField.setText(user.getPhoneNumber());
            }
        }
    }

    private void clearForm() {
        table.clearSelection();
        emailField.setText("");
        passwordField.setText("");
        roleBox.setSelectedIndex(0);
        fullNameField.setText("");
        phoneField.setText("");
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

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
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
            newUser = new Doctor(id, email, password, fullName, phone, "General", "MBBS", "Room 101", "None");
        } else {
            String id = DataManager.getInstance().generateNextPatientId();
            newUser = new Patient(id, email, password, fullName, phone, "2000-01-01", "Other", "O+", "-", "parents", "None");
        }

        DataManager.getInstance().addUser(newUser);
        clearForm();
        refresh();
        JOptionPane.showMessageDialog(this, "User created successfully with ID: " + newUser.getId());
    }

    private void updateUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to update from the table.", "No User Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        User existingUser = DataManager.getInstance().findById(id);
        if (existingUser == null) {
            JOptionPane.showMessageDialog(this, "Selected user could not be found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.equalsIgnoreCase(existingUser.getEmail())) {
            User emailOwner = DataManager.getInstance().findByEmail(email);
            if (emailOwner != null && !emailOwner.getId().equalsIgnoreCase(existingUser.getId())) {
                JOptionPane.showMessageDialog(this, "Email address is already in use by another user.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        existingUser.setEmail(email);
        existingUser.setPassword(password);
        existingUser.setFullName(fullName);
        existingUser.setPhoneNumber(phone);

        DataManager.getInstance().updateUser(existingUser);
        if (Session.getCurrentUser() != null && Session.getCurrentUser().getId().equalsIgnoreCase(existingUser.getId())) {
            Session.setCurrentUser(existingUser);
        }

        refresh();
        JOptionPane.showMessageDialog(this, "User " + id + " updated successfully!");
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
            clearForm();
            refresh();
        }
    }
}

