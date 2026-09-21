package administrative_staff.ui;

import common.DataUtil;
import common.FileHandler;

import javax.swing.*;
import java.awt.*;

public class AddAssetDialog extends JDialog {
    private final JTextField nameField = new JTextField(20);
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{
            "CONSULTATION_ROOM", "INPATIENT_WARD", "LAB", "IMAGING_ROOM"
    });
    private final JTextField locationField = new JTextField(20);
    private final JTextField capacityField = new JTextField(20);

    private final JButton saveButton = new JButton("Create Asset");
    private final JButton cancelButton = new JButton("Cancel");

    public AddAssetDialog(JFrame parent, Runnable onSaved) {
        super(parent, "Create New Hospital Asset", true);
        setSize(450, 320);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Asset Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Asset Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(typeBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Location / Block:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(locationField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Capacity / Beds:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(capacityField, gbc);

        row++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
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

            JOptionPane.showMessageDialog(this, "Hospital Asset created successfully with ID: " + id, "Success", JOptionPane.INFORMATION_MESSAGE);
            if (onSaved != null) onSaved.run();
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        setContentPane(panel);
    }
}
