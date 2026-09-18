package administrative_staff.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DoctorManagerAssignUX extends JFrame {
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Doctor ID", "Doctor Name", "Specialty", "Assigned Manager ID", "Manager Name", "Department"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable table = new JTable(model);
    protected final JLabel selectedDoctorLabel = new JLabel("Selected Doctor: (None)");
    protected final JComboBox<String> managerBox = new JComboBox<>();
    protected final JButton assignButton = new JButton("Assign to Manager");
    protected final JButton unassignButton = new JButton("Unassign");
    protected final JButton refreshButton = new JButton("Refresh");
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");

    public DoctorManagerAssignUX() {
        setTitle("Assign Doctors to Medical Managers");
        setSize(950, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Doctor - Medical Manager Assignments", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(titleLabel, BorderLayout.NORTH);

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        formPanel.add(selectedDoctorLabel);
        formPanel.add(new JLabel("Medical Manager:"));
        managerBox.setPreferredSize(new Dimension(280, 28));
        formPanel.add(managerBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(backButton);
        buttonPanel.add(assignButton);
        buttonPanel.add(unassignButton);
        buttonPanel.add(refreshButton);

        south.add(formPanel, BorderLayout.NORTH);
        south.add(buttonPanel, BorderLayout.SOUTH);

        root.add(south, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
