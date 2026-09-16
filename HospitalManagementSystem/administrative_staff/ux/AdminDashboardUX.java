package administrative_staff.ux;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardUX extends JFrame {
    protected final JLabel welcomeLabel = new JLabel("Administrative Staff Dashboard", SwingConstants.CENTER);
    protected final JButton usersButton = new JButton("User Management");
    protected final JButton wardsButton = new JButton("Wards / Clinics");
    protected final JButton departmentsButton = new JButton("Departments / Specialties");
    protected final JButton billingButton = new JButton("Billing Management");
    protected final JButton profileButton = new JButton("My Profile");
    protected final JButton logoutButton = new JButton("Logout");

    public AdminDashboardUX() {
        setTitle("HMS - Administrative Staff");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 440);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(welcomeLabel, BorderLayout.NORTH);
        JPanel menu = new JPanel(new GridLayout(3, 2, 12, 12));
        menu.add(usersButton); menu.add(wardsButton);
        menu.add(departmentsButton); menu.add(billingButton);
        menu.add(profileButton);
        root.add(menu, BorderLayout.CENTER);
        root.add(logoutButton, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
