package administrative_staff.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RatesInsuranceUX extends JFrame {
    protected final JTabbedPane tabbedPane = new JTabbedPane();

    // Tab 1: Base Consultation Rates
    protected final DefaultTableModel ratesModel = new DefaultTableModel(
            new String[]{"Rate ID", "Specialty / Department", "Base Consultation Fee (RM)", "Emergency Fee (RM)"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable ratesTable = new JTable(ratesModel);
    protected final JButton addRateButton = new JButton("Add Rate");
    protected final JButton editRateButton = new JButton("Edit Selected Rate");
    protected final JButton deleteRateButton = new JButton("Delete Rate");
    protected final JButton refreshRatesButton = new JButton("Refresh Rates");

    // Tab 2: Accepted Insurance Networks
    protected final DefaultTableModel insuranceModel = new DefaultTableModel(
            new String[]{"Insurance ID", "Provider Name", "Policy / Plan Type", "Coverage (%)", "Contact Hotline"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable insuranceTable = new JTable(insuranceModel);
    protected final JButton addInsuranceButton = new JButton("Add Insurance Provider");
    protected final JButton editInsuranceButton = new JButton("Edit Selected Provider");
    protected final JButton deleteInsuranceButton = new JButton("Delete Provider");
    protected final JButton refreshInsuranceButton = new JButton("Refresh Insurance");

    // Common
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");

    public RatesInsuranceUX() {
        setTitle("Consultation Rates & Accepted Insurance Networks");
        setSize(960, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Setup Tab 1: Rates
        JPanel ratesPanel = new JPanel(new BorderLayout(10, 10));
        ratesPanel.add(new JScrollPane(ratesTable), BorderLayout.CENTER);

        JPanel ratesButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        ratesButtons.add(addRateButton);
        ratesButtons.add(editRateButton);
        ratesButtons.add(deleteRateButton);
        ratesButtons.add(refreshRatesButton);
        ratesPanel.add(ratesButtons, BorderLayout.SOUTH);

        // Setup Tab 2: Insurance
        JPanel insurancePanel = new JPanel(new BorderLayout(10, 10));
        insurancePanel.add(new JScrollPane(insuranceTable), BorderLayout.CENTER);

        JPanel insuranceButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        insuranceButtons.add(addInsuranceButton);
        insuranceButtons.add(editInsuranceButton);
        insuranceButtons.add(deleteInsuranceButton);
        insuranceButtons.add(refreshInsuranceButton);
        insurancePanel.add(insuranceButtons, BorderLayout.SOUTH);

        tabbedPane.addTab("Base Consultation Rates", ratesPanel);
        tabbedPane.addTab("Accepted Insurance Networks", insurancePanel);

        root.add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomBar.add(backButton);
        root.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
