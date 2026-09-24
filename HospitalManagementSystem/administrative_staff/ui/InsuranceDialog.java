package administrative_staff.ui;

import common.DataUtil;
import common.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InsuranceDialog extends JDialog {
    private final JTextField providerNameField = new JTextField(20);
    private final JTextField policyTypeField = new JTextField(20);
    private final JTextField coverageField = new JTextField(20);
    private final JTextField hotlineField = new JTextField(20);

    private final JButton saveButton = new JButton("Save Provider");
    private final JButton cancelButton = new JButton("Cancel");

    public InsuranceDialog(JFrame parent, String[] existingData, Runnable onSaved) {
        super(parent, existingData == null ? "Add Insurance Provider" : "Edit Insurance Provider (" + existingData[0] + ")", true);
        setSize(460, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        if (existingData != null && existingData.length >= 5) {
            providerNameField.setText(existingData[1]);
            policyTypeField.setText(existingData[2]);
            coverageField.setText(existingData[3]);
            hotlineField.setText(existingData[4]);
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Provider Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(providerNameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Policy / Plan Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(policyTypeField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Coverage (%):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(coverageField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Contact Hotline:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(hotlineField, gbc);

        row++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            String name = providerNameField.getText().trim();
            String policy = policyTypeField.getText().trim();
            String cov = coverageField.getText().trim();
            String hotline = hotlineField.getText().trim();

            int covPercent = DataUtil.toInt(cov, -1);
            if (name.isEmpty() || policy.isEmpty() || hotline.isEmpty() || covPercent < 0 || covPercent > 100) {
                JOptionPane.showMessageDialog(this, "Please enter valid provider name, policy type, hotline, and coverage % (0-100).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (existingData == null) {
                String id = FileHandler.nextId(FileHandler.INSURANCE, "INS", 3);
                FileHandler.append(FileHandler.INSURANCE, new String[]{id, name, policy, String.valueOf(covPercent), hotline});
                JOptionPane.showMessageDialog(this, "Insurance Provider added successfully with ID: " + id, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String id = existingData[0];
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
                JOptionPane.showMessageDialog(this, "Insurance Provider " + id + " updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            if (onSaved != null) onSaved.run();
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        setContentPane(panel);
    }
}
