package patients.ui;

import common.*;
import patients.ux.PatientPrescriptionFeedbackUX;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PatientPrescriptionFeedbackUI extends PatientPrescriptionFeedbackUX {
    private final List<String[]> patientAppointments = new ArrayList<>();

    public PatientPrescriptionFeedbackUI() {
        this(null);
    }

    public PatientPrescriptionFeedbackUI(String preselectedApptId) {
        super();
        backButton.addActionListener(e -> dispose());
        backButton1.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> refresh());
        refreshPrescriptionButton.addActionListener(e -> refresh());
        submitButton.addActionListener(e -> submit());

        prescriptionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePrescriptionSelection();
            }
        });
        prescriptionDetailButton.addActionListener(e -> openPrescriptionDetailDialog());

        appointmentBox.addActionListener(e -> updateSelectedAppointmentDetails());

        refresh();

        if (preselectedApptId != null && !preselectedApptId.trim().isEmpty()) {
            selectAppointment(preselectedApptId.trim());
            tabs.setSelectedIndex(1); // Switch to Ratings & Feedback tab
        }
    }

    private void updatePrescriptionSelection() {
        int row = prescriptionTable.getSelectedRow();
        if (row >= 0) {
            String rxId = prescriptionModel.getValueAt(row, 0).toString();
            String med = prescriptionModel.getValueAt(row, 4).toString();
            selectedPrescriptionLabel.setText("Selected: " + rxId + " (" + med + ")");
            prescriptionDetailButton.setEnabled(true);
        } else {
            selectedPrescriptionLabel.setText("Please select a prescription from the table.");
            prescriptionDetailButton.setEnabled(false);
        }
    }

    private void openPrescriptionDetailDialog() {
        int row = prescriptionTable.getSelectedRow();
        if (row < 0) return;

        String rxId = prescriptionModel.getValueAt(row, 0).toString();
        String patientId = prescriptionModel.getValueAt(row, 1).toString();
        String docId = prescriptionModel.getValueAt(row, 2).toString();
        String date = prescriptionModel.getValueAt(row, 3).toString();
        String medicine = prescriptionModel.getValueAt(row, 4).toString();
        String instructions = prescriptionModel.getValueAt(row, 5).toString();

        // Get Patient and Doctor display names
        String patientName = Session.getCurrentUser() != null ? Session.getCurrentUser().getFullName() : patientId;
        String docName = docId;
        String specialty = "Specialist Consultation";

        for (common.model.User u : DataManager.getInstance().getAllUsers()) {
            if (u.getId().equalsIgnoreCase(docId)) {
                docName = u.getFullName();
                if (u instanceof doctors.Doctor) {
                    specialty = ((doctors.Doctor) u).getSpecialty();
                }
            }
        }

        new common.ui.DetailDialogBuilder(this, "Digital Prescription Details - " + rxId)
                .setSize(650, 480)
                .setHeader("APU MEDICAL CENTRE - OFFICIAL DIGITAL PRESCRIPTION",
                        "Prescription ID: " + rxId + "   |   Date Issued: " + date)
                .setBorderTitle("Prescription & Dosage Information")
                .addField("Patient Name:", patientName + " (" + patientId + ")")
                .addField("Prescribing Doctor:", "Dr. " + docName + " (" + docId + ") - " + specialty)
                .addHighlightField("Prescribed Medicine:", medicine, new Color(30, 90, 180))
                .addTextAreaField("Intake Instructions:", instructions, 4)
                .show();
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

        // Load Appointment History for this patient
        patientAppointments.clear();
        appointmentBox.removeAllItems();

        for (String[] r : FileHandler.read(FileHandler.APPOINTMENTS)) {
            if (r.length >= 10 && r[1].equals(patient)) {
                patientAppointments.add(r);
                // Display: [ApptID] Date | DoctorName (Specialty) - Status
                String display = String.format("[%s] %s | Dr. %s (%s) - %s", r[0], r[5], r[3], r[4], r[8]);
                appointmentBox.addItem(display);
            }
        }

        if (patientAppointments.isEmpty()) {
            appointmentBox.addItem("-- No Appointment History Found --");
            doctorField.setText("No appointment history available");
            visitField.setText("N/A");
            submitButton.setEnabled(false);
        } else {
            submitButton.setEnabled(true);
            updateSelectedAppointmentDetails();
        }
    }

    private void updateSelectedAppointmentDetails() {
        int index = appointmentBox.getSelectedIndex();
        if (index >= 0 && index < patientAppointments.size()) {
            String[] appt = patientAppointments.get(index);
            // appt: 0:id, 1:patient, 2:docId, 3:docName, 4:specialty, 5:date, 6:time, 7:room, 8:status, 9:reason
            String docId = appt[2];
            String docName = appt[3];
            String specialty = appt[4];
            String apptId = appt[0];

            doctorField.setText(docId + " - Dr. " + docName + " (" + specialty + ")");
            visitField.setText(apptId);
        } else {
            doctorField.setText("");
            visitField.setText("");
        }
    }

    private void selectAppointment(String apptId) {
        for (int i = 0; i < patientAppointments.size(); i++) {
            if (patientAppointments.get(i)[0].equalsIgnoreCase(apptId)) {
                appointmentBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void submit() {
        if (patientAppointments.isEmpty() || appointmentBox.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this,
                    "You can only submit feedback for consultations in your appointment history.\nNo valid appointment history is available.",
                    "No Appointment Selected", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int index = appointmentBox.getSelectedIndex();
        if (index < 0 || index >= patientAppointments.size()) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from your history.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] selectedAppt = patientAppointments.get(index);
        String apptId = selectedAppt[0];
        String doctor = selectedAppt[2];
        String docName = selectedAppt[3];
        String rating = (String) ratingBox.getSelectedItem();
        String msg = feedbackArea.getText().trim();

        if (msg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your comments/feedback.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String patient = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "P001";
        String id = FileHandler.nextId(FileHandler.FEEDBACK, "F", 4);

        // Format: ID | PatientID | DoctorID | Date | Type | Rating | VisitRef | Message
        FileHandler.append(FileHandler.FEEDBACK, new String[]{
                id, patient, doctor, DataUtil.today(), "PATIENT_FEEDBACK", rating, apptId, msg
        });

        feedbackArea.setText("");
        ratingBox.setSelectedIndex(0);
        refresh();
        JOptionPane.showMessageDialog(this,
                "Thank you! Your feedback for appointment " + apptId + " with Dr. " + docName + " has been submitted successfully.",
                "Feedback Submitted", JOptionPane.INFORMATION_MESSAGE);
    }
}
