package patients.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MedicalRecordUX extends JFrame {
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Assessment ID", "Patient", "Doctor", "Type", "Date", "Grade", "Result / Notes", "Lab Result", "Bill RM", "Status"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable table = new JTable(model);
    protected final JLabel selectedRecordLabel = new JLabel("Select a medical record to view clinical details.");
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");
    protected final JButton detailButton = new JButton("Detail \u2192");
    protected final JButton refreshButton = new JButton("Refresh My Records");

    public MedicalRecordUX() {
        setTitle("My Medical Records & Clinical Assessments");
        setSize(1180, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Allow table to resize to window edges like other tables
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setRowHeight(24);

        if (table.getColumnModel().getColumnCount() >= 10) {
            table.getColumnModel().getColumn(0).setPreferredWidth(100); // Assessment ID
            table.getColumnModel().getColumn(1).setPreferredWidth(70);  // Patient
            table.getColumnModel().getColumn(2).setPreferredWidth(70);  // Doctor
            table.getColumnModel().getColumn(3).setPreferredWidth(100); // Type
            table.getColumnModel().getColumn(4).setPreferredWidth(90);  // Date
            table.getColumnModel().getColumn(5).setPreferredWidth(110); // Grade
            table.getColumnModel().getColumn(6).setPreferredWidth(230); // Result
            table.getColumnModel().getColumn(7).setPreferredWidth(230); // Lab Result
            table.getColumnModel().getColumn(8).setPreferredWidth(75);  // Bill RM
            table.getColumnModel().getColumn(9).setPreferredWidth(85);  // Status
        }

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        selectedRecordLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bottomPanel.add(selectedRecordLabel, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        detailButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        detailButton.setEnabled(false);

        buttons.add(backButton);
        buttons.add(detailButton);
        buttons.add(refreshButton);
        bottomPanel.add(buttons, BorderLayout.EAST);

        root.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
