package medical_manager.ux;

import java.awt.*;
import javax.swing.*;

public class ManagerDashboardUX extends JFrame {

    protected final JLabel welcomeLabel = new JLabel("Medical Manager Dashboard", SwingConstants.CENTER);
    protected final JButton assessmentTypeButton = new JButton("Assessment / Check-up Types");
    protected final JButton gradingButton = new JButton("Medical Grading");
    protected final JButton requestApprovalButton = new JButton("Diagnostic Requisition Approval");
    protected final JButton reportsButton = new JButton("Analytical Reports");
    protected final JButton departmentButton = new JButton("Department Management");
    protected final JButton shiftrosterButton = new JButton("Doctor Shift Roster");
    protected final JButton profileButton = new JButton("My Profile");
    protected final JButton logoutButton = new JButton("Logout");

    public ManagerDashboardUX() {
        setTitle("HMS - Medical Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 480);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(welcomeLabel, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(2, 3, 12, 12));
        menu.add(assessmentTypeButton);
        menu.add(gradingButton);
        menu.add(requestApprovalButton);
        menu.add(reportsButton);
        menu.add(departmentButton);
        menu.add(shiftrosterButton);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(profileButton);
        actions.add(logoutButton);

        root.add(menu, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
