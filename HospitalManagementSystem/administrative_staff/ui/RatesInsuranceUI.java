package administrative_staff.ui;

import administrative_staff.ux.RatesInsuranceUX;
import common.DataUtil;
import common.FileHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class RatesInsuranceUI extends RatesInsuranceUX {
    public RatesInsuranceUI() {
        super();
        backButton.addActionListener(e -> dispose());

        // Rates Tab listeners
        refreshRatesButton.addActionListener(e -> refreshRates());
        addRateButton.addActionListener(e -> addRate());
        updateRateButton.addActionListener(e -> updateRate());
        deleteRateButton.addActionListener(e -> deleteRate());

        ratesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = ratesTable.getSelectedRow();
                if (row >= 0) {
                    specialtyField.setText(ratesModel.getValueAt(row, 1).toString());
                    baseFeeField.setText(ratesModel.getValueAt(row, 2).toString());
                    emergencyFeeField.setText(ratesModel.getValueAt(row, 3).toString());
                }
            }
        });

        // Insurance Tab listeners
        refreshInsuranceButton.addActionListener(e -> refreshInsurance());
        addInsuranceButton.addActionListener(e -> addInsurance());
        updateInsuranceButton.addActionListener(e -> updateInsurance());
        deleteInsuranceButton.addActionListener(e -> deleteInsurance());

        insuranceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = insuranceTable.getSelectedRow();
                if (row >= 0) {
                    providerNameField.setText(insuranceModel.getValueAt(row, 1).toString());
                    policyTypeField.setText(insuranceModel.getValueAt(row, 2).toString());
                    coverageField.setText(insuranceModel.getValueAt(row, 3).toString());
                    hotlineField.setText(insuranceModel.getValueAt(row, 4).toString());
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

    private void addRate() {
        String spec = specialtyField.getText().trim();
        String base = baseFeeField.getText().trim();
        String emer = emergencyFeeField.getText().trim();

        double baseFee = DataUtil.toDouble(base, -1);
        double emerFee = DataUtil.toDouble(emer, -1);

        if (spec.isEmpty() || baseFee < 0 || emerFee < 0) {
            JOptionPane.showMessageDialog(this, "Please enter a valid specialty and non-negative numeric fees.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = FileHandler.nextId(FileHandler.RATES, "R", 3);
        FileHandler.append(FileHandler.RATES, new String[]{id, spec, String.format("%.2f", baseFee), String.format("%.2f", emerFee)});
        specialtyField.setText("");
        baseFeeField.setText("");
        emergencyFeeField.setText("");
        refreshRates();
        JOptionPane.showMessageDialog(this, "Consultation Rate added successfully with ID: " + id);
    }

    private void updateRate() {
        int row = ratesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a rate row to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String spec = specialtyField.getText().trim();
        String base = baseFeeField.getText().trim();
        String emer = emergencyFeeField.getText().trim();

        double baseFee = DataUtil.toDouble(base, -1);
        double emerFee = DataUtil.toDouble(emer, -1);

        if (spec.isEmpty() || baseFee < 0 || emerFee < 0) {
            JOptionPane.showMessageDialog(this, "Please enter a valid specialty and numeric fees.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = ratesModel.getValueAt(row, 0).toString();
        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.RATES));
        for (String[] r : rows) {
            if (r.length >= 4 && r[0].equals(id)) {
                r[1] = spec;
                r[2] = String.format("%.2f", baseFee);
                r[3] = String.format("%.2f", emerFee);
                break;
            }
        }
        FileHandler.writeAll(FileHandler.RATES, rows);
        refreshRates();
        JOptionPane.showMessageDialog(this, "Rate " + id + " updated successfully!");
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
            specialtyField.setText("");
            baseFeeField.setText("");
            emergencyFeeField.setText("");
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

    private void addInsurance() {
        String name = providerNameField.getText().trim();
        String policy = policyTypeField.getText().trim();
        String cov = coverageField.getText().trim();
        String hotline = hotlineField.getText().trim();

        int covPercent = DataUtil.toInt(cov, -1);
        if (name.isEmpty() || policy.isEmpty() || hotline.isEmpty() || covPercent < 0 || covPercent > 100) {
            JOptionPane.showMessageDialog(this, "Please enter valid provider name, policy type, hotline, and coverage % (0-100).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = FileHandler.nextId(FileHandler.INSURANCE, "INS", 3);
        FileHandler.append(FileHandler.INSURANCE, new String[]{id, name, policy, String.valueOf(covPercent), hotline});
        providerNameField.setText("");
        policyTypeField.setText("");
        coverageField.setText("");
        hotlineField.setText("");
        refreshInsurance();
        JOptionPane.showMessageDialog(this, "Insurance Provider added successfully with ID: " + id);
    }

    private void updateInsurance() {
        int row = insuranceTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an insurance provider to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = providerNameField.getText().trim();
        String policy = policyTypeField.getText().trim();
        String cov = coverageField.getText().trim();
        String hotline = hotlineField.getText().trim();

        int covPercent = DataUtil.toInt(cov, -1);
        if (name.isEmpty() || policy.isEmpty() || hotline.isEmpty() || covPercent < 0 || covPercent > 100) {
            JOptionPane.showMessageDialog(this, "Please enter valid provider name, policy type, hotline, and coverage % (0-100).", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = insuranceModel.getValueAt(row, 0).toString();
        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.INSURANCE));
        for (String[] r : rows) {
            if (r.length >= 5 && r[0].equals(id)) {
                r[1] = name;
                r[2] = policy;
                r[3] = String.valueOf(covPercent);
                r[4] = hotline;
                break;
            }
        }
        FileHandler.writeAll(FileHandler.INSURANCE, rows);
        refreshInsurance();
        JOptionPane.showMessageDialog(this, "Insurance Provider " + id + " updated successfully!");
    }

    private void deleteInsurance() {
        int row = insuranceTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an insurance provider to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = insuranceModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete insurance " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.INSURANCE));
            rows.removeIf(r -> r.length > 0 && r[0].equals(id));
            FileHandler.writeAll(FileHandler.INSURANCE, rows);
            providerNameField.setText("");
            policyTypeField.setText("");
            coverageField.setText("");
            hotlineField.setText("");
            refreshInsurance();
        }
    }
}
