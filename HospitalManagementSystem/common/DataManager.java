package common;

import administrative_staff.AdministrativeStaff;
import common.model.User;
import doctors.Doctor;
import medical_manager.MedicalManager;
import patients.Patient;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataManager {
    private static volatile DataManager instance;

    public static final String USERS_FILE = "users.txt";
    public static final String PATIENTS_FILE = "patients.txt";
    public static final String DOCTORS_FILE = "doctors.txt";
    public static final String MANAGERS_FILE = "managers.txt";
    public static final String ADMINS_FILE = "admins.txt";

    private final List<User> users = new CopyOnWriteArrayList<>();

    private static Path getDataDir() {
        return FileHandler.getDataDir();
    }

    private DataManager() {
        ensureDataFiles();
        loadUsers();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    public synchronized void ensureDataFiles() {
        try {
            Path dataDir = getDataDir();
            Files.createDirectories(dataDir);
            String[] files = {USERS_FILE, PATIENTS_FILE, DOCTORS_FILE, MANAGERS_FILE, ADMINS_FILE};
            for (String file : files) {
                Path p = dataDir.resolve(file);
                if (!Files.exists(p)) {
                    Files.createFile(p);
                }
            }
            seedDefaultsIfEmpty();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage files: " + e.getMessage(), e);
        }
    }

    private void seedDefaultsIfEmpty() {
        try {
            Path usersPath = getDataDir().resolve(USERS_FILE);
            if (Files.size(usersPath) == 0) {
                AdministrativeStaff admin = new AdministrativeStaff("A001", "admin@gmail.com", "admin123", "System Administrator", "+60120000001", "SUPER_ADMIN");
                MedicalManager manager = new MedicalManager("M001", "manager@gmail.com", "manager123", "Medical Manager", "+60120000002", "Operations", "OF-302");
                Doctor doctor = new Doctor("D001", "doctor@gmail.com", "doctor123", "Dr. Aisha", "+60120000003", "Cardiology", "MBBS, MD", "CR-105");
                Patient patient = new Patient("P001", "patient@gmail.com", "patient123", "Demo Patient", "+60120000004", "1995-06-15", "Male", "O+", "+60198888888", "No known allergies");

                users.addAll(Arrays.asList(admin, manager, doctor, patient));
                saveUsers();
            }
        } catch (IOException e) {
            System.err.println("Warning: Unable to seed default data: " + e.getMessage());
        }
    }

    public synchronized void loadUsers() {
        users.clear();
        Map<String, String[]> parentMap = new LinkedHashMap<>();

        // 1. Read Parent Users (ROLE|ID|EMAIL|PASSWORD|FULL_NAME|PHONE)
        for (String[] row : readRows(USERS_FILE)) {
            if (row.length >= 6) {
                parentMap.put(row[1], row); // key by ID
            }
        }

        // 2. Read and join Patients (ID|dateOfBirth|gender|bloodGroup|emergencyContact|medicalHistorySummary)
        for (String[] row : readRows(PATIENTS_FILE)) {
            if (row.length >= 6 && parentMap.containsKey(row[0])) {
                String[] p = parentMap.get(row[0]);
                users.add(new Patient(p[1], p[2], p[3], p[4], p[5], row[1], row[2], row[3], row[4], row[5]));
                parentMap.remove(row[0]);
            }
        }

        // 3. Read and join Doctors (ID|specialty|qualification|roomNumber)
        for (String[] row : readRows(DOCTORS_FILE)) {
            if (row.length >= 4 && parentMap.containsKey(row[0])) {
                String[] p = parentMap.get(row[0]);
                users.add(new Doctor(p[1], p[2], p[3], p[4], p[5], row[1], row[2], row[3]));
                parentMap.remove(row[0]);
            }
        }

        // 4. Read and join Managers (ID|department|officeNumber)
        for (String[] row : readRows(MANAGERS_FILE)) {
            if (row.length >= 3 && parentMap.containsKey(row[0])) {
                String[] p = parentMap.get(row[0]);
                users.add(new MedicalManager(p[1], p[2], p[3], p[4], p[5], row[1], row[2]));
                parentMap.remove(row[0]);
            }
        }

        // 5. Read and join Admins (ID|accessLevel)
        for (String[] row : readRows(ADMINS_FILE)) {
            if (row.length >= 2 && parentMap.containsKey(row[0])) {
                String[] p = parentMap.get(row[0]);
                users.add(new AdministrativeStaff(p[1], p[2], p[3], p[4], p[5], row[1]));
                parentMap.remove(row[0]);
            }
        }

        // Fallback for any unmapped parent records
        for (String[] p : parentMap.values()) {
            String roleStr = p[0];
            if ("ADMINISTRATIVE_STAFF".equalsIgnoreCase(roleStr)) {
                users.add(new AdministrativeStaff(p[1], p[2], p[3], p[4], p[5], "ADMIN"));
            } else if ("MEDICAL_MANAGER".equalsIgnoreCase(roleStr)) {
                users.add(new MedicalManager(p[1], p[2], p[3], p[4], p[5], "General", "Main"));
            } else if ("DOCTOR".equalsIgnoreCase(roleStr)) {
                users.add(new Doctor(p[1], p[2], p[3], p[4], p[5], "General", "MBBS", "101"));
            } else {
                users.add(new Patient(p[1], p[2], p[3], p[4], p[5], "2000-01-01", "Other", "O+", "-", "None"));
            }
        }
    }

    public synchronized void saveUsers() {
        List<String> baseLines = new ArrayList<>();
        List<String> patientLines = new ArrayList<>();
        List<String> doctorLines = new ArrayList<>();
        List<String> managerLines = new ArrayList<>();
        List<String> adminLines = new ArrayList<>();

        for (User u : users) {
            baseLines.add(u.toBaseFileString());
            if (u instanceof Patient) {
                patientLines.add(u.toChildFileString());
            } else if (u instanceof Doctor) {
                doctorLines.add(u.toChildFileString());
            } else if (u instanceof MedicalManager) {
                managerLines.add(u.toChildFileString());
            } else if (u instanceof AdministrativeStaff) {
                adminLines.add(u.toChildFileString());
            }
        }

        writeLines(USERS_FILE, baseLines);
        writeLines(PATIENTS_FILE, patientLines);
        writeLines(DOCTORS_FILE, doctorLines);
        writeLines(MANAGERS_FILE, managerLines);
        writeLines(ADMINS_FILE, adminLines);
    }

    public synchronized void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public synchronized void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equalsIgnoreCase(user.getId())) {
                users.set(i, user);
                break;
            }
        }
        saveUsers();
    }

    public synchronized void deleteUser(String userId) {
        users.removeIf(u -> u.getId().equalsIgnoreCase(userId));
        saveUsers();
    }

    public User findByEmail(String email) {
        if (email == null) return null;
        String cleanEmail = email.trim();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(cleanEmail)) {
                return user;
            }
        }
        return null;
    }

    public User findById(String id) {
        if (id == null) return null;
        String cleanId = id.trim();
        for (User user : users) {
            if (user.getId().equalsIgnoreCase(cleanId)) {
                return user;
            }
        }
        return null;
    }

    public boolean isEmailTaken(String email) {
        return findByEmail(email) != null;
    }

    public synchronized String generateNextPatientId() {
        int max = 0;
        for (User u : users) {
            if (u.getId().startsWith("P")) {
                try {
                    int num = Integer.parseInt(u.getId().substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("P%03d", max + 1);
    }

    public synchronized String generateNextId(String prefix, int width) {
        int max = 0;
        for (User u : users) {
            if (u.getId().startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(u.getId().substring(prefix.length()));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return prefix + String.format("%0" + width + "d", max + 1);
    }

    public List<User> getAllUsers() {
        return Collections.unmodifiableList(users);
    }

    private List<String[]> readRows(String filename) {
        List<String[]> list = new ArrayList<>();
        Path path = getDataDir().resolve(filename);
        if (!Files.exists(path)) return list;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    list.add(line.split("\\|", -1));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading " + filename + ": " + e.getMessage(), e);
        }
        return list;
    }

    private void writeLines(String filename, List<String> lines) {
        Path path = getDataDir().resolve(filename);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing " + filename + ": " + e.getMessage(), e);
        }
    }
}
