package common;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class FileHandler {
    public static final String USERS = "users.txt";
    public static final String WARDS = "wards.txt";
    public static final String DEPARTMENTS = "departments.txt";
    public static final String ASSESSMENT_TYPES = "assessment_types.txt";
    public static final String ASSESSMENTS = "assessments.txt";
    public static final String BILLING = "billing.txt";
    public static final String PRESCRIPTIONS = "prescriptions.txt";
    public static final String FEEDBACK = "feedback.txt";
    public static final String APPOINTMENTS = "appointments.txt";
    public static final String ASSETS = "assets.txt";
    public static final String INSURANCE = "insurance.txt";
    public static final String RATES = "rates.txt";
    public static final String ROSTERS = "rosters.txt";
    public static final String REQUESTS = "requests.txt";
    public static final String DOCTOR_DEPARTMENTS = "doctor_departments.txt";

    public static Path getDataDir() {
        Path sub = Paths.get("HospitalManagementSystem", "data");
        if (Files.exists(sub)) {
            return sub;
        }
        return Paths.get("data");
    }

    public static void ensureDataFiles() {
        try {
            Path dataDir = getDataDir();
            Files.createDirectories(dataDir);
            String[] names = {USERS, WARDS, DEPARTMENTS, ASSESSMENT_TYPES, ASSESSMENTS, BILLING, PRESCRIPTIONS, FEEDBACK, APPOINTMENTS, ASSETS, INSURANCE, RATES, ROSTERS, REQUESTS, DOCTOR_DEPARTMENTS};
            for (String name : names) {
                Path p = dataDir.resolve(name);
                if (!Files.exists(p)) Files.createFile(p);
            }
            DataManager.getInstance().ensureDataFiles();
            seedIfEmpty();
        } catch (IOException e) {
            throw new RuntimeException("Unable to initialize data files: " + e.getMessage(), e);
        }
    }

    private static void seedIfEmpty() throws IOException {
        Path dataDir = getDataDir();
        if (Files.size(dataDir.resolve(WARDS)) == 0) {
            append(WARDS, new String[]{"W001","General Clinic","CLINIC","Ground Floor","30"});
        }
        if (Files.size(dataDir.resolve(DEPARTMENTS)) == 0) {
            append(DEPARTMENTS, new String[]{"DEP001","General Medicine","Primary Care","Dr. Aisha"});
        }
        if (Files.size(dataDir.resolve(ASSESSMENT_TYPES)) == 0) {
            append(ASSESSMENT_TYPES, new String[]{"AT001","General Check-up","Routine vital signs and consultation","50.00"});
            append(ASSESSMENT_TYPES, new String[]{"AT002","Blood Test","Basic laboratory blood screening","80.00"});
        }
        if (Files.size(dataDir.resolve(ASSETS)) == 0) {
            append(ASSETS, new String[]{"AST001","Consultation Room 101","CONSULTATION_ROOM","Level 1, Block A","1","ALLOCATED","D001"});
            append(ASSETS, new String[]{"AST002","Consultation Room 102","CONSULTATION_ROOM","Level 1, Block A","1","AVAILABLE","None"});
            append(ASSETS, new String[]{"AST003","General Inpatient Ward 1","INPATIENT_WARD","Level 2, Block B","20","AVAILABLE","None"});
            append(ASSETS, new String[]{"AST004","Central Pathology Lab","LAB","Level 1, Block C","10","AVAILABLE","None"});
            append(ASSETS, new String[]{"AST005","Digital X-Ray Suite","IMAGING_ROOM","Ground Floor, Radiography","5","AVAILABLE","None"});
            append(ASSETS, new String[]{"AST006","MRI & CT Imaging Room","IMAGING_ROOM","Ground Floor, Radiography","2","AVAILABLE","None"});
        }
        if (Files.size(dataDir.resolve(RATES)) == 0) {
            append(RATES, new String[]{"R001","General Practice","50.00","80.00"});
            append(RATES, new String[]{"R002","Cardiology","120.00","180.00"});
            append(RATES, new String[]{"R003","Dermatology","90.00","140.00"});
            append(RATES, new String[]{"R004","Pediatrics","70.00","100.00"});
            append(RATES, new String[]{"R005","Orthopedics","110.00","160.00"});
        }
        if (Files.size(dataDir.resolve(INSURANCE)) == 0) {
            append(INSURANCE, new String[]{"INS001","Great Eastern Medical","Comprehensive Gold","80","1-300-88-1234"});
            append(INSURANCE, new String[]{"INS002","AIA Health Shield","Platinum Care Plus","90","1-300-88-5678"});
            append(INSURANCE, new String[]{"INS003","Prudential Assurance","PruValue Med","85","1-300-88-9012"});
            append(INSURANCE, new String[]{"INS004","Allianz General","Care Complete","75","1-300-88-3456"});
        }
        if (Files.size(dataDir.resolve(REQUESTS)) == 0) {
            append(REQUESTS, new String[]{"REQ001","D001","P001","LAB_TEST","Comprehensive Blood Panel (Full Blood Count & Lipid Profile)","PENDING","2026-09-20"});
            append(REQUESTS, new String[]{"REQ002","D001","P002","X_RAY","Chest X-Ray (PA View) for persistent cough","APPROVED","2026-09-18"});
        }
    }

    public static synchronized List<String[]> read(String fileName) {
        ensureDirectoryOnly();
        List<String[]> rows = new ArrayList<>();
        Path path = getDataDir().resolve(fileName);
        if (!Files.exists(path)) return rows;
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) rows.add(line.split("\\|", -1));
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read " + fileName + ": " + e.getMessage(), e);
        }
        return rows;
    }

    public static synchronized void append(String fileName, String[] values) {
        ensureDirectoryOnly();
        Path path = getDataDir().resolve(fileName);
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write(join(values));
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Unable to write " + fileName + ": " + e.getMessage(), e);
        }
    }

    public static synchronized void writeAll(String fileName, List<String[]> rows) {
        ensureDirectoryOnly();
        Path path = getDataDir().resolve(fileName);
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String[] row : rows) {
                bw.write(join(row));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to rewrite " + fileName + ": " + e.getMessage(), e);
        }
    }

    public static String nextId(String fileName, String prefix, int width) {
        int max = 0;
        for (String[] row : read(fileName)) {
            if (row.length == 0) continue;
            String id = row[0];
            if (id.startsWith(prefix)) {
                try { max = Math.max(max, Integer.parseInt(id.substring(prefix.length()))); }
                catch (NumberFormatException ignored) {}
            }
        }
        return prefix + String.format("%0" + width + "d", max + 1);
    }

    public static synchronized String nextAppointmentId(String dateStr) {
        String datePrefix;
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            datePrefix = dateStr.replace("-", "").replace("/", "").trim();
        } else {
            datePrefix = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        int maxCase = 0;
        for (String[] row : read(APPOINTMENTS)) {
            if (row.length > 0 && row[0] != null) {
                String id = row[0].trim();
                if (id.startsWith(datePrefix)) {
                    try {
                        String suffix = id.substring(datePrefix.length());
                        int num = Integer.parseInt(suffix);
                        if (num > maxCase) {
                            maxCase = num;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return datePrefix + String.format("%04d", maxCase + 1);
    }

    public static String clean(String value) {
        if (value == null) return "";
        return value.replace('|', '/').replace('\n', ' ').replace('\r', ' ').trim();
    }

    private static String join(String[] values) {
        String[] cleaned = new String[values.length];
        for (int i = 0; i < values.length; i++) cleaned[i] = clean(values[i]);
        return String.join("|", cleaned);
    }

    private static void ensureDirectoryOnly() {
        try { Files.createDirectories(getDataDir()); }
        catch (IOException e) { throw new RuntimeException(e); }
    }
}
