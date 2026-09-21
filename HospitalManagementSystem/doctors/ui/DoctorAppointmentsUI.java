package doctors.ui;

import common.DataManager;
import common.FileHandler;
import common.Session;
import common.model.User;
import common.model.UserRole;
import doctors.Doctor;
import doctors.ux.DoctorAppointmentsUX;
import patients.Patient;
import patients.ui.AppointmentHistoryUI;
import patients.ui.AppointmentRescheduleUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentsUI extends DoctorAppointmentsUX {

    public static class AppointmentRecord {
        public String apptId;
        public String patientId;
        public String doctorId;
        public String doctorName;
        public String specialty;
        public String date;
        public String timeSlot;
        public String room;
        public String status;
        public String reason;
        public String patientName = "Unknown";
        public String patientPhone = "-";

        public AppointmentRecord(String[] raw) {
            this.apptId = raw.length > 0 ? raw[0] : "";
            this.patientId = raw.length > 1 ? raw[1] : "";
            this.doctorId = raw.length > 2 ? raw[2] : "";
            this.doctorName = raw.length > 3 ? raw[3] : "";
            this.specialty = raw.length > 4 ? raw[4] : "";
            this.date = raw.length > 5 ? raw[5] : "";
            this.timeSlot = raw.length > 6 ? raw[6] : "";
            this.room = raw.length > 7 ? raw[7] : "";
            this.status = raw.length > 8 ? raw[8] : "BOOKED";
            this.reason = raw.length > 9 ? raw[9] : "";
        }

        public int getPriorityRank() {
            if ("URGENT".equalsIgnoreCase(status)) return 0; // Highest Priority
            if ("BOOKED".equalsIgnoreCase(status)) return 1;
            if ("RESCHEDULED".equalsIgnoreCase(status)) return 2;
            if ("COMPLETED".equalsIgnoreCase(status)) return 3;
            return 4;
        }

        public String getPriorityLabel() {
            if ("URGENT".equalsIgnoreCase(status)) return "[URGENT] High";
            if ("COMPLETED".equalsIgnoreCase(status)) return "Completed";
            if ("RESCHEDULED".equalsIgnoreCase(status)) return "Rescheduled";
            return "Normal";
        }
    }

    private final List<AppointmentRecord> cachedAppointments = new ArrayList<>();

    public DoctorAppointmentsUI() {
        super();
        setupDoctorInfo();
        setupTableRenderer();
        initListeners();
        refreshAppointments();
    }

    private String getCurrentDoctorId() {
        User u = Session.getCurrentUser();
        return (u != null && u.getRole() == UserRole.DOCTOR) ? u.getId() : "D001";
    }

    private void setupDoctorInfo() {
        User u = Session.getCurrentUser();
        if (u instanceof Doctor) {
            Doctor d = (Doctor) u;
            doctorInfoLabel.setText(String.format("Logged in: Dr. %s (%s)  |  Specialty: %s  |  Room: %s",
                    d.getFullName(), d.getId(), d.getSpecialty(), d.getRoomNumber()));
        } else if (u != null) {
            doctorInfoLabel.setText(String.format("Logged in: %s (%s)", u.getFullName(), u.getId()));
        } else {
            doctorInfoLabel.setText("Logged in: Dr. Aisha (D001) [Default]");
        }
    }

    private void setupTableRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                Object statusObj = table.getValueAt(row, 8);
                String status = (statusObj != null) ? statusObj.toString() : "";

                if (!isSelected) {
                    if ("URGENT".equalsIgnoreCase(status)) {
                        comp.setBackground(new Color(255, 235, 235));
                        comp.setForeground(new Color(180, 0, 0));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("RESCHEDULED".equalsIgnoreCase(status)) {
                        comp.setBackground(new Color(245, 248, 255));
                        comp.setForeground(new Color(25, 70, 160));
                        setFont(getFont().deriveFont(Font.PLAIN));
                    } else if ("COMPLETED".equalsIgnoreCase(status)) {
                        comp.setBackground(new Color(248, 248, 248));
                        comp.setForeground(new Color(110, 110, 110));
                        setFont(getFont().deriveFont(Font.PLAIN));
                    } else {
                        comp.setBackground(Color.WHITE);
                        comp.setForeground(new Color(25, 35, 50));
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                } else {
                    if ("URGENT".equalsIgnoreCase(status)) {
                        comp.setForeground(Color.RED);
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }

                if (column == 0 || column == 8) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                return comp;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private void initListeners() {
        backButton.addActionListener(e -> dispose());
        refreshButton.addActionListener(e -> refreshAppointments());

        clearFiltersButton.addActionListener(e -> {
            searchField.setText("");
            statusFilterBox.setSelectedIndex(0);
            doctorScopeBox.setSelectedIndex(0);
            applyFilters();
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        statusFilterBox.addActionListener(e -> applyFilters());
        doctorScopeBox.addActionListener(e -> applyFilters());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectionDetails();
            }
        });

        completeButton.addActionListener(e -> markAppointmentAsCompleted());
        rescheduleButton.addActionListener(e -> rescheduleSelectedAppointment());
        historyButton.addActionListener(e -> viewPatientAppointmentHistory());
        patientDetailsButton.addActionListener(e -> viewPatientDetails());
        assessmentButton.addActionListener(e -> openAssessmentNotes());
        prescriptionButton.addActionListener(e -> openPrescriptions());
    }

    public void refreshAppointments() {
        cachedAppointments.clear();

        // Read all appointments from FileHandler.APPOINTMENTS
        List<String[]> allRows = FileHandler.read(FileHandler.APPOINTMENTS);

        for (String[] row : allRows) {
            if (row.length >= 9) {
                String status = row[8].trim();

                // Requirement: Cancelled appointments will NOT be shown
                if ("CANCELLED".equalsIgnoreCase(status)) {
                    continue;
                }

                AppointmentRecord rec = new AppointmentRecord(row);

                // Enrich with patient details (Name & Phone)
                User patientUser = DataManager.getInstance().findById(rec.patientId);
                if (patientUser != null) {
                    rec.patientName = patientUser.getFullName();
                    rec.patientPhone = patientUser.getPhoneNumber();
                }

                cachedAppointments.add(rec);
            }
        }

        // Sort appointments: URGENT cases have highest priority (Priority 0)
        // Followed by BOOKED, RESCHEDULED, then COMPLETED. Within same priority, sort by Date and Time.
        cachedAppointments.sort((a, b) -> {
            int pA = a.getPriorityRank();
            int pB = b.getPriorityRank();
            if (pA != pB) {
                return Integer.compare(pA, pB);
            }
            int dateComp = a.date.compareToIgnoreCase(b.date);
            if (dateComp != 0) {
                return dateComp;
            }
            return a.timeSlot.compareToIgnoreCase(b.timeSlot);
        });

        applyFilters();
    }

    private void applyFilters() {
        model.setRowCount(0);

        String currentDocId = getCurrentDoctorId();
        String scope = (String) doctorScopeBox.getSelectedItem();
        boolean myDocOnly = !"All Doctor Appointments".equals(scope);

        String statusFilter = (String) statusFilterBox.getSelectedItem();
        if (statusFilter == null) statusFilter = "All Active & Completed";

        String query = searchField.getText().trim().toLowerCase();

        int urgentCount = 0;
        int bookedCount = 0;
        int rescheduledCount = 0;
        int completedCount = 0;
        int totalShown = 0;

        for (AppointmentRecord rec : cachedAppointments) {
            // Filter by Doctor Scope
            if (myDocOnly && !rec.doctorId.equalsIgnoreCase(currentDocId)) {
                continue;
            }

            // Filter by Status dropdown
            if ("URGENT Priority Only".equals(statusFilter) && !"URGENT".equalsIgnoreCase(rec.status)) {
                continue;
            }
            if ("BOOKED Only".equals(statusFilter) && !"BOOKED".equalsIgnoreCase(rec.status)) {
                continue;
            }
            if ("RESCHEDULED Only".equals(statusFilter) && !"RESCHEDULED".equalsIgnoreCase(rec.status)) {
                continue;
            }
            if ("COMPLETED Only".equals(statusFilter) && !"COMPLETED".equalsIgnoreCase(rec.status)) {
                continue;
            }

            // Filter by Text Search
            if (!query.isEmpty()) {
                boolean match = rec.apptId.toLowerCase().contains(query)
                        || rec.patientId.toLowerCase().contains(query)
                        || rec.patientName.toLowerCase().contains(query)
                        || rec.reason.toLowerCase().contains(query)
                        || rec.room.toLowerCase().contains(query)
                        || rec.date.toLowerCase().contains(query);
                if (!match) {
                    continue;
                }
            }

            // Update stats
            if ("URGENT".equalsIgnoreCase(rec.status)) urgentCount++;
            else if ("BOOKED".equalsIgnoreCase(rec.status)) bookedCount++;
            else if ("RESCHEDULED".equalsIgnoreCase(rec.status)) rescheduledCount++;
            else if ("COMPLETED".equalsIgnoreCase(rec.status)) completedCount++;
            totalShown++;

            model.addRow(new Object[]{
                    rec.getPriorityLabel(),
                    rec.apptId,
                    rec.date,
                    rec.timeSlot,
                    rec.room,
                    rec.patientId,
                    rec.patientName,
                    rec.patientPhone,
                    rec.status,
                    rec.reason
            });
        }

        statsBadgeLabel.setText(String.format("🔥 Urgent: %d  |  📅 Booked: %d  |  🔄 Rescheduled: %d  |  ✔ Completed: %d  |  Total: %d",
                urgentCount, bookedCount, rescheduledCount, completedCount, totalShown));

        updateSelectionDetails();
    }

    private void updateSelectionDetails() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String apptId = model.getValueAt(row, 1).toString();
            String pId = model.getValueAt(row, 5).toString();
            String pName = model.getValueAt(row, 6).toString();
            String time = model.getValueAt(row, 3).toString();
            String date = model.getValueAt(row, 2).toString();
            String status = model.getValueAt(row, 8).toString();

            selectedApptLabel.setText(String.format("Selected: %s (%s - %s) on %s %s [%s]",
                    apptId, pId, pName, date, time, status));

            boolean isAlreadyCompleted = "COMPLETED".equalsIgnoreCase(status);
            completeButton.setEnabled(!isAlreadyCompleted);
            rescheduleButton.setEnabled(!isAlreadyCompleted);
            historyButton.setEnabled(true);
            patientDetailsButton.setEnabled(true);
            assessmentButton.setEnabled(true);
            prescriptionButton.setEnabled(true);
        } else {
            selectedApptLabel.setText("Selected Appointment: (None selected)");
            completeButton.setEnabled(false);
            rescheduleButton.setEnabled(false);
            historyButton.setEnabled(false);
            patientDetailsButton.setEnabled(false);
            assessmentButton.setEnabled(false);
            prescriptionButton.setEnabled(false);
        }
    }

    private void markAppointmentAsCompleted() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table to mark as completed.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apptId = model.getValueAt(row, 1).toString();
        String currentStatus = model.getValueAt(row, 8).toString();
        String patientName = model.getValueAt(row, 6).toString();

        if ("COMPLETED".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This appointment is already completed.",
                    "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Mark appointment " + apptId + " for patient " + patientName + " as COMPLETED?",
                "Confirm Completion", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            List<String[]> allRows = new ArrayList<>(FileHandler.read(FileHandler.APPOINTMENTS));
            boolean updated = false;

            for (String[] r : allRows) {
                if (r.length >= 9 && r[0].equalsIgnoreCase(apptId)) {
                    r[8] = "COMPLETED";
                    updated = true;
                    break;
                }
            }

            if (updated) {
                FileHandler.writeAll(FileHandler.APPOINTMENTS, allRows);
                refreshAppointments();
                JOptionPane.showMessageDialog(this,
                        "Appointment " + apptId + " has been successfully marked as COMPLETED.",
                        "Consultation Completed", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Reschedules the selected appointment directly using AppointmentRescheduleUI.
     * Cancels the existing appointment, creates the new rescheduled appointment,
     * and refreshes the queue (cancelled appointment disappears, new appointment appears).
     */
    private void rescheduleSelectedAppointment() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table to reschedule.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String apptId = model.getValueAt(row, 1).toString();
        String currentDate = model.getValueAt(row, 2).toString();
        String room = model.getValueAt(row, 4).toString();
        String status = model.getValueAt(row, 8).toString();
        String reason = model.getValueAt(row, 9).toString();

        if ("COMPLETED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Cannot reschedule an appointment that is already COMPLETED.",
                    "Invalid Action", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Find appointment in cachedAppointments to get doctor info
        AppointmentRecord selectedRec = null;
        for (AppointmentRecord r : cachedAppointments) {
            if (r.apptId.equalsIgnoreCase(apptId)) {
                selectedRec = r;
                break;
            }
        }

        if (selectedRec == null) {
            JOptionPane.showMessageDialog(this, "Appointment record not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prompt doctor for the new reschedule date
        String inputDate = JOptionPane.showInputDialog(this,
                "Enter new appointment date (YYYY-MM-DD) for patient " + selectedRec.patientName + ":",
                currentDate);

        if (inputDate == null || inputDate.trim().isEmpty()) {
            return;
        }

        inputDate = inputDate.trim();
        LocalDate targetLocalDate;
        try {
            targetLocalDate = LocalDate.parse(inputDate);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD (e.g. 2026-10-15).",
                    "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (targetLocalDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Cannot reschedule to a past date (" + inputDate + ").",
                    "Past Date Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AppointmentRescheduleUI.openRescheduleTimeDialog(
                this,
                selectedRec.apptId,
                selectedRec.doctorId,
                selectedRec.doctorName,
                selectedRec.specialty,
                selectedRec.room,
                selectedRec.reason,
                inputDate,
                targetLocalDate,
                this::refreshAppointments
        );
    }

    /**
     * Opens the patient's "My Appointment History & Reschedule" interface (AppointmentHistoryUI)
     * pre-filtered for the selected patient.
     */
    private void viewPatientAppointmentHistory() {
        int row = table.getSelectedRow();
        String targetPatientId = null;
        if (row >= 0) {
            targetPatientId = model.getValueAt(row, 5).toString();
        }

        AppointmentHistoryUI historyWindow = new AppointmentHistoryUI(targetPatientId);
        historyWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Refresh doctor queue when the patient history window is closed in case an appointment was rescheduled
                refreshAppointments();
            }
        });
        historyWindow.setVisible(true);
    }

    private void viewPatientDetails() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        String patientId = model.getValueAt(row, 5).toString();
        String patientName = model.getValueAt(row, 6).toString();
        String contact = model.getValueAt(row, 7).toString();
        String apptId = model.getValueAt(row, 1).toString();
        String status = model.getValueAt(row, 8).toString();
        String reason = model.getValueAt(row, 9).toString();

        User u = DataManager.getInstance().findById(patientId);

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("           PATIENT CLINICAL SUMMARY RECORD          \n");
        sb.append("====================================================\n\n");
        sb.append("Patient ID       : ").append(patientId).append("\n");
        sb.append("Full Name        : ").append(patientName).append("\n");
        sb.append("Phone Number     : ").append(contact).append("\n");
        if (u != null) {
            sb.append("Email Address    : ").append(u.getEmail()).append("\n");
        }

        if (u instanceof Patient) {
            Patient p = (Patient) u;
            sb.append("Date of Birth    : ").append(p.getDateOfBirth()).append("\n");
            sb.append("Gender           : ").append(p.getGender()).append("\n");
            sb.append("Blood Group      : ").append(p.getBloodGroup()).append("\n");
            sb.append("Emergency Contact: ").append(p.getEmergencyContact())
                    .append(" (").append(p.getEmergencyRelationship()).append(")\n");
            sb.append("\n[Known Medical History / Allergies]\n");
            sb.append(p.getMedicalHistorySummary() != null && !p.getMedicalHistorySummary().trim().isEmpty()
                    ? p.getMedicalHistorySummary()
                    : "No known previous medical history or allergies recorded.");
            sb.append("\n");
        } else {
            sb.append("\nAdditional patient demographic details not found in patients database.\n");
        }

        sb.append("\n----------------------------------------------------\n");
        sb.append("Current Appointment : ").append(apptId).append(" [").append(status).append("]\n");
        sb.append("Reason / Symptoms   : ").append(reason).append("\n");
        sb.append("----------------------------------------------------\n");

        JTextArea area = new JTextArea(sb.toString(), 18, 48);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setCaretPosition(0);

        JScrollPane sp = new JScrollPane(area);
        JOptionPane.showMessageDialog(this, sp, "Patient Details - " + patientName + " (" + patientId + ")",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void openAssessmentNotes() {
        new AssessmentResultUI().setVisible(true);
    }

    private void openPrescriptions() {
        new PrescriptionUI().setVisible(true);
    }
}
