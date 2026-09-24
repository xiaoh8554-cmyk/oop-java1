package administrative_staff.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AssetManagementUX extends JFrame {
    protected final JTabbedPane tabbedPane = new JTabbedPane();

    // Tab 1: Assets Inventory & Direct Allocation
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Asset ID", "Asset Name", "Type", "Floor", "Capacity", "Status", "Allocated To"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable table = new JTable(model);

    // Filter controls for Tab 1
    protected final JComboBox<String> filterTypeBox = new JComboBox<>(new String[]{
            "ALL", "CONSULTATION_ROOM", "INPATIENT_WARD", "ICU", "CLINIC", "IMAGING_ROOM", "LAB", "OFFICE"
    });
    protected final JComboBox<String> filterFloorBox = new JComboBox<>(new String[]{
            "ALL", "Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6"
    });
    protected final JTextField searchField = new JTextField(10);
    protected final JButton resetFilterButton = new JButton("Reset Filters");
    protected final JLabel floorCountLabel = new JLabel("Rooms: -");
    protected final JButton addFloorButton = new JButton("Edit Floor Capacities");

    // Allocation fields for Tab 1 (only allocates to Doctor or Manager)
    protected final JComboBox<String> allocateToBox = new JComboBox<>();
    protected final JButton addButton = new JButton("Create Asset");
    protected final JButton editFloorButton = new JButton("Edit Floor Capacities");
    protected final JButton allocateButton = new JButton("Allocate to Doctor / Manager");
    protected final JButton releaseButton = new JButton("Release / Make Available");
    protected final JButton maintenanceButton = new JButton("Set Maintenance");
    protected final JButton deleteButton = new JButton("Delete Selected");
    protected final JButton refreshButton = new JButton("Refresh Inventory");
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");

    // Tab 2: Inpatient Admission Requests & Bed Allocation Queue
    protected final DefaultTableModel admissionModel = new DefaultTableModel(
            new String[]{"Admission ID", "Patient ID", "Doctor ID", "Requested Ward", "Reason", "Urgency", "Assigned Bed", "Status", "Request Date", "Discharge Date"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable admissionTable = new JTable(admissionModel);

    // Tab 2 Action buttons
    protected final JButton assignBedButton = new JButton("Check Availability & Assign Bed");
    protected final JButton dischargeButton = new JButton("Discharge Patient");
    protected final JButton refreshAdmissionsButton = new JButton("Refresh Requests");
    protected final JButton backButton2 = new JButton("\u2190 Back to Dashboard");

    public AssetManagementUX() {
        setTitle("Hospital Asset & Facility Allocation (Rooms, Wards & Inpatient Beds)");
        setSize(1140, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel("Hospital Asset & Facility Allocation (Rooms, Wards & Inpatient Beds)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(titleLabel, BorderLayout.NORTH);

        // --- Build Tab 1: Facility Inventory ---
        JPanel tab1 = new JPanel(new BorderLayout(10, 10));
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter & Search Facilities"));
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(filterTypeBox);
        filterPanel.add(new JLabel("Floor:"));
        filterPanel.add(filterFloorBox);

        floorCountLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        floorCountLabel.setForeground(new Color(20, 80, 160));
        filterPanel.add(floorCountLabel);
        filterPanel.add(addFloorButton);

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(resetFilterButton);
        tab1.add(filterPanel, BorderLayout.NORTH);

        tab1.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel tab1South = new JPanel(new BorderLayout(8, 8));
        JPanel allocPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        allocPanel.setBorder(BorderFactory.createTitledBorder("Direct Asset Allocation (Doctor or Medical Manager Only)"));
        allocPanel.add(new JLabel("Allocate Selected Asset To:"));
        allocateToBox.setPreferredSize(new Dimension(240, 26));
        allocPanel.add(allocateToBox);
        allocPanel.add(allocateButton);
        allocPanel.add(releaseButton);
        allocPanel.add(maintenanceButton);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonBar.add(backButton);
        buttonBar.add(addButton);
        buttonBar.add(editFloorButton);
        buttonBar.add(deleteButton);
        buttonBar.add(refreshButton);

        tab1South.add(allocPanel, BorderLayout.NORTH);
        tab1South.add(buttonBar, BorderLayout.SOUTH);
        tab1.add(tab1South, BorderLayout.SOUTH);

        // --- Build Tab 2: Admission Requests Queue ---
        JPanel tab2 = new JPanel(new BorderLayout(10, 10));
        JLabel tab2Info = new JLabel("Pending Doctor Inpatient Admission Requests - Review Ward Availability & Assign Room/Bed", SwingConstants.LEFT);
        tab2Info.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tab2.add(tab2Info, BorderLayout.NORTH);

        tab2.add(new JScrollPane(admissionTable), BorderLayout.CENTER);

        JPanel tab2Buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        tab2Buttons.add(backButton2);
        tab2Buttons.add(assignBedButton);
        tab2Buttons.add(dischargeButton);
        tab2Buttons.add(refreshAdmissionsButton);
        tab2.add(tab2Buttons, BorderLayout.SOUTH);

        // Add both tabs
        tabbedPane.addTab("Facility & Room Inventory", tab1);
        tabbedPane.addTab("Inpatient Admission & Bed Allocation", tab2);

        root.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(root);
    }
}
