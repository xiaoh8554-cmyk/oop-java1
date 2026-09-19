package patients.ui;

import common.*;
import patients.ux.PatientPrescriptionFeedbackUX;

import javax.swing.*;

public class PatientPrescriptionFeedbackUI extends PatientPrescriptionFeedbackUX {
    public PatientPrescriptionFeedbackUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> refresh());
        submitButton.addActionListener(e -> submit());
        refresh();
    }

    private void refresh() {
        String patient = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "P001";

        // Load Prescriptions
        prescriptionModel.setRowCount(0);
        for (String[] r : FileHandler.read(FileHandler.PRESCRIPTIONS)) {
            if (r.length >= 6 && r[1].equals(patient)) {
                prescriptionModel.addRow(r);
            }
        }

        // Load Feedback
        feedbackModel.setRowCount(0);
        for (String[] r : FileHandler.read(FileHandler.FEEDBACK)) {
            if (r.length >= 8 && r[1].equals(patient)) {
                // ID, Patient, Doctor, Date, Rating, VisitRef, Message
                feedbackModel.addRow(new Object[]{r[0], r[1], r[2], r[3], r[5], r[6], r[7]});
            } else if (r.length >= 6 && r[1].equals(patient)) {
                // Backward-compatibility for 6-part rows
                feedbackModel.addRow(new Object[]{r[0], r[1], r[2], r[3], "N/A", "General Visit", r[5]});
            }
        }
    }

    private void submit() {
        String doctor = doctorField.getText().trim();
        String rating = (String) ratingBox.getSelectedItem();
        String visitRef = visitField.getText().trim();
        String msg = feedbackArea.getText().trim();

        if (doctor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Doctor ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!AuthService.userIdExists(doctor, "DOCTOR")) {
            JOptionPane.showMessageDialog(this, "Doctor ID does not exist in the system.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (msg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your comments/feedback.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (visitRef.isEmpty()) {
            visitRef = "General Visit";
        }

        String patient = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "P001";
        String id = FileHandler.nextId(FileHandler.FEEDBACK, "F", 4);

        // Format: ID | PatientID | DoctorID | Date | Type | Rating | VisitRef | Message
        FileHandler.append(FileHandler.FEEDBACK, new String[]{
                id, patient, doctor, DataUtil.today(), "PATIENT_FEEDBACK", rating, visitRef, msg
        });

        doctorField.setText("");
        visitField.setText("");
        feedbackArea.setText("");
        ratingBox.setSelectedIndex(0);
        refresh();
        JOptionPane.showMessageDialog(this, "Thank you! Your rating and feedback have been submitted successfully.");
    }
}
