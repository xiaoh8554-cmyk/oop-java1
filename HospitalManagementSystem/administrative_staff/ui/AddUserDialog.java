package administrative_staff.ui;

import administrative_staff.AdministrativeStaff;
import common.DataManager;
import common.model.User;
import common.ui.DatePicker;
import doctors.Doctor;
import medical_manager.MedicalManager;
import patients.Patient;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AddUserDialog extends JDialog {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final JComboBox<String> roleBox = new JComboBox<>(new String[]{
            "PATIENT", "DOCTOR", "MEDICAL_MANAGER", "ADMINISTRATIVE_STAFF"
    });

    private final JTextField emailField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JCheckBox showPasswordCheck = new JCheckBox("Show Password");
    private final JTextField fullNameField = new JTextField(18);

    private final JComboBox<String> phoneCountryCodeBox = new JComboBox<>(User.COUNTRY_CODES);
    private final JTextField phoneField = new JTextField(12);

    // Patient Fields
    private final DatePicker dobPicker = new DatePicker();
    private final JComboBox<String> genderBox = new JComboBox<>(Patient.GENDER_OPTIONS);
    private final JComboBox<String> bloodGroupBox = new JComboBox<>(Patient.BLOOD_GROUP_OPTIONS);
    private final JComboBox<String> emergencyCountryCodeBox = new JComboBox<>(User.COUNTRY_CODES);
    private final JTextField emergencyContactField = new JTextField(12);
    private final JComboBox<String> emergencyRelationshipBox = new JComboBox<>(Patient.RELATIONSHIP_OPTIONS);
    private final JTextArea medicalHistoryArea = new JTextArea(3, 18);

    // Doctor Fields
    private final JTextField specialtyField = new JTextField(18);
    private final JTextField qualificationField = new JTextField(18);
    private final JComboBox<String> managerBox = new JComboBox<>();
    private final Map<String, String> managerIdMap = new HashMap<>();

    // Medical Manager Fields
    private final JTextField officeField = new JTextField(18);

    private final JPanel dynamicRolePanel = new JPanel(new GridBagLayout());
    private final JButton createButton = new JButton("Create User");
    private final JButton cancelButton = new JButton("Cancel");

    private final Runnable onUserCreated;
    private char defaultEchoChar;

    public AddUserDialog(Frame parent, Runnable onUserCreated) {
        super(parent, "Add New User", true);
        this.onUserCreated = onUserCreated;

        setSize(600, 680);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setNumericOnly(phoneField);
        setNumericOnly(emergencyContactField);

        defaultEchoChar = passwordField.getEchoChar();
        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultEchoChar);
            }
        });

        initUI();
        loadManagers();
        updateRoleFields();

        roleBox.addActionListener(e -> updateRoleFields());
        createButton.addActionListener(e -> createUser());
        cancelButton.addActionListener(e -> dispose());
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1, 3, 3));
        JLabel titleLabel = new JLabel("Register New System User", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel subtitleLabel = new JLabel("Select the user role and fill in the required profile information", SwingConstants.CENTER);
        subtitleLabel.setForeground(Color.GRAY);
        header.add(titleLabel);
        header.add(subtitleLabel);
        root.add(header, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 5, 4, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        JPanel phonePanel = new JPanel(new BorderLayout(5, 0));
        phoneCountryCodeBox.setPreferredSize(new Dimension(75, 26));
        phonePanel.add(phoneCountryCodeBox, BorderLayout.WEST);
        phonePanel.add(phoneField, BorderLayout.CENTER);

        int row = 0;
        addFormRow(formPanel, g, row++, "User Role:", roleBox);
        addFormRow(formPanel, g, row++, "Email / Gmail Address:", emailField);
        addFormRow(formPanel, g, row++, "Password:", passwordField);

        g.gridx = 1; g.gridy = row++; g.gridwidth = 1; g.weightx = 0.65; g.anchor = GridBagConstraints.WEST;
        formPanel.add(showPasswordCheck, g);

        addFormRow(formPanel, g, row++, "Full Name:", fullNameField);
        addFormRow(formPanel, g, row++, "Phone Number:", phonePanel);

        // Dynamic Role-Specific Container
        g.gridx = 0; g.gridy = row++; g.gridwidth = 2; g.weightx = 1.0;
        formPanel.add(dynamicRolePanel, g);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        root.add(scrollPane, BorderLayout.CENTER);

        // South Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);
        root.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadManagers() {
        managerBox.removeAllItems();
        managerIdMap.clear();
        managerBox.addItem("None");
        managerIdMap.put("None", "None");

        for (User u : DataManager.getInstance().getAllUsers()) {
            if (u instanceof MedicalManager) {
                MedicalManager mm = (MedicalManager) u;
                String display = mm.getFullName() + " (" + mm.getId() + " - Office: " + mm.getOfficeNumber() + ")";
                managerBox.addItem(display);
                managerIdMap.put(display, mm.getId());
            }
        }
    }

    private void updateRoleFields() {
        dynamicRolePanel.removeAll();
        dynamicRolePanel.setBorder(BorderFactory.createTitledBorder("Role Specific Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 5, 4, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        int r = 0;

        String selectedRole = (String) roleBox.getSelectedItem();
        if ("PATIENT".equals(selectedRole)) {
            addFormRow(dynamicRolePanel, g, r++, "Date of Birth (Birthday):", dobPicker);
            addFormRow(dynamicRolePanel, g, r++, "Gender:", genderBox);
            addFormRow(dynamicRolePanel, g, r++, "Blood Group:", bloodGroupBox);

            JPanel emergencyPhonePanel = new JPanel(new BorderLayout(5, 0));
            emergencyCountryCodeBox.setPreferredSize(new Dimension(75, 26));
            emergencyPhonePanel.add(emergencyCountryCodeBox, BorderLayout.WEST);
            emergencyPhonePanel.add(emergencyContactField, BorderLayout.CENTER);
            addFormRow(dynamicRolePanel, g, r++, "Emergency Contact:", emergencyPhonePanel);

            addFormRow(dynamicRolePanel, g, r++, "Emergency Relationship:", emergencyRelationshipBox);

            g.gridx = 0; g.gridy = r; g.gridwidth = 1; g.weightx = 0.35; g.anchor = GridBagConstraints.NORTHWEST;
            dynamicRolePanel.add(new JLabel("Medical History / Allergies:"), g);
            g.gridx = 1; g.weightx = 0.65;
            medicalHistoryArea.setLineWrap(true);
            medicalHistoryArea.setWrapStyleWord(true);
            dynamicRolePanel.add(new JScrollPane(medicalHistoryArea), g);
        } else if ("DOCTOR".equals(selectedRole)) {
            addFormRow(dynamicRolePanel, g, r++, "Specialty:", specialtyField);
            addFormRow(dynamicRolePanel, g, r++, "Qualification:", qualificationField);
            addFormRow(dynamicRolePanel, g, r++, "Assigned Manager:", managerBox);
            JLabel roomNote = new JLabel("Room is allocated by Admin via Asset Allocation.");
            roomNote.setForeground(new Color(20, 60, 140));
            addFormRow(dynamicRolePanel, g, r++, "Consultation Room:", roomNote);
        } else if ("MEDICAL_MANAGER".equals(selectedRole)) {
            addFormRow(dynamicRolePanel, g, r++, "Office Number:", officeField);
        } else if ("ADMINISTRATIVE_STAFF".equals(selectedRole)) {
            JLabel adminInfo = new JLabel("Administrative staff members have full administrative privileges.");
            adminInfo.setForeground(new Color(20, 60, 140));
            addFormRow(dynamicRolePanel, g, r++, "Note:", adminInfo);
        }

        dynamicRolePanel.revalidate();
        dynamicRolePanel.repaint();
    }

    private void createUser() {
        String role = (String) roleBox.getSelectedItem();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String fullName = fullNameField.getText().trim();
        String phoneCode = (String) phoneCountryCodeBox.getSelectedItem();
        String phoneDigits = phoneField.getText().trim();
        String phone = phoneDigits.isEmpty() ? "" : (phoneCode + phoneDigits);

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || phoneDigits.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All base fields (Email, Password, Full Name, Phone Number) are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Email address is already registered.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser;
        if ("PATIENT".equals(role)) {
            String dob = dobPicker.getDateString();
            String gender = (String) genderBox.getSelectedItem();
            String bloodGroup = (String) bloodGroupBox.getSelectedItem();
            String emergencyCode = (String) emergencyCountryCodeBox.getSelectedItem();
            String emergencyDigits = emergencyContactField.getText().trim();
            String emergencyContact = emergencyDigits.isEmpty() ? "" : (emergencyCode + emergencyDigits);
            String relChoice = (String) emergencyRelationshipBox.getSelectedItem();
            String history = medicalHistoryArea.getText().trim();

            if (dob.isEmpty() || emergencyDigits.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Date of Birth and Emergency Contact are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
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

            String id = DataManager.getInstance().generateNextPatientId();
            newUser = new Patient(id, email, password, fullName, phone, dob, gender, bloodGroup, emergencyContact, relChoice, history.isEmpty() ? "None" : history);
        } else if ("DOCTOR".equals(role)) {
            String specialty = specialtyField.getText().trim();
            String qualification = qualificationField.getText().trim();
            String mgrItem = (String) managerBox.getSelectedItem();
            String assignedManagerId = managerIdMap.getOrDefault(mgrItem, "None");

            if (specialty.isEmpty() || qualification.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Specialty and Qualification are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = DataManager.getInstance().generateNextId("D", 3);
            newUser = new Doctor(id, email, password, fullName, phone, specialty, qualification, assignedManagerId);
        } else if ("MEDICAL_MANAGER".equals(role)) {
            String office = officeField.getText().trim();

            if (office.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Office Number is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = DataManager.getInstance().generateNextId("M", 3);
            newUser = new MedicalManager(id, email, password, fullName, phone, office);
        } else {
            String id = DataManager.getInstance().generateNextId("A", 3);
            newUser = new AdministrativeStaff(id, email, password, fullName, phone);
        }

        DataManager.getInstance().addUser(newUser);

        if (onUserCreated != null) {
            onUserCreated.run();
        }

        JOptionPane.showMessageDialog(this,
                "User created successfully!\nAssigned ID: " + newUser.getId() + "\nRole: " + newUser.getRole().name() + "\nName: " + newUser.getFullName(),
                "User Created",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
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
        g.gridx = 0; g.gridy = row; g.gridwidth = 1; g.weightx = 0.35; g.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 0.65;
        panel.add(comp, g);
    }
}
