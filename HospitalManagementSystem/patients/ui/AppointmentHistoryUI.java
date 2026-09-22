package patients.ui;

import common.FileHandler;
import common.Session;
import patients.ux.AppointmentHistoryUX;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentHistoryUI extends AppointmentHistoryUX {
    private final List<String[]> allPatientAppointments = new ArrayList<>();
    private final java.util.Set<String> specialties = new java.util.TreeSet<>();
    private String targetPatientId = null;

    public AppointmentHistoryUI() {
        this(null);
    }

    public AppointmentHistoryUI(String targetPatientId) {
        super();
        this.targetPatientId = targetPatientId;
        if (targetPatientId != null && !targetPatientId.trim().isEmpty()) {
            setTitle("Appointment History & Rescheduling - Patient ID: " + targetPatientId.trim());
        }

        backButton.addActionListener(e -> dispose());
        Runnable openBooking = () -> {
            new AppointmentBookingUI().setVisible(true);
            dispose();
        };
        bookNewButton.addActionListener(e -> openBooking.run());

        refreshHistoryButton.addActionListener(e -> refreshHistory());
        rescheduleButton.addActionListener(e -> rescheduleAppointment());
        cancelButton.addActionListener(e -> cancelAppointment());
        feedbackButton.addActionListener(e -> leaveFeedback());

        // Filter listeners
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
        });
        specialtyFilterBox.addActionListener(e -> applyFilters());
        statusFilterBox.addActionListener(e -> applyFilters());
        clearFiltersButton.addActionListener(e -> clearFilters());

        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateHistorySelection();
            }
        });

        refreshHistory();
    }

    private void leaveFeedback() {
        int row = historyTable.getSelectedRow();
        if (row >= 0) {
            String status = historyModel.getValueAt(row, 7).toString();
            if (!"COMPLETED".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this,
                        "Feedback can only be submitted for consultations that are COMPLETED (Selected status: " + status + ").",
                        "Completed Status Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String apptId = historyModel.getValueAt(row, 0).toString();
            new PatientPrescriptionFeedbackUI(apptId).setVisible(true);
        } else {
            new PatientPrescriptionFeedbackUI().setVisible(true);
        }
    }

    public void refreshHistory() {
        allPatientAppointments.clear();
        specialties.clear();
        specialties.add("All Specialties");

        String patientId = (targetPatientId != null && !targetPatientId.trim().isEmpty())
                ? targetPatientId.trim()
                : (Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "P001");

        for (String[] r : FileHandler.read(FileHandler.APPOINTMENTS)) {
            if (r.length >= 10 && r[1].equals(patientId)) {
                allPatientAppointments.add(r);
                if (r[4] != null && !r[4].trim().isEmpty()) {
                    specialties.add(r[4].trim());
                }
            }
        }

        // Update specialty filter items
        String currentSpec = (String) specialtyFilterBox.getSelectedItem();
        specialtyFilterBox.removeAllItems();
        for (String s : specialties) {
            specialtyFilterBox.addItem(s);
        }
        if (currentSpec != null && specialties.contains(currentSpec)) {
            specialtyFilterBox.setSelectedItem(currentSpec);
        }

        applyFilters();
    }

    private void clearFilters() {
        searchField.setText("");
        if (specialtyFilterBox.getItemCount() > 0) specialtyFilterBox.setSelectedIndex(0);
        statusFilterBox.setSelectedIndex(0);
        applyFilters();
    }

    private void applyFilters() {
        historyModel.setRowCount(0);
        String query = searchField.getText().trim().toLowerCase();
        String selectedSpec = (String) specialtyFilterBox.getSelectedItem();
        if (selectedSpec == null) selectedSpec = "All Specialties";
        String selectedStatus = (String) statusFilterBox.getSelectedItem();
        if (selectedStatus == null) selectedStatus = "All Statuses";

        for (String[] r : allPatientAppointments) {
            // r: 0:apptId, 1:patientId, 2:docId, 3:docName, 4:specialty, 5:date, 6:time, 7:room, 8:status, 9:reason
            String apptId = r[0].toLowerCase();
            String docId = r[2].toLowerCase();
            String docName = r[3].toLowerCase();
            String specialty = r[4];
            String status = r[8];

            // Filter by search text (Dr Name or Appt ID or Doc ID)
            boolean matchesSearch = query.isEmpty()
                    || docName.contains(query)
                    || apptId.contains(query)
                    || docId.contains(query);

            // Filter by specialty
            boolean matchesSpec = selectedSpec.equals("All Specialties")
                    || specialty.equalsIgnoreCase(selectedSpec);

            // Filter by status
            boolean matchesStatus = selectedStatus.equals("All Statuses")
                    || status.equalsIgnoreCase(selectedStatus);

            if (matchesSearch && matchesSpec && matchesStatus) {
                historyModel.addRow(new Object[]{
                        r[0], r[2], r[3], r[4], r[5], r[6], r[7], r[8], r[9]
                });
            }
        }
        updateHistorySelection();
    }

    private void updateHistorySelection() {
        int row = historyTable.getSelectedRow();
        if (row >= 0) {
            String apptId = historyModel.getValueAt(row, 0).toString();
            String docName = historyModel.getValueAt(row, 2).toString();
            String date = historyModel.getValueAt(row, 4).toString();
            String time = historyModel.getValueAt(row, 5).toString();
            String status = historyModel.getValueAt(row, 7).toString();

            selectedHistoryLabel.setText("Selected: " + apptId + " (" + docName + " - " + date + " " + time + " | " + status + ")");
            rescheduleDatePicker.setDateString(date);
        } else {
            selectedHistoryLabel.setText("Selected Appointment: (None selected)");
        }
    }

    private void rescheduleAppointment() {
        int row = historyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an appointment from your history table to reschedule.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apptId = historyModel.getValueAt(row, 0).toString();
        String docId = historyModel.getValueAt(row, 1).toString();
        String docName = historyModel.getValueAt(row, 2).toString();
        String specialty = historyModel.getValueAt(row, 3).toString();
        String room = historyModel.getValueAt(row, 6).toString();
        String currentStatus = historyModel.getValueAt(row, 7).toString();
        String originalReason = historyModel.getValueAt(row, 8).toString();

        if ("CANCELLED".equalsIgnoreCase(currentStatus) || "COMPLETED".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Cannot reschedule an appointment that is already " + currentStatus + ".", "Invalid Action", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate newLocalDate = rescheduleDatePicker.getSelectedDate();
        String newDateStr = rescheduleDatePicker.getDateString();

        if (newLocalDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Cannot reschedule to a past date (" + newDateStr + ").\nPlease select today or a future date.", "Past Date Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Delegate to AppointmentRescheduleUI
        AppointmentRescheduleUI.openRescheduleTimeDialog(
                this,
                apptId,
                docId,
                docName,
                specialty,
                room,
                originalReason,
                newDateStr,
                newLocalDate,
                this::refreshHistory
        );
    }

    private void cancelAppointment() {
        int row = historyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an appointment from your history table to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apptId = historyModel.getValueAt(row, 0).toString();
        String currentStatus = historyModel.getValueAt(row, 7).toString();

        if ("CANCELLED".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This appointment is already cancelled.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if ("COMPLETED".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "Cannot cancel a completed appointment.", "Invalid Action", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel appointment " + apptId + "?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<String[]> appts = new ArrayList<>(FileHandler.read(FileHandler.APPOINTMENTS));
            for (String[] r : appts) {
                if (r.length >= 10 && r[0].equals(apptId)) {
                    r[8] = "CANCELLED";
                    break;
                }
            }
            FileHandler.writeAll(FileHandler.APPOINTMENTS, appts);

            refreshHistory();
            JOptionPane.showMessageDialog(this, "Appointment " + apptId + " has been cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
