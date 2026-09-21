package doctors.ui;

import common.*;
import doctors.ux.PrescriptionUX;
import javax.swing.*;

public class PrescriptionUI extends PrescriptionUX {

    public PrescriptionUI() {
        super();
        initListeners();
        loadPatients();
        refresh();
    }

    private void initListeners() {
        backButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> save());
        clearButton.addActionListener(e -> clearForm());
        refreshButton.addActionListener(e -> {
            loadPatients();
            refresh();
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                populateFormFromTable(table.getSelectedRow());
            }
        });
    }

    private void loadPatients() {
        patientBox.removeAllItems();
        for (String[] r : FileHandler.read(FileHandler.USERS)) {
            if (r.length >= 5 && "PATIENT".equalsIgnoreCase(r[0])) {
                String patientId = r[1];
                String fullName = r[4];
                patientBox.addItem(patientId + " - " + fullName);
            }
        }
    }

    private String getSelectedPatientId() {
        String selected = (String) patientBox.getSelectedItem();
        if (selected == null || selected.trim().isEmpty()) return null;
        int dashIndex = selected.indexOf(" - ");
        if (dashIndex != -1) {
            return selected.substring(0, dashIndex).trim();
        }
        return selected.trim();
    }

    private void setSelectedPatientId(String patientId) {
        if (patientId == null) return;
        for (int i = 0; i < patientBox.getItemCount(); i++) {
            String item = patientBox.getItemAt(i);
            if (item.startsWith(patientId + " - ") || item.equalsIgnoreCase(patientId)) {
                patientBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void refresh() {
        model.setRowCount(0);
        String doctor = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "D001";
        for (String[] r : FileHandler.read(FileHandler.PRESCRIPTIONS)) {
            if (r.length >= 6 && r[2].equalsIgnoreCase(doctor)) {
                model.addRow(r);
            }
        }
    }

    private void populateFormFromTable(int row) {
        if (row < 0 || row >= model.getRowCount()) return;
        setSelectedPatientId(model.getValueAt(row, 1).toString());
        medicineField.setText(model.getValueAt(row, 4).toString());
        instructionsArea.setText(model.getValueAt(row, 5).toString());
    }

    private void clearForm() {
        if (patientBox.getItemCount() > 0) patientBox.setSelectedIndex(0);
        medicineField.setText("");
        instructionsArea.setText("");
        table.clearSelection();
    }

    private void save() {
        String patient = getSelectedPatientId();
        String med = medicineField.getText().trim();
        String ins = instructionsArea.getText().trim();

        if (patient == null || patient.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a registered patient from the dropdown.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!AuthService.userIdExists(patient, "PATIENT")) {
            JOptionPane.showMessageDialog(this, "Patient ID '" + patient + "' does not exist in the system.", "Patient Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (med.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the medication name and strength (e.g., Amlodipine 5mg).", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ins.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide dosage instructions for the patient.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String doctorId = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "D001";
        String id = FileHandler.nextId(FileHandler.PRESCRIPTIONS, "RX", 4);

        // Append to prescriptions.txt:
        // PRESCRIPTION_ID|PATIENT_ID|DOCTOR_ID|DATE|MEDICATION_NAME|DOSAGE_INSTRUCTIONS
        FileHandler.append(FileHandler.PRESCRIPTIONS, new String[]{
                id,
                patient,
                doctorId,
                DataUtil.today(),
                med,
                ins
        });

        clearForm();
        refresh();

        JOptionPane.showMessageDialog(this,
                "Prescription successfully issued!\n" +
                "• Prescription ID: " + id + "\n" +
                "• Medication: " + med + "\n" +
                "• Patient ID: " + patient,
                "Prescription Issued",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

