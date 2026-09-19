package patients.ux;

import common.ui.DatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class AppointmentHistoryUX extends JFrame {
    protected final DefaultTableModel historyModel = new DefaultTableModel(
            new String[]{"Appt ID", "Doctor ID", "Doctor Name", "Specialty", "Date", "Time Slot", "Room", "Status", "Reason / Symptoms"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable historyTable = new JTable(historyModel);
    protected final JLabel selectedHistoryLabel = new JLabel("Selected Appointment: (None selected)");
    protected final DatePicker rescheduleDatePicker = new DatePicker(LocalDate.now());
    protected final JButton rescheduleButton = new JButton("Reschedule Appointment (Select Time \u2192)");
    protected final JButton cancelButton = new JButton("Cancel Appointment");
    protected final JButton refreshHistoryButton = new JButton("Refresh History");
    protected final JButton bookNewButton = new JButton("+ Book New Consultation");
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");

    public AppointmentHistoryUX() {
        setTitle("My Appointment History & Rescheduling");
        setSize(1080, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

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
        historyButtons.add(refreshHistoryButton);
        historyButtons.add(bookNewButton);

        historySouth.add(rescheduleInputs, BorderLayout.CENTER);
        historySouth.add(historyButtons, BorderLayout.SOUTH);
        root.add(historySouth, BorderLayout.SOUTH);

        // Top/Bottom Navigation
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomBar.add(backButton);
        root.add(bottomBar, BorderLayout.NORTH);

        setContentPane(root);
    }
}
