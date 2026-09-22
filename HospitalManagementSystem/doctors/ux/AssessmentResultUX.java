package doctors.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AssessmentResultUX extends JFrame {
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Assessment ID", "Patient ID", "Doctor ID", "Type", "Date", "Grade", "Consultation Notes & Vitals", "Diagnosis / Lab", "Bill (RM)", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable table = new JTable(model);
    protected final JComboBox<String> patientBox = new JComboBox<>();
    protected final JComboBox<String> typeBox = new JComboBox<>();

    // Dedicated Vital Signs Fields
    protected final JTextField bpField = new JTextField(8);
    protected final JTextField heartRateField = new JTextField(8);
    protected final JTextField spo2Field = new JTextField(8);
    protected final JTextField tempField = new JTextField(8);

    // Structured Consultation Notes and Lab / Findings
    protected final JTextArea notesArea = new JTextArea(3, 20);
    protected final JTextArea labArea = new JTextArea(3, 20);

    // Admission Decision Controls
    protected final JComboBox<String> admissionBox = new JComboBox<>(new String[]{
            "No Admission (Outpatient)", "Admission Required (Inpatient)"
    });
    protected final JComboBox<String> wardTypeBox = new JComboBox<>(new String[]{
            "INPATIENT_WARD", "ICU", "SINGLE_ROOM"
    });
    protected final JComboBox<String> urgencyBox = new JComboBox<>(new String[]{
            "ROUTINE", "URGENT", "EMERGENCY"
    });
    protected final JTextField admissionReasonField = new JTextField(18);

    protected final JButton backButton = new JButton("← Back to Dashboard");
    protected final JButton saveButton = new JButton("Save Vitals & Consultation");
    protected final JButton clearButton = new JButton("Clear Form");
    protected final JButton refreshButton = new JButton("Refresh");

    public AssessmentResultUX() {
        setTitle("Doctor - Patient Vital Signs & Consultation Notes");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Table at the top / center
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(320);
        table.getColumnModel().getColumn(7).setPreferredWidth(260);
        table.getColumnModel().getColumn(8).setPreferredWidth(80);
        table.getColumnModel().getColumn(9).setPreferredWidth(100);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Patient Assessment & Consultation History"));
        root.add(tableScroll, BorderLayout.CENTER);

        // Entry Form at the bottom
        JPanel formContainer = new JPanel(new BorderLayout(8, 8));
        formContainer.setBorder(BorderFactory.createTitledBorder("Log Vital Signs & Write Consultation Notes"));

        JPanel formGrid = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Patient ID & Assessment Type
        g.gridx = 0; g.gridy = 0; g.gridwidth = 1;
        formGrid.add(new JLabel("Select Patient:"), g);
        g.gridx = 1;
        formGrid.add(patientBox, g);
        g.gridx = 2;
        formGrid.add(new JLabel("Assessment Type:"), g);
        g.gridx = 3; g.gridwidth = 3;
        formGrid.add(typeBox, g);

        // Row 1: Vital Signs Panel
        g.gridx = 0; g.gridy = 1; g.gridwidth = 6;
        JPanel vitalsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        vitalsPanel.setBorder(BorderFactory.createTitledBorder("Patient Vital Signs"));
        vitalsPanel.add(new JLabel("Blood Pressure (BP):"));
        bpField.setToolTipText("e.g., 120/80 mmHg");
        vitalsPanel.add(bpField);

        vitalsPanel.add(new JLabel("Heart Rate (bpm):"));
        heartRateField.setToolTipText("e.g., 72");
        vitalsPanel.add(heartRateField);

        vitalsPanel.add(new JLabel("SpO2 (%):"));
        spo2Field.setToolTipText("e.g., 98");
        vitalsPanel.add(spo2Field);

        vitalsPanel.add(new JLabel("Temperature (°C):"));
        tempField.setToolTipText("e.g., 36.8");
        vitalsPanel.add(tempField);

        formGrid.add(vitalsPanel, g);

        // Row 2: Consultation Notes
        g.gridx = 0; g.gridy = 2; g.gridwidth = 1;
        g.anchor = GridBagConstraints.NORTH;
        formGrid.add(new JLabel("Consultation Notes:"), g);
        g.gridx = 1; g.gridwidth = 5;
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        formGrid.add(new JScrollPane(notesArea), g);

        // Row 3: Diagnosis / Lab Results
        g.gridx = 0; g.gridy = 3; g.gridwidth = 1;
        formGrid.add(new JLabel("Diagnosis / Lab Results:"), g);
        g.gridx = 1; g.gridwidth = 5;
        labArea.setLineWrap(true);
        labArea.setWrapStyleWord(true);
        formGrid.add(new JScrollPane(labArea), g);

        // Row 4: Admission Decision
        g.gridx = 0; g.gridy = 4; g.gridwidth = 6;
        JPanel admissionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        admissionPanel.setBorder(BorderFactory.createTitledBorder("Inpatient Admission Decision"));
        admissionPanel.add(new JLabel("Decision:"));
        admissionPanel.add(admissionBox);
        admissionPanel.add(new JLabel("Ward Type:"));
        admissionPanel.add(wardTypeBox);
        admissionPanel.add(new JLabel("Urgency:"));
        admissionPanel.add(urgencyBox);
        admissionPanel.add(new JLabel("Reason:"));
        admissionPanel.add(admissionReasonField);
        formGrid.add(admissionPanel, g);

        formContainer.add(formGrid, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        buttons.add(backButton);
        buttons.add(clearButton);
        buttons.add(refreshButton);
        buttons.add(saveButton);

        formContainer.add(buttons, BorderLayout.SOUTH);
        root.add(formContainer, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
