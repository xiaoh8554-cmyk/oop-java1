package administrative_staff.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BillingUX extends JFrame {
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Bill ID", "Patient ID", "Assessment ID", "Amount (RM)", "Status", "Date"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable table = new JTable(model);
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");
    protected final JButton addButton = new JButton("Create Bill");
    protected final JButton paidButton = new JButton("Mark Paid");
    protected final JButton deleteButton = new JButton("Delete Bill");
    protected final JButton refreshButton = new JButton("Refresh");

    public BillingUX() {
        setTitle("Billing Management");
        setSize(900, 530);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonBar.add(backButton);
        buttonBar.add(addButton);
        buttonBar.add(paidButton);
        buttonBar.add(deleteButton);
        buttonBar.add(refreshButton);

        root.add(buttonBar, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
