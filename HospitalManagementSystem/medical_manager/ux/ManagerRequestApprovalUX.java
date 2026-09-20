package medical_manager.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManagerRequestApprovalUX extends JFrame {
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Request ID", "Doctor ID", "Doctor Name", "Patient ID", "Patient Name", "Modality", "Clinical Indication", "Status", "Date"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable table = new JTable(model);
    protected final JComboBox<String> filterBox = new JComboBox<>(new String[]{"All Requests", "PENDING", "APPROVED", "REJECTED"});
    protected final JLabel selectedInfoLabel = new JLabel("Select a diagnostic request to review and decide.");
    protected final JTextArea detailsArea = new JTextArea(3, 40);

    protected final JButton backButton = new JButton("← Back to Dashboard");
    protected final JButton approveButton = new JButton("✓ Accept / Approve Request");
    protected final JButton rejectButton = new JButton("✗ Reject / Decline Request");
    protected final JButton deleteButton = new JButton("Delete Request");
    protected final JButton refreshButton = new JButton("Refresh");

    public ManagerRequestApprovalUX() {
        setTitle("Medical Manager - Doctor Diagnostic Requisition Approvals");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Top Filter Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.add(new JLabel("Filter by Status:"));
        topBar.add(filterBox);
        root.add(topBar, BorderLayout.NORTH);

        // Center Table
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setPreferredWidth(130);
        table.getColumnModel().getColumn(6).setPreferredWidth(320);
        table.getColumnModel().getColumn(7).setPreferredWidth(110);
        table.getColumnModel().getColumn(8).setPreferredWidth(100);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Submitted Doctor Diagnostic Requests (Lab Tests, X-Rays, Imaging)"));
        root.add(tableScroll, BorderLayout.CENTER);

        // South Decision & Review Panel
        JPanel southPanel = new JPanel(new BorderLayout(8, 8));
        southPanel.setBorder(BorderFactory.createTitledBorder("Manager Decision & Clinical Review"));

        JPanel previewPanel = new JPanel(new BorderLayout(5, 5));
        selectedInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        previewPanel.add(selectedInfoLabel, BorderLayout.NORTH);

        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        previewPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        southPanel.add(previewPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        buttons.add(backButton);
        buttons.add(refreshButton);
        buttons.add(deleteButton);
        buttons.add(rejectButton);
        buttons.add(approveButton);

        southPanel.add(buttons, BorderLayout.SOUTH);
        root.add(southPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
