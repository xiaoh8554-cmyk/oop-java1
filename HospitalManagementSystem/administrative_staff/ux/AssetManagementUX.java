package administrative_staff.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AssetManagementUX extends JFrame {
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Asset ID", "Asset Name", "Type", "Location", "Capacity", "Status", "Allocated To"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable table = new JTable(model);

    // Allocation fields
    protected final JTextField allocateToField = new JTextField(12);

    // Action buttons
    protected final JButton addButton = new JButton("Create Asset");
    protected final JButton allocateButton = new JButton("Allocate to User/Dept");
    protected final JButton releaseButton = new JButton("Release / Make Available");
    protected final JButton maintenanceButton = new JButton("Set Maintenance");
    protected final JButton deleteButton = new JButton("Delete Selected");
    protected final JButton refreshButton = new JButton("Refresh");
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");

    public AssetManagementUX() {
        setTitle("Hospital Physical Asset Management & Allocation");
        setSize(1020, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Physical Hospital Assets & Room Allocation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(titleLabel, BorderLayout.NORTH);

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // South panel with allocation panel and action buttons
        JPanel south = new JPanel(new BorderLayout(10, 10));

        // Allocation panel
        JPanel allocPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        allocPanel.setBorder(BorderFactory.createTitledBorder("Asset Allocation & Status"));
        allocPanel.add(new JLabel("Allocate Selected Asset To (Doctor ID / Patient ID / Dept):"));
        allocPanel.add(allocateToField);
        allocPanel.add(allocateButton);
        allocPanel.add(releaseButton);
        allocPanel.add(maintenanceButton);

        // Action buttons bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonBar.add(backButton);
        buttonBar.add(addButton);
        buttonBar.add(deleteButton);
        buttonBar.add(refreshButton);

        south.add(allocPanel, BorderLayout.NORTH);
        south.add(buttonBar, BorderLayout.SOUTH);

        root.add(south, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
