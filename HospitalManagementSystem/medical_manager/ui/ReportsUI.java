package medical_manager.ui;

import common.*;
import common.model.User;
import common.model.UserRole;
import medical_manager.ux.ReportsUX;
import java.util.*;

public class ReportsUI extends ReportsUX {
    public ReportsUI(){
        super();
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> generate());
        generate();
    }

    private void generate() {
        List<User> users = DataManager.getInstance().getAllUsers();
        List<String[]> wards = FileHandler.read(FileHandler.WARDS);
        List<String[]> deps = FileHandler.read(FileHandler.DEPARTMENTS);
        List<String[]> types = FileHandler.read(FileHandler.ASSESSMENT_TYPES);
        List<String[]> assessments = FileHandler.read(FileHandler.ASSESSMENTS);
        List<String[]> bills = FileHandler.read(FileHandler.BILLING);
        List<String[]> rx = FileHandler.read(FileHandler.PRESCRIPTIONS);
        List<String[]> fb = FileHandler.read(FileHandler.FEEDBACK);

        long patients = users.stream().filter(u -> u.getRole() == UserRole.PATIENT).count();
        long doctors = users.stream().filter(u -> u.getRole() == UserRole.DOCTOR).count();

        double total = 0, paid = 0, unpaid = 0;
        for (String[] r : bills) {
            if (r.length >= 5) {
                double a = DataUtil.toDouble(r[3], 0);
                total += a;
                if (r[4].equals("PAID")) paid += a;
                else unpaid += a;
            }
        }

        Map<String, Integer> grades = new LinkedHashMap<>();
        for (String[] r : assessments) {
            if (r.length >= 6) {
                grades.put(r[5], grades.getOrDefault(r[5], 0) + 1);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("APU MEDICAL CENTRE - ANALYTICAL REPORT\n");
        sb.append("Generated: ").append(DataUtil.timestamp()).append("\n");
        sb.append("============================================================\n\n");
        sb.append("USERS\n");
        sb.append("Total users: ").append(users.size()).append("\n");
        sb.append("Patients: ").append(patients).append("\n");
        sb.append("Doctors: ").append(doctors).append("\n\n");
        sb.append("FACILITIES\n");
        sb.append("Wards / Clinics: ").append(wards.size()).append("\n");
        sb.append("Departments / Specialties: ").append(deps.size()).append("\n");
        sb.append("Assessment Types: ").append(types.size()).append("\n\n");
        sb.append("CLINICAL ACTIVITY\n");
        sb.append("Assessments recorded: ").append(assessments.size()).append("\n");
        sb.append("Prescriptions issued: ").append(rx.size()).append("\n");
        sb.append("Feedback records: ").append(fb.size()).append("\n\n");
        sb.append("BILLING\n");
        sb.append(String.format("Total billed: RM %.2f\nPaid revenue: RM %.2f\nOutstanding: RM %.2f\n", total, paid, unpaid));
        sb.append("\nMEDICAL GRADE DISTRIBUTION\n");
        if (grades.isEmpty()) {
            sb.append("No graded assessments yet.\n");
        } else {
            grades.forEach((k, v) -> sb.append(String.format("%-22s %d\n", k, v)));
        }

        reportArea.setText(sb.toString());
        reportArea.setCaretPosition(0);
    }
}
