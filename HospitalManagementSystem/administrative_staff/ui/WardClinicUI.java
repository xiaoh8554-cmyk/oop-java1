package administrative_staff.ui;

import administrative_staff.ux.WardClinicUX;
import common.FileHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class WardClinicUI extends WardClinicUX {
    public WardClinicUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> refresh());
        addButton.addActionListener(e -> new AddWardClinicDialog(this, this::refresh).setVisible(true));
        deleteButton.addActionListener(e -> deleteWard());
        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        for (String[] r : FileHandler.read(FileHandler.WARDS)) {
            if (r.length >= 5) {
                model.addRow(r);
            }
        }
    }

    private void deleteWard() {
        int i = table.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Select a ward / clinic to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = model.getValueAt(i, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.WARDS));
            rows.removeIf(r -> r.length > 0 && r[0].equals(id));
            FileHandler.writeAll(FileHandler.WARDS, rows);
            refresh();
        }
    }
}
