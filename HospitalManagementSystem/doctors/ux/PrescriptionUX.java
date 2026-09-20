package doctors.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PrescriptionUX extends JFrame {
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Prescription ID", "Patient ID", "Doctor ID", "Date", "Medicine", "Instructions"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable table = new JTable(model);
    protected final JComboBox<String> patientBox = new JComboBox<>();
    protected final JTextField medicineField = new JTextField(20);
    protected final JTextArea instructionsArea = new JTextArea(4, 30);
    protected final JButton backButton = new JButton("← Back to Dashboard");
    protected final JButton saveButton = new JButton("Issue Prescription");
    protected final JButton clearButton = new JButton("Clear Form");
    protected final JButton refreshButton = new JButton("Refresh");

    public PrescriptionUX() {
        setTitle("Doctor - Digital Prescriptions Management");
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(220);
        table.getColumnModel().getColumn(5).setPreferredWidth(380);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Issued Prescriptions History"));
        root.add(tableScroll, BorderLayout.CENTER);

        JPanel formContainer = new JPanel(new BorderLayout(8, 8));
        formContainer.setBorder(BorderFactory.createTitledBorder("Issue Digital Medication Prescription"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Select Patient:"), g);
        g.gridx = 1;
        form.add(patientBox, g);

        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("Medication Name & Strength:"), g);
        g.gridx = 1;
        form.add(medicineField, g);

        g.gridx = 0; g.gridy = 2;
        g.anchor = GridBagConstraints.NORTH;
        form.add(new JLabel("Dosage & Instructions:"), g);
        g.gridx = 1;
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        form.add(new JScrollPane(instructionsArea), g);

        formContainer.add(form, BorderLayout.CENTER);

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

