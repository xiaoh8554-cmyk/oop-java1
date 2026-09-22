package medical_manager.ui;

import common.*;
import medical_manager.ux.AssessmentTypeUX;
import javax.swing.*;
import java.util.*;

public class AssessmentTypeUI extends AssessmentTypeUX {
    public AssessmentTypeUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refresh();
        addButton.addActionListener(e -> add());
        deleteButton.addActionListener(e -> del());
        refreshButton.addActionListener(e -> refresh());
    }

    private void refresh() {
        model.setRowCount(0);
        for (String[] r : FileHandler.read(FileHandler.ASSESSMENT_TYPES)) {
            if (r.length >= 4) {
                model.addRow(r);
            }
        }
    }

    private void add() {
        String n = nameField.getText().trim();
        String d = descriptionField.getText().trim();
        String f = feeField.getText().trim();
        double fee = DataUtil.toDouble(f, -1);
        if (n.isEmpty() || d.isEmpty() || fee < 0) {
            JOptionPane.showMessageDialog(this, "Enter valid name, description and fee.");
            return;
        }

        // Add to assessment_types.txt
        String id = FileHandler.nextId(FileHandler.ASSESSMENT_TYPES, "AT", 3);
        FileHandler.append(FileHandler.ASSESSMENT_TYPES, new String[]{id, n, d, String.format("%.2f", fee)});

        // Also sync/link to rates.txt
        String rateId = FileHandler.nextId(FileHandler.RATES, "R", 3);
        FileHandler.append(FileHandler.RATES, new String[]{
                rateId,
                n + " (" + id + ")",
                String.format("%.2f", fee),
                String.format("%.2f", fee * 1.5)
        });

        nameField.setText("");
        descriptionField.setText("");
        feeField.setText("");
        refresh();
        JOptionPane.showMessageDialog(this, "Assessment type added and linked to Rates (Rate ID: " + rateId + ").");
    }

    private void del() {
        int i = table.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Select a row.");
            return;
        }
        String id = model.getValueAt(i, 0).toString();
        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.ASSESSMENT_TYPES));
        rows.removeIf(r -> r.length > 0 && r[0].equals(id));
        FileHandler.writeAll(FileHandler.ASSESSMENT_TYPES, rows);
        refresh();
    }
}
