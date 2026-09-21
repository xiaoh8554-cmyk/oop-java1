package medical_manager.ux;

import java.awt.*;
import javax.swing.*;

public class ManagerDashboardUX extends JFrame {
    protected final JLabel welcomeLabel = new JLabel("Medical Manager Dashboard", SwingConstants.CENTER);
    protected final JButton assessmentTypeButton = new JButton("Assessment / Check-up Types");
    protected final JButton gradingButton = new JButton("Medical Grading");
    protected final JButton reportsButton = new JButton("Analytical Reports");
    protected final JButton shiftrosterButton = new JButton("Doctor Shift Roster");
    protected final JButton profileButton = new JButton("My Profile");
    protected final JButton logoutButton = new JButton("Logout");

    public ManagerDashboardUX() {
        setTitle("HMS - Medical Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 420);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(welcomeLabel, BorderLayout.NORTH);
        JPanel menu = new JPanel(new GridLayout(2, 3, 12, 12));
        menu.add(assessmentTypeButton); menu.add(gradingButton); menu.add(reportsButton); menu.add(profileButton); menu.add(shiftrosterButton);
        root.add(menu, BorderLayout.CENTER);
        root.add(logoutButton, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
