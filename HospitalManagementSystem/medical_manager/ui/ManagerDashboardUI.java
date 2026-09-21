package medical_manager.ui;

import common.Navigation;
import common.Session;
import common.ui.ProfileUI;
import medical_manager.ux.ManagerDashboardUX;

public class ManagerDashboardUI extends ManagerDashboardUX {
    public ManagerDashboardUI() {
        super();
        refreshWelcome();
        assessmentTypeButton.addActionListener(e -> new AssessmentTypeUI().setVisible(true));
        gradingButton.addActionListener(e -> new MedicalGradingUI().setVisible(true));
        reportsButton.addActionListener(e -> new ReportsUI().setVisible(true));
        profileButton.addActionListener(e -> new ProfileUI(this::refreshWelcome).setVisible(true));
        shiftrosterButton.addActionListener(e -> new ShiftRosterUI().setVisible(true));
        logoutButton.addActionListener(e -> Navigation.logout(this));
    }

    public void refreshWelcome() {
        if (Session.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, Medical Manager " + Session.getCurrentUser().getFullName());
        }
    }
}
