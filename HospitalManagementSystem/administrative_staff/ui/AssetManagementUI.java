package administrative_staff.ui;

import administrative_staff.ux.AssetManagementUX;
import common.DataUtil;
import common.FileHandler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssetManagementUI extends AssetManagementUX {
    public AssetManagementUI() {
        super();

        // Tab 1 listeners
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> {
            loadDoctorAndManagerOptions();
            refresh();
        });
        addButton.addActionListener(e -> addAsset());
        allocateButton.addActionListener(e -> allocateAsset());
        releaseButton.addActionListener(e -> releaseAsset());
        maintenanceButton.addActionListener(e -> setMaintenance());
        deleteButton.addActionListener(e -> deleteAsset());
        addFloorButton.addActionListener(e -> openEditFloorDialog());
        editFloorButton.addActionListener(e -> openEditFloorDialog());

        // Initialize floor filter dynamically from floor_capacities.txt
        onFloorsUpdated();

        filterTypeBox.addActionListener(e -> refresh());
        filterFloorBox.addActionListener(e -> refresh());
        resetFilterButton.addActionListener(e -> {
            filterTypeBox.setSelectedIndex(0);
            filterFloorBox.setSelectedIndex(0);
            searchField.setText("");
            refresh();
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0 && model.getColumnCount() >= 7) {
                    String alloc = model.getValueAt(row, 6).toString();
                    setSelectedAllocation(alloc);
                }
            }
        });

        // Tab 2 listeners
        backButton2.addActionListener(e -> dispose());
        refreshAdmissionsButton.addActionListener(e -> refreshAdmissions());
        assignBedButton.addActionListener(e -> assignBedToAdmission());
        dischargeButton.addActionListener(e -> dischargeAdmission());

        // Initial loads
        loadDoctorAndManagerOptions();
        refresh();
        refreshAdmissions();
    }

    private void loadDoctorAndManagerOptions() {
        allocateToBox.removeAllItems();
        allocateToBox.addItem("-- Select Doctor or Manager --");
        for (String[] u : FileHandler.read(FileHandler.USERS)) {
            if (u.length >= 5) {
                String role = u[0];
                String id = u[1];
                String name = u[4];
                if ("DOCTOR".equalsIgnoreCase(role)) {
                    allocateToBox.addItem(id + " - " + name + " (Doctor)");
                } else if ("MEDICAL_MANAGER".equalsIgnoreCase(role)) {
                    allocateToBox.addItem(id + " - " + name + " (Manager)");
                }
            }
        }
    }

    private void setSelectedAllocation(String targetId) {
        if (targetId == null || targetId.equalsIgnoreCase("None") || targetId.isEmpty()) {
            if (allocateToBox.getItemCount() > 0) allocateToBox.setSelectedIndex(0);
            return;
        }
        for (int i = 0; i < allocateToBox.getItemCount(); i++) {
            String item = allocateToBox.getItemAt(i);
            if (item.startsWith(targetId + " - ") || item.equalsIgnoreCase(targetId)) {
                allocateToBox.setSelectedIndex(i);
                return;
            }
        }
    }

    // ==========================================
    // TAB 1: INVENTORY & DIRECT ALLOCATION
    // ==========================================
    private void refresh() {
        model.setRowCount(0);
        String selectedType = (String) filterTypeBox.getSelectedItem();
        String selectedFloor = (String) filterFloorBox.getSelectedItem();
        String query = searchField.getText().trim().toLowerCase();

        List<String[]> rows = FileHandler.read(FileHandler.ASSETS);
        int floorRoomCount = 0;
        int totalRooms = rows.size();

        for (String[] r : rows) {
            String[] displayRow = null;
            if (r.length >= 7) {
                displayRow = r;
            } else if (r.length >= 5) {
                displayRow = new String[]{r[0], r[1], r[2], r[3], r[4], "AVAILABLE", "None"};
            }
            if (displayRow == null) continue;

            String loc = displayRow[3];
            if (selectedFloor != null && !"ALL".equalsIgnoreCase(selectedFloor)) {
                if (loc.toLowerCase().contains(selectedFloor.toLowerCase())) {
                    floorRoomCount++;
                }
            }

            // Type filter
            if (selectedType != null && !"ALL".equalsIgnoreCase(selectedType)) {
                if (!selectedType.equalsIgnoreCase(displayRow[2])) continue;
            }

            // Floor filter
            if (selectedFloor != null && !"ALL".equalsIgnoreCase(selectedFloor)) {
                if (!loc.toLowerCase().contains(selectedFloor.toLowerCase())) continue;
            }

            // Search query filter
            if (!query.isEmpty()) {
                String id = displayRow[0].toLowerCase();
                String name = displayRow[1].toLowerCase();
                String type = displayRow[2].toLowerCase();
                String locLower = loc.toLowerCase();
                String status = displayRow[5].toLowerCase();
                String allocated = displayRow[6].toLowerCase();

                if (!id.contains(query) && !name.contains(query) && !type.contains(query)
                        && !locLower.contains(query) && !status.contains(query) && !allocated.contains(query)) {
                    continue;
                }
            }

            model.addRow(displayRow);
        }

        // Update Floor capacity status label
        Map<String, Integer> floorLimits = AddAssetDialog.loadFloorLimits();
        if (selectedFloor != null && !"ALL".equalsIgnoreCase(selectedFloor)) {
            int maxForFloor = floorLimits.getOrDefault(selectedFloor, 10);
            floorCountLabel.setText(selectedFloor + " Rooms: " + floorRoomCount + " / " + maxForFloor);
        } else {
            floorCountLabel.setText("Total Rooms in Hospital: " + totalRooms);
        }
    }

    private void openEditFloorDialog() {
        new EditFloorDialog(this, this::onFloorsUpdated).setVisible(true);
    }

    private void onFloorsUpdated() {
        Object selected = filterFloorBox.getSelectedItem();
        filterFloorBox.removeAllItems();
        filterFloorBox.addItem("ALL");
        for (String[] f : FileHandler.read(FileHandler.FLOOR_CAPACITIES)) {
            if (f.length >= 1) {
                filterFloorBox.addItem(f[0].trim());
            }
        }
        if (selected != null) {
            filterFloorBox.setSelectedItem(selected);
        }
        refresh();
    }

    private void addAsset() {
        new AddAssetDialog(this, this::refresh).setVisible(true);
    }

    private void allocateAsset() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an asset from the table to allocate.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedUserIndex = allocateToBox.getSelectedIndex();
        if (selectedUserIndex <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a Doctor or Medical Manager from the dropdown.", "No Doctor/Manager Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String targetStr = (String) allocateToBox.getSelectedItem();
        String targetId = targetStr.split(" - ")[0].trim();

        String id = model.getValueAt(row, 0).toString();
        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.ASSETS));
        for (String[] r : rows) {
            if (r.length >= 7 && r[0].equals(id)) {
                r[5] = "ALLOCATED";
                r[6] = targetId;
                break;
            }
        }
        FileHandler.writeAll(FileHandler.ASSETS, rows);
        refresh();
        JOptionPane.showMessageDialog(this, "Asset " + id + " has been successfully allocated to " + targetStr + "!");
    }

    private void releaseAsset() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an asset to release.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.ASSETS));
        for (String[] r : rows) {
            if (r.length >= 7 && r[0].equals(id)) {
                r[5] = "AVAILABLE";
                r[6] = "None";
                break;
            }
        }
        FileHandler.writeAll(FileHandler.ASSETS, rows);
        if (allocateToBox.getItemCount() > 0) allocateToBox.setSelectedIndex(0);
        refresh();
        JOptionPane.showMessageDialog(this, "Asset " + id + " is now released and set to AVAILABLE.");
    }

    private void setMaintenance() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an asset to set under maintenance.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.ASSETS));
        for (String[] r : rows) {
            if (r.length >= 7 && r[0].equals(id)) {
                r[5] = "MAINTENANCE";
                r[6] = "None";
                break;
            }
        }
        FileHandler.writeAll(FileHandler.ASSETS, rows);
        if (allocateToBox.getItemCount() > 0) allocateToBox.setSelectedIndex(0);
        refresh();
        JOptionPane.showMessageDialog(this, "Asset " + id + " status set to MAINTENANCE.");
    }

    private void deleteAsset() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an asset to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete asset " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.ASSETS));
            rows.removeIf(r -> r.length > 0 && r[0].equals(id));
            FileHandler.writeAll(FileHandler.ASSETS, rows);
            if (allocateToBox.getItemCount() > 0) allocateToBox.setSelectedIndex(0);
            refresh();
        }
    }

    // ==========================================
    // TAB 2: INPATIENT ADMISSION & BED QUEUE
    // ==========================================
    private void refreshAdmissions() {
        admissionModel.setRowCount(0);
        List<String[]> admissions = FileHandler.read(FileHandler.ADMISSIONS);
        for (String[] row : admissions) {
            if (row.length >= 10) {
                admissionModel.addRow(row);
            }
        }
    }

    private void assignBedToAdmission() {
        int row = admissionTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a pending admission request from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = admissionModel.getValueAt(row, 7).toString();
        if (!"PENDING".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only PENDING admission requests can be assigned a bed (Current Status: " + status + ").", "Invalid Status", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String admId = admissionModel.getValueAt(row, 0).toString();
        String patientId = admissionModel.getValueAt(row, 1).toString();
        String requestedWard = admissionModel.getValueAt(row, 3).toString();

        List<String[]> assets = FileHandler.read(FileHandler.ASSETS);
        List<String> availableOptions = new ArrayList<>();
        List<String> assetIds = new ArrayList<>();

        for (String[] a : assets) {
            if (a.length >= 7 && "AVAILABLE".equalsIgnoreCase(a[5])) {
                String type = a[2];
                if ("INPATIENT_WARD".equalsIgnoreCase(type) || "ICU".equalsIgnoreCase(type) || type.toUpperCase().contains(requestedWard.toUpperCase())) {
                    String desc = a[0] + " - " + a[1] + " (" + a[3] + " | Cap: " + a[4] + ")";
                    availableOptions.add(desc);
                    assetIds.add(a[0]);
                }
            }
        }

        if (availableOptions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No AVAILABLE beds currently found for requested ward type: " + requestedWard + "\n" +
                    "Please check floor availability or wait for an inpatient discharge.",
                    "No Vacancy", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object selected = JOptionPane.showInputDialog(
                this,
                "Select an available room/bed for Patient " + patientId + ":",
                "Assign Room / Bed (" + admId + ")",
                JOptionPane.QUESTION_MESSAGE,
                null,
                availableOptions.toArray(),
                availableOptions.get(0)
        );

        if (selected == null) return;

        int chosenIndex = availableOptions.indexOf(selected);
        String chosenAssetId = assetIds.get(chosenIndex);

        for (String[] a : assets) {
            if (a.length >= 7 && a[0].equals(chosenAssetId)) {
                a[5] = "ALLOCATED";
                a[6] = patientId;
                break;
            }
        }
        FileHandler.writeAll(FileHandler.ASSETS, assets);

        List<String[]> admissions = FileHandler.read(FileHandler.ADMISSIONS);
        for (String[] adm : admissions) {
            if (adm.length >= 10 && adm[0].equals(admId)) {
                adm[6] = chosenAssetId;
                adm[7] = "ADMITTED";
                break;
            }
        }
        FileHandler.writeAll(FileHandler.ADMISSIONS, admissions);

        refresh();
        refreshAdmissions();

        JOptionPane.showMessageDialog(this,
                "Patient " + patientId + " has been successfully ADMITTED to " + selected + "!\n" +
                "Bed allocated and recorded in system.",
                "Bed Assigned Successfully",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void dischargeAdmission() {
        int row = admissionTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an ADMITTED patient record to discharge.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = admissionModel.getValueAt(row, 7).toString();
        if (!"ADMITTED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Only currently ADMITTED patients can be discharged (Current Status: " + status + ").", "Invalid Status", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String admId = admissionModel.getValueAt(row, 0).toString();
        String patientId = admissionModel.getValueAt(row, 1).toString();
        String assignedAssetId = admissionModel.getValueAt(row, 6).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to discharge Patient " + patientId + " (Admission ID: " + admId + ")?\n" +
                "This will release room/bed " + assignedAssetId + " back to AVAILABLE.",
                "Confirm Patient Discharge",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        List<String[]> assets = FileHandler.read(FileHandler.ASSETS);
        for (String[] a : assets) {
            if (a.length >= 7 && a[0].equals(assignedAssetId)) {
                a[5] = "AVAILABLE";
                a[6] = "None";
                break;
            }
        }
        FileHandler.writeAll(FileHandler.ASSETS, assets);

        List<String[]> admissions = FileHandler.read(FileHandler.ADMISSIONS);
        for (String[] adm : admissions) {
            if (adm.length >= 10 && adm[0].equals(admId)) {
                adm[7] = "DISCHARGED";
                adm[9] = DataUtil.today();
                break;
            }
        }
        FileHandler.writeAll(FileHandler.ADMISSIONS, admissions);

        refresh();
        refreshAdmissions();

        JOptionPane.showMessageDialog(this,
                "Patient " + patientId + " successfully discharged!\n" +
                "Room/bed " + assignedAssetId + " is now set to AVAILABLE for new patients.",
                "Discharge Completed",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
