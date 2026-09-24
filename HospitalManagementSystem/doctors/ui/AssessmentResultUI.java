package doctors.ui;

import common.*;
import doctors.ux.AssessmentResultUX;
import javax.swing.*;
import java.util.*;

public class AssessmentResultUI extends AssessmentResultUX {
    private final Map<String, String[]> typeMap = new LinkedHashMap<>();

    public AssessmentResultUI() {
        super();
        initListeners();
        loadPatients();
        loadTypes();
        refresh();
    }

    private void initListeners() {
        backButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> save());
        updateButton.addActionListener(e -> updateSelectedAssessment());
        deleteButton.addActionListener(e -> deleteSelectedAssessment());
        clearButton.addActionListener(e -> clearForm());
        refreshButton.addActionListener(e -> {
            loadPatients();
            loadTypes();
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

    private void loadTypes() {
        typeBox.removeAllItems();
        typeMap.clear();
        for (String[] r : FileHandler.read(FileHandler.ASSESSMENT_TYPES)) {
            if (r.length >= 4) {
                String display = r[0] + " - " + r[1] + " (RM " + r[3] + ")";
                typeMap.put(display, r);
                typeBox.addItem(display);
            }
        }
    }

    private void refresh() {
        model.setRowCount(0);
        String doctorId = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "D001";
        for (String[] r : FileHandler.read(FileHandler.ASSESSMENTS)) {
            if (r.length >= 10 && r[2].equalsIgnoreCase(doctorId)) {
                model.addRow(r);
            }
        }
    }

    private void populateFormFromTable(int row) {
        if (row < 0 || row >= model.getRowCount()) return;
        setSelectedPatientId(model.getValueAt(row, 1).toString());
        String typeId = model.getValueAt(row, 3).toString();
        for (int i = 0; i < typeBox.getItemCount(); i++) {
            if (typeBox.getItemAt(i).startsWith(typeId)) {
                typeBox.setSelectedIndex(i);
                break;
            }
        }
        String notesAndVitals = model.getValueAt(row, 6).toString();
        notesArea.setText(notesAndVitals);
        labArea.setText(model.getValueAt(row, 7).toString());
    }

    private void clearForm() {
        if (patientBox.getItemCount() > 0) patientBox.setSelectedIndex(0);
        bpField.setText("");
        heartRateField.setText("");
        spo2Field.setText("");
        tempField.setText("");
        notesArea.setText("");
        labArea.setText("");
        admissionBox.setSelectedIndex(0);
        wardTypeBox.setSelectedIndex(0);
        urgencyBox.setSelectedIndex(0);
        admissionReasonField.setText("");
        table.clearSelection();
    }

    private void save() {
        String patient = getSelectedPatientId();
        String bp = bpField.getText().trim();
        String hr = heartRateField.getText().trim();
        String spo2 = spo2Field.getText().trim();
        String temp = tempField.getText().trim();
        String notes = notesArea.getText().trim();
        String lab = labArea.getText().trim();

        if (patient == null || patient.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a registered patient from the dropdown.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!AuthService.userIdExists(patient, "PATIENT")) {
            JOptionPane.showMessageDialog(this, "Patient ID '" + patient + "' does not exist in the system.", "Patient Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selected = (String) typeBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an Assessment / Check-up Type.", "Missing Type", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (bp.isEmpty() && hr.isEmpty() && spo2.isEmpty() && temp.isEmpty() && notes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please record either patient vital signs or consultation notes.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] type = typeMap.get(selected);
        if (type == null) {
            JOptionPane.showMessageDialog(this, "Selected assessment type is invalid. Please refresh the form and try again.", "Invalid Type", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String clinicalNotes = buildClinicalNotes(bp, hr, spo2, temp, notes);
        String diagnosisLab = lab.isEmpty() ? "Standard observations recorded. No abnormal lab findings." : lab;
        String typeId = type[0];
        String fee = type[3];
        String doctorId = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "D001";
        String assessmentId = FileHandler.nextId(FileHandler.ASSESSMENTS, "AS", 4);

        FileHandler.append(FileHandler.ASSESSMENTS, new String[]{
                assessmentId,
                patient,
                doctorId,
                typeId,
                DataUtil.today(),
                "PENDING_REVIEW",
                clinicalNotes,
                diagnosisLab,
                fee,
                "COMPLETED"
        });

        String admissionMsg = "";
        if (admissionBox.getSelectedIndex() == 1) {
            String wardType = (String) wardTypeBox.getSelectedItem();
            String urgency = (String) urgencyBox.getSelectedItem();
            String reason = admissionReasonField.getText().trim();
            if (reason.isEmpty()) {
                reason = "Doctor requested inpatient admission: " + diagnosisLab;
            }
            String admId = FileHandler.nextId(FileHandler.ADMISSIONS, "ADM", 3);
            FileHandler.append(FileHandler.ADMISSIONS, new String[]{
                    admId,
                    patient,
                    doctorId,
                    wardType,
                    reason,
                    urgency,
                    "None",
                    "PENDING",
                    DataUtil.today(),
                    "None"
            });
            admissionMsg = "\n• Inpatient Admission Request submitted to Admin (ID: " + admId + " | " + wardType + " | " + urgency + ")";
        }

        clearForm();
        refresh();

        JOptionPane.showMessageDialog(this,
                """
                Clinical Assessment & Vital Signs successfully saved!
                • Assessment ID: %s
                • Assessment Fee: RM %s (Submitted to Admin for Billing)%s
                """.formatted(assessmentId, fee, admissionMsg),
                "Assessment Recorded",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateSelectedAssessment() {
        int rowIndex = table.getSelectedRow();
        if (rowIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select an assessment record to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String patient = getSelectedPatientId();
        String bp = bpField.getText().trim();
        String hr = heartRateField.getText().trim();
        String spo2 = spo2Field.getText().trim();
        String temp = tempField.getText().trim();
        String notes = notesArea.getText().trim();
        String lab = labArea.getText().trim();

        if (patient == null || patient.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a registered patient from the dropdown.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selected = (String) typeBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an Assessment / Check-up Type.", "Missing Type", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] type = typeMap.get(selected);
        if (type == null) {
            JOptionPane.showMessageDialog(this, "Selected assessment type is invalid. Please refresh the form and try again.", "Invalid Type", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (bp.isEmpty() && hr.isEmpty() && spo2.isEmpty() && temp.isEmpty() && notes.isEmpty() && lab.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter updated notes, vitals, or diagnosis before saving the changes.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String assessmentId = model.getValueAt(rowIndex, 0).toString();
        String doctorId = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "D001";
        String newDate = DataUtil.today();
        String clinicalNotes = buildClinicalNotes(bp, hr, spo2, temp, notes);
        String diagnosisLab = lab.isEmpty() ? "Standard observations recorded. No abnormal lab findings." : lab;
        String fee = type[3];

        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.ASSESSMENTS));
        boolean found = false;
        for (String[] row : rows) {
            if (row.length >= 10 && row[0].equals(assessmentId)) {
                row[1] = patient;
                row[2] = doctorId;
                row[3] = type[0];
                row[4] = newDate;
                row[5] = "PENDING_REVIEW";
                row[6] = clinicalNotes;
                row[7] = diagnosisLab;
                row[8] = fee;
                row[9] = "COMPLETED";
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Selected assessment could not be found for update.", "Update Failed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FileHandler.writeAll(FileHandler.ASSESSMENTS, rows);
        clearForm();
        refresh();

        JOptionPane.showMessageDialog(this,
                """
                Assessment updated successfully.
                • Assessment ID: %s
                • Updated date: %s
                """.formatted(assessmentId, newDate),
                "Assessment Updated",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedAssessment() {
        int rowIndex = table.getSelectedRow();
        if (rowIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select an assessment to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String assessmentId = model.getValueAt(rowIndex, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete assessment " + assessmentId + "? This action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.ASSESSMENTS));
        List<String[]> updated = new ArrayList<>();
        for (String[] row : rows) {
            if (row.length >= 10 && row[0].equals(assessmentId)) {
                continue;
            }
            updated.add(row);
        }

        FileHandler.writeAll(FileHandler.ASSESSMENTS, updated);
        clearForm();
        refresh();

        JOptionPane.showMessageDialog(this, "Assessment " + assessmentId + " deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
    }

    private String buildClinicalNotes(String bp, String hr, String spo2, String temp, String notes) {
        StringBuilder structuredNotes = new StringBuilder();
        List<String> vitalsList = new ArrayList<>();
        if (!bp.isEmpty()) vitalsList.add("BP: " + (bp.toLowerCase().contains("mmhg") ? bp : bp + " mmHg"));
        if (!hr.isEmpty()) vitalsList.add("HR: " + (hr.toLowerCase().contains("bpm") ? hr : hr + " bpm"));
        if (!spo2.isEmpty()) vitalsList.add("SpO2: " + (spo2.contains("%") ? spo2 : spo2 + "%"));
        if (!temp.isEmpty()) vitalsList.add("Temp: " + (temp.contains("°C") || temp.toLowerCase().contains("c") ? temp : temp + "°C"));

        if (!vitalsList.isEmpty()) {
            structuredNotes.append("[Vitals: ").append(String.join(" | ", vitalsList)).append("] ");
        }
        if (!notes.isEmpty()) {
            structuredNotes.append(notes);
        }

        return structuredNotes.toString().trim();
    }
}


