package patients.ui;

import common.*;
import patients.ux.PatientBillingUX;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientBillingUI extends PatientBillingUX {
    private final List<String[]> patientBills = new ArrayList<>();

    public PatientBillingUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> refresh());
        payButton.addActionListener(e -> pay());
        detailButton.addActionListener(e -> openBillDetailDialog());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateBillSelection();
            }
        });

        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        patientBills.clear();
        String patient = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "P001";
        double outstanding = 0;

        for (String[] r : FileHandler.read(FileHandler.BILLING)) {
            if (r.length >= 6 && r[1].equals(patient)) {
                patientBills.add(r);
                // Columns: Bill ID, Amount (RM), Status, Date
                model.addRow(new Object[]{r[0], "RM " + r[3], r[4], r[5]});
                if (!r[4].equalsIgnoreCase("PAID")) {
                    outstanding += DataUtil.toDouble(r[3], 0);
                }
            }
        }
        totalLabel.setText(String.format("Outstanding: RM %.2f", outstanding));
        updateBillSelection();
    }

    private void updateBillSelection() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < patientBills.size()) {
            String[] bill = patientBills.get(row);
            String billId = bill[0];
            String status = bill[4];
            String amount = bill[3];

            selectedBillLabel.setText("Selected: " + billId + " (RM " + amount + " | " + status + ")");
            detailButton.setEnabled(true);
            payButton.setEnabled(!"PAID".equalsIgnoreCase(status));
        } else {
            selectedBillLabel.setText("Select a bill to view details or make a payment.");
            detailButton.setEnabled(false);
            payButton.setEnabled(false);
        }
    }

    private void pay() {
        int i = table.getSelectedRow();
        if (i < 0 || i >= patientBills.size()) {
            JOptionPane.showMessageDialog(this, "Please select an unpaid bill from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] bill = patientBills.get(i);
        String id = bill[0];
        String status = bill[4];
        String amount = bill[3];

        if ("PAID".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "This bill (" + id + ") is already paid.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this,
                "Confirm payment of RM " + amount + " for bill " + id + "?",
                "Confirm Payment", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;

        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.BILLING));
        for (String[] r : rows) {
            if (r.length >= 5 && r[0].equals(id)) {
                r[4] = "PAID";
            }
        }
        FileHandler.writeAll(FileHandler.BILLING, rows);
        refresh();
        JOptionPane.showMessageDialog(this, "Payment of RM " + amount + " recorded successfully for Bill " + id + ".");
    }

    private void openBillDetailDialog() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= patientBills.size()) return;

        String[] bill = patientBills.get(row);
        // bill: 0:id, 1:patientId, 2:assessmentId, 3:amount, 4:status, 5:date
        String billId = bill[0];
        String patientId = bill[1];
        String assessmentId = bill[2];
        String amount = bill[3];
        String status = bill[4];
        String date = bill[5];
        String insuranceInfo = bill.length >= 7 ? bill[6].trim() : "None (0% coverage)";

        String patientName = Session.getCurrentUser() != null ? Session.getCurrentUser().getFullName() : patientId;

        // Check if assessmentId references standard rates (e.g. R001, R005, etc.)
        Map<String, String[]> rateLookup = new HashMap<>();
        for (String[] r : FileHandler.read(FileHandler.RATES)) {
            if (r.length >= 3) {
                rateLookup.put(r[0].trim().toUpperCase(), r);
            }
        }

        List<String> matchedRateLines = new ArrayList<>();
        double rateSubtotal = 0.0;
        String[] tokens = assessmentId.split("[,;]");
        for (String token : tokens) {
            String key = token.trim().toUpperCase();
            if (rateLookup.containsKey(key)) {
                String[] r = rateLookup.get(key);
                matchedRateLines.add((matchedRateLines.size() + 1) + ". " + r[1] + "  -  RM " + r[2]);
                rateSubtotal += DataUtil.toDouble(r[2], 0.0);
            }
        }

        String statusTag = "PAID".equalsIgnoreCase(status) ? "[PAID / SETTLED]" : "[UNPAID / OUTSTANDING]";
        Color statusColor = "PAID".equalsIgnoreCase(status) ? new Color(34, 139, 34) : new Color(200, 40, 40);

        String foundAssessmentId = null;
        for (String token : tokens) {
            String t = token.trim();
            if (t.toUpperCase().startsWith("AS")) {
                foundAssessmentId = t;
                break;
            }
        }

        // Clinical assessment information (if present)
        String docId = "N/A";
        String docName = "Attending Doctor";
        String specialty = "General Consultation";
        String typeId = "N/A";
        String typeName = "Medical Consultation & Examination";
        String typeDesc = "Clinical assessment and diagnosis";
        String grade = "N/A";
        String clinicalResult = "Routine checkup and consultation";
        String labResult = "None";
        String asFee = "0.00";
        boolean hasAssessment = false;

        if (foundAssessmentId != null) {
            for (String[] as : FileHandler.read(FileHandler.ASSESSMENTS)) {
                if (as.length >= 9 && as[0].equalsIgnoreCase(foundAssessmentId)) {
                    hasAssessment = true;
                    docId = as[2];
                    typeId = as[3];
                    if (as.length >= 10) {
                        grade = as[5];
                        clinicalResult = as[6];
                        labResult = as[7];
                        asFee = as[8];
                    }
                    break;
                }
            }

            if (hasAssessment) {
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
            }
        }

        boolean hasRates = !matchedRateLines.isEmpty();
        int dialogH = (hasAssessment && hasRates) ? 720 : 640;

        common.ui.DetailDialogBuilder builder = new common.ui.DetailDialogBuilder(this, "Invoice & Medical Service Details - " + billId)
                .setSize(750, dialogH)
                .setHeader("APU MEDICAL CENTRE - PATIENT INVOICE DETAIL",
                        "Invoice No: " + billId + "   |   Date: " + date + "   |   Status: " + statusTag,
                        statusColor);

        builder.addField("Patient:", patientName + " (" + patientId + ")");

        if (hasAssessment && hasRates) {
            builder.setBorderTitle("Consolidated Medical & Hospital Service Breakdown");
            builder.addField("Attending Doctor:", "Dr. " + docName + " (" + docId + ") - " + specialty);
            builder.addField("Assessment Check-up:", foundAssessmentId + " (" + typeName + " - RM " + asFee + ")");
            builder.addField("Diagnostic Grade:", grade);
            builder.addTextAreaField("Clinical & Lab Findings:", "Notes: " + clinicalResult + "\nLab Results: " + labResult, 3);

            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < matchedRateLines.size(); k++) {
                if (k > 0) sb.append("\n");
                sb.append(matchedRateLines.get(k));
            }
            builder.addTextAreaField("Hospital Rates & Facilities:", sb.toString(), Math.min(8, Math.max(3, matchedRateLines.size())));
            builder.addField("Rates Subtotal:", String.format("RM %.2f", rateSubtotal));
            builder.addField("Insurance Policy:", insuranceInfo);
            builder.addHighlightField("Total Payable:", "RM " + amount, new Color(20, 60, 140));
        } else if (hasAssessment) {
            builder.setBorderTitle("Clinical Assessment Breakdown");
            builder.addField("Attending Doctor:", "Dr. " + docName + " (" + docId + ") - " + specialty);
            builder.addField("Assessment Ref:", foundAssessmentId + " (" + typeName + " - " + typeDesc + ")");
            builder.addField("Diagnostic Grade:", grade);
            builder.addTextAreaField("Clinical Findings:", clinicalResult, 3);
            builder.addTextAreaField("Lab Test Results:", labResult, 3);
            builder.addField("Insurance Policy:", insuranceInfo);
            builder.addHighlightField("Total Amount:", "RM " + amount, new Color(20, 60, 140));
        } else {
            builder.setBorderTitle("Billed Services & Fee Breakdown");
            builder.addField("Billing Category:", "Hospital Consultation, Diagnostics & Room Rates");

            if (hasRates) {
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < matchedRateLines.size(); k++) {
                    if (k > 0) sb.append("\n");
                    sb.append(matchedRateLines.get(k));
                }
                builder.addTextAreaField("Billed Services & Rates:", sb.toString(), Math.min(10, Math.max(4, matchedRateLines.size())));
                if (rateSubtotal > 0) {
                    builder.addField("Rates Subtotal:", String.format("RM %.2f", rateSubtotal));
                }
            } else {
                builder.addField("Item Description:", assessmentId);
            }
            builder.addField("Insurance Policy:", insuranceInfo);
            builder.addHighlightField("Total Payable:", "RM " + amount, new Color(20, 60, 140));
        }

        if (!"PAID".equalsIgnoreCase(status)) {
            JButton dialogPayBtn = new JButton("Pay This Bill Now (RM " + amount + ")");
            dialogPayBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            dialogPayBtn.addActionListener(e -> {
                builder.dispose();
                pay();
            });
            builder.addActionButton(dialogPayBtn);
        }

        builder.show();
    }
}
