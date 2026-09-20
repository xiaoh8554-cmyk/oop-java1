package patients.ui;

import common.*;
import patients.ux.PatientBillingUX;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

        String patientName = Session.getCurrentUser() != null ? Session.getCurrentUser().getFullName() : patientId;

        // Lookup assessment details
        String docId = "N/A";
        String docName = "Attending Doctor";
        String specialty = "General Consultation";
        String typeId = "N/A";
        String typeName = "Medical Consultation & Examination";
        String typeDesc = "Clinical assessment and diagnosis";
        String grade = "N/A";
        String clinicalResult = "Routine checkup and consultation";
        String labResult = "None";

        for (String[] as : FileHandler.read(FileHandler.ASSESSMENTS)) {
            if (as.length >= 10 && as[0].equalsIgnoreCase(assessmentId)) {
                docId = as[2];
                typeId = as[3];
                grade = as[5];
                clinicalResult = as[6];
                labResult = as[7];
                break;
            }
        }

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

        JDialog dialog = new JDialog(this, "Invoice & Medical Service Details - " + billId, true);
        dialog.setSize(680, 540);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1, 4, 4));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 210, 245), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        header.setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("APU MEDICAL CENTRE - PATIENT INVOICE DETAIL");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(new Color(20, 60, 140));

        String statusTag = "PAID".equalsIgnoreCase(status) ? "[PAID / SETTLED]" : "[UNPAID / OUTSTANDING]";
        JLabel meta = new JLabel("Invoice No: " + billId + "   |   Date: " + date + "   |   Status: " + statusTag);
        meta.setFont(new Font("SansSerif", Font.BOLD, 12));
        meta.setForeground("PAID".equalsIgnoreCase(status) ? new Color(34, 139, 34) : new Color(200, 40, 40));

        header.add(title);
        header.add(meta);
        content.add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(BorderFactory.createTitledBorder("Service Breakdown & Clinical Reference"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new java.awt.Insets(6, 10, 6, 10);
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
        body.add(new JLabel("Assessment Ref:"), g);
        g.gridx = 1;
        body.add(new JLabel(assessmentId + " (" + typeName + " - " + typeDesc + ")"), g);

        y++;
        g.gridx = 0; g.gridy = y;
        body.add(new JLabel("Diagnostic Grade:"), g);
        g.gridx = 1;
        body.add(new JLabel(grade), g);

        y++;
        g.gridx = 0; g.gridy = y;
        body.add(new JLabel("Clinical Findings:"), g);
        g.gridx = 1;
        body.add(new JLabel(clinicalResult), g);

        y++;
        g.gridx = 0; g.gridy = y;
        body.add(new JLabel("Lab Test Results:"), g);
        g.gridx = 1;
        body.add(new JLabel(labResult), g);

        y++;
        g.gridx = 0; g.gridy = y;
        body.add(new JLabel("Total Amount:"), g);
        g.gridx = 1;
        JLabel amtLabel = new JLabel("RM " + amount);
        amtLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        amtLabel.setForeground(new Color(20, 60, 140));
        body.add(amtLabel, g);

        content.add(body, BorderLayout.CENTER);

        // Footer Actions
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        if (!"PAID".equalsIgnoreCase(status)) {
            JButton dialogPayBtn = new JButton("Pay This Bill Now (RM " + amount + ")");
            dialogPayBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            dialogPayBtn.addActionListener(e -> {
                dialog.dispose();
                pay();
            });
            south.add(dialogPayBtn);
        }

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        south.add(closeBtn);

        content.add(south, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }
}
