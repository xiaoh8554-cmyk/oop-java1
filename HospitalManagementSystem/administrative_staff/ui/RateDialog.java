package administrative_staff.ui;

import common.DataUtil;
import common.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RateDialog extends JDialog {
    private final JTextField specialtyField = new JTextField(20);
    private final JTextField baseFeeField = new JTextField(20);
    private final JTextField emergencyFeeField = new JTextField(20);

    private final JButton saveButton = new JButton("Save Rate");
    private final JButton cancelButton = new JButton("Cancel");

    public RateDialog(JFrame parent, String[] existingData, Runnable onSaved) {
        super(parent, existingData == null ? "Add Consultation Rate" : "Edit Consultation Rate (" + existingData[0] + ")", true);
        setSize(450, 270);
        setLocationRelativeTo(parent);
        setResizable(false);

        if (existingData != null && existingData.length >= 4) {
            specialtyField.setText(existingData[1]);
            baseFeeField.setText(existingData[2]);
            emergencyFeeField.setText(existingData[3]);
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Specialty / Department:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(specialtyField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Base Fee (RM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(baseFeeField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Emergency Fee (RM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(emergencyFeeField, gbc);

        row++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            String spec = specialtyField.getText().trim();
            String base = baseFeeField.getText().trim();
            String emer = emergencyFeeField.getText().trim();

            double baseFee = DataUtil.toDouble(base, -1);
            double emerFee = DataUtil.toDouble(emer, -1);

            if (spec.isEmpty() || baseFee < 0 || emerFee < 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid specialty and non-negative numeric fees.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (existingData == null) {
                String id = FileHandler.nextId(FileHandler.RATES, "R", 3);
                FileHandler.append(FileHandler.RATES, new String[]{id, spec, String.format("%.2f", baseFee), String.format("%.2f", emerFee)});
                JOptionPane.showMessageDialog(this, "Consultation Rate added successfully with ID: " + id, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String id = existingData[0];
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
                JOptionPane.showMessageDialog(this, "Rate " + id + " updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            if (onSaved != null) onSaved.run();
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        setContentPane(panel);
    }
}
