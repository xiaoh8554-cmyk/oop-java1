package administrative_staff.ui;

import administrative_staff.ux.UserManagementUX;
import common.DataManager;
import common.Session;
import common.model.User;
import common.ui.ProfileUI;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserManagementUI extends UserManagementUX {

    public UserManagementUI() {
        super();
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> refresh());
        addButton.addActionListener(e -> openAddUserDialog());
        detailButton.addActionListener(e -> openUserDetail());
        deleteButton.addActionListener(e -> deleteUser());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    openUserDetail();
                }
            }
        });

        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        for (User u : DataManager.getInstance().getAllUsers()) {
            model.addRow(new Object[]{u.getId(), u.getEmail(), u.getRole().name(), u.getFullName(), u.getPhoneNumber()});
        }
    }

    private void openAddUserDialog() {
        new AddUserDialog(this, this::refresh).setVisible(true);
    }

    private void openUserDetail() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user from the table to view/edit full profile details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = model.getValueAt(row, 0).toString();
        User user = DataManager.getInstance().findById(id);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Selected user could not be found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new ProfileUI(user, this::refresh).setVisible(true);
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = model.getValueAt(row, 0).toString();
        if (Session.getCurrentUser() != null && id.equalsIgnoreCase(Session.getCurrentUser().getId())) {
            JOptionPane.showMessageDialog(this, "You cannot delete your currently logged-in account.", "Action Prohibited", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete user " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DataManager.getInstance().deleteUser(id);
            refresh();
            JOptionPane.showMessageDialog(this, "User " + id + " has been deleted.");
        }
    }
}
