package patients.ui;

import common.FileHandler;
import common.Session;
import patients.ux.PatientAdmissionUX;

import java.awt.*;
import java.util.List;

public class PatientAdmissionUI extends PatientAdmissionUX {
    public PatientAdmissionUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> loadAdmissionData());
        loadAdmissionData();
    }

    private void loadAdmissionData() {
        historyModel.setRowCount(0);
        String patientId = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "P001";

        List<String[]> admissions = FileHandler.read(FileHandler.ADMISSIONS);
        List<String[]> assets = FileHandler.read(FileHandler.ASSETS);
        List<String[]> rates = FileHandler.read(FileHandler.RATES);

        String[] activeAdmission = null;

        for (String[] row : admissions) {
            if (row.length >= 10 && row[1].equalsIgnoreCase(patientId)) {
                // Find asset name for assigned asset ID
                String assetId = row[6];
                String roomName = assetId;
                for (String[] a : assets) {
                    if (a.length >= 2 && a[0].equalsIgnoreCase(assetId)) {
                        roomName = a[1];
                        break;
                    }
                }

                // Add to history table
                historyModel.addRow(new Object[]{
                        row[0], // ADM ID
                        row[2], // Doctor ID
                        roomName, // Room name
                        row[4], // Reason
                        row[7], // Status
                        row[8], // Admitted date
                        row[9]  // Discharge date
                });

                // Check for most recent active admission
                String status = row[7];
                if ("ADMITTED".equalsIgnoreCase(status) || "PENDING".equalsIgnoreCase(status)) {
                    activeAdmission = row;
                }
            }
        }

        // Display current active admission card
        if (activeAdmission != null) {
            String admId = activeAdmission[0];
            String docId = activeAdmission[2];
            String wardReq = activeAdmission[3];
            String reason = activeAdmission[4];
            String urgency = activeAdmission[5];
            String assetId = activeAdmission[6];
            String status = activeAdmission[7];
            String admDate = activeAdmission[8];

            admIdVal.setText(admId);
            doctorVal.setText(docId);
            urgencyVal.setText(urgency);
            reasonVal.setText(reason);
            dateVal.setText(admDate);

            if ("ADMITTED".equalsIgnoreCase(status)) {
                statusLabel.setText("● CURRENTLY ADMITTED - Inpatient Room Assigned");
                statusLabel.setBackground(new Color(210, 245, 220)); // Soft Green
                statusLabel.setForeground(new Color(10, 100, 30));

                // Find room name and location
                String roomName = assetId;
                String roomLoc = "Inpatient Ward";
                for (String[] a : assets) {
                    if (a.length >= 4 && a[0].equalsIgnoreCase(assetId)) {
                        roomName = a[1];
                        roomLoc = a[3];
                        break;
                    }
                }
                wardVal.setText(roomName);
                locationVal.setText(roomLoc);

                // Find daily rate from rates.txt
                String rateDesc = "RM 120.00 / day";
                for (String[] r : rates) {
                    if (r.length >= 3 && (r[1].toLowerCase().contains(roomName.toLowerCase()) || r[1].toLowerCase().contains(wardReq.toLowerCase()))) {
                        rateDesc = "RM " + r[2] + " / day";
                        break;
                    }
                }
                rateVal.setText(rateDesc);

            } else { // PENDING
                statusLabel.setText("ADMISSION REQUESTED - Pending Bed Allocation by Administration");
                statusLabel.setBackground(new Color(255, 245, 210)); // Soft Yellow
                statusLabel.setForeground(new Color(130, 90, 10));

                wardVal.setText("Requested: " + wardReq);
                locationVal.setText("Awaiting Bed Assignment");
                rateVal.setText("Determined upon room assignment");
            }
        } else {
            statusLabel.setText("○ NO ACTIVE INPATIENT ADMISSION (Outpatient Status)");
            statusLabel.setBackground(new Color(240, 240, 240));
            statusLabel.setForeground(Color.DARK_GRAY);

            admIdVal.setText("None");
            doctorVal.setText("None");
            wardVal.setText("None");
            locationVal.setText("None");
            urgencyVal.setText("None");
            reasonVal.setText("None");
            dateVal.setText("None");
            rateVal.setText("None");
        }
    }
}
