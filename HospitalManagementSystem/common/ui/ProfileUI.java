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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class ProfileUI extends ProfileUX {
    private final User targetUser;
    private final Runnable onProfileUpdated;
    private char defaultEchoChar;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private final JComboBox<String> managerOfficeBox = new JComboBox<>();

    public ProfileUI() {
        this(null, null);
    }

    public ProfileUI(Runnable onProfileUpdated) {
        this(null, onProfileUpdated);
    }

    public ProfileUI(User targetUser) {
        this(targetUser, null);
    }

    public ProfileUI(User targetUser, Runnable onProfileUpdated) {
        super();
        this.targetUser = (targetUser != null) ? targetUser : Session.getCurrentUser();
        this.onProfileUpdated = onProfileUpdated;
        this.defaultEchoChar = passwordField.getEchoChar();

        if (this.targetUser != null && (targetUser != null && (Session.getCurrentUser() == null || !targetUser.getId().equalsIgnoreCase(Session.getCurrentUser().getId())))) {
            setTitle("User Profile - " + this.targetUser.getId() + " (" + this.targetUser.getFullName() + ")");
            titleLabel.setText("User Profile: " + this.targetUser.getFullName() + " (" + this.targetUser.getId() + ")");
            backButton.setText("\u2190 Back / Close");
        }

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

    private void setPhoneWithCode(String fullPhone, JComboBox<String> countryCodeBox, JTextField phoneDigitsField) {
        if (fullPhone == null || fullPhone.trim().isEmpty()) {
            countryCodeBox.setSelectedItem("+60");
            phoneDigitsField.setText("");
            return;
        }
        String trimmed = fullPhone.trim();
        boolean matched = false;
        for (String code : User.COUNTRY_CODES) {
            if (trimmed.startsWith(code)) {
                countryCodeBox.setSelectedItem(code);
                phoneDigitsField.setText(trimmed.substring(code.length()));
                matched = true;
                break;
            }
        }
        if (!matched) {
            countryCodeBox.setSelectedItem("+60");
            phoneDigitsField.setText(trimmed.startsWith("+") ? trimmed.substring(1) : trimmed);
        }
    }

    private void loadUserData() {
        User user = this.targetUser;
        if (user == null) {
            JOptionPane.showMessageDialog(this, "No user data found to display.");
            dispose();
            return;
        }

        emailField.setText(user.getEmail());
        fullNameField.setText(user.getFullName());
        passwordField.setText(user.getPassword());
        setPhoneWithCode(user.getPhoneNumber(), phoneCountryCodeBox, phoneField);

        extraPanel.removeAll();
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        int r = 0;

        if (user instanceof Patient) {
            Patient p = (Patient) user;

            dobPicker.setDateString(p.getDateOfBirth());
            addFormRow(extraPanel, g, r++, "Date of Birth (Birthday):", dobPicker);

            if (p.getGender() != null) {
                genderBox.setSelectedItem(p.getGender());
            }
            addFormRow(extraPanel, g, r++, "Gender:", genderBox);

            if (p.getBloodGroup() != null) {
                bloodGroupBox.setSelectedItem(p.getBloodGroup());
            }
            addFormRow(extraPanel, g, r++, "Blood Group:", bloodGroupBox);

            setPhoneWithCode(p.getEmergencyContact(), emergencyCountryCodeBox, emergencyContactField);
            JPanel emergencyPhonePanel = new JPanel(new BorderLayout(5, 0));
            emergencyCountryCodeBox.setPreferredSize(new Dimension(75, 26));
            emergencyPhonePanel.add(emergencyCountryCodeBox, BorderLayout.WEST);
            emergencyPhonePanel.add(emergencyContactField, BorderLayout.CENTER);
            addFormRow(extraPanel, g, r++, "Emergency Contact:", emergencyPhonePanel);

            if (p.getEmergencyRelationship() != null && !p.getEmergencyRelationship().trim().isEmpty()) {
                emergencyRelationshipBox.setSelectedItem(p.getEmergencyRelationship().trim());
            } else {
                emergencyRelationshipBox.setSelectedIndex(0);
            }
            addFormRow(extraPanel, g, r++, "Emergency Relationship:", emergencyRelationshipBox);

            g.gridx = 0; g.gridy = r; g.gridwidth = 1; g.weightx = 0.35; g.anchor = GridBagConstraints.NORTHWEST;
            extraPanel.add(new JLabel("Medical History / Allergies:"), g);
            g.gridx = 1; g.weightx = 0.65;
            medicalHistoryArea.setLineWrap(true);
            medicalHistoryArea.setWrapStyleWord(true);
            medicalHistoryArea.setText(p.getMedicalHistorySummary() != null ? p.getMedicalHistorySummary() : "");
            extraPanel.add(new JScrollPane(medicalHistoryArea), g);
        } else if (user instanceof Doctor) {
            Doctor d = (Doctor) user;
            extra1Label.setText("Specialty:");
            extra1Field.setText(d.getSpecialty());
            extra1Field.setEditable(true);
            addFormRow(extraPanel, g, r++, extra1Label.getText(), extra1Field);

            extra2Label.setText("Qualification:");
            extra2Field.setText(d.getQualification());
            extra2Field.setEditable(true);
            addFormRow(extraPanel, g, r++, extra2Label.getText(), extra2Field);

            extra3Label.setText("Allocated Room (Assets):");
            extra3Field.setText(d.getRoomNumber());
            extra3Field.setEditable(false);
            addFormRow(extraPanel, g, r++, extra3Label.getText(), extra3Field);
        } else if (user instanceof MedicalManager) {
            MedicalManager m = (MedicalManager) user;
            managerOfficeBox.removeAllItems();
            java.util.List<String[]> assets = common.FileHandler.read(common.FileHandler.ASSETS);
            for (String[] a : assets) {
                if (a.length >= 4 && ("OFFICE".equalsIgnoreCase(a[2]) || a[3].contains("Level 6"))) {
                    String roomName = a[1].trim();
                    if (roomName.startsWith("Office ")) {
                        roomName = roomName.substring(7).trim();
                    }
                    managerOfficeBox.addItem(roomName);
                }
            }
            if (managerOfficeBox.getItemCount() == 0) {
                managerOfficeBox.addItem("A06-01");
                managerOfficeBox.addItem("A06-02");
                managerOfficeBox.addItem("A06-03");
                managerOfficeBox.addItem("A06-04");
                managerOfficeBox.addItem("A06-05");
            }
            if (m.getOfficeNumber() != null) {
                managerOfficeBox.setSelectedItem(m.getOfficeNumber());
            }
            addFormRow(extraPanel, g, r++, "Office Number (Level 6):", managerOfficeBox);
        } else if (user instanceof AdministrativeStaff) {
            // Administrative staff has no extra role-specific fields
            extraPanel.removeAll();
        }

        extraPanel.revalidate();
        extraPanel.repaint();
    }

    private void saveChanges() {
        User currentUser = this.targetUser;
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "No user found to update.");
            return;
        }

        String newEmail = emailField.getText().trim();
        String newFullName = fullNameField.getText().trim();
        String phoneCode = (String) phoneCountryCodeBox.getSelectedItem();
        String phoneDigits = phoneField.getText().trim();
        String newPhone = phoneDigits.isEmpty() ? "" : (phoneCode + phoneDigits);
        String newPassword = new String(passwordField.getPassword()).trim();

        if (newEmail.isEmpty() || newFullName.isEmpty() || phoneDigits.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All base fields (Email, Full Name, Phone Number, Password) are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Uniqueness check if email changed
        if (!newEmail.equalsIgnoreCase(currentUser.getEmail())) {
            User existing = DataManager.getInstance().findByEmail(newEmail);
            if (existing != null && !existing.getId().equalsIgnoreCase(currentUser.getId())) {
                JOptionPane.showMessageDialog(this, "Email address is already in use by another account.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (currentUser instanceof Patient) {
            String dob = dobPicker.getDateString();
            String gender = (String) genderBox.getSelectedItem();
            String bloodGroup = (String) bloodGroupBox.getSelectedItem();
            String emergencyCode = (String) emergencyCountryCodeBox.getSelectedItem();
            String emergencyDigits = emergencyContactField.getText().trim();
            String emergencyContact = emergencyDigits.isEmpty() ? "" : (emergencyCode + emergencyDigits);
            String relChoice = (String) emergencyRelationshipBox.getSelectedItem();
            String medicalHistory = medicalHistoryArea.getText().trim();

            if (dob.isEmpty() || gender == null || gender.trim().isEmpty() ||
                bloodGroup == null || bloodGroup.trim().isEmpty() ||
                emergencyDigits.isEmpty() || relChoice == null || relChoice.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required except Medical History / Allergies.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                LocalDate parsedDob = LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (parsedDob.isAfter(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this, "Date of birth cannot be in the future.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid Date of Birth format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Pipe delimiter check
            String[] checks = {newEmail, newFullName, newPhone, newPassword, dob, gender, bloodGroup, emergencyContact, relChoice, medicalHistory};
            for (String c : checks) {
                if (c != null && c.contains("|")) {
                    JOptionPane.showMessageDialog(this, "Pipe character '|' is not allowed in any field.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Patient p = (Patient) currentUser;
            p.setEmail(newEmail);
            p.setFullName(newFullName);
            p.setPhoneNumber(newPhone);
            p.setPassword(newPassword);
            p.setDateOfBirth(dob);
            p.setGender(gender);
            p.setBloodGroup(bloodGroup);
            p.setEmergencyContact(emergencyContact);
            p.setEmergencyRelationship(relChoice.trim());
            p.setMedicalHistorySummary(medicalHistory.isEmpty() ? "None" : medicalHistory);
        } else if (currentUser instanceof Doctor) {
            String specialty = extra1Field.getText().trim();
            String qualification = extra2Field.getText().trim();

            if (specialty.isEmpty() || qualification.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Doctor Specialty and Qualification are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] checks = {newEmail, newFullName, newPhone, newPassword, specialty, qualification};
            for (String c : checks) {
                if (c != null && c.contains("|")) {
                    JOptionPane.showMessageDialog(this, "Pipe character '|' is not allowed in any field.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Doctor d = (Doctor) currentUser;
            d.setEmail(newEmail);
            d.setFullName(newFullName);
            d.setPhoneNumber(newPhone);
            d.setPassword(newPassword);
            d.setSpecialty(specialty);
            d.setQualification(qualification);
        } else if (currentUser instanceof MedicalManager) {
            String office = (String) managerOfficeBox.getSelectedItem();
            if (office == null || office.trim().isEmpty()) {
                office = "A06-01";
            }

            String[] checks = {newEmail, newFullName, newPhone, newPassword, office};
            for (String c : checks) {
                if (c != null && c.contains("|")) {
                    JOptionPane.showMessageDialog(this, "Pipe character '|' is not allowed in any field.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            MedicalManager m = (MedicalManager) currentUser;
            m.setEmail(newEmail);
            m.setFullName(newFullName);
            m.setPhoneNumber(newPhone);
            m.setPassword(newPassword);
            m.setOfficeNumber(office);
        } else if (currentUser instanceof AdministrativeStaff) {
            String[] checks = {newEmail, newFullName, newPhone, newPassword};
            for (String c : checks) {
                if (c != null && c.contains("|")) {
                    JOptionPane.showMessageDialog(this, "Pipe character '|' is not allowed in any field.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            AdministrativeStaff a = (AdministrativeStaff) currentUser;
            a.setEmail(newEmail);
            a.setFullName(newFullName);
            a.setPhoneNumber(newPhone);
            a.setPassword(newPassword);
        }

        DataManager.getInstance().updateUser(currentUser);
        if (Session.getCurrentUser() != null && Session.getCurrentUser().getId().equalsIgnoreCase(currentUser.getId())) {
            Session.setCurrentUser(currentUser);
        }

        if (onProfileUpdated != null) {
            onProfileUpdated.run();
        }

        JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
