package patients.ux;

import common.ui.DatePicker;
import common.ui.ReadOnlyTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class AppointmentHistoryUX extends JFrame {
    protected final DefaultTableModel historyModel = new ReadOnlyTableModel(
            new String[]{"Appt ID", "Doctor ID", "Doctor Name", "Specialty", "Date", "Time Slot", "Room", "Status", "Reason / Symptoms"}, 0);
    protected final JTable historyTable = new JTable(historyModel);
    // Filter Controls
    protected final JTextField searchField = new JTextField(12);
    protected final JComboBox<String> specialtyFilterBox = new JComboBox<>();
    protected final JComboBox<String> statusFilterBox = new JComboBox<>(new String[]{
            "All Statuses", "BOOKED", "COMPLETED", "CANCELLED"
    });
    protected final JButton clearFiltersButton = new JButton("Clear Filters");

    protected final JLabel selectedHistoryLabel = new JLabel("Selected Appointment: (None selected)");
    protected final DatePicker rescheduleDatePicker = new DatePicker(LocalDate.now());
    protected final JButton rescheduleButton = new JButton("Reschedule Appointment (Select Time \u2192)");
    protected final JButton cancelButton = new JButton("Cancel Appointment");
    protected final JButton feedbackButton = new JButton("Feedback on Appointment \u2192");
    protected final JButton refreshHistoryButton = new JButton("Refresh History");
    protected final JButton bookNewButton = new JButton("📅 Book Doctor Consultation \u2192");
    protected final JButton bottomBookButton = new JButton("📅 Book Doctor Consultation \u2192");
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");

    public AppointmentHistoryUX() {
        setTitle("My Appointment History & Rescheduling");
        setSize(1160, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        backButton.addActionListener(e -> dispose());

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Top Panel: Navigation + Filters
        JPanel topPanel = new JPanel(new BorderLayout(8, 8));

        JPanel navBar = new JPanel(new BorderLayout());
        navBar.add(backButton, BorderLayout.WEST);

        bookNewButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        bookNewButton.setBackground(new Color(230, 245, 255));
        bookNewButton.setForeground(new Color(15, 65, 160));
        navBar.add(bookNewButton, BorderLayout.EAST);

        topPanel.add(navBar, BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterBar.setBorder(BorderFactory.createTitledBorder("Filter Appointments by Doctor Name, ID, Specialty, or Status"));

        filterBar.add(new JLabel("Search (Dr Name / ID):"));
        searchField.setToolTipText("Filter by Doctor Name, Appt ID, or Doctor ID");
        filterBar.add(searchField);

        filterBar.add(new JLabel("Specialty:"));
        specialtyFilterBox.setPreferredSize(new Dimension(160, 26));
        filterBar.add(specialtyFilterBox);

        filterBar.add(new JLabel("Status:"));
        statusFilterBox.setPreferredSize(new Dimension(130, 26));
        filterBar.add(statusFilterBox);

        filterBar.add(clearFiltersButton);

        topPanel.add(filterBar, BorderLayout.CENTER);
        root.add(topPanel, BorderLayout.NORTH);

        // Center: History table
        root.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        // South: Reschedule and Cancel Controls
        JPanel historySouth = new JPanel(new BorderLayout(8, 8));
        historySouth.setBorder(BorderFactory.createTitledBorder("Manage / Reschedule Selected Appointment"));

        JPanel rescheduleInputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        rescheduleInputs.add(selectedHistoryLabel);
        rescheduleInputs.add(new JLabel("Select New Date:"));
        rescheduleInputs.add(rescheduleDatePicker);

        JPanel historyButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        rescheduleButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        historyButtons.add(rescheduleButton);
        historyButtons.add(cancelButton);
        historyButtons.add(feedbackButton);
        historyButtons.add(refreshHistoryButton);

        bottomBookButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        bottomBookButton.setBackground(new Color(230, 245, 255));
        bottomBookButton.setForeground(new Color(15, 65, 160));
        historyButtons.add(bottomBookButton);

        historySouth.add(rescheduleInputs, BorderLayout.CENTER);
        historySouth.add(historyButtons, BorderLayout.SOUTH);
        root.add(historySouth, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
