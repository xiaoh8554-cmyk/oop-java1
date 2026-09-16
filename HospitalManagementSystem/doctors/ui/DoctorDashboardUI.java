package doctors.ui;

import common.Navigation;
import common.Session;
import common.ui.ProfileUI;
import doctors.ux.DoctorDashboardUX;

public class DoctorDashboardUI extends DoctorDashboardUX {
    public DoctorDashboardUI() {
        super();
        refreshWelcome();
        assessmentButton.addActionListener(e -> new AssessmentResultUI().setVisible(true));
        feedbackButton.addActionListener(e -> new ClinicalFeedbackUI().setVisible(true));
        prescriptionButton.addActionListener(e -> new PrescriptionUI().setVisible(true));
        profileButton.addActionListener(e -> new ProfileUI(this::refreshWelcome).setVisible(true));
        logoutButton.addActionListener(e -> Navigation.logout(this));
    }

    public void refreshWelcome() {
        if (Session.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + Session.getCurrentUser().getFullName() + " - Doctor");
        }
    }
}
