package medical_manager.ui;

import common.FileHandler;
import common.Navigation;
import common.Session;
import common.ui.ProfileUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import medical_manager.ux.ManagerDashboardUX;

public class ManagerDashboardUI extends ManagerDashboardUX {
    private boolean gradingDismissed = false;
    private boolean requestDismissed = false;
    private String previousGradingAlertKey = "";
    private String previousRequestAlertKey = "";

    public ManagerDashboardUI() {
        super();
        refreshWelcome();
        refreshAlerts();

        assessmentTypeButton.addActionListener(e -> new AssessmentTypeUI().setVisible(true));
        gradingButton.addActionListener(e -> openGradingView());
        requestApprovalButton.addActionListener(e -> openRequestApprovalView());
        reportsButton.addActionListener(e -> new ReportsUI().setVisible(true));
        departmentButton.addActionListener(e -> new DepartmentUI().setVisible(true));
        profileButton.addActionListener(e -> new ProfileUI(this::refreshWelcome).setVisible(true));
        shiftrosterButton.addActionListener(e -> new ShiftRosterUI().setVisible(true));
        logoutButton.addActionListener(e -> Navigation.logout(this));

        Timer refreshTimer = new Timer(2000, e -> refreshAlerts());
        refreshTimer.setRepeats(true);
        refreshTimer.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                refreshAlerts();
            }
        });
    }

    public final void refreshWelcome() {
        if (Session.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, Medical Manager " + Session.getCurrentUser().getFullName());
        }
    }

    private void openGradingView() {
        gradingDismissed = true;
        refreshAlerts();
        MedicalGradingUI view = new MedicalGradingUI();
        attachCloseRefresh(view, "grading");
        view.setVisible(true);
    }

    private void openRequestApprovalView() {
        requestDismissed = true;
        refreshAlerts();
        ManagerRequestApprovalUI view = new ManagerRequestApprovalUI();
        attachCloseRefresh(view, "request");
        view.setVisible(true);
    }

    private void attachCloseRefresh(JFrame frame, String key) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if ("grading".equals(key)) {
                    gradingDismissed = true;
                } else if ("request".equals(key)) {
                    requestDismissed = true;
                }
                refreshAlerts();
            }
        });
    }

    private void refreshAlerts() {
        String gradingAlertKey = getGradingAlertKey();
        String requestAlertKey = getRequestAlertKey();
        boolean gradingPending = !gradingAlertKey.isEmpty();
        boolean requestPending = !requestAlertKey.isEmpty();

        if (!gradingPending || !gradingAlertKey.equals(previousGradingAlertKey)) {
            gradingDismissed = false;
        }
        if (!requestPending || !requestAlertKey.equals(previousRequestAlertKey)) {
            requestDismissed = false;
        }

        setAlertState(gradingButton, gradingPending && !gradingDismissed);
        setAlertState(requestApprovalButton, requestPending && !requestDismissed);
        previousGradingAlertKey = gradingAlertKey;
        previousRequestAlertKey = requestAlertKey;
    }

    private void setAlertState(JButton button, boolean active) {
        if (active) {
            button.setBackground(new Color(220, 53, 69));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorderPainted(false);
        } else {
            button.setBackground(UIManager.getColor("Button.background"));
            button.setForeground(UIManager.getColor("Button.foreground"));
            button.setOpaque(false);
            button.setBorderPainted(true);
        }
        button.repaint();
    }

        private String getGradingAlertKey() {
        String currentManagerId = (Session.getCurrentUser() != null && Session.getCurrentUser().getRole() != null && "MEDICAL_MANAGER".equalsIgnoreCase(Session.getCurrentUser().getRole().name()))
                ? Session.getCurrentUser().getUserId() : "";
        if (currentManagerId.isEmpty()) {
            return "";
        }

        List<String> assignedDoctorIds = new ArrayList<>();
        for (String[] doc : FileHandler.read("doctors.txt")) {
            if (doc.length >= 4 && doc[3].equalsIgnoreCase(currentManagerId)) {
                assignedDoctorIds.add(doc[0].trim());
            }
        }

        if (assignedDoctorIds.isEmpty()) {
            return "";
        }

        StringBuilder alertKey = new StringBuilder();
        for (String[] r : FileHandler.read(FileHandler.ASSESSMENTS)) {
            if (r.length >= 10) {
                String docId = r[2] == null ? "" : r[2].trim();
                String grade = r[5] == null ? "" : r[5].trim();
                if (assignedDoctorIds.contains(docId) && "PENDING_REVIEW".equalsIgnoreCase(grade)) {
                    alertKey.append(r[0] == null ? "" : r[0].trim()).append('|');
                }
            }
        }
        return alertKey.toString();
    }

    private String getRequestAlertKey() {
        String currentManagerId = (Session.getCurrentUser() != null && Session.getCurrentUser().getRole() != null && "MEDICAL_MANAGER".equalsIgnoreCase(Session.getCurrentUser().getRole().name()))
                ? Session.getCurrentUser().getUserId() : "";
        if (currentManagerId.isEmpty()) {
            return "";
        }

        List<String> assignedDoctorIds = new ArrayList<>();
        for (String[] doc : FileHandler.read("doctors.txt")) {
            if (doc.length >= 4 && doc[3].equalsIgnoreCase(currentManagerId)) {
                assignedDoctorIds.add(doc[0].trim());
            }
        }

        if (assignedDoctorIds.isEmpty()) {
            return "";
        }

        StringBuilder alertKey = new StringBuilder();
        for (String[] r : FileHandler.read(FileHandler.REQUESTS)) {
            if (r.length >= 7) {
                String docId = r[1] == null ? "" : r[1].trim();
                String status = r[5] == null ? "" : r[5].trim();
                if (assignedDoctorIds.contains(docId) && "PENDING".equalsIgnoreCase(status)) {
                    alertKey.append(r[0] == null ? "" : r[0].trim()).append('|');
                }
            }
        }
        return alertKey.toString();
    }
}

