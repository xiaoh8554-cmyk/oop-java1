package patients.ux;

import common.ui.ReadOnlyTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PatientPrescriptionFeedbackUX extends JFrame {
    protected final DefaultTableModel prescriptionModel = new ReadOnlyTableModel(
            new String[]{"Prescription ID", "Patient", "Doctor", "Date", "Medicine", "Instructions"}, 0);
    protected final DefaultTableModel feedbackModel = new ReadOnlyTableModel(
            new String[]{"Feedback ID", "Patient", "Doctor", "Date", "Rating", "Visit Ref", "Message"}, 0);

    protected final JTable prescriptionTable = new JTable(prescriptionModel);
    protected final JTable feedbackTable = new JTable(feedbackModel);

    protected final JComboBox<String> appointmentBox = new JComboBox<>();
    protected final JTextField doctorField = new JTextField(20);
    protected final JComboBox<String> ratingBox = new JComboBox<>(new String[]{
            "5 Stars - Excellent", "4 Stars - Good", "3 Stars - Average", "2 Stars - Poor", "1 Star - Very Poor"
    });
    protected final JTextField visitField = new JTextField(15);
    protected final JTextArea feedbackArea = new JTextArea(4, 35);

    // Tab 1 Prescriptions Controls
    protected final JLabel selectedPrescriptionLabel = new JLabel("Please select a prescription from the table.");
    protected final JButton prescriptionDetailButton = new JButton("Detail \u2192");
    protected final JButton refreshPrescriptionButton = new JButton("Refresh");
    protected final JButton backButton1 = new JButton("\u2190 Back to Dashboard");

    // Tab 2 Feedback Controls
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");
    protected final JButton submitButton = new JButton("Submit Rating & Feedback");
    protected final JButton refreshButton = new JButton("Refresh");
    protected final JTabbedPane tabs = new JTabbedPane();

    public PatientPrescriptionFeedbackUX() {
        setTitle("Prescriptions & Doctor Visit Feedback");
        setSize(1020, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        backButton.addActionListener(e -> dispose());
        backButton1.addActionListener(e -> dispose());

        doctorField.setEditable(false);
        doctorField.setBackground(new Color(245, 245, 245));
        visitField.setEditable(false);
        visitField.setBackground(new Color(245, 245, 245));

        // TAB 1: Prescriptions Panel
        JPanel prescriptionPanel = new JPanel(new BorderLayout(10, 10));
        prescriptionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        prescriptionPanel.add(new JScrollPane(prescriptionTable), BorderLayout.CENTER);

        JPanel rxBottomPanel = new JPanel(new BorderLayout(8, 8));
        rxBottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        selectedPrescriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rxBottomPanel.add(selectedPrescriptionLabel, BorderLayout.CENTER);

        JPanel rxActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        prescriptionDetailButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        prescriptionDetailButton.setPreferredSize(new Dimension(130, 32));
        prescriptionDetailButton.setEnabled(false);
        rxActionPanel.add(backButton1);
        rxActionPanel.add(prescriptionDetailButton);
        rxActionPanel.add(refreshPrescriptionButton);
        rxBottomPanel.add(rxActionPanel, BorderLayout.EAST);

        prescriptionPanel.add(rxBottomPanel, BorderLayout.SOUTH);
        tabs.addTab("My Digital Prescriptions", prescriptionPanel);

        // TAB 2: Feedback & Ratings
        JPanel feedbackPanel = new JPanel(new BorderLayout(10, 10));
        feedbackPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        feedbackPanel.add(new JScrollPane(feedbackTable), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Submit Rating & Feedback on Your Appointment History"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Select Appointment History:"), g);
        g.gridx = 1;
        form.add(appointmentBox, g);

        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("Doctor & Consultation:"), g);
        g.gridx = 1;
        form.add(doctorField, g);

        g.gridx = 0; g.gridy = 2;
        form.add(new JLabel("Doctor / Visit Rating:"), g);
        g.gridx = 1;
        form.add(ratingBox, g);

        g.gridx = 0; g.gridy = 3; g.anchor = GridBagConstraints.NORTH;
        form.add(new JLabel("Comments & Feedback:"), g);
        g.gridx = 1;
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        form.add(new JScrollPane(feedbackArea), g);

        JPanel b = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        b.add(backButton);
        b.add(submitButton);
        b.add(refreshButton);

        south.add(form, BorderLayout.CENTER);
        south.add(b, BorderLayout.SOUTH);
        feedbackPanel.add(south, BorderLayout.SOUTH);

        tabs.addTab("Ratings & Feedback History", feedbackPanel);
        setContentPane(tabs);
    }
}
