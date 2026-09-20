package doctors.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClinicalFeedbackUX extends JFrame {
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Feedback ID", "Patient ID", "Doctor ID", "Date", "Type", "Clinical Feedback Message"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable table = new JTable(model);
    protected final JComboBox<String> patientBox = new JComboBox<>();
    protected final JTextArea messageArea = new JTextArea(4, 30);
    protected final JButton backButton = new JButton("← Back to Dashboard");
    protected final JButton saveButton = new JButton("Save Clinical Feedback");
    protected final JButton clearButton = new JButton("Clear Form");
    protected final JButton refreshButton = new JButton("Refresh");

    public ClinicalFeedbackUX() {
        setTitle("Doctor - Clinical Care Feedback");
        setSize(1050, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(160);
        table.getColumnModel().getColumn(5).setPreferredWidth(440);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Clinical Feedback History"));
        root.add(tableScroll, BorderLayout.CENTER);

        JPanel formContainer = new JPanel(new BorderLayout(8, 8));
        formContainer.setBorder(BorderFactory.createTitledBorder("Provide Clinical Feedback to Patient"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Select Patient:"), g);
        g.gridx = 1;
        form.add(patientBox, g);

        g.gridx = 0; g.gridy = 1;
        g.anchor = GridBagConstraints.NORTH;
        form.add(new JLabel("Clinical Feedback / Care Advice:"), g);
        g.gridx = 1;
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        form.add(new JScrollPane(messageArea), g);

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

