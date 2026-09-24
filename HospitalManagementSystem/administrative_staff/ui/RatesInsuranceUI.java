package administrative_staff.ui;

import administrative_staff.ux.RatesInsuranceUX;
import common.FileHandler;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class RatesInsuranceUI extends RatesInsuranceUX {
    public RatesInsuranceUI() {
        super();
        backButton.addActionListener(e -> dispose());

        // Rates Tab listeners
        refreshRatesButton.addActionListener(e -> refreshRates());
        addRateButton.addActionListener(e -> new RateDialog(this, null, this::refreshRates).setVisible(true));
        editRateButton.addActionListener(e -> openEditRate());
        deleteRateButton.addActionListener(e -> deleteRate());

        ratesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && ratesTable.getSelectedRow() >= 0) {
                    openEditRate();
                }
            }
        });

        // Insurance Tab listeners
        refreshInsuranceButton.addActionListener(e -> refreshInsurance());
        addInsuranceButton.addActionListener(e -> new InsuranceDialog(this, null, this::refreshInsurance).setVisible(true));
        editInsuranceButton.addActionListener(e -> openEditInsurance());
        deleteInsuranceButton.addActionListener(e -> deleteInsurance());

        insuranceTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && insuranceTable.getSelectedRow() >= 0) {
                    openEditInsurance();
                }
            }
        });

        refreshRates();
        refreshInsurance();
    }

    // --- RATES METHODS ---
    private void refreshRates() {
        ratesModel.setRowCount(0);
        for (String[] r : FileHandler.read(FileHandler.RATES)) {
            if (r.length >= 4) {
                ratesModel.addRow(r);
            }
        }
    }

    private void openEditRate() {
        int row = ratesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a consultation rate to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = ratesModel.getValueAt(row, 0).toString();
        String spec = ratesModel.getValueAt(row, 1).toString();
        String base = ratesModel.getValueAt(row, 2).toString();
        String emer = ratesModel.getValueAt(row, 3).toString();
        new RateDialog(this, new String[]{id, spec, base, emer}, this::refreshRates).setVisible(true);
    }

    private void deleteRate() {
        int row = ratesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a rate row to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = ratesModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete rate " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.RATES));
            rows.removeIf(r -> r.length > 0 && r[0].equals(id));
            FileHandler.writeAll(FileHandler.RATES, rows);
            refreshRates();
        }
    }

    // --- INSURANCE METHODS ---
    private void refreshInsurance() {
        insuranceModel.setRowCount(0);
        for (String[] r : FileHandler.read(FileHandler.INSURANCE)) {
            if (r.length >= 5) {
                insuranceModel.addRow(r);
            }
        }
    }

    private void openEditInsurance() {
        int row = insuranceTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an insurance provider to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = insuranceModel.getValueAt(row, 0).toString();
        String name = insuranceModel.getValueAt(row, 1).toString();
        String policy = insuranceModel.getValueAt(row, 2).toString();
        String cov = insuranceModel.getValueAt(row, 3).toString();
        String hotline = insuranceModel.getValueAt(row, 4).toString();
        new InsuranceDialog(this, new String[]{id, name, policy, cov, hotline}, this::refreshInsurance).setVisible(true);
    }

    private void deleteInsurance() {
        int row = insuranceTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an insurance provider to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = insuranceModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete insurance provider " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.INSURANCE));
            rows.removeIf(r -> r.length > 0 && r[0].equals(id));
            FileHandler.writeAll(FileHandler.INSURANCE, rows);
            refreshInsurance();
        }
    }
}
