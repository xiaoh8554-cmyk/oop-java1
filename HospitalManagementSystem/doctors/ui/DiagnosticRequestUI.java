package doctors.ui;

import common.*;
import doctors.ux.DiagnosticRequestUX;
import javax.swing.*;
import java.util.*;

public class DiagnosticRequestUI extends DiagnosticRequestUX {

    public DiagnosticRequestUI() {
        super();
        initListeners();
        loadPatients();
        refresh();
    }

    private void initListeners() {
        backButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> save());
        updateButton.addActionListener(e -> updateRequest());
        deleteButton.addActionListener(e -> deleteRequest());
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
        String doctorId = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "D001";
        for (String[] r : FileHandler.read(FileHandler.REQUESTS)) {
            if (r.length >= 7 && r[1].equalsIgnoreCase(doctorId)) {
                model.addRow(r);
            }
        }
    }

    private void populateFormFromTable(int row) {
        if (row < 0 || row >= model.getRowCount()) return;
        setSelectedPatientId(model.getValueAt(row, 2).toString());
        String modality = model.getValueAt(row, 3).toString();
        for (int i = 0; i < typeBox.getItemCount(); i++) {
            if (typeBox.getItemAt(i).equalsIgnoreCase(modality)) {
                typeBox.setSelectedIndex(i);
                break;
            }
        }
        descriptionArea.setText(model.getValueAt(row, 4).toString());
    }

    private void clearForm() {
        if (patientBox.getItemCount() > 0) patientBox.setSelectedIndex(0);
        typeBox.setSelectedIndex(0);
        descriptionArea.setText("");
        table.clearSelection();
    }

    private void save() {
        String patientId = getSelectedPatientId();
        String modality = (String) typeBox.getSelectedItem();
        String description = descriptionArea.getText().trim();

        if (patientId == null || patientId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a registered patient.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!AuthService.userIdExists(patientId, "PATIENT")) {
            JOptionPane.showMessageDialog(this, "Patient ID '" + patientId + "' does not exist in the system.", "Patient Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter clinical indications or requested test items.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String doctorId = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "D001";
        String reqId = FileHandler.nextId(FileHandler.REQUESTS, "REQ", 3);

        // Append to requests.txt:
        // REQUEST_ID|DOCTOR_ID|PATIENT_ID|REQUEST_TYPE|DESCRIPTION|STATUS|DATE
        FileHandler.append(FileHandler.REQUESTS, new String[]{
                reqId,
                doctorId,
                patientId,
                modality,
                description,
                "PENDING",
                DataUtil.today()
        });

        clearForm();
        refresh();

        JOptionPane.showMessageDialog(this,
                "Diagnostic Requisition successfully submitted to Medical Management!\n" +
                "• Request ID: " + reqId + "\n" +
                "• Modality: " + modality + "\n" +
                "• Status: PENDING (Awaiting Medical Manager Review)",
                "Requisition Submitted",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateRequest() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= model.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a requisition from the table to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reqId = model.getValueAt(row, 0).toString();
        String patientId = getSelectedPatientId();
        String modality = (String) typeBox.getSelectedItem();
        String description = descriptionArea.getText().trim();

        if (patientId == null || patientId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a valid patient.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Clinical indication or order details cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String[]> allRows = new ArrayList<>(FileHandler.read(FileHandler.REQUESTS));
        boolean found = false;
        for (String[] r : allRows) {
            if (r.length >= 7 && r[0].equalsIgnoreCase(reqId)) {
                r[2] = patientId;
                r[3] = modality;
                r[4] = description;
                // If it was previously rejected, resetting status to PENDING gives it a fresh review
                if ("REJECTED".equalsIgnoreCase(r[5])) {
                    r[5] = "PENDING";
                }
                found = true;
                break;
            }
        }

        if (found) {
            FileHandler.writeAll(FileHandler.REQUESTS, allRows);
            clearForm();
            refresh();
            JOptionPane.showMessageDialog(this, "Requisition " + reqId + " has been successfully updated.", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to locate requisition in database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRequest() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= model.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a requisition from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reqId = model.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete Diagnostic Requisition " + reqId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        List<String[]> allRows = new ArrayList<>(FileHandler.read(FileHandler.REQUESTS));
        boolean removed = allRows.removeIf(r -> r.length >= 7 && r[0].equalsIgnoreCase(reqId));

        if (removed) {
            FileHandler.writeAll(FileHandler.REQUESTS, allRows);
            clearForm();
            refresh();
            JOptionPane.showMessageDialog(this, "Requisition " + reqId + " was permanently deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to locate requisition in database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

