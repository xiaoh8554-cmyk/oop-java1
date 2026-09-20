package patients.ui;

import common.*;
import patients.ux.MedicalRecordUX;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordUI extends MedicalRecordUX {
    private final List<String[]> patientRecords = new ArrayList<>();

    public MedicalRecordUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> refresh());
        detailButton.addActionListener(e -> openRecordDetailDialog());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateRecordSelection();
            }
        });

        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        patientRecords.clear();
        String patient = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "P001";

        for (String[] r : FileHandler.read(FileHandler.ASSESSMENTS)) {
            if (r.length >= 10 && r[1].equals(patient)) {
                patientRecords.add(r);
                model.addRow(r);
            }
        }
        updateRecordSelection();
    }

    private void updateRecordSelection() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < patientRecords.size()) {
            String[] r = patientRecords.get(row);
            String asId = r[0];
            String date = r[4];
            String grade = r[5];
            selectedRecordLabel.setText("Selected: " + asId + " (" + grade + " | " + date + ")");
            detailButton.setEnabled(true);
        } else {
            selectedRecordLabel.setText("Select a medical record to view clinical details.");
            detailButton.setEnabled(false);
        }
    }

    private void openRecordDetailDialog() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= patientRecords.size()) return;

        String[] r = patientRecords.get(row);
        // r: 0:asId, 1:patientId, 2:docId, 3:typeId, 4:date, 5:grade, 6:result, 7:labResult, 8:billRm, 9:status
        String asId = r[0];
        String patientId = r[1];
        String docId = r[2];
        String typeId = r[3];
        String date = r[4];
        String grade = r[5];
        String result = r[6];
        String labResult = r[7];
        String billRm = r[8];
        String status = r[9];

        String patientName = Session.getCurrentUser() != null ? Session.getCurrentUser().getFullName() : patientId;
        String docName = docId;
        String specialty = "Specialist";
        String typeName = typeId;
        String typeDesc = "Clinical Diagnostic Service";

        for (String[] at : FileHandler.read(FileHandler.ASSESSMENT_TYPES)) {
            if (at.length >= 3 && at[0].equalsIgnoreCase(typeId)) {
                typeName = at[1];
                typeDesc = at[2];
                break;
            }
        }

        for (common.model.User u : DataManager.getInstance().getAllUsers()) {
            if (u.getId().equalsIgnoreCase(docId)) {
                docName = u.getFullName();
                if (u instanceof doctors.Doctor) {
                    specialty = ((doctors.Doctor) u).getSpecialty();
                }
            }
        }

        new common.ui.DetailDialogBuilder(this, "Clinical Assessment Details - " + asId)
                .setSize(720, 560)
                .setHeader("APU MEDICAL CENTRE - CLINICAL ASSESSMENT RECORD",
                        "Assessment ID: " + asId + "   |   Date: " + date + "   |   Diagnostic Grade: " + grade + "   |   Status: " + status,
                        grade.equalsIgnoreCase("HEALTHY") ? new Color(34, 139, 34) : new Color(200, 100, 20))
                .setBorderTitle("Medical Findings & Diagnostic Summary")
                .addField("Patient:", patientName + " (" + patientId + ")")
                .addField("Attending Doctor:", "Dr. " + docName + " (" + docId + ") - " + specialty)
                .addField("Assessment Type:", typeName + " (" + typeDesc + ")")
                .addHighlightField("Severity / Grade:", grade, grade.equalsIgnoreCase("HEALTHY") ? new Color(34, 139, 34) : new Color(200, 100, 20))
                .addTextAreaField("Doctor's Findings & Notes:", result, 3)
                .addTextAreaField("Laboratory / Test Results:", labResult, 3)
                .addHighlightField("Consultation Fee:", "RM " + billRm, new Color(20, 60, 140))
                .show();
    }
}
