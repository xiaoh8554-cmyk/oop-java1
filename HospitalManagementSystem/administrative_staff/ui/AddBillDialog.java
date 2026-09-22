package administrative_staff.ui;

import common.AuthService;
import common.DataManager;
import common.DataUtil;
import common.FileHandler;
import common.model.User;
import patients.Patient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddBillDialog extends JDialog {
    private final JComboBox<String> patientBox = new JComboBox<>();
    private final JComboBox<String> assessmentBox = new JComboBox<>();
    private final List<String[]> currentPatientAssessments = new ArrayList<>();

    private final DefaultListModel<String> rateListModel = new DefaultListModel<>();
    private final JList<String> rateList = new JList<>(rateListModel);
    private final JLabel hintLabel = new JLabel("Hold Ctrl or Shift to select multiple rates");
    private final JComboBox<String> insuranceBox = new JComboBox<>();
    private final JLabel discountInfoLabel = new JLabel("Subtotal: RM 0.00  |  Insurance Deduct: -RM 0.00");
    private final JTextField assessmentField = new JTextField(20);
    private final JTextField amountField = new JTextField(20);

    private final JButton saveButton = new JButton("Create Bill");
    private final JButton cancelButton = new JButton("Cancel");
    private final List<String[]> rates;

    public AddBillDialog(JFrame parent, Runnable onSaved) {
        super(parent, "Create New Patient Bill", true);
        setSize(620, 620);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Populate patient dropdown
        List<User> allUsers = DataManager.getInstance().getAllUsers();
        for (User u : allUsers) {
            if (u instanceof Patient || (u.getRole() != null && "PATIENT".equalsIgnoreCase(u.getRole().name()))) {
                patientBox.addItem(u.getId() + " - " + u.getFullName());
            }
        }

        // Populate rates from rates.txt
        rates = FileHandler.read(FileHandler.RATES);
        rateList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        rateList.setVisibleRowCount(5);

        // Populate insurance options from insurance.txt
        insuranceBox.addItem("None (0% coverage)");
        for (String[] ins : FileHandler.read(FileHandler.INSURANCE)) {
            if (ins.length >= 4) {
                insuranceBox.addItem(ins[1] + " (" + ins[3] + "% coverage)");
            }
        }

        // Initial loading of unbilled assessments for first patient
        if (patientBox.getItemCount() > 0) {
            String patientStr = patientBox.getItemAt(0);
            String initialPid = patientStr.contains(" - ") ? patientStr.split(" - ")[0].trim() : patientStr;
            loadPatientAssessments(initialPid);
        } else {
            updateRateList();
        }

        // Listeners for dynamic updates
        patientBox.addActionListener(e -> {
            Object selectedItem = patientBox.getSelectedItem();
            if (selectedItem != null) {
                String patientStr = selectedItem.toString().trim();
                String pid = patientStr.contains(" - ") ? patientStr.split(" - ")[0].trim() : patientStr;
                loadPatientAssessments(pid);
                updateRateList();
                recalculate();
            }
        });

        assessmentBox.addActionListener(e -> {
            updateRateList();
            recalculate();
        });

        rateList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) recalculate();
        });

        insuranceBox.addActionListener(e -> recalculate());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Select Patient:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(patientBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Clinical Assessment:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(assessmentBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Select Rate(s):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.35;
        JScrollPane rateScrollPane = new JScrollPane(rateList);
        panel.add(rateScrollPane, gbc);

        row++;
        gbc.gridx = 1; gbc.gridy = row; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        panel.add(hintLabel, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Insurance Policy:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(insuranceBox, gbc);

        row++;
        gbc.gridx = 1; gbc.gridy = row;
        discountInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        discountInfoLabel.setForeground(new Color(20, 100, 40));
        panel.add(discountInfoLabel, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Rates / Ref:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(assessmentField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Net Payable (RM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(amountField, gbc);

        row++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            Object selectedItem = patientBox.getSelectedItem();
            if (selectedItem == null || selectedItem.toString().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a Patient.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String patientStr = selectedItem.toString().trim();
            String patientId = patientStr.contains(" - ") ? patientStr.split(" - ")[0].trim() : patientStr;

            if (!AuthService.userIdExists(patientId, "PATIENT")) {
                JOptionPane.showMessageDialog(this, "Patient ID '" + patientId + "' does not exist.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String assessmentId = assessmentField.getText().trim();
            if (assessmentId.isEmpty()) {
                assessmentId = "Standard Rate";
            }

            String amtStr = amountField.getText().trim();
            double amount = DataUtil.toDouble(amtStr, -1);
            if (amount < 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid non-negative amount or select rates.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String insOption = (String) insuranceBox.getSelectedItem();
            if (insOption == null || insOption.startsWith("None")) {
                insOption = "None (0% coverage)";
            }

            String id = FileHandler.nextId(FileHandler.BILLING, "B", 4);
            FileHandler.append(FileHandler.BILLING, new String[]{
                    id, patientId, assessmentId, String.format("%.2f", amount), "UNPAID", DataUtil.today(), insOption
            });

            String linkedRef = !assessmentId.equals("Standard Rate") ? "\nReference / Assessment: " + assessmentId : "";
            JOptionPane.showMessageDialog(this,
                    "Bill created successfully with ID: " + id +
                            linkedRef +
                            "\nInsurance Applied: " + insOption +
                            "\nNet Amount: RM " + String.format("%.2f", amount) +
                            "\nStatus: UNPAID (Sent to Patient)",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            if (onSaved != null) onSaved.run();
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        setContentPane(panel);
        updateRateList();
        recalculate();
    }

    private void loadPatientAssessments(String patientId) {
        assessmentBox.removeAllItems();
        currentPatientAssessments.clear();
        assessmentBox.addItem("None (Standard Rates Only)");

        if (patientId == null || patientId.trim().isEmpty()) {
            return;
        }

        // Collect all assessment IDs that are already billed in billing.txt
        Set<String> billedAssessmentIds = new HashSet<>();
        for (String[] b : FileHandler.read(FileHandler.BILLING)) {
            if (b.length >= 3) {
                String ref = b[2];
                String[] tokens = ref.split("[,;]");
                for (String t : tokens) {
                    String clean = t.trim().toUpperCase();
                    if (clean.startsWith("AS")) {
                        billedAssessmentIds.add(clean);
                    }
                }
            }
        }

        // Map typeId -> typeName
        Map<String, String> typeMap = new HashMap<>();
        for (String[] at : FileHandler.read(FileHandler.ASSESSMENT_TYPES)) {
            if (at.length >= 2) {
                typeMap.put(at[0].trim().toUpperCase(), at[1].trim());
            }
        }

        for (String[] as : FileHandler.read(FileHandler.ASSESSMENTS)) {
            // as: 0:id, 1:patientId, 2:docId, 3:typeId, 4:date, 5:grade, 6:notes, 7:lab, 8:price, 9:status
            if (as.length >= 9 && as[1].equalsIgnoreCase(patientId)) {
                String asId = as[0].trim().toUpperCase();
                if (!billedAssessmentIds.contains(asId)) {
                    currentPatientAssessments.add(as);
                    String typeName = typeMap.getOrDefault(as[3].trim().toUpperCase(), as[3].trim());
                    assessmentBox.addItem(as[0] + " - " + typeName + " (RM " + as[8] + ") [Dr. " + as[2] + " | " + as[4] + "]");
                }
            }
        }

        // Auto-select latest unbilled assessment if available
        if (!currentPatientAssessments.isEmpty()) {
            assessmentBox.setSelectedIndex(currentPatientAssessments.size());
        } else {
            assessmentBox.setSelectedIndex(0);
        }
    }

    private void updateRateList() {
        // Save current selection IDs to preserve if still applicable
        List<String> prevSelectedIds = new ArrayList<>();
        for (String sel : rateList.getSelectedValuesList()) {
            String id = sel.contains(" - ") ? sel.split(" - ")[0].trim() : "";
            if (!id.isEmpty()) {
                prevSelectedIds.add(id);
            }
        }

        int asIdx = assessmentBox.getSelectedIndex();
        boolean hasAssessment = (asIdx > 0 && asIdx - 1 < currentPatientAssessments.size());
        String selectedTypeId = "";
        String selectedTypeName = "";
        if (hasAssessment) {
            String[] as = currentPatientAssessments.get(asIdx - 1);
            if (as.length >= 4) {
                selectedTypeId = as[3].trim().toUpperCase();
            }
            String selectedItemStr = (String) assessmentBox.getSelectedItem();
            if (selectedItemStr != null && selectedItemStr.contains(" - ")) {
                String afterDash = selectedItemStr.split(" - ")[1];
                if (afterDash.contains(" (")) {
                    selectedTypeName = afterDash.substring(0, afterDash.indexOf(" (")).trim();
                }
            }
        }

        rateListModel.clear();
        List<Integer> newSelectionIndices = new ArrayList<>();
        int currentModelIdx = 0;

        for (String[] r : rates) {
            if (r.length < 3) continue;

            String rateId = r[0].trim();
            String rateDesc = r[1].trim();

            boolean isConsultation = rateDesc.toLowerCase().contains("consultation");
            boolean isMatchingType = (!selectedTypeId.isEmpty() && rateDesc.toUpperCase().contains(selectedTypeId))
                    || (!selectedTypeName.isEmpty() && rateDesc.toLowerCase().contains(selectedTypeName.toLowerCase()));

            // If an assessment is selected, filter out consultation & duplicate examination rates
            if (hasAssessment && (isConsultation || isMatchingType)) {
                continue;
            }

            rateListModel.addElement(rateId + " - " + rateDesc + " (RM " + r[2] + ")");

            if (prevSelectedIds.contains(rateId)) {
                newSelectionIndices.add(currentModelIdx);
            }
            currentModelIdx++;
        }

        // Restore any preserved selections
        if (!newSelectionIndices.isEmpty()) {
            int[] indices = new int[newSelectionIndices.size()];
            for (int k = 0; k < newSelectionIndices.size(); k++) {
                indices[k] = newSelectionIndices.get(k);
            }
            rateList.setSelectedIndices(indices);
        }

        // Update guidance label
        if (hasAssessment) {
            hintLabel.setText("ℹ️ Consultation covered by Doctor Assessment. Showing room, ward & facility rates.");
            hintLabel.setForeground(new Color(20, 110, 40));
            hintLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        } else {
            hintLabel.setText("Hold Ctrl or Shift to select multiple rates (Consultations, Rooms, Imaging)");
            hintLabel.setForeground(Color.GRAY);
            hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        }
    }

    private void recalculate() {
        double subtotal = 0.0;
        String linkedAssessmentId = "";

        int asIdx = assessmentBox.getSelectedIndex();
        if (asIdx > 0 && asIdx - 1 < currentPatientAssessments.size()) {
            String[] as = currentPatientAssessments.get(asIdx - 1);
            subtotal += DataUtil.toDouble(as[8], 0.0);
            linkedAssessmentId = as[0].trim();
        }

        StringBuilder selectedRateIds = new StringBuilder();
        for (String selected : rateList.getSelectedValuesList()) {
            for (String[] r : rates) {
                if (r.length >= 3 && selected.startsWith(r[0])) {
                    subtotal += DataUtil.toDouble(r[2], 0.0);
                    if (selectedRateIds.length() > 0) {
                        selectedRateIds.append(", ");
                    }
                    selectedRateIds.append(r[0]);
                    break;
                }
            }
        }

        StringBuilder refSb = new StringBuilder();
        if (!linkedAssessmentId.isEmpty()) {
            refSb.append(linkedAssessmentId);
        }
        if (selectedRateIds.length() > 0) {
            if (refSb.length() > 0) {
                refSb.append(", ");
            }
            refSb.append(selectedRateIds);
        }
        if (refSb.length() == 0) {
            refSb.append("Standard Rate");
        }
        assessmentField.setText(refSb.toString());

        // Deduct insurance percentage
        int discountPercent = 0;
        String insItem = (String) insuranceBox.getSelectedItem();
        if (insItem != null && insItem.contains("%")) {
            int pStart = insItem.indexOf("(");
            int pEnd = insItem.indexOf("%");
            if (pStart >= 0 && pEnd > pStart) {
                discountPercent = DataUtil.toInt(insItem.substring(pStart + 1, pEnd).trim(), 0);
            }
        }

        double discount = subtotal * (discountPercent / 100.0);
        double netAmount = Math.max(0.0, subtotal - discount);

        discountInfoLabel.setText(String.format("Subtotal: RM %.2f  |  Insurance Deduct (%d%%): -RM %.2f", subtotal, discountPercent, discount));
        amountField.setText(String.format("%.2f", netAmount));
    }
}
