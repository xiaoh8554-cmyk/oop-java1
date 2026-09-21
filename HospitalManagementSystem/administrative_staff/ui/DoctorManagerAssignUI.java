package administrative_staff.ui;

import administrative_staff.ux.DoctorManagerAssignUX;
import common.DataManager;
import common.model.User;
import common.model.UserRole;
import doctors.Doctor;
import medical_manager.MedicalManager;

import javax.swing.*;
import java.util.*;

public class DoctorManagerAssignUI extends DoctorManagerAssignUX {
    private final Map<String, String> managerIdMap = new HashMap<>();

    public DoctorManagerAssignUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> refresh());
        assignButton.addActionListener(e -> assignDoctor());
        unassignButton.addActionListener(e -> unassignDoctor());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedLabel();
            }
        });

        loadManagers();
        refresh();
    }

    private void loadManagers() {
        managerBox.removeAllItems();
        managerIdMap.clear();

        for (User u : DataManager.getInstance().getAllUsers()) {
            if (u.getRole() == UserRole.MEDICAL_MANAGER && u instanceof MedicalManager) {
                MedicalManager mm = (MedicalManager) u;
                String display = mm.getId() + " - " + mm.getFullName() + " (Office: " + mm.getOfficeNumber() + ")";
                managerIdMap.put(display, mm.getId());
                managerBox.addItem(display);
            }
        }
    }

    private void updateSelectedLabel() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String docId = model.getValueAt(row, 0).toString();
            String docName = model.getValueAt(row, 1).toString();
            selectedDoctorLabel.setText("Selected Doctor: " + docId + " - " + docName);
        } else {
            selectedDoctorLabel.setText("Selected Doctor: (None)");
        }
    }

    private void refresh() {
        model.setRowCount(0);
        loadManagers();

        Map<String, MedicalManager> managerMap = new HashMap<>();
        for (User u : DataManager.getInstance().getAllUsers()) {
            if (u instanceof MedicalManager) {
                managerMap.put(u.getId(), (MedicalManager) u);
            }
        }

        for (User u : DataManager.getInstance().getAllUsers()) {
            if (u instanceof Doctor) {
                Doctor d = (Doctor) u;
                String mgrId = d.getAssignedManagerId();
                String mgrName = "-";
                String mgrOffice = "-";

                if (mgrId != null && !mgrId.equalsIgnoreCase("None") && managerMap.containsKey(mgrId)) {
                    MedicalManager mm = managerMap.get(mgrId);
                    mgrName = mm.getFullName();
                    mgrOffice = mm.getOfficeNumber();
                }

                model.addRow(new Object[]{
                        d.getId(),
                        d.getFullName(),
                        d.getSpecialty(),
                        (mgrId == null || mgrId.trim().isEmpty() ? "None" : mgrId),
                        mgrName,
                        mgrOffice
                });
            }
        }
        updateSelectedLabel();
    }

    private void assignDoctor() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedItem = (String) managerBox.getSelectedItem();
        if (selectedItem == null || !managerIdMap.containsKey(selectedItem)) {
            JOptionPane.showMessageDialog(this, "No Medical Manager selected.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String doctorId = model.getValueAt(row, 0).toString();
        String managerId = managerIdMap.get(selectedItem);

        User docUser = DataManager.getInstance().findById(doctorId);
        if (docUser instanceof Doctor) {
            Doctor doc = (Doctor) docUser;
            doc.setAssignedManagerId(managerId);
            DataManager.getInstance().updateUser(doc);
            refresh();
            JOptionPane.showMessageDialog(this, "Doctor " + doc.getFullName() + " (" + doctorId + ") has been assigned to Manager " + managerId + " successfully!");
        }
    }

    private void unassignDoctor() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String doctorId = model.getValueAt(row, 0).toString();
        User docUser = DataManager.getInstance().findById(doctorId);
        if (docUser instanceof Doctor) {
            Doctor doc = (Doctor) docUser;
            doc.setAssignedManagerId("None");
            DataManager.getInstance().updateUser(doc);
            refresh();
            JOptionPane.showMessageDialog(this, "Doctor " + doc.getFullName() + " (" + doctorId + ") has been unassigned.");
        }
    }
}
