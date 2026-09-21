package patients.ui;

import common.Navigation;
import common.Session;
import common.ui.ProfileUI;
import patients.ux.PatientDashboardUX;

public class PatientDashboardUI extends PatientDashboardUX {
    public PatientDashboardUI() {
        super();
        refreshWelcome();
        historyButton.addActionListener(e -> new AppointmentHistoryUI().setVisible(true));
        recordsButton.addActionListener(e -> new MedicalRecordUI().setVisible(true));
        billingButton.addActionListener(e -> new PatientBillingUI().setVisible(true));
        prescriptionButton.addActionListener(e -> new PatientPrescriptionFeedbackUI().setVisible(true));
        profileButton.addActionListener(e -> new ProfileUI(this::refreshWelcome).setVisible(true));
        logoutButton.addActionListener(e -> Navigation.logout(this));
    }

    public void refreshWelcome() {
        if (Session.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + Session.getCurrentUser().getFullName());
        }
    }
}
