package common.ui;

import administrative_staff.AdministrativeStaff;
import common.DataManager;
import common.Session;
import common.model.User;
import common.ux.ProfileUX;
import doctors.Doctor;
import medical_manager.MedicalManager;
import patients.Patient;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

public class ProfileUI extends ProfileUX {
    private final Runnable onProfileUpdated;
    private char defaultEchoChar;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public ProfileUI() {
        this(null);
    }

    public ProfileUI(Runnable onProfileUpdated) {
        super();
        this.onProfileUpdated = onProfileUpdated;
        this.defaultEchoChar = passwordField.getEchoChar();

        loadUserData();

        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultEchoChar);
            }
        });

        backButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveChanges());
    }

    private void loadUserData() {
        User user = Session.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "No active session found.");
            dispose();
            return;
        }

        emailField.setText(user.getEmail());
        fullNameField.setText(user.getFullName());
        phoneField.setText(user.getPhoneNumber());
        passwordField.setText(user.getPassword());

        extraPanel.removeAll();
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        int r = 0;

        if (user instanceof Patient) {
            Patient p = (Patient) user;
            extra1Label.setText("Date of Birth (YYYY-MM-DD):");
            extra1Field.setText(p.getDateOfBirth());
            addExtraRow(g, r++, extra1Label, extra1Field);

            extra2Label.setText("Gender:");
            extra2Field.setText(p.getGender());
            addExtraRow(g, r++, extra2Label, extra2Field);

            extra3Label.setText("Blood Group:");
            extra3Field.setText(p.getBloodGroup());
            addExtraRow(g, r++, extra3Label, extra3Field);

            extra4Label.setText("Emergency Contact:");
            extra4Field.setText(p.getEmergencyContact());
            addExtraRow(g, r++, extra4Label, extra4Field);

            extra5Label.setText("Emergency Relationship:");
            if (p.getEmergencyRelationship() != null && !p.getEmergencyRelationship().trim().isEmpty()) {
                emergencyRelationshipBox.setSelectedItem(p.getEmergencyRelationship().trim());
            } else {
                emergencyRelationshipBox.setSelectedIndex(0);
            }
            addExtraRow(g, r++, extra5Label, emergencyRelationshipBox);

            g.gridx = 0; g.gridy = r; g.weightx = 0.35; g.anchor = GridBagConstraints.NORTHWEST;
            extraPanel.add(extraAreaLabel, g);
            g.gridx = 1; g.weightx = 0.65;
            extraArea.setText(p.getMedicalHistorySummary());
            extraPanel.add(new JScrollPane(extraArea), g);
        } else if (user instanceof Doctor) {
            Doctor d = (Doctor) user;
            extra1Label.setText("Specialty:");
            extra1Field.setText(d.getSpecialty());
            addExtraRow(g, r++, extra1Label, extra1Field);

            extra2Label.setText("Qualification:");
            extra2Field.setText(d.getQualification());
            addExtraRow(g, r++, extra2Label, extra2Field);

            extra3Label.setText("Consultation Room:");
            extra3Field.setText(d.getRoomNumber());
            addExtraRow(g, r++, extra3Label, extra3Field);
        } else if (user instanceof MedicalManager) {
            MedicalManager m = (MedicalManager) user;
            extra1Label.setText("Department:");
            extra1Field.setText(m.getDepartment());
            addExtraRow(g, r++, extra1Label, extra1Field);

            extra2Label.setText("Office Number:");
            extra2Field.setText(m.getOfficeNumber());
            addExtraRow(g, r++, extra2Label, extra2Field);
        } else if (user instanceof AdministrativeStaff) {
            AdministrativeStaff a = (AdministrativeStaff) user;
            extra1Label.setText("Access Level:");
            extra1Field.setText(a.getAccessLevel());
            addExtraRow(g, r++, extra1Label, extra1Field);
        }

        extraPanel.revalidate();
        extraPanel.repaint();
    }

    private void addExtraRow(GridBagConstraints g, int row, JLabel label, JComponent comp) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.35; g.anchor = GridBagConstraints.WEST;
        extraPanel.add(label, g);
        g.gridx = 1; g.weightx = 0.65;
        extraPanel.add(comp, g);
    }

    private void saveChanges() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "No active session found.");
            return;
        }

        String newEmail = emailField.getText().trim();
        String newFullName = fullNameField.getText().trim();
        String newPhone = phoneField.getText().trim();
        String newPassword = new String(passwordField.getPassword()).trim();

        if (newEmail.isEmpty() || newFullName.isEmpty() || newPhone.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Base fields (Email, Name, Phone, Password) cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Delimiter protection
        String relChoice = (String) emergencyRelationshipBox.getSelectedItem();
        String[] checks = {newEmail, newFullName, newPhone, newPassword, extra1Field.getText(), extra2Field.getText(), extra3Field.getText(), extra4Field.getText(), relChoice, extraArea.getText()};
        for (String c : checks) {
            if (c != null && c.contains("|")) {
                JOptionPane.showMessageDialog(this, "Pipe character '|' is not allowed in any field.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Uniqueness check if email changed
        if (!newEmail.equalsIgnoreCase(currentUser.getEmail())) {
            User existing = DataManager.getInstance().findByEmail(newEmail);
            if (existing != null && !existing.getId().equalsIgnoreCase(currentUser.getId())) {
                JOptionPane.showMessageDialog(this, "Email address is already in use by another account.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        currentUser.setEmail(newEmail);
        currentUser.setFullName(newFullName);
        currentUser.setPhoneNumber(newPhone);
        currentUser.setPassword(newPassword);

        if (currentUser instanceof Patient) {
            Patient p = (Patient) currentUser;
            p.setDateOfBirth(extra1Field.getText().trim());
            p.setGender(extra2Field.getText().trim());
            p.setBloodGroup(extra3Field.getText().trim());
            p.setEmergencyContact(extra4Field.getText().trim());
            p.setEmergencyRelationship(relChoice != null ? relChoice.trim() : "parents");
            p.setMedicalHistorySummary(extraArea.getText().trim());
        } else if (currentUser instanceof Doctor) {
            Doctor d = (Doctor) currentUser;
            d.setSpecialty(extra1Field.getText().trim());
            d.setQualification(extra2Field.getText().trim());
            d.setRoomNumber(extra3Field.getText().trim());
        } else if (currentUser instanceof MedicalManager) {
            MedicalManager m = (MedicalManager) currentUser;
            m.setDepartment(extra1Field.getText().trim());
            m.setOfficeNumber(extra2Field.getText().trim());
        } else if (currentUser instanceof AdministrativeStaff) {
            AdministrativeStaff a = (AdministrativeStaff) currentUser;
            a.setAccessLevel(extra1Field.getText().trim());
        }

        DataManager.getInstance().updateUser(currentUser);
        Session.setCurrentUser(currentUser);

        if (onProfileUpdated != null) {
            onProfileUpdated.run();
        }

        JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
