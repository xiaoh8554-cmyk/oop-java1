package administrative_staff.ui;

import common.AuthService;
import common.DataManager;
import common.DataUtil;
import common.FileHandler;
import common.model.User;
import patients.Patient;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddBillDialog extends JDialog {
    private final JComboBox<String> patientBox = new JComboBox<>();
    private final JTextField assessmentField = new JTextField(20);
    private final JTextField amountField = new JTextField(20);

    private final JButton saveButton = new JButton("Create Bill");
    private final JButton cancelButton = new JButton("Cancel");

    public AddBillDialog(JFrame parent, Runnable onSaved) {
        super(parent, "Create New Patient Bill", true);
        setSize(480, 280);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Populate patient dropdown
        patientBox.setEditable(true);
        List<User> allUsers = DataManager.getInstance().getAllUsers();
        for (User u : allUsers) {
            if (u instanceof Patient || (u.getRole() != null && "PATIENT".equalsIgnoreCase(u.getRole().name()))) {
                patientBox.addItem(u.getId() + " - " + u.getFullName());
            }
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(patientBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Assessment ID (optional):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(assessmentField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel("Amount (RM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(amountField, gbc);

        row++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            Object selectedItem = patientBox.getSelectedItem();
            if (selectedItem == null || selectedItem.toString().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select or enter a Patient ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String patientStr = selectedItem.toString().trim();
            String patientId = patientStr.contains(" - ") ? patientStr.split(" - ")[0].trim() : patientStr;

            if (!AuthService.userIdExists(patientId, "PATIENT")) {
                JOptionPane.showMessageDialog(this, "Patient ID '" + patientId + "' does not exist.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String assessmentId = assessmentField.getText().trim();
            String amtStr = amountField.getText().trim();
            double amount = DataUtil.toDouble(amtStr, -1);
            if (amount < 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid non-negative amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = FileHandler.nextId(FileHandler.BILLING, "B", 4);
            FileHandler.append(FileHandler.BILLING, new String[]{
                    id, patientId, assessmentId, String.format("%.2f", amount), "UNPAID", DataUtil.today()
            });

            JOptionPane.showMessageDialog(this, "Bill created successfully with ID: " + id, "Success", JOptionPane.INFORMATION_MESSAGE);
            if (onSaved != null) onSaved.run();
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        setContentPane(panel);
    }
}
