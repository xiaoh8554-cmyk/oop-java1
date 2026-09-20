package patients.ui;

import common.*;
import patients.ux.MedicalRecordUX;

import javax.swing.*;
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

        JDialog dialog = new JDialog(this, "Clinical Assessment Details - " + asId, true);
        dialog.setSize(720, 560);
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

        JLabel title = new JLabel("APU MEDICAL CENTRE - CLINICAL ASSESSMENT RECORD");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(new Color(20, 60, 140));

        JLabel meta = new JLabel("Assessment ID: " + asId + "   |   Date: " + date + "   |   Diagnostic Grade: " + grade + "   |   Status: " + status);
        meta.setFont(new Font("SansSerif", Font.BOLD, 12));
        meta.setForeground(grade.equalsIgnoreCase("HEALTHY") ? new Color(34, 139, 34) : new Color(200, 100, 20));

        header.add(title);
        header.add(meta);
        content.add(header, BorderLayout.NORTH);

        // Body Panel
        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(BorderFactory.createTitledBorder("Medical Findings & Diagnostic Summary"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        g.gridx = 0; g.gridy = y;
        body.add(new JLabel("Patient:"), g);
        g.gridx = 1;
        body.add(new JLabel(patientName + " (" + patientId + ")"), g);

        y++;
        g.gridx = 0; g.gridy = y;
        body.add(new JLabel("Attending Doctor:"), g);
        g.gridx = 1;
        body.add(new JLabel("Dr. " + docName + " (" + docId + ") - " + specialty), g);

        y++;
        g.gridx = 0; g.gridy = y;
        body.add(new JLabel("Assessment Type:"), g);
        g.gridx = 1;
        body.add(new JLabel(typeName + " (" + typeDesc + ")"), g);

        y++;
        g.gridx = 0; g.gridy = y;
        body.add(new JLabel("Severity / Grade:"), g);
        g.gridx = 1;
        JLabel gradeLabel = new JLabel(grade);
        gradeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        body.add(gradeLabel, g);

        y++;
        g.gridx = 0; g.gridy = y; g.anchor = GridBagConstraints.NORTHWEST;
        body.add(new JLabel("Doctor's Findings & Notes:"), g);
        g.gridx = 1;
        JTextArea resultArea = new JTextArea(result, 3, 32);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(250, 250, 250));
        resultArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        body.add(new JScrollPane(resultArea), g);

        y++;
        g.gridx = 0; g.gridy = y; g.anchor = GridBagConstraints.NORTHWEST;
        body.add(new JLabel("Laboratory / Test Results:"), g);
        g.gridx = 1;
        JTextArea labArea = new JTextArea(labResult, 3, 32);
        labArea.setLineWrap(true);
        labArea.setWrapStyleWord(true);
        labArea.setEditable(false);
        labArea.setBackground(new Color(250, 250, 250));
        labArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        body.add(new JScrollPane(labArea), g);

        y++;
        g.gridx = 0; g.gridy = y; g.anchor = GridBagConstraints.WEST;
        body.add(new JLabel("Consultation Fee:"), g);
        g.gridx = 1;
        JLabel feeLabel = new JLabel("RM " + billRm);
        feeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        feeLabel.setForeground(new Color(20, 60, 140));
        body.add(feeLabel, g);

        content.add(body, BorderLayout.CENTER);

        // Footer
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        south.add(closeBtn);
        content.add(south, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }
}
