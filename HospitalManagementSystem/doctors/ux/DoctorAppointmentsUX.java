package doctors.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DoctorAppointmentsUX extends JFrame {
    protected final JLabel titleLabel = new JLabel("Patient Appointments Queue & Schedule");
    protected final JLabel doctorInfoLabel = new JLabel("Doctor: Loading...");
    protected final JLabel statsBadgeLabel = new JLabel("Urgent: 0 | Booked: 0 | Rescheduled: 0 | Total: 0");

    // Filter controls
    protected final JTextField searchField = new JTextField(16);
    protected final JComboBox<String> statusFilterBox = new JComboBox<>(new String[]{
            "All Active & Completed", "URGENT Priority Only", "BOOKED Only", "RESCHEDULED Only", "COMPLETED Only"
    });
    protected final JComboBox<String> doctorScopeBox = new JComboBox<>(new String[]{
            "My Appointments Only", "All Doctor Appointments"
    });
    protected final JButton clearFiltersButton = new JButton("Clear Filters");
    protected final JButton refreshButton = new JButton("Refresh");

    // Table Model & JTable
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                    "Priority", "Appt ID", "Date", "Time Slot", "Room",
                    "Patient ID", "Patient Name", "Contact", "Status", "Reason / Symptoms"
            }, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    protected final JTable table = new JTable(model);

    // Selected Details & Actions
    protected final JLabel selectedApptLabel = new JLabel("Selected Appointment: (None selected)");
    protected final JButton completeButton = new JButton("✔ Mark as Completed");
    protected final JButton rescheduleButton = new JButton("📅 Reschedule Appointment \u2192");
    protected final JButton historyButton = new JButton("📋 Patient's Appointment History \u2192");
    protected final JButton patientDetailsButton = new JButton("ℹ View Patient Details");
    protected final JButton assessmentButton = new JButton("Log Vitals & Notes \u2192");
    protected final JButton prescriptionButton = new JButton("Issue Prescription \u2192");
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");

    public DoctorAppointmentsUX() {
        setTitle("Doctor - Patient Appointments Queue");
        setSize(1350, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 18, 15, 18));

        // North: Header & Stats
        JPanel northPanel = new JPanel(new BorderLayout(10, 8));

        JPanel titleBox = new JPanel(new BorderLayout(5, 5));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 60, 130));

        doctorInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        doctorInfoLabel.setForeground(new Color(60, 75, 95));

        statsBadgeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        statsBadgeLabel.setOpaque(true);
        statsBadgeLabel.setBackground(new Color(240, 244, 252));
        statsBadgeLabel.setForeground(new Color(20, 50, 110));
        statsBadgeLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 210, 240)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        titleBox.add(titleLabel, BorderLayout.NORTH);
        titleBox.add(doctorInfoLabel, BorderLayout.SOUTH);
        northPanel.add(titleBox, BorderLayout.WEST);
        northPanel.add(statsBadgeLabel, BorderLayout.EAST);

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filterBar.setBorder(BorderFactory.createTitledBorder("Search & Filter Appointments (Cancelled Appointments Excluded)"));
        filterBar.add(new JLabel("Search:"));
        filterBar.add(searchField);
        filterBar.add(new JLabel("Status:"));
        filterBar.add(statusFilterBox);
        filterBar.add(new JLabel("View Scope:"));
        filterBar.add(doctorScopeBox);
        filterBar.add(clearFiltersButton);
        filterBar.add(refreshButton);

        northPanel.add(filterBar, BorderLayout.SOUTH);
        root.add(northPanel, BorderLayout.NORTH);

        // Center: JTable
        table.setRowHeight(26);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(110); // Priority
        table.getColumnModel().getColumn(1).setPreferredWidth(105); // Appt ID
        table.getColumnModel().getColumn(2).setPreferredWidth(95);  // Date
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Time Slot
        table.getColumnModel().getColumn(4).setPreferredWidth(85);  // Room
        table.getColumnModel().getColumn(5).setPreferredWidth(85);  // Patient ID
        table.getColumnModel().getColumn(6).setPreferredWidth(140); // Patient Name
        table.getColumnModel().getColumn(7).setPreferredWidth(110); // Contact
        table.getColumnModel().getColumn(8).setPreferredWidth(105); // Status
        table.getColumnModel().getColumn(9).setPreferredWidth(270); // Reason / Symptoms

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Scheduled Appointments (Urgent Cases Prioritized at Top)"));
        root.add(tableScroll, BorderLayout.CENTER);

        // South: Action Bar & Controls
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        JPanel selectionPanel = new JPanel(new BorderLayout());
        selectedApptLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        selectedApptLabel.setForeground(new Color(40, 60, 90));
        selectionPanel.add(selectedApptLabel, BorderLayout.WEST);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        buttonBar.add(completeButton);
        buttonBar.add(rescheduleButton);
        buttonBar.add(historyButton);
        buttonBar.add(patientDetailsButton);
        buttonBar.add(assessmentButton);
        buttonBar.add(prescriptionButton);
        buttonBar.add(backButton);

        southPanel.add(selectionPanel, BorderLayout.NORTH);
        southPanel.add(buttonBar, BorderLayout.SOUTH);

        root.add(southPanel, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
