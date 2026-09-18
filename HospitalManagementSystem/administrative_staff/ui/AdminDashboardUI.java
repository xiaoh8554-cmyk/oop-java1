package administrative_staff.ui;

import administrative_staff.ux.AdminDashboardUX;
import common.Navigation;
import common.Session;
import common.ui.ProfileUI;

public class AdminDashboardUI extends AdminDashboardUX {
    public AdminDashboardUI() {
        super();
        refreshWelcome();
        usersButton.addActionListener(e -> new UserManagementUI().setVisible(true));
        wardsButton.addActionListener(e -> new WardClinicUI().setVisible(true));
        departmentsButton.addActionListener(e -> new DepartmentUI().setVisible(true));
        billingButton.addActionListener(e -> new BillingUI().setVisible(true));
        profileButton.addActionListener(e -> new ProfileUI(this::refreshWelcome).setVisible(true));
        logoutButton.addActionListener(e -> Navigation.logout(this));
    }

    public void refreshWelcome() {
        if (Session.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + Session.getCurrentUser().getFullName());
        }
    }
}
