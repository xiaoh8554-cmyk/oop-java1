package doctors.ux;

import javax.swing.*;
import java.awt.*;

public class DoctorDashboardUX extends JFrame {
    protected final JLabel welcomeLabel = new JLabel("Doctor Dashboard", SwingConstants.CENTER);
    protected final JButton assessmentButton = new JButton("Assessment & Lab Results");
    protected final JButton feedbackButton = new JButton("Clinical Feedback");
    protected final JButton prescriptionButton = new JButton("Prescriptions");
    protected final JButton profileButton = new JButton("My Profile");
    protected final JButton logoutButton = new JButton("Logout");

    public DoctorDashboardUX() {
        setTitle("HMS - Doctor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 420);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(welcomeLabel, BorderLayout.NORTH);
        JPanel menu = new JPanel(new GridLayout(2, 2, 12, 12));
        menu.add(assessmentButton); menu.add(feedbackButton); menu.add(prescriptionButton); menu.add(profileButton);
        root.add(menu, BorderLayout.CENTER);
        root.add(logoutButton, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
