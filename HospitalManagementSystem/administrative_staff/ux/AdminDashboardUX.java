package administrative_staff.ux;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardUX extends JFrame {
    protected final JLabel welcomeLabel = new JLabel("Administrative Staff Dashboard", SwingConstants.CENTER);
    protected final JButton usersButton = new JButton("User Management");
    protected final JButton doctorManagerButton = new JButton("Doctor - Manager Assignment");
    protected final JButton assetsButton = new JButton("Physical Asset Allocation");
    protected final JButton wardsButton = new JButton("Wards / Clinics");
    protected final JButton departmentsButton = new JButton("Departments / Specialties");
    protected final JButton ratesInsuranceButton = new JButton("Rates & Insurance Networks");
    protected final JButton billingButton = new JButton("Billing Management");
    protected final JButton profileButton = new JButton("My Profile");
    protected final JButton logoutButton = new JButton("Logout");

    public AdminDashboardUX() {
        setTitle("HMS - Administrative Staff");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(welcomeLabel, BorderLayout.NORTH);
        JPanel menu = new JPanel(new GridLayout(4, 2, 12, 12));
        menu.add(usersButton); menu.add(doctorManagerButton);
        menu.add(assetsButton); menu.add(wardsButton);
        menu.add(departmentsButton); menu.add(ratesInsuranceButton);
        menu.add(billingButton); menu.add(profileButton);
        root.add(menu, BorderLayout.CENTER);
        root.add(logoutButton, BorderLayout.SOUTH);
        setContentPane(root);
    }
}

