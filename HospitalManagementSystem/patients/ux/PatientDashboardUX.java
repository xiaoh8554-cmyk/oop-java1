package patients.ux;

import javax.swing.*;
import java.awt.*;

public class PatientDashboardUX extends JFrame {
    protected final JLabel welcomeLabel = new JLabel("Patient Dashboard", SwingConstants.CENTER);
    protected final JButton historyButton = new JButton("My Appointment History & Reschedule");
    protected final JButton recordsButton = new JButton("My Medical Records");
    protected final JButton billingButton = new JButton("My Billing");
    protected final JButton prescriptionButton = new JButton("Prescriptions & Feedback");
    protected final JButton profileButton = new JButton("My Profile");
    protected final JButton logoutButton = new JButton("Logout");

    public PatientDashboardUX() {
        setTitle("HMS - Patient Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 480);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(welcomeLabel, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(3, 2, 12, 12));
        menu.add(historyButton);
        menu.add(recordsButton);
        menu.add(billingButton);
        menu.add(prescriptionButton);
        menu.add(profileButton);

        root.add(menu, BorderLayout.CENTER);
        root.add(logoutButton, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
