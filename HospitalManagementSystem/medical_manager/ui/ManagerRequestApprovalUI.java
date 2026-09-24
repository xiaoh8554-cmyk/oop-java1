package medical_manager.ui;

import common.*;
import medical_manager.ux.ManagerRequestApprovalUX;
import javax.swing.*;
import java.util.*;

public class ManagerRequestApprovalUI extends ManagerRequestApprovalUX {
    private final Map<String, String> userNames = new HashMap<>();

    public ManagerRequestApprovalUI() {
        super();
        initListeners();
        cacheUserNames();
        refresh();
    }

    private void initListeners() {
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> {
            cacheUserNames();
            refresh();
        });
        filterBox.addActionListener(e -> refresh());

        approveButton.addActionListener(e -> updateStatus("APPROVED"));
        rejectButton.addActionListener(e -> updateStatus("REJECTED"));
        deleteButton.addActionListener(e -> deleteRequest());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelection();
            }
        });
    }

    private void cacheUserNames() {
        userNames.clear();
        for (String[] u : FileHandler.read(FileHandler.USERS)) {
            if (u.length >= 5) {
                userNames.put(u[1], u[4]);
            }
        }
    }

    private void refresh() {
        model.setRowCount(0);
        String selectedFilter = (String) filterBox.getSelectedItem();
        if (selectedFilter == null) selectedFilter = "All Requests";

        String currentManagerId = (Session.getCurrentUser() != null && Session.getCurrentUser().getRole() != null && "MEDICAL_MANAGER".equalsIgnoreCase(Session.getCurrentUser().getRole().name()))
                ? Session.getCurrentUser().getUserId() : "";
        List<String> assignedDoctorIds = new ArrayList<>();
        if (!currentManagerId.isEmpty()) {
            for (String[] doc : FileHandler.read("doctors.txt")) {
                if (doc.length >= 4 && doc[3].equalsIgnoreCase(currentManagerId)) {
                    assignedDoctorIds.add(doc[0].trim());
                }
            }
        }

        for (String[] r : FileHandler.read(FileHandler.REQUESTS)) {
            // r: 0:reqId, 1:docId, 2:patientId, 3:type, 4:description, 5:status, 6:date
            if (r.length >= 7) {
                String reqId = r[0];
                String docId = r[1];

                // Only show requests from doctors assigned to this manager
                if (!assignedDoctorIds.isEmpty() && !assignedDoctorIds.contains(docId)) {
                    continue;
                }

                String docName = userNames.getOrDefault(docId, docId);
                String patientId = r[2];
                String patientName = userNames.getOrDefault(patientId, patientId);
                String modality = r[3];
                String description = r[4];
                String status = r[5];
                String date = r[6];

                if ("All Requests".equalsIgnoreCase(selectedFilter) || status.equalsIgnoreCase(selectedFilter)) {
                    model.addRow(new Object[]{
                            reqId, docId, docName, patientId, patientName, modality, description, status, date
                    });
                }
            }
        }
        updateSelection();
    }

    private void updateSelection() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < model.getRowCount()) {
            String reqId = model.getValueAt(row, 0).toString();
            String doc = model.getValueAt(row, 2).toString() + " (" + model.getValueAt(row, 1).toString() + ")";
            String pat = model.getValueAt(row, 4).toString() + " (" + model.getValueAt(row, 3).toString() + ")";
            String type = model.getValueAt(row, 5).toString();
            String status = model.getValueAt(row, 7).toString();
            String desc = model.getValueAt(row, 6).toString();

            selectedInfoLabel.setText("Selected Requisition: " + reqId + " | Status: " + status + " | Doctor: " + doc + " | Patient: " + pat + " | Type: " + type);
            detailsArea.setText(desc);
            approveButton.setEnabled(true);
            rejectButton.setEnabled(true);
        } else {
            selectedInfoLabel.setText("Select a diagnostic request to review and decide.");
            detailsArea.setText("");
            approveButton.setEnabled(false);
            rejectButton.setEnabled(false);
        }
    }

    private void updateStatus(String newStatus) {
        int row = table.getSelectedRow();
        if (row < 0 || row >= model.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a diagnostic requisition from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reqId = model.getValueAt(row, 0).toString();
        String currentStatus = model.getValueAt(row, 7).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to set status of Request " + reqId + " to '" + newStatus + "'?",
                "Confirm Decision",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        List<String[]> allRows = new ArrayList<>(FileHandler.read(FileHandler.REQUESTS));
        boolean updated = false;
        for (String[] r : allRows) {
            if (r.length >= 7 && r[0].equalsIgnoreCase(reqId)) {
                r[5] = newStatus;
                updated = true;
                break;
            }
        }

        if (updated) {
            FileHandler.writeAll(FileHandler.REQUESTS, allRows);
            refresh();
            JOptionPane.showMessageDialog(this,
                    "Requisition " + reqId + " status updated successfully to '" + newStatus + "'.",
                    "Status Updated",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to locate requisition in database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRequest() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= model.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Please select a diagnostic requisition from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reqId = model.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to permanently delete Diagnostic Requisition " + reqId + "?",
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
            refresh();
            JOptionPane.showMessageDialog(this, "Requisition " + reqId + " was permanently deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to locate requisition in database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
