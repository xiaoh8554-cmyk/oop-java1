package patients.ux;

import common.ui.ReadOnlyTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PatientBillingUX extends JFrame {
    protected final DefaultTableModel model = new ReadOnlyTableModel(
            new String[]{"Bill ID", "Amount (RM)", "Status", "Date"}, 0);
    protected final JTable table = new JTable(model);
    protected final JLabel totalLabel = new JLabel("Outstanding: RM 0.00");
    protected final JLabel selectedBillLabel = new JLabel("Select a bill to view details or make a payment.");
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");
    protected final JButton detailButton = new JButton("Detail \u2192");
    protected final JButton payButton = new JButton("Pay Selected Bill");
    protected final JButton refreshButton = new JButton("Refresh");

    public PatientBillingUX() {
        setTitle("My Invoices & Billing Breakdown");
        setSize(920, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        backButton.addActionListener(e -> dispose());

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        table.setRowHeight(24);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        if (table.getColumnModel().getColumnCount() >= 4) {
            table.getColumnModel().getColumn(0).setPreferredWidth(140); // Bill ID
            table.getColumnModel().getColumn(1).setPreferredWidth(120); // Amount (RM)
            table.getColumnModel().getColumn(2).setPreferredWidth(120); // Status
            table.getColumnModel().getColumn(3).setPreferredWidth(140); // Date
        }

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom panel: info + actions
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));

        JPanel infoPanel = new JPanel(new BorderLayout());
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalLabel.setForeground(new Color(180, 40, 40));
        selectedBillLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoPanel.add(totalLabel, BorderLayout.WEST);
        infoPanel.add(selectedBillLabel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        detailButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        detailButton.setEnabled(false);
        payButton.setEnabled(false);

        buttonPanel.add(backButton);
        buttonPanel.add(detailButton);
        buttonPanel.add(payButton);
        buttonPanel.add(refreshButton);

        bottomPanel.add(infoPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        root.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
