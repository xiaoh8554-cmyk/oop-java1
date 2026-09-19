package patients.ui;

import common.FileHandler;
import common.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRescheduleUI {
    private static final String[] HALF_HOUR_SLOTS = {
            "10:00 - 10:30", "10:30 - 11:00", "11:00 - 11:30", "11:30 - 12:00",
            "12:00 - 12:30", "12:30 - 13:00", "13:00 - 13:30", "13:30 - 14:00",
            "14:00 - 14:30", "14:30 - 15:00", "15:00 - 15:30", "15:30 - 16:00",
            "16:00 - 16:30", "16:30 - 17:00", "17:00 - 17:30", "17:30 - 18:00"
    };
    private static final int MAX_PATIENTS_PER_SLOT = 2;

    public static void openRescheduleTimeDialog(Component parent, String oldApptId, String doctorId, String doctorName,
                                                String specialty, String room, String originalReason,
                                                String newDateStr, LocalDate newLocalDate, Runnable onRescheduled) {
        Window parentWindow = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog;
        if (parentWindow instanceof Frame) {
            dialog = new JDialog((Frame) parentWindow, "Reschedule Appointment - Select New Time Slot", true);
        } else {
            dialog = new JDialog();
            dialog.setTitle("Reschedule Appointment - Select New Time Slot");
            dialog.setModal(true);
        }

        dialog.setSize(750, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout(12, 12));

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 6, 15));
        JLabel titleLabel = new JLabel("Reschedule Consultation Time (10:00 AM - 6:00 PM)", SwingConstants.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel oldApptInfo = new JLabel("Rescheduling Appointment ID: " + oldApptId + " (previous appointment will be cancelled)");
        oldApptInfo.setFont(new Font("SansSerif", Font.BOLD, 12));
        oldApptInfo.setForeground(new Color(180, 50, 0));
        JLabel infoLabel = new JLabel("Doctor: Dr. " + doctorName + " (" + doctorId + ")  |  Specialty: " + specialty + "  |  Room: " + room + "  |  New Date: " + newDateStr);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(40, 70, 140));
        headerPanel.add(titleLabel);
        headerPanel.add(oldApptInfo);
        headerPanel.add(infoLabel);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Center: Time Slots Table with remaining patient slots for the new date
        DefaultTableModel slotModel = new DefaultTableModel(
                new String[]{"No.", "Time Slot (Half Hour)", "Remaining Patient Capacity", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable slotTable = new JTable(slotModel);
        slotTable.setRowHeight(24);

        for (int i = 0; i < HALF_HOUR_SLOTS.length; i++) {
            String slot = HALF_HOUR_SLOTS[i];
            boolean passed = isSlotPassed(newLocalDate, slot);

            if (passed) {
                slotModel.addRow(new Object[]{i + 1, slot, "0 / " + MAX_PATIENTS_PER_SLOT + " (PASSED)", "PASSED"});
            } else {
                int booked = getBookedCountForSlot(doctorId, newDateStr, slot);
                int remaining = Math.max(0, MAX_PATIENTS_PER_SLOT - booked);

                String remainStr = remaining + " / " + MAX_PATIENTS_PER_SLOT + " slots left";
                String statusStr;
                if (remaining == MAX_PATIENTS_PER_SLOT) {
                    statusStr = "AVAILABLE (2 Open)";
                } else if (remaining > 0) {
                    statusStr = "AVAILABLE (1 Open)";
                } else {
                    remainStr = "0 / " + MAX_PATIENTS_PER_SLOT + " (FULL)";
                    statusStr = "FULL / BOOKED";
                }
                slotModel.addRow(new Object[]{i + 1, slot, remainStr, statusStr});
            }
        }

        // Custom renderer for Status and Remaining Slots colors
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);
                if (isSelected) {
                    return comp;
                }
                Object statusObj = table.getValueAt(r, 3);
                String status = (statusObj != null) ? statusObj.toString() : "";

                if (status.startsWith("AVAILABLE")) {
                    comp.setForeground(new Color(0, 130, 40));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (status.contains("FULL") || status.contains("BOOKED")) {
                    comp.setForeground(new Color(210, 0, 0));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (status.contains("PASSED")) {
                    comp.setForeground(Color.GRAY);
                    setFont(getFont().deriveFont(Font.ITALIC));
                } else {
                    comp.setForeground(Color.BLACK);
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
                return comp;
            }
        };

        slotTable.getColumnModel().getColumn(2).setCellRenderer(statusRenderer);
        slotTable.getColumnModel().getColumn(3).setCellRenderer(statusRenderer);

        dialog.add(new JScrollPane(slotTable), BorderLayout.CENTER);

        // South Panel: Emergency Button, Reason Field, and Actions
        JPanel southPanel = new JPanel(new BorderLayout(8, 8));
        southPanel.setBorder(BorderFactory.createEmptyBorder(6, 15, 12, 15));

        // Emergency Status Banner
        JLabel emergencyBanner = new JLabel("Normal Reschedule Mode (Select an open 30-min slot on the new date)", SwingConstants.CENTER);
        emergencyBanner.setFont(new Font("SansSerif", Font.BOLD, 13));
        emergencyBanner.setOpaque(true);
        emergencyBanner.setBackground(new Color(235, 242, 250));
        emergencyBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230)),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        formPanel.add(new JLabel("Reason / Symptoms:"), g);
        g.gridx = 1; g.weightx = 1.0;
        JTextField reasonInput = new JTextField(originalReason);
        formPanel.add(reasonInput, g);

        // Button bar
        JPanel actionButtonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton emergencyButton = new JButton("\u26A0 Emergency (Urgent Booking)");
        emergencyButton.setBackground(new Color(255, 220, 220));
        emergencyButton.setForeground(new Color(180, 0, 0));
        emergencyButton.setFont(new Font("SansSerif", Font.BOLD, 12));

        JButton confirmButton = new JButton("Confirm Reschedule");
        confirmButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        JButton cancelButton = new JButton("Cancel");

        actionButtonBar.add(emergencyButton);
        actionButtonBar.add(confirmButton);
        actionButtonBar.add(cancelButton);

        // Emergency toggle state
        final boolean[] isEmergency = {false};

        emergencyButton.addActionListener(ev -> {
            isEmergency[0] = !isEmergency[0];
            if (isEmergency[0]) {
                emergencyBanner.setText("\u26A0 EMERGENCY MODE ACTIVE: Time Slot set to URGENT (Immediate Priority Queue)");
                emergencyBanner.setBackground(new Color(255, 210, 210));
                emergencyBanner.setForeground(new Color(180, 0, 0));
                emergencyButton.setText("Switch back to Normal Slot");
                emergencyButton.setBackground(new Color(220, 235, 255));
                emergencyButton.setForeground(Color.BLUE);
                if (reasonInput.getText().trim().isEmpty() || reasonInput.getText().equals(originalReason)) {
                    reasonInput.setText("[EMERGENCY] " + originalReason);
                }
            } else {
                emergencyBanner.setText("Normal Reschedule Mode (Select an open 30-min slot on the new date)");
                emergencyBanner.setBackground(new Color(235, 242, 250));
                emergencyBanner.setForeground(Color.BLACK);
                emergencyButton.setText("\u26A0 Emergency (Urgent Booking)");
                emergencyButton.setBackground(new Color(255, 220, 220));
                emergencyButton.setForeground(new Color(180, 0, 0));
            }
        });

        confirmButton.addActionListener(ev -> {
            String selectedSlot;
            String status;

            if (isEmergency[0]) {
                selectedSlot = "URGENT (Immediate Queue)";
                status = "URGENT";
            } else {
                int slotRow = slotTable.getSelectedRow();
                if (slotRow < 0) {
                    JOptionPane.showMessageDialog(dialog, "Please select an available time slot from the table, or click 'Emergency'.", "No Time Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String slotStatus = slotModel.getValueAt(slotRow, 3).toString();

                if ("PASSED".equalsIgnoreCase(slotStatus)) {
                    JOptionPane.showMessageDialog(dialog, "Cannot reschedule to a time slot that has already passed.\nPlease choose an upcoming time slot.", "Time Slot Passed", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (slotStatus.contains("FULL") || slotStatus.contains("BOOKED")) {
                    JOptionPane.showMessageDialog(dialog, "This time slot is full (maximum 2 patients already booked).\nPlease choose another time slot with remaining capacity.", "Time Slot Full", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                selectedSlot = slotModel.getValueAt(slotRow, 1).toString();
                status = "RESCHEDULED";
            }

            String reason = reasonInput.getText().trim();
            if (reason.isEmpty()) {
                reason = originalReason.isEmpty() ? "General Medical Consultation" : originalReason;
            }

            String patientId = Session.getCurrentUser() != null ? Session.getCurrentUser().getUserId() : "P001";

            // 1. Cancel the OLD appointment in appointments.txt
            List<String[]> allAppts = new ArrayList<>(FileHandler.read(FileHandler.APPOINTMENTS));
            for (String[] r : allAppts) {
                if (r.length >= 10 && r[0].equals(oldApptId)) {
                    r[8] = "CANCELLED";
                    break;
                }
            }
            FileHandler.writeAll(FileHandler.APPOINTMENTS, allAppts);

            // 2. Create the NEW appointment with new ID (YYYYMMDD0001 format)
            String newApptId = FileHandler.nextAppointmentId(newDateStr);
            FileHandler.append(FileHandler.APPOINTMENTS, new String[]{
                    newApptId, patientId, doctorId, doctorName, specialty, newDateStr, selectedSlot, room, status, reason
            });

            dialog.dispose();

            if (onRescheduled != null) {
                onRescheduled.run();
            }

            String msg = "Appointment Rescheduled Successfully!\n\n" +
                    "Previous Appointment ID: " + oldApptId + " (Status: CANCELLED)\n" +
                    "New Appointment ID: " + newApptId + "\n" +
                    "Doctor: Dr. " + doctorName + " (" + specialty + ")\n" +
                    "Consultation Room: " + room + "\n" +
                    "New Date: " + newDateStr + "\n" +
                    "New Time: " + selectedSlot + "\n" +
                    "Status: " + status;
            JOptionPane.showMessageDialog(parent, msg, "Reschedule Confirmed", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelButton.addActionListener(ev -> dialog.dispose());

        JPanel centerSouth = new JPanel(new BorderLayout(6, 6));
        centerSouth.add(emergencyBanner, BorderLayout.NORTH);
        centerSouth.add(formPanel, BorderLayout.CENTER);

        southPanel.add(centerSouth, BorderLayout.CENTER);
        southPanel.add(actionButtonBar, BorderLayout.SOUTH);

        dialog.add(southPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static boolean isSlotPassed(LocalDate selectedDate, String timeSlot) {
        LocalDate today = LocalDate.now();
        if (selectedDate.isBefore(today)) {
            return true;
        }
        if (selectedDate.isEqual(today)) {
            try {
                String startTimeStr = timeSlot.split("-")[0].trim();
                LocalTime slotStart = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
                return LocalTime.now().isAfter(slotStart);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private static int getBookedCountForSlot(String doctorId, String date, String timeSlot) {
        int count = 0;
        for (String[] appt : FileHandler.read(FileHandler.APPOINTMENTS)) {
            if (appt.length >= 9) {
                String doc = appt[2].trim();
                String apptDate = appt[5].trim();
                String apptSlot = appt[6].trim();
                String status = appt[8].trim();

                if (doc.equals(doctorId) && apptDate.equals(date) && apptSlot.equalsIgnoreCase(timeSlot)) {
                    if (!"CANCELLED".equalsIgnoreCase(status)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
