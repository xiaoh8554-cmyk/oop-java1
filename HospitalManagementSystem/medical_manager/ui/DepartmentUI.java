package medical_manager.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import medical_manager.ux.DepartmentUX;
import medical_manager.ux.ShiftRosterUX;

public class DepartmentUI extends JFrame {
	private final DefaultTableModel tableModel;
	private final JTable departmentTable;
	private final JTextField nameField;
	private final JTextField specializationField;
	private final JComboBox<String> doctorField;
	private final JComboBox<String> departmentFilterBox;
	private final JComboBox<String> specializationFilterBox;
	private String selectedDepartmentId = "";

	public DepartmentUI() {
		setTitle("Department Management");
		setSize(900, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));

		tableModel = new DefaultTableModel(
				new String[]{"Department ID", "Department Name", "Specialization", "Doctor Name"}, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		departmentTable = new JTable(tableModel);
		departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		departmentTable.getSelectionModel().addListSelectionListener(e -> loadSelectedDepartment());

		departmentFilterBox = new JComboBox<>();
		departmentFilterBox.addItem("All Departments");
		DepartmentUX.loadDepartmentNames().forEach(departmentFilterBox::addItem);
		specializationFilterBox = new JComboBox<>();
		specializationFilterBox.addItem("All Specializations");
		DepartmentUX.loadSpecializations().forEach(specializationFilterBox::addItem);
		JButton filterButton = new JButton("Filter");
		JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		filterPanel.add(new JLabel("Department Name:"));
		filterPanel.add(departmentFilterBox);
		filterPanel.add(new JLabel("Specialization:"));
		filterPanel.add(specializationFilterBox);
		filterPanel.add(filterButton);
		add(filterPanel, BorderLayout.NORTH);
		filterButton.addActionListener(e -> refreshTable());

		JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
		form.setBorder(BorderFactory.createTitledBorder("Create New Department"));
		form.add(new JLabel("Department Name:"));
		nameField = new JTextField();
		form.add(nameField);
		form.add(new JLabel("Specialization:"));
		specializationField = new JTextField();
		form.add(specializationField);
		form.add(new JLabel("Doctor Name:"));
		doctorField = new JComboBox<>(ShiftRosterUX.loadDoctorOptions(
				new String[]{"D001 - Aisha", "D002 - Lee", "D003 - Tan Wei Ming"}));
		form.add(doctorField);

		JPanel content = new JPanel(new BorderLayout(10, 10));
		content.add(new JScrollPane(departmentTable), BorderLayout.CENTER);
		content.add(form, BorderLayout.SOUTH);
		add(content, BorderLayout.CENTER);

		JButton createButton = new JButton("Create");
		JButton updateButton = new JButton("Update");
		JButton deleteButton = new JButton("Delete");
		JButton clearButton = new JButton("Clear");
		JButton backButton = new JButton("Back to Dashboard");
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		buttons.add(createButton);
		buttons.add(updateButton);
		buttons.add(deleteButton);
		buttons.add(clearButton);
		buttons.add(backButton);
		add(buttons, BorderLayout.SOUTH);

		createButton.addActionListener(e -> createDepartment());
		updateButton.addActionListener(e -> updateDepartment());
		deleteButton.addActionListener(e -> deleteDepartment());
		clearButton.addActionListener(e -> clearAll());
		backButton.addActionListener(e -> dispose());
		refreshTable();
	}

	private void refreshTable() {
		tableModel.setRowCount(0);
		for (String[] department : DepartmentUX.filterDepartments(
				selectedDepartmentFilter(), selectedSpecializationFilter())) {
			if (department.length >= 4) {
				tableModel.addRow(department);
			}
		}
		selectedDepartmentId = "";
	}

	private void loadSelectedDepartment() {
		int row = departmentTable.getSelectedRow();
		if (row < 0) {
			return;
		}
		List<String[]> filteredDepartments = DepartmentUX.filterDepartments(
				selectedDepartmentFilter(), selectedSpecializationFilter());
		if (row >= filteredDepartments.size()) {
			return;
		}
		String[] department = filteredDepartments.get(row);
		selectedDepartmentId = department[0];
		nameField.setText(department[1]);
		specializationField.setText(department[2]);
		selectDoctor(department[3]);
	}

	private void selectDoctor(String doctorName) {
		String name = doctorName.startsWith("Dr. ") ? doctorName.substring(4) : doctorName;
		for (int i = 0; i < doctorField.getItemCount(); i++) {
			String option = doctorField.getItemAt(i);
			int separator = option.indexOf(" - ");
			if (separator >= 0 && option.substring(separator + 3).equalsIgnoreCase(name)) {
				doctorField.setSelectedIndex(i);
				return;
			}
		}
	}

	private String selectedDoctorName() {
		String option = doctorField.getSelectedItem().toString();
		int separator = option.indexOf(" - ");
		String name = separator >= 0 ? option.substring(separator + 3) : option;
		return name.startsWith("Dr. ") ? name : "Dr. " + name;
	}

	private void createDepartment() {
		try {
			DepartmentUX.createDepartment(nameField.getText(), specializationField.getText(), selectedDoctorName());
			refreshTable();
			clearForm();
			JOptionPane.showMessageDialog(this, "Department created successfully.");
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void updateDepartment() {
		if (selectedDepartmentId.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Select a department to update.");
			return;
		}
		try {
			DepartmentUX.updateDepartment(selectedDepartmentId, nameField.getText(),
					 specializationField.getText(), selectedDoctorName());
			refreshTable();
			clearForm();
			JOptionPane.showMessageDialog(this, "Department updated successfully.");
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void deleteDepartment() {
		if (selectedDepartmentId.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Select a department to delete.");
			return;
		}
		int choice = JOptionPane.showConfirmDialog(this, "Delete the selected department?",
				"Confirm Delete", JOptionPane.YES_NO_OPTION);
		if (choice != JOptionPane.YES_OPTION) {
			return;
		}
		DepartmentUX.deleteDepartment(selectedDepartmentId);
		refreshTable();
		clearForm();
	}

	private void clearForm() {
		nameField.setText("");
		specializationField.setText("");
		if (doctorField.getItemCount() > 0) {
			doctorField.setSelectedIndex(0);
		}
		selectedDepartmentId = "";
		departmentTable.clearSelection();
	}

	private String selectedDepartmentFilter() {
		return departmentFilterBox.getSelectedIndex() == 0 ? ""
				: departmentFilterBox.getSelectedItem().toString();
	}

	private String selectedSpecializationFilter() {
		return specializationFilterBox.getSelectedIndex() == 0 ? ""
				: specializationFilterBox.getSelectedItem().toString();
	}

	private void clearAll() {
		departmentFilterBox.setSelectedIndex(0);
		specializationFilterBox.setSelectedIndex(0);
		clearForm();
		refreshTable();
	}
}
