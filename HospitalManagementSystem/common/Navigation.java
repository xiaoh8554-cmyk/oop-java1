package common;

import common.model.User;
import common.model.UserRole;
import common.ui.LoginUI;
import administrative_staff.ui.AdminDashboardUI;
import medical_manager.ui.ManagerDashboardUI;
import doctors.ui.DoctorDashboardUI;
import patients.ui.PatientDashboardUI;
import javax.swing.*;

public class Navigation {
    public static void openDashboard(User user) {
        Session.setCurrentUser(user);
        JFrame frame;

        UserRole role = user.getRole();
        if (role == UserRole.ADMINISTRATIVE_STAFF) {
            frame = new AdminDashboardUI();
        } else if (role == UserRole.MEDICAL_MANAGER) {
            frame = new ManagerDashboardUI();
        } else if (role == UserRole.DOCTOR) {
            frame = new DoctorDashboardUI();
        } else {
            frame = new PatientDashboardUI();
        }

        frame.setVisible(true);
    }

    public static void logout(JFrame current) {
        Session.clear();
        current.dispose();
        new LoginUI().setVisible(true);
    }
}
