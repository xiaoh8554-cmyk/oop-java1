package patients.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PatientAdmissionUX extends JFrame {
    protected final JLabel statusLabel = new JLabel("Status: Checking...", SwingConstants.CENTER);

    // Current admission details
    protected final JLabel admIdVal = new JLabel("-");
    protected final JLabel doctorVal = new JLabel("-");
    protected final JLabel wardVal = new JLabel("-");
    protected final JLabel locationVal = new JLabel("-");
    protected final JLabel urgencyVal = new JLabel("-");
    protected final JLabel reasonVal = new JLabel("-");
    protected final JLabel dateVal = new JLabel("-");
    protected final JLabel rateVal = new JLabel("-");

    // Admission history table
    protected final DefaultTableModel historyModel = new DefaultTableModel(
            new String[]{"Admission ID", "Doctor ID", "Ward / Room", "Reason", "Status", "Admitted Date", "Discharge Date"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable historyTable = new JTable(historyModel);

    // Buttons
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");
    protected final JButton refreshButton = new JButton("Refresh Status");

    public PatientAdmissionUX() {
        setTitle("My Inpatient Admission & Ward Location");
        setSize(850, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Status Header
        JPanel topPanel = new JPanel(new BorderLayout(6, 6));
        JLabel titleLabel = new JLabel("Inpatient Admission & Assigned Ward Location", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(230, 240, 250));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        topPanel.add(statusLabel, BorderLayout.SOUTH);
        root.add(topPanel, BorderLayout.NORTH);

        // Center: Details Card + History Table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // Active Ward Card
        JPanel card = new JPanel(new GridLayout(4, 4, 8, 8));
        card.setBorder(BorderFactory.createTitledBorder("Current Inpatient Facility Details"));
        card.add(new JLabel("Admission ID:"));
        card.add(admIdVal);
        card.add(new JLabel("Attending Doctor:"));
        card.add(doctorVal);

        card.add(new JLabel("Assigned Ward / Room:"));
        card.add(wardVal);
        card.add(new JLabel("Floor / Building:"));
        card.add(locationVal);

        card.add(new JLabel("Admission Urgency:"));
        card.add(urgencyVal);
        card.add(new JLabel("Admission Date:"));
        card.add(dateVal);

        card.add(new JLabel("Clinical Reason:"));
        card.add(reasonVal);
        card.add(new JLabel("Daily Ward Rate:"));
        card.add(rateVal);

        centerPanel.add(card, BorderLayout.NORTH);

        // History Table
        JPanel histPanel = new JPanel(new BorderLayout(6, 6));
        histPanel.setBorder(BorderFactory.createTitledBorder("Admission & Stay History"));
        histPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        centerPanel.add(histPanel, BorderLayout.CENTER);

        root.add(centerPanel, BorderLayout.CENTER);

        // Bottom Button Bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        buttonBar.add(backButton);
        buttonBar.add(refreshButton);
        root.add(buttonBar, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
