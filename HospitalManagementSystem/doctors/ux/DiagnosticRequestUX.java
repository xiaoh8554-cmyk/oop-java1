package doctors.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DiagnosticRequestUX extends JFrame {
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Request ID", "Patient ID", "Doctor ID", "Request Type", "Clinical Indication / Notes", "Status", "Date"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable table = new JTable(model);
    protected final JComboBox<String> patientBox = new JComboBox<>();
    protected final JComboBox<String> typeBox = new JComboBox<>(new String[]{
            "LAB_TEST",
            "X_RAY",
            "MRI_SCAN",
            "CT_SCAN",
            "ULTRASOUND",
            "SPECIALIZED_IMAGING"
    });
    protected final JTextArea descriptionArea = new JTextArea(4, 30);
    protected final JButton backButton = new JButton("← Back to Dashboard");
    protected final JButton saveButton = new JButton("Submit New Request");
    protected final JButton updateButton = new JButton("Update / Change Request");
    protected final JButton deleteButton = new JButton("Delete Request");
    protected final JButton clearButton = new JButton("Clear Form");
    protected final JButton refreshButton = new JButton("Refresh");

    public DiagnosticRequestUX() {
        setTitle("Doctor - Diagnostic Requisition Orders (Lab & Imaging)");
        setSize(1150, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(360);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("My Diagnostic Requisitions & Approval Status"));
        root.add(tableScroll, BorderLayout.CENTER);

        JPanel formContainer = new JPanel(new BorderLayout(8, 8));
        formContainer.setBorder(BorderFactory.createTitledBorder("Issue, Update or Manage Diagnostic Requisitions"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Select Patient:"), g);
        g.gridx = 1;
        form.add(patientBox, g);

        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("Diagnostic Modality:"), g);
        g.gridx = 1;
        form.add(typeBox, g);

        g.gridx = 0; g.gridy = 2;
        g.anchor = GridBagConstraints.NORTH;
        form.add(new JLabel("Clinical Indication / Order Details:"), g);
        g.gridx = 1;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        form.add(new JScrollPane(descriptionArea), g);

        formContainer.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        buttons.add(backButton);
        buttons.add(clearButton);
        buttons.add(refreshButton);
        buttons.add(deleteButton);
        buttons.add(updateButton);
        buttons.add(saveButton);

        formContainer.add(buttons, BorderLayout.SOUTH);
        root.add(formContainer, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
