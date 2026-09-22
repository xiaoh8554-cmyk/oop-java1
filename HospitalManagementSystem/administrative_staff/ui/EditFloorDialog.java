package administrative_staff.ui;

import common.DataUtil;
import common.FileHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditFloorDialog extends JDialog {
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Floor Name", "Current Rooms", "Max Room Limit", "Available Capacity"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    private final JButton editLimitBtn = new JButton("Edit Selected Limit");
    private final JButton addFloorBtn = new JButton("Add New Floor");
    private final JButton deleteFloorBtn = new JButton("Delete Floor");
    private final JButton closeBtn = new JButton("Close");

    private final Runnable onUpdated;

    public EditFloorDialog(JFrame parent, Runnable onUpdated) {
        super(parent, "Hospital Floor Capacities & Limits", true);
        this.onUpdated = onUpdated;

        setSize(640, 440);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Hospital Building Floor Capacities (data/floor_capacities.txt)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLabel.setForeground(new Color(20, 60, 140));
        root.add(titleLabel, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.add(editLimitBtn);
        btnPanel.add(addFloorBtn);
        btnPanel.add(deleteFloorBtn);
        btnPanel.add(closeBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        editLimitBtn.addActionListener(e -> editLimit());
        addFloorBtn.addActionListener(e -> addNewFloor());
        deleteFloorBtn.addActionListener(e -> deleteFloor());
        closeBtn.addActionListener(e -> dispose());

        refreshTable();
        setContentPane(root);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        // Count existing rooms per floor in assets.txt
        Map<String, Integer> currentCounts = new HashMap<>();
        for (String[] a : FileHandler.read(FileHandler.ASSETS)) {
            if (a.length >= 4) {
                String loc = a[3].trim();
                currentCounts.put(loc.toLowerCase(), currentCounts.getOrDefault(loc.toLowerCase(), 0) + 1);
            }
        }

        List<String[]> floors = FileHandler.read(FileHandler.FLOOR_CAPACITIES);
        for (String[] f : floors) {
            if (f.length >= 2) {
                String floorName = f[0].trim();
                int limit = DataUtil.toInt(f[1].trim(), 10);
                int count = currentCounts.getOrDefault(floorName.toLowerCase(), 0);
                int available = Math.max(0, limit - count);
                String status = count >= limit ? "FULL (" + count + "/" + limit + ")" : available + " rooms available";
                tableModel.addRow(new Object[]{floorName, count, limit, status});
            }
        }
    }

    private void editLimit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a floor from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String floorName = tableModel.getValueAt(row, 0).toString();
        int currentRooms = (int) tableModel.getValueAt(row, 1);
        int currentLimit = (int) tableModel.getValueAt(row, 2);

        String input = JOptionPane.showInputDialog(
                this,
                "Enter new max room limit for " + floorName + " (Current rooms: " + currentRooms + "):",
                String.valueOf(currentLimit)
        );
        if (input == null || input.trim().isEmpty()) return;

        int newLimit = DataUtil.toInt(input.trim(), -1);
        if (newLimit < 1) {
            JOptionPane.showMessageDialog(this, "Max room limit must be a positive number.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newLimit < currentRooms) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Warning: The new limit (" + newLimit + ") is lower than currently created rooms (" + currentRooms + ").\nDo you want to proceed?",
                    "Limit Lower Than Current Rooms",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.FLOOR_CAPACITIES));
        boolean found = false;
        for (String[] r : rows) {
            if (r.length >= 2 && r[0].trim().equalsIgnoreCase(floorName)) {
                r[1] = String.valueOf(newLimit);
                found = true;
                break;
            }
        }
        if (!found) {
            rows.add(new String[]{floorName, String.valueOf(newLimit)});
        }
        FileHandler.writeAll(FileHandler.FLOOR_CAPACITIES, rows);

        refreshTable();
        if (onUpdated != null) onUpdated.run();
        JOptionPane.showMessageDialog(this, "Capacity limit for " + floorName + " updated to " + newLimit + " rooms.", "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addNewFloor() {
        String floorName = JOptionPane.showInputDialog(this, "Enter New Floor Name (e.g., Level 7):", "Add New Floor", JOptionPane.QUESTION_MESSAGE);
        if (floorName == null || floorName.trim().isEmpty()) return;
        floorName = floorName.trim();

        // Check if exists
        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.FLOOR_CAPACITIES));
        for (String[] r : rows) {
            if (r.length >= 1 && r[0].trim().equalsIgnoreCase(floorName)) {
                JOptionPane.showMessageDialog(this, "Floor '" + floorName + "' already exists.", "Duplicate", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String capInput = JOptionPane.showInputDialog(this, "Enter initial room capacity for " + floorName + ":", "10");
        if (capInput == null || capInput.trim().isEmpty()) return;

        int cap = DataUtil.toInt(capInput.trim(), -1);
        if (cap < 1) {
            JOptionPane.showMessageDialog(this, "Capacity must be a positive number.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
            return;
        }

        rows.add(new String[]{floorName, String.valueOf(cap)});
        FileHandler.writeAll(FileHandler.FLOOR_CAPACITIES, rows);

        refreshTable();
        if (onUpdated != null) onUpdated.run();
        JOptionPane.showMessageDialog(this, "New floor '" + floorName + "' added with capacity " + cap + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteFloor() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a floor to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String floorName = tableModel.getValueAt(row, 0).toString();
        int currentRooms = (int) tableModel.getValueAt(row, 1);

        if (currentRooms > 0) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete floor '" + floorName + "' because it currently contains " + currentRooms + " room(s).\nPlease remove or relocate the rooms first.",
                    "Cannot Delete Floor", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete floor '" + floorName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.FLOOR_CAPACITIES));
            rows.removeIf(r -> r.length >= 1 && r[0].trim().equalsIgnoreCase(floorName));
            FileHandler.writeAll(FileHandler.FLOOR_CAPACITIES, rows);

            refreshTable();
            if (onUpdated != null) onUpdated.run();
            JOptionPane.showMessageDialog(this, "Floor '" + floorName + "' has been deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
