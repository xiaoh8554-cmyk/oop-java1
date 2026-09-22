package medical_manager.ui;

import common.FileHandler;
import java.util.*;
import javax.swing.*;
import medical_manager.ux.MedicalGradingUX;

public class MedicalGradingUI extends MedicalGradingUX {
    public MedicalGradingUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refresh();
        refreshButton.addActionListener(e -> refresh());
        updateButton.addActionListener(e -> updateGrade());
    }

    private void refresh() {
        model.setRowCount(0);
        String currentManagerId = (common.Session.getCurrentUser() != null && common.Session.getCurrentUser().getRole() != null && "MEDICAL_MANAGER".equalsIgnoreCase(common.Session.getCurrentUser().getRole().name()))
                ? common.Session.getCurrentUser().getUserId() : "";
        List<String> assignedDoctorIds = new ArrayList<>();
        if (!currentManagerId.isEmpty()) {
            for (String[] doc : FileHandler.read("doctors.txt")) {
                if (doc.length >= 4 && doc[3].equalsIgnoreCase(currentManagerId)) {
                    assignedDoctorIds.add(doc[0].trim());
                }
            }
        }

        for (String[] r : FileHandler.read(FileHandler.ASSESSMENTS)) {
            if (r.length >= 10) {
                String docId = r[2].trim();
                if (!assignedDoctorIds.isEmpty() && !assignedDoctorIds.contains(docId)) {
                    continue;
                }
                model.addRow(r);
            }
        }
    }

    private void updateGrade() {
        int i = table.getSelectedRow();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Select an assessment.");
            return;
        }
        String id = model.getValueAt(i, 0).toString();
        List<String[]> rows = new ArrayList<>(FileHandler.read(FileHandler.ASSESSMENTS));
        for (String[] r : rows) {
            if (r.length >= 10 && r[0].equals(id)) {
                r[5] = gradeBox.getSelectedItem().toString();
                r[9] = statusBox.getSelectedItem().toString();
            }
        }
        FileHandler.writeAll(FileHandler.ASSESSMENTS, rows);
        refresh();
        JOptionPane.showMessageDialog(this, "Medical grade updated.");
    }
}
