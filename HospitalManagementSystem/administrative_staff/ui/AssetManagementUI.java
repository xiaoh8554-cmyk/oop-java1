package administrative_staff.ui;

import administrative_staff.ux.AssetManagementUX;
import common.DataUtil;
import common.FileHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AssetManagementUI extends AssetManagementUX {
    public AssetManagementUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> refresh());
        addButton.addActionListener(e -> addAsset());
        allocateButton.addActionListener(e -> allocateAsset());
        releaseButton.addActionListener(e -> releaseAsset());
        maintenanceButton.addActionListener(e -> setMaintenance());
        deleteButton.addActionListener(e -> deleteAsset());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0 && model.getColumnCount() >= 7) {
                    String alloc = model.getValueAt(row, 6).toString();
                    allocateToField.setText(alloc.equalsIgnoreCase("None") ? "" : alloc);
                }
            }
        });

        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        List<String[]> rows = FileHandler.read(FileHandler.ASSETS);
        for (String[] r : rows) {
            if (r.length >= 7) {
                model.addRow(r);
            } else if (r.length >= 5) {
                // Fallback for 5-part legacy rows
                model.addRow(new Object[]{r[0], r[1], r[2], r[3], r[4], "AVAILABLE", "None"});
            }
        }
    }

    private void addAsset() {
        String name = nameField.getText().trim();
        String type = (String) typeBox.getSelectedItem();
        String loc = locationField.getText().trim();
        String cap = capacityField.getText().trim();

        if (name.isEmpty() || loc.isEmpty() || cap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete all fields for the asset.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int capacity = DataUtil.toInt(cap, -1);
        if (capacity < 1) {
            JOptionPane.showMessageDialog(this, "Capacity must be a positive number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = FileHandler.nextId(FileHandler.ASSETS, "AST", 3);
        FileHandler.append(FileHandler.ASSETS, new String[]{id, name, type, loc, String.valueOf(capacity), "AVAILABLE", "None"});

        nameField.setText("");
        locationField.setText("");
        capacityField.setText("");
        refresh();
        JOptionPane.showMessageDialog(this, "Hospital Asset created successfully with ID: " + id);
    }

    private void allocateAsset() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an asset from the table to allocate.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String target = allocateToField.getText().trim();
        if (target.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the recipient ID or department name to allocate to.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.ASSETS));
        for (String[] r : rows) {
            if (r.length >= 7 && r[0].equals(id)) {
                r[5] = "ALLOCATED";
                r[6] = target;
                break;
            }
        }
        FileHandler.writeAll(FileHandler.ASSETS, rows);
        refresh();
        JOptionPane.showMessageDialog(this, "Asset " + id + " has been allocated to " + target + " successfully!");
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
        allocateToField.setText("");
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
        allocateToField.setText("");
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
            allocateToField.setText("");
            refresh();
        }
    }
}
