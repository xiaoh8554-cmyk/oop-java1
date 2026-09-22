package administrative_staff.ui;

import common.DataUtil;
import common.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AddAssetDialog extends JDialog {
    private final JTextField nameField = new JTextField(20);
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{
            "CONSULTATION_ROOM", "INPATIENT_WARD", "ICU", "CLINIC", "IMAGING_ROOM", "LAB", "OFFICE"
    });
    private final JComboBox<String> floorBox = new JComboBox<>(new String[]{
            "Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6"
    });
    private final JTextField capacityField = new JTextField("1", 20);

    private final JButton saveButton = new JButton("Create Asset");
    private final JButton cancelButton = new JButton("Cancel");

    public AddAssetDialog(JFrame parent, Runnable onSaved) {
        super(parent, "Create New Hospital Asset", true);
        setSize(460, 330);
        setLocationRelativeTo(parent);
        setResizable(false);

        floorBox.removeAllItems();
        for (String[] f : FileHandler.read(FileHandler.FLOOR_CAPACITIES)) {
            if (f.length >= 1) {
                floorBox.addItem(f[0].trim());
            }
        }
        if (floorBox.getItemCount() == 0) {
            floorBox.addItem("Level 1");
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Asset / Room Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Asset Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(typeBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Floor / Level:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(floorBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Capacity / Beds:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
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
            String loc = (String) floorBox.getSelectedItem();
            String cap = capacityField.getText().trim();

            if (name.isEmpty() || loc == null || loc.isEmpty() || cap.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please complete all fields for the asset.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int capacity = DataUtil.toInt(cap, -1);
            if (capacity < 1) {
                JOptionPane.showMessageDialog(this, "Capacity must be a positive number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check floor capacity limit
            Map<String, Integer> floorLimits = loadFloorLimits();
            int maxRooms = floorLimits.getOrDefault(loc, 10);

            int currentRoomCount = 0;
            for (String[] a : FileHandler.read(FileHandler.ASSETS)) {
                if (a.length >= 4 && a[3].equalsIgnoreCase(loc)) {
                    currentRoomCount++;
                }
            }

            if (currentRoomCount >= maxRooms) {
                JOptionPane.showMessageDialog(this,
                        "Cannot create room: " + loc + " has reached its maximum room limit of " + maxRooms + " (" + currentRoomCount + "/" + maxRooms + ").\n" +
                        "Use 'Add / Configure Floor Capacity' on the asset dashboard to adjust the floor limit if needed.",
                        "Floor Capacity Exceeded", JOptionPane.WARNING_MESSAGE);
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

    public static Map<String, Integer> loadFloorLimits() {
        Map<String, Integer> map = new HashMap<>();
        // Default standard quantities
        map.put("Level 1", 10);
        map.put("Level 2", 5);
        map.put("Level 3", 5);
        map.put("Level 4", 5);
        map.put("Level 5", 10);
        map.put("Level 6", 5);

        for (String[] row : FileHandler.read(FileHandler.FLOOR_CAPACITIES)) {
            if (row.length >= 2) {
                int qty = DataUtil.toInt(row[1], -1);
                if (qty > 0) {
                    map.put(row[0].trim(), qty);
                }
            }
        }
        return map;
    }
}
