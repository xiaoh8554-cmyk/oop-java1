package patients.ui;

import common.DataManager;
import common.FileHandler;
import common.Session;
import common.model.User;
import common.model.UserRole;
import doctors.Doctor;
import patients.ux.AppointmentBookingUX;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TreeSet;

public class AppointmentBookingUI extends AppointmentBookingUX {
    private static final String[] HALF_HOUR_SLOTS = {
            "10:00 - 10:30", "10:30 - 11:00", "11:00 - 11:30", "11:30 - 12:00",
            "12:00 - 12:30", "12:30 - 13:00", "13:00 - 13:30", "13:30 - 14:00",
            "14:00 - 14:30", "14:30 - 15:00", "15:00 - 15:30", "15:30 - 16:00",
            "16:00 - 16:30", "16:30 - 17:00", "17:00 - 17:30", "17:30 - 18:00"
    };

    private static final int MAX_PATIENTS_PER_SLOT = 2;
    private final Set<String> specialties = new TreeSet<>();
    private javax.swing.Timer clockTimer;

    public AppointmentBookingUI() {
        super();

        backButton.addActionListener(e -> {
            if (clockTimer != null) clockTimer.stop();
            new AppointmentHistoryUI().setVisible(true);
            dispose();
        });

        viewHistoryButton.addActionListener(e -> {
            if (clockTimer != null) clockTimer.stop();
            new AppointmentHistoryUI().setVisible(true);
            dispose();
        });

        // Live Clock Timer (updates time beside refresh button every second)
        updateClock();
        clockTimer = new javax.swing.Timer(1000, e -> updateClock());
        clockTimer.start();

        // Filter listeners
        loadSpecialties();
        specialtyFilterBox.addActionListener(e -> filterDoctors());
        bookingDatePicker.setOnDateChanged(this::filterDoctors);
        refreshDoctorsButton.addActionListener(e -> {
            loadSpecialties();
            filterDoctors();
            updateClock();
        });

        // Doctor table selection listener to enable/disable "Detail" button
        doctorsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDoctorSelection();
            }
        });

        // Detail and View Feedback button actions
        detailButton.addActionListener(e -> openTimeSelectionDialog());
        viewFeedbackButton.addActionListener(e -> openDoctorFeedbackDialog());

        filterDoctors();
    }

    private void updateClock() {
        String currentTimeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        liveClockLabel.setText("Current Time: " + currentTimeStr);
    }

    private void loadSpecialties() {
        String current = (String) specialtyFilterBox.getSelectedItem();
        specialties.clear();
        specialties.add("All Specialties");

        for (String[] dep : FileHandler.read(FileHandler.DEPARTMENTS)) {
            if (dep.length >= 2 && !dep[1].trim().isEmpty()) {
                specialties.add(dep[1].trim());
            }
        }

        for (User u : DataManager.getInstance().getAllUsers()) {
            if (u.getRole() == UserRole.DOCTOR && u instanceof Doctor) {
                Doctor d = (Doctor) u;
                if (d.getSpecialty() != null && !d.getSpecialty().trim().isEmpty()) {
                    specialties.add(d.getSpecialty().trim());
                }
            }
        }

        specialtyFilterBox.removeAllItems();
        for (String s : specialties) {
            specialtyFilterBox.addItem(s);
        }

        if (current != null && specialties.contains(current)) {
            specialtyFilterBox.setSelectedItem(current);
        }
    }

    private void filterDoctors() {
        doctorsModel.setRowCount(0);
        String selectedSpec = (String) specialtyFilterBox.getSelectedItem();
        if (selectedSpec == null) selectedSpec = "All Specialties";

        for (User u : DataManager.getInstance().getAllUsers()) {
            if (u.getRole() == UserRole.DOCTOR && u instanceof Doctor) {
                Doctor d = (Doctor) u;
                java.util.List<String> docDepts = DataManager.getInstance().getDepartmentNamesForDoctor(d.getId());
                if (docDepts.isEmpty() && d.getSpecialty() != null && !d.getSpecialty().trim().isEmpty()) {
                    docDepts.add(d.getSpecialty().trim());
                }

                boolean matches = selectedSpec.equals("All Specialties");
                if (!matches) {
                    for (String deptName : docDepts) {
                        if (deptName.equalsIgnoreCase(selectedSpec)) {
                            matches = true;
                            break;
                        }
                    }
                    if (!matches && d.getSpecialty() != null && d.getSpecialty().equalsIgnoreCase(selectedSpec)) {
                        matches = true;
                    }
                }

                if (matches) {
                    String displayDept = !docDepts.isEmpty() ? String.join(", ", docDepts) : d.getSpecialty();
                    doctorsModel.addRow(new Object[]{
                            d.getId(),
                            d.getFullName(),
                            displayDept,
                            d.getRoomNumber(),
                            d.getQualification()
                    });
                }
            }
        }
        updateDoctorSelection();
    }

    private void updateDoctorSelection() {
        int row = doctorsTable.getSelectedRow();
        if (row >= 0) {
            String docName = doctorsModel.getValueAt(row, 1).toString();
            String spec = doctorsModel.getValueAt(row, 2).toString();
            String room = doctorsModel.getValueAt(row, 3).toString();
            String date = bookingDatePicker.getDateString();

            selectedDoctorInfoLabel.setText("Selected: Dr. " + docName + " (" + spec + " | Room: " + room + ") on Date: " + date);
            detailButton.setEnabled(true);
            viewFeedbackButton.setEnabled(true);
        } else {
            selectedDoctorInfoLabel.setText("Please select a specialty, date, and doctor from the table.");
            detailButton.setEnabled(false);
            viewFeedbackButton.setEnabled(false);
        }
    }

    private void openDoctorFeedbackDialog() {
        int row = doctorsTable.getSelectedRow();
        if (row < 0) return;

        String docId = doctorsModel.getValueAt(row, 0).toString();
        String docName = doctorsModel.getValueAt(row, 1).toString();
        String spec = doctorsModel.getValueAt(row, 2).toString();
        String room = doctorsModel.getValueAt(row, 3).toString();
        String qual = doctorsModel.getValueAt(row, 4).toString();

        // Load feedback for this doctor
        DefaultTableModel fbModel = new DefaultTableModel(
                new String[]{"Date", "Rating", "Appt / Visit Ref", "Patient Comments & Feedback"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        double totalStars = 0;
        int ratingCount = 0;

        for (String[] r : FileHandler.read(FileHandler.FEEDBACK)) {
            if (r.length >= 8 && r[2].equalsIgnoreCase(docId)) {
                String date = r[3];
                String rating = r[5];
                String visitRef = r[6];
                String msg = r[7];
                fbModel.addRow(new Object[]{date, rating, visitRef, msg});

                int stars = parseStars(rating);
                if (stars > 0) {
                    totalStars += stars;
                    ratingCount++;
                }
            } else if (r.length >= 6 && r[2].equalsIgnoreCase(docId)) {
                String date = r[3];
                String msg = r[5];
                fbModel.addRow(new Object[]{date, "N/A", "General Visit", msg});
            }
        }

        JDialog dialog = new JDialog(this, "Doctor Feedback & Patient Reviews - Dr. " + docName, true);
        dialog.setSize(800, 520);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header Info
        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 245), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        header.setBackground(new Color(245, 248, 255));

        JLabel titleLabel = new JLabel("Dr. " + docName + " (" + docId + ")  |  Specialty: " + spec + "  |  Room: " + room);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLabel.setForeground(new Color(25, 60, 130));

        String avgRatingStr = ratingCount > 0
                ? String.format("Overall Rating: \u2605 %.1f / 5.0  (%d patient reviews)  |  Qualification: %s", (totalStars / ratingCount), ratingCount, qual)
                : "Overall Rating: No patient ratings yet  |  Qualification: " + qual;
        JLabel ratingLabel = new JLabel(avgRatingStr);
        ratingLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        header.add(titleLabel);
        header.add(ratingLabel);
        content.add(header, BorderLayout.NORTH);

        // Feedback table
        JTable fbTable = new JTable(fbModel);
        fbTable.setRowHeight(24);
        fbTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        fbTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        fbTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        fbTable.getColumnModel().getColumn(3).setPreferredWidth(380);

        if (fbModel.getRowCount() == 0) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            JLabel emptyLabel = new JLabel("No feedback has been submitted for Dr. " + docName + " yet.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            content.add(emptyPanel, BorderLayout.CENTER);
        } else {
            content.add(new JScrollPane(fbTable), BorderLayout.CENTER);
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(closeBtn);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }

    private int parseStars(String ratingStr) {
        if (ratingStr == null) return 0;
        if (ratingStr.startsWith("5")) return 5;
        if (ratingStr.startsWith("4")) return 4;
        if (ratingStr.startsWith("3")) return 3;
        if (ratingStr.startsWith("2")) return 2;
        if (ratingStr.startsWith("1")) return 1;
        return 0;
    }

    private boolean isSlotPassed(LocalDate selectedDate, String timeSlot) {
        LocalDate today = LocalDate.now();
        if (selectedDate.isBefore(today)) {
            return true; // Past date
        }
        if (selectedDate.isEqual(today)) {
            try {
                String startTimeStr = timeSlot.split("-")[0].trim();
                LocalTime slotStart = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                return LocalTime.now().isAfter(slotStart);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private int getBookedCountForSlot(String doctorId, String date, String timeSlot) {
        int count = 0;
        for (String[] appt : FileHandler.read(FileHandler.APPOINTMENTS)) {
            if (appt.length >= 9) {
                String doc = appt[2].trim();
                String apptDate = appt[5].trim();
                String apptSlot = appt[6].trim();
                String status = appt[8].trim();

                if (doc.equals(doctorId) && apptDate.equals(date) && apptSlot.equalsIgnoreCase(timeSlot)) {
                    if (!"CANCELLED".equalsIgnoreCase(status)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void openTimeSelectionDialog() {
        int row = doctorsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String doctorId = doctorsModel.getValueAt(row, 0).toString();
        String doctorName = doctorsModel.getValueAt(row, 1).toString();
        String specialty = doctorsModel.getValueAt(row, 2).toString();
        String room = doctorsModel.getValueAt(row, 3).toString();
        LocalDate selectedLocalDate = bookingDatePicker.getSelectedDate();
        String selectedDate = bookingDatePicker.getDateString();

        JDialog dialog = new JDialog(this, "Select Consultation Time - Dr. " + doctorName, true);
        dialog.setSize(750, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(12, 12));

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 6, 15));
        JLabel titleLabel = new JLabel("Available Time Slots (10:00 AM - 6:00 PM)  [Max 2 Patients per Slot]", SwingConstants.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel infoLabel = new JLabel("Doctor: Dr. " + doctorName + " (" + doctorId + ")  |  Specialty: " + specialty + "  |  Room: " + room + "  |  Date: " + selectedDate);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(40, 70, 140));
        headerPanel.add(titleLabel);
        headerPanel.add(infoLabel);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Center: Time Slots Table
        DefaultTableModel slotModel = new DefaultTableModel(
                new String[]{"No.", "Time Slot (Half Hour)", "Remaining Patient Capacity", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable slotTable = new JTable(slotModel);
        slotTable.setRowHeight(24);

        for (int i = 0; i < HALF_HOUR_SLOTS.length; i++) {
            String slot = HALF_HOUR_SLOTS[i];
            boolean passed = isSlotPassed(selectedLocalDate, slot);

            if (passed) {
                slotModel.addRow(new Object[]{i + 1, slot, "0 / " + MAX_PATIENTS_PER_SLOT + " (PASSED)", "PASSED"});
            } else {
                int booked = getBookedCountForSlot(doctorId, selectedDate, slot);
                int remaining = Math.max(0, MAX_PATIENTS_PER_SLOT - booked);

                String remainStr = remaining + " / " + MAX_PATIENTS_PER_SLOT + " slots left";
                String statusStr;
                if (remaining == MAX_PATIENTS_PER_SLOT) {
                    statusStr = "AVAILABLE (2 Open)";
                } else if (remaining > 0) {
                    statusStr = "AVAILABLE (1 Open)";
                } else {
                    remainStr = "0 / " + MAX_PATIENTS_PER_SLOT + " (FULL)";
                    statusStr = "FULL / BOOKED";
                }
                slotModel.addRow(new Object[]{i + 1, slot, remainStr, statusStr});
            }
        }

        // Custom renderer for Status and Remaining Slots colors
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);
                if (isSelected) {
                    return comp;
                }
                Object statusObj = table.getValueAt(r, 3);
                String status = (statusObj != null) ? statusObj.toString() : "";

                if (status.startsWith("AVAILABLE")) {
                    comp.setForeground(new Color(0, 130, 40));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (status.contains("FULL") || status.contains("BOOKED")) {
                    comp.setForeground(new Color(210, 0, 0));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (status.contains("PASSED")) {
                    comp.setForeground(Color.GRAY);
                    setFont(getFont().deriveFont(Font.ITALIC));
                } else {
                    comp.setForeground(Color.BLACK);
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
                return comp;
            }
        };

        slotTable.getColumnModel().getColumn(2).setCellRenderer(statusRenderer);
        slotTable.getColumnModel().getColumn(3).setCellRenderer(statusRenderer);

        dialog.add(new JScrollPane(slotTable), BorderLayout.CENTER);

        // South Panel: Emergency Button, Reason Field, and Actions
        JPanel southPanel = new JPanel(new BorderLayout(8, 8));
        southPanel.setBorder(BorderFactory.createEmptyBorder(6, 15, 12, 15));

        JLabel emergencyBanner = new JLabel("Normal Booking Mode (Select an open 30-min slot with available capacity)", SwingConstants.CENTER);
        emergencyBanner.setFont(new Font("SansSerif", Font.BOLD, 13));
        emergencyBanner.setOpaque(true);
        emergencyBanner.setBackground(new Color(235, 242, 250));
        emergencyBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230)),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        java.util.List<String> docDepts = DataManager.getInstance().getDepartmentNamesForDoctor(doctorId);
        if (docDepts.isEmpty() && specialty != null && !specialty.trim().isEmpty()) {
            for (String s : specialty.split(",")) {
                if (!s.trim().isEmpty()) docDepts.add(s.trim());
            }
        }
        if (docDepts.isEmpty()) docDepts.add("General Practice");

        JComboBox<String> deptChoiceBox = new JComboBox<>(docDepts.toArray(new String[0]));
        String activeFilter = (String) specialtyFilterBox.getSelectedItem();
        if (activeFilter != null && docDepts.contains(activeFilter)) {
            deptChoiceBox.setSelectedItem(activeFilter);
        }

        g.gridx = 0; g.gridy = 0;
        formPanel.add(new JLabel("Consultation Department:"), g);
        g.gridx = 1; g.weightx = 1.0;
        formPanel.add(deptChoiceBox, g);

        g.gridx = 0; g.gridy = 1;
        formPanel.add(new JLabel("Reason / Symptoms:"), g);
        g.gridx = 1; g.weightx = 1.0;
        JTextField reasonInput = new JTextField();
        formPanel.add(reasonInput, g);

        JPanel actionButtonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton emergencyButton = new JButton("\u26A0 Emergency (Urgent Booking)");
        emergencyButton.setBackground(new Color(255, 220, 220));
        emergencyButton.setForeground(new Color(180, 0, 0));
        emergencyButton.setFont(new Font("SansSerif", Font.BOLD, 12));

        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        JButton cancelButton = new JButton("Cancel");

        actionButtonBar.add(emergencyButton);
        actionButtonBar.add(confirmButton);
        actionButtonBar.add(cancelButton);

        final boolean[] isEmergency = {false};

        emergencyButton.addActionListener(ev -> {
            isEmergency[0] = !isEmergency[0];
            if (isEmergency[0]) {
                emergencyBanner.setText("\u26A0 EMERGENCY MODE ACTIVE: Time Slot set to URGENT (Immediate Priority Queue)");
                emergencyBanner.setBackground(new Color(255, 210, 210));
                emergencyBanner.setForeground(new Color(180, 0, 0));
                emergencyButton.setText("Switch back to Normal Slot");
                emergencyButton.setBackground(new Color(220, 235, 255));
                emergencyButton.setForeground(Color.BLUE);
                if (reasonInput.getText().trim().isEmpty()) {
                    reasonInput.setText("[EMERGENCY] Urgent consultation required");
                }
            } else {
                emergencyBanner.setText("Normal Booking Mode (Select an open 30-min slot with available capacity)");
                emergencyBanner.setBackground(new Color(235, 242, 250));
                emergencyBanner.setForeground(Color.BLACK);
                emergencyButton.setText("\u26A0 Emergency (Urgent Booking)");
                emergencyButton.setBackground(new Color(255, 220, 220));
                emergencyButton.setForeground(new Color(180, 0, 0));
            }
        });

        confirmButton.addActionListener(ev -> {
            String selectedSlot;
            String status;

            if (isEmergency[0]) {
                if (selectedLocalDate.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(dialog, "Cannot make an emergency booking for a date in the past. Please select today or a future date.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                selectedSlot = "URGENT (Immediate Queue)";
                status = "URGENT";
            } else {
                int slotRow = slotTable.getSelectedRow();
                if (slotRow < 0) {
                    JOptionPane.showMessageDialog(dialog, "Please select an available time slot from the table, or click 'Emergency'.", "No Time Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String slotStatus = slotModel.getValueAt(slotRow, 3).toString();

                if ("PASSED".equalsIgnoreCase(slotStatus)) {
                    JOptionPane.showMessageDialog(dialog, "Cannot make an appointment for a time slot that has already passed.\nPlease choose an upcoming time slot or a future date.", "Time Slot Passed", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (slotStatus.contains("FULL") || slotStatus.contains("BOOKED")) {
                    JOptionPane.showMessageDialog(dialog, "This time slot is full (maximum 2 patients already booked).\nPlease choose another time slot with remaining capacity or select Emergency.", "Time Slot Full", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                selectedSlot = slotModel.getValueAt(slotRow, 1).toString();
                status = "BOOKED";
            }

            String reason = reasonInput.getText().trim();
            if (reason.isEmpty()) {
                reason = isEmergency[0] ? "[EMERGENCY] Urgent Medical Care" : "General Medical Consultation";
            }

            String patientId = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "P001";
            String apptId = FileHandler.nextAppointmentId(selectedDate);
            String chosenDept = (String) deptChoiceBox.getSelectedItem();
            if (chosenDept == null || chosenDept.trim().isEmpty()) {
                chosenDept = specialty;
            }

            // Save to appointments.txt
            FileHandler.append(FileHandler.APPOINTMENTS, new String[]{
                    apptId, patientId, doctorId, doctorName, chosenDept, selectedDate, selectedSlot, room, status, reason
            });

            dialog.dispose();

            String msg = "Appointment booked successfully!\n\n" +
                    "Appointment ID: " + apptId + "\n" +
                    "Doctor: Dr. " + doctorName + " (" + chosenDept + ")\n" +
                    "Consultation Room: " + room + "\n" +
                    "Date: " + selectedDate + "\n" +
                    "Time: " + selectedSlot + "\n" +
                    "Status: " + status;
            JOptionPane.showMessageDialog(this, msg, "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);

            // Open appointment history
            if (clockTimer != null) clockTimer.stop();
            new AppointmentHistoryUI().setVisible(true);
            dispose();
        });

        cancelButton.addActionListener(ev -> dialog.dispose());

        JPanel centerSouth = new JPanel(new BorderLayout(6, 6));
        centerSouth.add(emergencyBanner, BorderLayout.NORTH);
        centerSouth.add(formPanel, BorderLayout.CENTER);

        southPanel.add(centerSouth, BorderLayout.CENTER);
        southPanel.add(actionButtonBar, BorderLayout.SOUTH);

        dialog.add(southPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
