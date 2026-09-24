package medical_manager.ux;

import common.FileHandler;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ShiftRosterUX {
    public static Vector<String> loadDataFromFile(String fileName, String[] fallback) {
        return loadDataFromFile(fileName, fallback, 1);
    }

    public static Vector<String> loadDataFromFile(String fileName, String[] fallback, int valueIndex) {
        Vector<String> values = new Vector<>();
        for (String[] row : FileHandler.read(fileName)) {
            if (row.length > valueIndex) {
                values.add(row[valueIndex]);
            }
        }
        if (values.isEmpty()) {
            for (String value : fallback) {
                values.add(value);
            }
        }
        return values;
    }

    public static List<String> getDoctorIdsForManager(String managerId) {
        List<String> list = new ArrayList<>();
        if (managerId == null || managerId.trim().isEmpty()) {
            return list;
        }
        for (String[] d : FileHandler.read("doctors.txt")) {
            if (d.length >= 4 && d[3].equalsIgnoreCase(managerId.trim())) {
                list.add(d[0].trim());
            }
        }
        return list;
    }

    public static Vector<String> loadDoctorOptions(String[] fallback) {
        return loadDoctorOptions(fallback, null);
    }

    public static Vector<String> loadDoctorOptions(String[] fallback, String managerId) {
        Vector<String> options = new Vector<>();
        for (String[] doctor : FileHandler.read("doctors.txt")) {
            if (doctor.length == 0) {
                continue;
            }
            String doctorId = doctor[0];
            if (managerId != null && !managerId.trim().isEmpty()) {
                if (doctor.length < 4 || !doctor[3].equalsIgnoreCase(managerId.trim())) {
                    continue;
                }
            }
            String doctorName = doctorId;
            for (String[] user : FileHandler.read(FileHandler.USERS)) {
                if (user.length > 4 && "DOCTOR".equals(user[0]) && doctorId.equals(user[1])) {
                    doctorName = user[4];
                    break;
                }
            }
            options.add(doctorId + " - " + doctorName);
        }
        if (options.isEmpty()) {
            for (String value : fallback) {
                options.add(value);
            }
        }
        return options;
    }

    public static String doctorIdFromOption(String option) {
        int separatorIndex = option.indexOf(" - ");
        return separatorIndex >= 0 ? option.substring(0, separatorIndex) : option;
    }

    public static String timeRangeFromOption(String option) {
        if (option.startsWith("On-Call Duty")) {
            return "00:00 - 24:00";
        }
        int labelStart = option.indexOf(" (");
        return labelStart >= 0 ? option.substring(0, labelStart) : option;
    }

    public static List<Vector<String>> loadRosterTableData() {
        return loadRosterTableData("", "", "");
    }

    public static List<Vector<String>> loadRosterTableData(String doctorId,
                                                            String department,
                                                            String date) {
        return loadRosterTableData(doctorId, department, date, "");
    }

    public static List<Vector<String>> loadRosterTableData(String doctorId,
                                                            String department,
                                                            String date,
                                                            String managerId) {
        List<String> allowedDoctors = getDoctorIdsForManager(managerId);
        List<Vector<String>> tableRows = new ArrayList<>();
        for (String[] parts : getRosterRecords()) {
            if (parts.length < 8) {
                continue;
            }
            if (!allowedDoctors.isEmpty() && !allowedDoctors.contains(parts[1])) {
                continue;
            }
            if (!doctorId.isEmpty() && !doctorId.equals(parts[1])) {
                continue;
            }
            if (!department.isEmpty() && !department.equals(parts[3])) {
                continue;
            }
            if (!date.isEmpty() && !date.equals(parts[4])) {
                continue;
            }
            Vector<String> rowData = new Vector<>();
            rowData.add(parts[3]);
            rowData.add(parts[2]);
            rowData.add(parts[4]);
            rowData.add(parts[5]);
            rowData.add(parts[6]);
            rowData.add(parts[7]);
            tableRows.add(rowData);
        }
        return tableRows;
    }

    public static String[] getRosterRecord(String rosterId) {
        for (String[] record : getRosterRecords()) {
            if (record[0].equals(rosterId)) {
                return record;
            }
        }
        return null;
    }

    public static String[] getRosterRecordAtFilter(int tableRow, String doctorId,
                                                   String department, String date) {
        return getRosterRecordAtFilter(tableRow, doctorId, department, date, "");
    }

    public static String[] getRosterRecordAtFilter(int tableRow, String doctorId,
                                                   String department, String date, String managerId) {
        List<String> allowedDoctors = getDoctorIdsForManager(managerId);
        List<String[]> filteredRecords = new ArrayList<>();
        for (String[] record : getRosterRecords()) {
            if (!allowedDoctors.isEmpty() && !allowedDoctors.contains(record[1])) {
                continue;
            }
            if ((!doctorId.isEmpty() && !doctorId.equals(record[1]))
                    || (!department.isEmpty() && !department.equals(record[3]))
                    || (!date.isEmpty() && !date.equals(record[4]))) {
                continue;
            }
            filteredRecords.add(record);
        }
        if (tableRow < 0 || tableRow >= filteredRecords.size()) {
            return null;
        }
        return filteredRecords.get(tableRow);
    }

    public static void updateRoster(String rosterId, String department, String doctor,
                                    String date, String time, String location, String status) {
        validateDate(date);
        validateTime(time);
        validateDoctorSchedule(rosterId, doctor, date, time);
        String[] record = getRosterRecord(rosterId);
        if (record == null) {
            throw new IllegalArgumentException("Select a roster to update.");
        }
        record[1] = doctor;
        record[2] = findDoctorName(doctor);
        record[3] = department;
        record[4] = date;
        record[5] = time;
        record[6] = location;
        record[7] = status;
        writeRosterRecords(record);
    }

    public static void deleteRoster(String rosterId) {
        List<String[]> records = getRosterRecords();
        boolean removed = records.removeIf(record -> record[0].equals(rosterId));
        if (!removed) {
            throw new IllegalArgumentException("Select a roster to delete.");
        }
        FileHandler.writeAll(FileHandler.ROSTERS, records);
    }

    private static void writeRosterRecords(String[] updatedRecord) {
        List<String[]> records = getRosterRecords();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i)[0].equals(updatedRecord[0])) {
                records.set(i, updatedRecord);
                break;
            }
        }
        FileHandler.writeAll(FileHandler.ROSTERS, records);
    }

    public static void addRoster(String department, String doctor, String date,
                                 String time, String location, String status) {
        validateDate(date);
        validateTime(time);
        validateDoctorSchedule("", doctor, date, time);

        String doctorName = findDoctorName(doctor);
        String id = FileHandler.nextId(FileHandler.ROSTERS, "ROS", 3);
        FileHandler.append(FileHandler.ROSTERS,
                new String[]{id, doctor, doctorName, department, date, time, location, status});
    }

    private static List<String[]> getRosterRecords() {
        List<String[]> records = new ArrayList<>();
        for (String[] record : FileHandler.read(FileHandler.ROSTERS)) {
            if (record.length >= 8) {
                records.add(record);
            }
        }
        return records;
    }

    private static void validateDate(String date) {
        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must use YYYY-MM-DD format.");
        }
    }

    private static void validateTime(String time) {
        parseTimeRange(time);
    }

    private static void validateDoctorSchedule(String rosterId, String doctor,
                                               String date, String time) {
        int[] requestedRange = parseTimeRange(time);
        for (String[] record : getRosterRecords()) {
            if (record[0].equals(rosterId)
                    || !record[1].equals(doctor)
                    || !record[4].equals(date)) {
                continue;
            }
                int[] existingRange = parseTimeRange(record[5]);
                boolean overlaps = requestedRange[0] < existingRange[1]
                    && existingRange[0] < requestedRange[1];
            if (overlaps) {
                throw new IllegalArgumentException(
                        "This doctor already has an overlapping shift on " + date + ".");
            }
        }
    }

    private static int[] parseTimeRange(String time) {
        String[] parts = time.trim().split("\\s*-\\s*");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Time must use HH:mm-HH:mm format.");
        }
        try {
            int start = toMinutes(parts[0]);
            int end = toMinutes(parts[1]);
            if (end == 0 && start > 0) {
                end = 24 * 60;
            }
            if (start >= end) {
                throw new IllegalArgumentException("Time slot end must be after its start.");
            }
            return new int[]{start, end};
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Time must use HH:mm-HH:mm format.");
        }
    }

    private static int toMinutes(String value) {
        if ("24:00".equals(value)) {
            return 24 * 60;
        }
        LocalTime parsed = LocalTime.parse(value);
        return parsed.getHour() * 60 + parsed.getMinute();
    }

    private static String findDoctorName(String doctor) {
        String doctorName = doctor;
        for (String[] user : FileHandler.read(FileHandler.USERS)) {
            if (user.length > 4 && "DOCTOR".equals(user[0]) && doctor.equals(user[1])) {
                doctorName = user[4];
                break;
            }
        }

        return doctorName;
    }
}