package administrative_staff.ui;

import administrative_staff.ux.BillingUX;
import common.FileHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class BillingUI extends BillingUX {
    public BillingUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> refresh());
        addButton.addActionListener(e -> new AddBillDialog(this, this::refresh).setVisible(true));
        paidButton.addActionListener(e -> markPaid());
        deleteButton.addActionListener(e -> deleteBill());
        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        for (String[] r : FileHandler.read(FileHandler.BILLING)) {
            if (r.length >= 6) {
                model.addRow(r);
            }
        }
    }

    private void markPaid() {
        int i = table.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Select a bill to mark as paid.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = model.getValueAt(i, 0).toString();
        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.BILLING));
        for (String[] r : rows) {
            if (r.length >= 5 && r[0].equals(id)) {
                r[4] = "PAID";
            }
        }
        FileHandler.writeAll(FileHandler.BILLING, rows);
        refresh();
        JOptionPane.showMessageDialog(this, "Bill " + id + " marked as PAID.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteBill() {
        int i = table.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Select a bill to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = model.getValueAt(i, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete bill " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.BILLING));
            rows.removeIf(r -> r.length > 0 && r[0].equals(id));
            FileHandler.writeAll(FileHandler.BILLING, rows);
            refresh();
        }
    }
}
