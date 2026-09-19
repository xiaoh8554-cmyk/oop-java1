package patients.ui;

import common.FileHandler;
import common.Session;
import patients.ux.AppointmentHistoryUX;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentHistoryUI extends AppointmentHistoryUX {

    public AppointmentHistoryUI() {
        super();

        backButton.addActionListener(e -> dispose());
        bookNewButton.addActionListener(e -> {
            new AppointmentBookingUI().setVisible(true);
            dispose();
        });

        refreshHistoryButton.addActionListener(e -> refreshHistory());
        rescheduleButton.addActionListener(e -> rescheduleAppointment());
        cancelButton.addActionListener(e -> cancelAppointment());

        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateHistorySelection();
            }
        });

        refreshHistory();
    }

    public void refreshHistory() {
        historyModel.setRowCount(0);
        String patientId = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "P001";

        for (String[] r : FileHandler.read(FileHandler.APPOINTMENTS)) {
            if (r.length >= 10 && r[1].equals(patientId)) {
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
