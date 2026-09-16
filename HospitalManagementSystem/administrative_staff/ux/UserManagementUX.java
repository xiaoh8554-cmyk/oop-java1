package administrative_staff.ux;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserManagementUX extends JFrame {
    protected final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Email / Gmail", "Role", "Full Name", "Phone"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    protected final JTable table = new JTable(model);
    protected final JTextField emailField = new JTextField();
    protected final JPasswordField passwordField = new JPasswordField();
    protected final JComboBox<String> roleBox = new JComboBox<>(new String[]{"ADMINISTRATIVE_STAFF","MEDICAL_MANAGER","DOCTOR","PATIENT"});
    protected final JTextField fullNameField = new JTextField();
    protected final JTextField phoneField = new JTextField();
    protected final JButton backButton = new JButton("\u2190 Back to Dashboard");
    protected final JButton addButton = new JButton("Add User");
    protected final JButton deleteButton = new JButton("Delete Selected");
    protected final JButton refreshButton = new JButton("Refresh");

    public UserManagementUX() {
        setTitle("User Management"); setSize(920, 560); setLocationRelativeTo(null); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout(10,10)); root.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel form = new JPanel(new GridLayout(2,5,7,7));
        form.add(new JLabel("Email / Gmail")); form.add(new JLabel("Password")); form.add(new JLabel("Role")); form.add(new JLabel("Full Name")); form.add(new JLabel("Phone"));
        form.add(emailField); form.add(passwordField); form.add(roleBox); form.add(fullNameField); form.add(phoneField);
        JPanel south = new JPanel(new BorderLayout(8,8)); south.add(form, BorderLayout.CENTER);
        JPanel buttons = new JPanel(); buttons.add(backButton); buttons.add(addButton); buttons.add(deleteButton); buttons.add(refreshButton); south.add(buttons, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH); setContentPane(root);
    }
}
