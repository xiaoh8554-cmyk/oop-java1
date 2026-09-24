package medical_manager.ui;

import common.*;
import common.model.User;
import common.model.UserRole;
import java.util.*;
import medical_manager.ux.ReportsUX;

public class ReportsUI extends ReportsUX {
    public ReportsUI() {
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
        List<String[]> assets = FileHandler.read(FileHandler.ASSETS);
        List<String[]> admissions = FileHandler.read(FileHandler.ADMISSIONS);

        long patients = users.stream().filter(u -> u.getRole() == UserRole.PATIENT).count();
        long doctors = users.stream().filter(u -> u.getRole() == UserRole.DOCTOR).count();

        int totalInpatientBeds = 0;
        int occupiedBeds = 0;
        for (String[] a : assets) {
            if (a.length >= 6 && ("INPATIENT_WARD".equalsIgnoreCase(a[2]) || "ICU".equalsIgnoreCase(a[2]))) {
                int cap = DataUtil.toInt(a[4], 1);
                totalInpatientBeds += cap;
                if ("ALLOCATED".equalsIgnoreCase(a[5])) {
                    occupiedBeds += 1;
                }
            }
        }

        long admittedCount = 0;
        long pendingCount = 0;
        for (String[] adm : admissions) {
            if (adm.length >= 8) {
                if ("ADMITTED".equalsIgnoreCase(adm[7])) {
                    admittedCount++;
                } else if ("PENDING".equalsIgnoreCase(adm[7])) {
                    pendingCount++;
                }
            }
        }

        double total = 0, paid = 0, unpaid = 0;
        for (String[] r : bills) {
            if (r.length >= 5) {
                double a = DataUtil.toDouble(r[3], 0);
                total += a;
                if ("PAID".equalsIgnoreCase(r[4])) {
                    paid += a;
                } else {
                    unpaid += a;
                }
            }
        }

        Map<String, Integer> grades = new LinkedHashMap<>();
        for (String[] r : assessments) {
            if (r.length >= 6) {
                grades.put(r[5], grades.getOrDefault(r[5], 0) + 1);
            }
        }

        double occ = totalInpatientBeds > 0 ? ((double) occupiedBeds / totalInpatientBeds) * 100 : 0;

        StringBuilder html = new StringBuilder();
        html.append("<html><body style='margin:0; font-family:Arial, sans-serif; background:#f3f6fb; color:#1f2a37;'>");
        html.append("<div style='padding:12px 10px 18px 10px;'>");
        html.append("<div style='background:linear-gradient(135deg,#eaf3ff,#f8fbff); border:1px solid #dbe7ff; border-radius:12px; padding:18px 20px; margin-bottom:16px;'>");
        html.append("<div style='font-size:30px; font-weight:700; color:#153a6c;'>APU Medical Centre</div>");
        html.append("<div style='font-size:12px; color:#5b6b85; margin-top:6px;'>Generated: ").append(DataUtil.timestamp()).append("</div>");
        html.append("</div>");

        html.append("<table style='width:100%; border-collapse:separate; border-spacing:10px 10px;'>");
        html.append("<tr>");
        appendMetricCard(html, "TOTAL USERS", String.valueOf(users.size()), "#1d4ed8", "#eff6ff");
        appendMetricCard(html, "PATIENTS", String.valueOf(patients), "#0f766e", "#ecfeff");
        appendMetricCard(html, "DOCTORS", String.valueOf(doctors), "#7c3aed", "#f5f3ff");
        appendMetricCard(html, "BED OCCUPANCY", String.format(Locale.US, "%.1f%%", occ), "#ea580c", "#fff7ed");
        html.append("</tr>");
        html.append("</table>");

        html.append("<div style='display:block; margin-top:8px; margin-bottom:8px; font-size:12px; letter-spacing:0.08em; text-transform:uppercase; color:#64748b; font-weight:700;'>Operational Snapshot</div>");
        html.append("<div style='background:#ffffff; border:1px solid #dde6f4; border-radius:12px; padding:10px 14px; margin-bottom:14px;'>");
        appendInfoRow(html, "Wards / Clinics", String.valueOf(wards.size()));
        appendInfoRow(html, "Departments / Specialties", String.valueOf(deps.size()));
        appendInfoRow(html, "Assessment Types", String.valueOf(types.size()));
        appendInfoRow(html, "Active Admitted Inpatients", String.valueOf(admittedCount));
        appendInfoRow(html, "Pending Admission Requests", String.valueOf(pendingCount));
        appendInfoRow(html, "Inpatient Beds Available", String.valueOf(totalInpatientBeds));
        appendInfoRow(html, "Occupied Beds", String.valueOf(occupiedBeds));
        html.append("</div>");

        html.append("<div style='display:block; margin-top:8px; margin-bottom:8px; font-size:12px; letter-spacing:0.08em; text-transform:uppercase; color:#64748b; font-weight:700;'>Clinical Activity</div>");
        html.append("<div style='background:#ffffff; border:1px solid #dde6f4; border-radius:12px; padding:10px 14px; margin-bottom:14px;'>");
        appendInfoRow(html, "Assessments recorded", String.valueOf(assessments.size()));
        appendInfoRow(html, "Prescriptions issued", String.valueOf(rx.size()));
        appendInfoRow(html, "Feedback records", String.valueOf(fb.size()));
        html.append("</div>");

        html.append("<div style='display:block; margin-top:8px; margin-bottom:8px; font-size:12px; letter-spacing:0.08em; text-transform:uppercase; color:#64748b; font-weight:700;'>Financial Summary</div>");
        html.append("<div style='background:#ffffff; border:1px solid #dde6f4; border-radius:12px; padding:10px 14px; margin-bottom:14px;'>");
        appendInfoRow(html, "Total billed", String.format(Locale.US, "RM %.2f", total));
        appendInfoRow(html, "Paid revenue", String.format(Locale.US, "RM %.2f", paid));
        appendInfoRow(html, "Outstanding", String.format(Locale.US, "RM %.2f", unpaid));
        html.append("</div>");

        html.append("<div style='display:block; margin-top:8px; margin-bottom:8px; font-size:12px; letter-spacing:0.08em; text-transform:uppercase; color:#64748b; font-weight:700;'>Medical Grade Distribution</div>");
        html.append("<div style='background:#ffffff; border:1px solid #dde6f4; border-radius:12px; padding:10px 14px;'>");
        if (grades.isEmpty()) {
            html.append("<div style='padding:8px 0; color:#475569;'>No graded assessments yet.</div>");
        } else {
            html.append("<table style='width:100%; border-collapse:collapse; font-size:13px;'>");
            for (Map.Entry<String, Integer> entry : grades.entrySet()) {
                html.append("<tr><td style='padding:7px 6px; border-bottom:1px solid #edf2f7; color:#334155;'>")
                    .append(entry.getKey())
                    .append("</td><td style='padding:7px 6px; border-bottom:1px solid #edf2f7; text-align:right; font-weight:700; color:#0f172a;'>")
                    .append(entry.getValue())
                    .append("</td></tr>");
            }
            html.append("</table>");
        }
        html.append("</div>");
        html.append("</div></body></html>");

        reportArea.setText(html.toString());
        reportArea.setCaretPosition(0);
    }

    private void appendMetricCard(StringBuilder html, String label, String value, String accent, String bg) {
        html.append("<td style='width:25%; background:").append(bg)
            .append("; border:1px solid #dfeaf8; border-radius:12px; padding:12px 14px; vertical-align:top;'>")
            .append("<div style='font-size:11px; letter-spacing:0.08em; text-transform:uppercase; color:#475569; font-weight:700;'>")
            .append(label)
            .append("</div>")
            .append("<div style='font-size:28px; font-weight:700; color:")
            .append(accent)
            .append("; margin-top:8px; line-height:1.2;'>")
            .append(value)
            .append("</div>")
            .append("</td>");
    }

    private void appendInfoRow(StringBuilder html, String label, String value) {
        html.append("<div style='display:flex; justify-content:space-between; align-items:center; padding:7px 0; border-bottom:1px solid #edf2f7;'>")
            .append("<span style='color:#475569; font-weight:600;'>")
            .append(label)
            .append("</span>")
            .append("<span style='color:#0f172a; font-weight:700;'>")
            .append(value)
            .append("</span>")
            .append("</div>");
    }
}
