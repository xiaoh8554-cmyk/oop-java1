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
    protected final JTextField specialtyField = new JTextField();
    protected final JTextField baseFeeField = new JTextField();
    protected final JTextField emergencyFeeField = new JTextField();
    protected final JButton addRateButton = new JButton("Add Rate");
    protected final JButton updateRateButton = new JButton("Update Selected Rate");
    protected final JButton deleteRateButton = new JButton("Delete Rate");
    protected final JButton refreshRatesButton = new JButton("Refresh Rates");

    // Tab 2: Accepted Insurance Networks
    protected final DefaultTableModel insuranceModel = new DefaultTableModel(
            new String[]{"Insurance ID", "Provider Name", "Policy / Plan Type", "Coverage (%)", "Contact Hotline"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable insuranceTable = new JTable(insuranceModel);
    protected final JTextField providerNameField = new JTextField();
    protected final JTextField policyTypeField = new JTextField();
    protected final JTextField coverageField = new JTextField();
    protected final JTextField hotlineField = new JTextField();
    protected final JButton addInsuranceButton = new JButton("Add Insurance Provider");
    protected final JButton updateInsuranceButton = new JButton("Update Selected Insurance");
    protected final JButton deleteInsuranceButton = new JButton("Delete Insurance");
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

        // Setup Tab 1
        JPanel ratesPanel = new JPanel(new BorderLayout(10, 10));
        ratesPanel.add(new JScrollPane(ratesTable), BorderLayout.CENTER);

        JPanel ratesSouth = new JPanel(new BorderLayout(8, 8));
        JPanel ratesForm = new JPanel(new GridLayout(2, 3, 8, 6));
        ratesForm.setBorder(BorderFactory.createTitledBorder("Add / Edit Consultation Rate"));
        ratesForm.add(new JLabel("Specialty / Department:"));
        ratesForm.add(new JLabel("Base Consultation Fee (RM):"));
        ratesForm.add(new JLabel("Emergency Fee (RM):"));
        ratesForm.add(specialtyField);
        ratesForm.add(baseFeeField);
        ratesForm.add(emergencyFeeField);

        JPanel ratesButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        ratesButtons.add(addRateButton);
        ratesButtons.add(updateRateButton);
        ratesButtons.add(deleteRateButton);
        ratesButtons.add(refreshRatesButton);

        ratesSouth.add(ratesForm, BorderLayout.NORTH);
        ratesSouth.add(ratesButtons, BorderLayout.SOUTH);
        ratesPanel.add(ratesSouth, BorderLayout.SOUTH);

        // Setup Tab 2
        JPanel insurancePanel = new JPanel(new BorderLayout(10, 10));
        insurancePanel.add(new JScrollPane(insuranceTable), BorderLayout.CENTER);

        JPanel insuranceSouth = new JPanel(new BorderLayout(8, 8));
        JPanel insuranceForm = new JPanel(new GridLayout(2, 4, 8, 6));
        insuranceForm.setBorder(BorderFactory.createTitledBorder("Add / Edit Accepted Insurance Provider"));
        insuranceForm.add(new JLabel("Provider Name:"));
        insuranceForm.add(new JLabel("Policy / Plan Type:"));
        insuranceForm.add(new JLabel("Coverage (%):"));
        insuranceForm.add(new JLabel("Contact Hotline:"));
        insuranceForm.add(providerNameField);
        insuranceForm.add(policyTypeField);
        insuranceForm.add(coverageField);
        insuranceForm.add(hotlineField);

        JPanel insuranceButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        insuranceButtons.add(addInsuranceButton);
        insuranceButtons.add(updateInsuranceButton);
        insuranceButtons.add(deleteInsuranceButton);
        insuranceButtons.add(refreshInsuranceButton);

        insuranceSouth.add(insuranceForm, BorderLayout.NORTH);
        insuranceSouth.add(insuranceButtons, BorderLayout.SOUTH);
        insurancePanel.add(insuranceSouth, BorderLayout.SOUTH);

        tabbedPane.addTab("Base Consultation Rates", ratesPanel);
        tabbedPane.addTab("Accepted Insurance Networks", insurancePanel);

        root.add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomBar.add(backButton);
        root.add(bottomBar, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
