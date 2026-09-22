package medical_manager.ui;

import common.ui.DatePicker;
import java.awt.*;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import medical_manager.ux.ShiftRosterUX;

public class ShiftRosterUI extends JFrame {
    private JTable rosterTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbDepartment;
    private JComboBox<String> cbDoctor;
    private DatePicker datePicker;
    private JComboBox<String> cbTime;
    private JComboBox<String> cbLocation;
    private JComboBox<String> cbStatus;
    private JComboBox<String> filterDepartment;
    private JComboBox<String> filterDoctor;
    private DatePicker filterDatePicker;
    private JCheckBox filterDateEnabled;
    private String selectedRosterId = "";
    private final String currentManagerId;

    public ShiftRosterUI() {
        currentManagerId = (common.Session.getCurrentUser() != null && common.Session.getCurrentUser().getRole() != null && "MEDICAL_MANAGER".equalsIgnoreCase(common.Session.getCurrentUser().getRole().name()))
                ? common.Session.getCurrentUser().getUserId() : "";

        setTitle("Doctor Shift Roster");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        String[] columnNames = {"Department", "Doctor Name", "Date", "Time Slot", "Location", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        rosterTable = new JTable(tableModel);
        rosterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rosterTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRoster();
            }
        });
        JScrollPane scrollPane = new JScrollPane(rosterTable);

        JPanel panelForm = new JPanel(new GridLayout(6, 2, 8, 8));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Vector<String> departments = ShiftRosterUX.loadDataFromFile("departments.txt", new String[]{"Cardiology", "Pediatrics", "Emergency"});
        Vector<String> doctors = ShiftRosterUX.loadDoctorOptions(new String[]{"D001 - Aisha", "D002 - Lee", "D003 - Tan Wei Ming"}, currentManagerId);
        Vector<String> locations = ShiftRosterUX.loadDataFromFile("assets.txt", new String[]{"R101", "R102", "R103"});

        filterDepartment = new JComboBox<>();
        filterDepartment.addItem("All Departments");
        departments.forEach(filterDepartment::addItem);
        filterDoctor = new JComboBox<>();
        filterDoctor.addItem("All Doctors");
        doctors.forEach(filterDoctor::addItem);
        filterDatePicker = new DatePicker();
        filterDateEnabled = new JCheckBox("Filter date");
        JButton btnFilter = new JButton("Filter");
        JButton btnShowAll = new JButton("Show All");
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Department:"));
        filterPanel.add(filterDepartment);
        filterPanel.add(new JLabel("Doctor:"));
        filterPanel.add(filterDoctor);
        filterPanel.add(filterDateEnabled);
        filterPanel.add(filterDatePicker);
        filterPanel.add(btnFilter);
        filterPanel.add(btnShowAll);
        add(filterPanel, BorderLayout.NORTH);
        btnFilter.addActionListener(e -> refreshTable());
        btnShowAll.addActionListener(e -> clearFilters());

        panelForm.add(new JLabel("Department:"));
        cbDepartment = new JComboBox<>(departments);
        panelForm.add(cbDepartment);

        panelForm.add(new JLabel("Doctor Name:"));
        cbDoctor = new JComboBox<>(doctors);
        panelForm.add(cbDoctor);

        panelForm.add(new JLabel("Date:"));
        datePicker = new DatePicker();
        panelForm.add(datePicker);

        panelForm.add(new JLabel("Time Slot:"));
        cbTime = new JComboBox<>(new String[]{
            "08:00 - 16:00 (Morning Shift)",
            "16:00 - 00:00 (Evening Shift)",
            "00:00 - 08:00 (Night Shift)"
        });
        panelForm.add(cbTime);

        panelForm.add(new JLabel("Location"));
        cbLocation = new JComboBox<>(locations);
        panelForm.add(cbLocation);

        panelForm.add(new JLabel("Status:"));
        String[] statuses = {"Scheduled", "Completed", "Cancelled"};
        cbStatus = new JComboBox<>(statuses);
        panelForm.add(cbStatus);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(panelForm, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
                
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        JButton btnBack = new JButton("Back to Dashboard");
        
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15,10));
        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnClear);
        panelButtons.add(btnBack);
        
        add(panelButtons, BorderLayout.SOUTH);
        btnAdd.addActionListener(e -> addRoster());
        btnClear.addActionListener(e -> clearForm());
        btnUpdate.addActionListener(e -> updateRoster());
        btnDelete.addActionListener(e -> deleteRoster());
        btnBack.addActionListener(e -> dispose());
        refreshTable();
    }

    private void addRoster() {
        String date = datePicker.getDateString();
        String time = ShiftRosterUX.timeRangeFromOption(cbTime.getSelectedItem().toString());
        try {
            ShiftRosterUX.addRoster(
                cbDepartment.getSelectedItem().toString(),
                ShiftRosterUX.doctorIdFromOption(cbDoctor.getSelectedItem().toString()), date, time,
                cbLocation.getSelectedItem().toString(),
                cbStatus.getSelectedItem().toString());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        refreshTable();
        clearForm();
        JOptionPane.showMessageDialog(this, "Roster added successfully.");
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        String department = filterDepartment.getSelectedIndex() == 0
                ? "" : filterDepartment.getSelectedItem().toString();
        String doctor = filterDoctor.getSelectedIndex() == 0
                ? "" : ShiftRosterUX.doctorIdFromOption(filterDoctor.getSelectedItem().toString());
        String date = filterDateEnabled.isSelected() ? filterDatePicker.getDateString() : "";
        List<Vector<String>> rows = ShiftRosterUX.loadRosterTableData(doctor, department, date, currentManagerId);
        for (Vector<String> row : rows) {
            tableModel.addRow(row);
        }
        selectedRosterId = "";
    }

    private void loadSelectedRoster() {
        int selectedRow = rosterTable.getSelectedRow();
        String[] record = selectedRow < 0 ? null
            : ShiftRosterUX.getRosterRecordAtFilter(selectedRow,
                currentDoctorFilter(), currentDepartmentFilter(), currentDateFilter(), currentManagerId);
        if (record == null) {
            return;
        }
        selectedRosterId = record[0];
        cbDepartment.setSelectedItem(record[3]);
        selectDoctor(record[1]);
        datePicker.setDateString(record[4]);
        selectTime(record[5]);
        cbLocation.setSelectedItem(record[6]);
        cbStatus.setSelectedItem(record[7]);
    }

    private void selectDoctor(String doctorId) {
        for (int i = 0; i < cbDoctor.getItemCount(); i++) {
            String option = cbDoctor.getItemAt(i);
            if (ShiftRosterUX.doctorIdFromOption(option).equals(doctorId)) {
                cbDoctor.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectTime(String time) {
        for (int i = 0; i < cbTime.getItemCount(); i++) {
            if (ShiftRosterUX.timeRangeFromOption(cbTime.getItemAt(i)).equals(time)) {
                cbTime.setSelectedIndex(i);
                return;
            }
        }
    }

    private void updateRoster() {
        if (selectedRosterId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a roster to update.");
            return;
        }
        String time = ShiftRosterUX.timeRangeFromOption(cbTime.getSelectedItem().toString());
        try {
            ShiftRosterUX.updateRoster(selectedRosterId,
                    cbDepartment.getSelectedItem().toString(),
                    ShiftRosterUX.doctorIdFromOption(cbDoctor.getSelectedItem().toString()),
                    datePicker.getDateString(), time,
                    cbLocation.getSelectedItem().toString(),
                    cbStatus.getSelectedItem().toString());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        refreshTable();
        clearForm();
        JOptionPane.showMessageDialog(this, "Roster updated successfully.");
    }

    private void deleteRoster() {
        if (selectedRosterId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a roster to delete.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete the selected roster?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        ShiftRosterUX.deleteRoster(selectedRosterId);
        refreshTable();
        clearForm();
        JOptionPane.showMessageDialog(this, "Roster deleted successfully.");
    }

    private void clearForm() {
        datePicker.setDate(java.time.LocalDate.now());
        cbTime.setSelectedIndex(0);
        cbDepartment.setSelectedIndex(0);
        cbDoctor.setSelectedIndex(0);
        cbLocation.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        selectedRosterId = "";
        rosterTable.clearSelection();
    }

    private String currentDepartmentFilter() {
        return filterDepartment.getSelectedIndex() == 0 ? "" : filterDepartment.getSelectedItem().toString();
    }

    private String currentDoctorFilter() {
        return filterDoctor.getSelectedIndex() == 0 ? ""
                : ShiftRosterUX.doctorIdFromOption(filterDoctor.getSelectedItem().toString());
    }

    private String currentDateFilter() {
        return filterDateEnabled.isSelected() ? filterDatePicker.getDateString() : "";
    }

    private void clearFilters() {
        filterDepartment.setSelectedIndex(0);
        filterDoctor.setSelectedIndex(0);
        filterDateEnabled.setSelected(false);
        refreshTable();
    }
}


    

