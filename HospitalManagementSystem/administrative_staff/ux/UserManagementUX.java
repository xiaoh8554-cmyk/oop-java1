package administrative_staff.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserManagementUX extends JFrame {
    protected final DefaultTableModel model = new DefaultTableModel(
            new String[]{"User ID", "Email / Gmail", "Role", "Full Name", "Phone Number"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable table = new JTable(model);

    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");
    protected final JButton addButton = new JButton("Add User");
    protected final JButton detailButton = new JButton("Detail");
    protected final JButton deleteButton = new JButton("Delete Selected");
    protected final JButton refreshButton = new JButton("Refresh");

    public UserManagementUX() {
        setTitle("User Management");
        setSize(960, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel titleLabel = new JLabel("System User Directory & Role Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(titleLabel, BorderLayout.NORTH);

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttons.add(backButton);
        buttons.add(addButton);
        buttons.add(detailButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);

        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);
    }
}
