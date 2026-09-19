package patients.ux;

import common.ui.DatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class AppointmentBookingUX extends JFrame {
    // Doctor Booking Filter & Table
    protected final JComboBox<String> specialtyFilterBox = new JComboBox<>();
    protected final DatePicker bookingDatePicker = new DatePicker(LocalDate.now());

    protected final DefaultTableModel doctorsModel = new DefaultTableModel(
            new String[]{"Doctor ID", "Doctor Name", "Specialty", "Consultation Room", "Qualification"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable doctorsTable = new JTable(doctorsModel);

    protected final JLabel selectedDoctorInfoLabel = new JLabel("Please select a specialty, date, and doctor from the table.");
    protected final JButton detailButton = new JButton("Detail \u2192");
    protected final JButton refreshDoctorsButton = new JButton("Refresh");
    protected final JLabel liveClockLabel = new JLabel("Current Time: --:--:--");

    // Navigation Buttons
    protected final JButton viewHistoryButton = new JButton("View My Appointment History \u2192");
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");

    public AppointmentBookingUX() {
        setTitle("Doctor Consultation Booking & Schedule");
        setSize(1080, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Top Filter Bar with Specialty, DatePicker, Refresh, Live Clock, and View History Button
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filterBar.setBorder(BorderFactory.createTitledBorder("Step 1: Choose Specialty and Appointment Date"));
        filterBar.add(new JLabel("Specialty:"));
        specialtyFilterBox.setPreferredSize(new Dimension(180, 28));
        filterBar.add(specialtyFilterBox);

        filterBar.add(new JLabel("Consultation Date:"));
        filterBar.add(bookingDatePicker);
        filterBar.add(refreshDoctorsButton);

        liveClockLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        liveClockLabel.setForeground(new Color(20, 60, 130));
        liveClockLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 205, 240)),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
        filterBar.add(Box.createHorizontalStrut(8));
        filterBar.add(liveClockLabel);

        root.add(filterBar, BorderLayout.NORTH);
        root.add(new JScrollPane(doctorsTable), BorderLayout.CENTER);

        // Bottom Bar with Info Label and Detail / History Buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        selectedDoctorInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bottomPanel.add(selectedDoctorInfoLabel, BorderLayout.CENTER);

        JPanel rightActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        detailButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        detailButton.setPreferredSize(new Dimension(130, 32));
        detailButton.setEnabled(false); // Initially unclickable
        rightActionPanel.add(detailButton);
        rightActionPanel.add(viewHistoryButton);
        bottomPanel.add(rightActionPanel, BorderLayout.EAST);

        JPanel navigationBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navigationBar.add(backButton);
        bottomPanel.add(navigationBar, BorderLayout.SOUTH);

        root.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
