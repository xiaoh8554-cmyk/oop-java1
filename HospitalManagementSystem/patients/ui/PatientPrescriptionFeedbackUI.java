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

        JDialog dialog = new JDialog(this, "Digital Prescription Details - " + rxId, true);
        dialog.setSize(650, 480);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 210, 245), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        header.setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("APU MEDICAL CENTRE - OFFICIAL DIGITAL PRESCRIPTION");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(new Color(20, 60, 140));

        JLabel meta = new JLabel("Prescription ID: " + rxId + "   |   Date Issued: " + date);
        meta.setFont(new Font("SansSerif", Font.PLAIN, 12));

        header.add(title);
        header.add(meta);
        content.add(header, BorderLayout.NORTH);

        // Body Info
        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(BorderFactory.createTitledBorder("Prescription & Dosage Information"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new java.awt.Insets(6, 10, 6, 10);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        g.gridx = 0; g.gridy = y;
        body.add(new JLabel("Patient Name:"), g);
        g.gridx = 1;
        body.add(new JLabel(patientName + " (" + patientId + ")"), g);

        y++;
        g.gridx = 0; g.gridy = y;
        body.add(new JLabel("Prescribing Doctor:"), g);
        g.gridx = 1;
        body.add(new JLabel("Dr. " + docName + " (" + docId + ") - " + specialty), g);

        y++;
        g.gridx = 0; g.gridy = y;
        body.add(new JLabel("Prescribed Medicine:"), g);
        g.gridx = 1;
        JLabel medLabel = new JLabel(medicine);
        medLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        medLabel.setForeground(new Color(30, 90, 180));
        body.add(medLabel, g);

        y++;
        g.gridx = 0; g.gridy = y; g.anchor = GridBagConstraints.NORTHWEST;
        body.add(new JLabel("Intake Instructions:"), g);
        g.gridx = 1;
        JTextArea instrArea = new JTextArea(instructions, 4, 30);
        instrArea.setLineWrap(true);
        instrArea.setWrapStyleWord(true);
        instrArea.setEditable(false);
        instrArea.setBackground(new java.awt.Color(250, 250, 250));
        instrArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        body.add(new JScrollPane(instrArea), g);

        content.add(body, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        south.add(closeBtn);
        content.add(south, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
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
