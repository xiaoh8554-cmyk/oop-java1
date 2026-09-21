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
            String[] files = {USERS_FILE, PATIENTS_FILE, DOCTORS_FILE, MANAGERS_FILE};
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
                AdministrativeStaff admin = new AdministrativeStaff("A001", "Defaultadmin@gmail.com", "admin123", "Default Admin", "+60120000001");
                MedicalManager manager = new MedicalManager("M001", "manager@gmail.com", "manager123", "Medical Manager", "+60120000002", "OF-302");
                Doctor doctor = new Doctor("D001", "doctor@gmail.com", "doctor123", "Aisha", "+60120000003", "Cardiology", "MBBS, MD", "M001");
                Patient patient = new Patient("P001", "patient@gmail.com", "patient123", "Demo Patient", "+60120000004", "1995-06-15", "Male", "O+", "+60198888888", "parents", "No known allergies");

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

        // 2. Read and join Patients (ID|dateOfBirth|gender|bloodGroup|emergencyContact|emergencyRelationship|medicalHistorySummary)
        for (String[] row : readRows(PATIENTS_FILE)) {
            if (row.length >= 6 && parentMap.containsKey(row[0])) {
                String[] p = parentMap.get(row[0]);
                if (row.length >= 7) {
                    users.add(new Patient(p[1], p[2], p[3], p[4], p[5], row[1], row[2], row[3], row[4], row[5], row[6]));
                } else {
                    // Backward compatible with 6-part patient record
                    users.add(new Patient(p[1], p[2], p[3], p[4], p[5], row[1], row[2], row[3], row[4], "parents", row[5]));
                }
                parentMap.remove(row[0]);
            }
        }

        // 3. Read and join Doctors (ID|specialty|qualification|assignedManagerId)
        for (String[] row : readRows(DOCTORS_FILE)) {
            if (row.length >= 3 && parentMap.containsKey(row[0])) {
                String[] p = parentMap.get(row[0]);
                String mgr = "None";
                if (row.length >= 5) {
                    mgr = !row[4].trim().isEmpty() ? row[4].trim() : "None";
                } else if (row.length == 4) {
                    mgr = !row[3].trim().isEmpty() ? row[3].trim() : "None";
                }
                users.add(new Doctor(p[1], p[2], p[3], p[4], p[5], row[1], row[2], mgr));
                parentMap.remove(row[0]);
            }
        }

        // 4. Read and join Managers (ID|officeNumber)
        for (String[] row : readRows(MANAGERS_FILE)) {
            if (row.length >= 2 && parentMap.containsKey(row[0])) {
                String[] p = parentMap.get(row[0]);
                String office = (row.length >= 3) ? row[2] : row[1];
                users.add(new MedicalManager(p[1], p[2], p[3], p[4], p[5], office));
                parentMap.remove(row[0]);
            }
        }

        // 5. Load Administrative Staff and any unmapped parent records from parentMap
        for (String[] p : parentMap.values()) {
            String roleStr = p[0];
            if ("ADMINISTRATIVE_STAFF".equalsIgnoreCase(roleStr)) {
                users.add(new AdministrativeStaff(p[1], p[2], p[3], p[4], p[5]));
            } else if ("MEDICAL_MANAGER".equalsIgnoreCase(roleStr)) {
                users.add(new MedicalManager(p[1], p[2], p[3], p[4], p[5], "Main Office"));
            } else if ("DOCTOR".equalsIgnoreCase(roleStr)) {
                users.add(new Doctor(p[1], p[2], p[3], p[4], p[5], "General", "MBBS", "None"));
            } else {
                users.add(new Patient(p[1], p[2], p[3], p[4], p[5], "2000-01-01", "Other", "O+", "-", "parents", "None"));
            }
        }
    }

    public synchronized void saveUsers() {
        List<String> baseLines = new ArrayList<>();
        List<String> patientLines = new ArrayList<>();
        List<String> doctorLines = new ArrayList<>();
        List<String> managerLines = new ArrayList<>();

        for (User u : users) {
            baseLines.add(u.toBaseFileString());
            if (u instanceof Patient) {
                patientLines.add(u.toChildFileString());
            } else if (u instanceof Doctor) {
                doctorLines.add(u.toChildFileString());
            } else if (u instanceof MedicalManager) {
                managerLines.add(u.toChildFileString());
            }
        }

        writeLines(USERS_FILE, baseLines);
        writeLines(PATIENTS_FILE, patientLines);
        writeLines(DOCTORS_FILE, doctorLines);
        writeLines(MANAGERS_FILE, managerLines);
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

    public synchronized List<String> getDepartmentIdsForDoctor(String doctorId) {
        List<String> list = new ArrayList<>();
        if (doctorId == null) return list;
        for (String[] r : FileHandler.read(FileHandler.DOCTOR_DEPARTMENTS)) {
            if (r.length >= 2 && r[0].equalsIgnoreCase(doctorId.trim())) {
                list.add(r[1].trim());
            }
        }
        return list;
    }

    public synchronized List<String> getDepartmentNamesForDoctor(String doctorId) {
        List<String> depNames = new ArrayList<>();
        List<String> depIds = getDepartmentIdsForDoctor(doctorId);
        Map<String, String> depMap = new HashMap<>();
        for (String[] d : FileHandler.read(FileHandler.DEPARTMENTS)) {
            if (d.length >= 2) {
                depMap.put(d[0], d[1]);
            }
        }
        for (String id : depIds) {
            if (depMap.containsKey(id)) {
                depNames.add(depMap.get(id));
            } else {
                depNames.add(id);
            }
        }
        return depNames;
    }

    public synchronized List<String> getDoctorIdsForDepartment(String departmentId) {
        List<String> list = new ArrayList<>();
        if (departmentId == null) return list;
        for (String[] r : FileHandler.read(FileHandler.DOCTOR_DEPARTMENTS)) {
            if (r.length >= 2 && r[1].equalsIgnoreCase(departmentId.trim())) {
                list.add(r[0].trim());
            }
        }
        return list;
    }

    public synchronized void assignDoctorToDepartment(String doctorId, String departmentId) {
        if (doctorId == null || departmentId == null) return;
        List<String> existing = getDepartmentIdsForDoctor(doctorId);
        if (!existing.contains(departmentId)) {
            FileHandler.append(FileHandler.DOCTOR_DEPARTMENTS, new String[]{doctorId.trim(), departmentId.trim()});
        }
    }

    public synchronized void removeDoctorFromDepartment(String doctorId, String departmentId) {
        if (doctorId == null || departmentId == null) return;
        List<String[]> all = new ArrayList<>(FileHandler.read(FileHandler.DOCTOR_DEPARTMENTS));
        all.removeIf(r -> r.length >= 2 && r[0].equalsIgnoreCase(doctorId.trim()) && r[1].equalsIgnoreCase(departmentId.trim()));
        FileHandler.writeAll(FileHandler.DOCTOR_DEPARTMENTS, all);
    }

    public synchronized String getDoctorRoomNumber(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) return "Not Allocated";
        List<String[]> assets = FileHandler.read(FileHandler.ASSETS);
        for (String[] r : assets) {
            // ASSETS format: Asset ID, Asset Name, Type, Location, Capacity, Status, Allocated To
            if (r.length >= 7 && "ALLOCATED".equalsIgnoreCase(r[5]) && doctorId.trim().equalsIgnoreCase(r[6].trim())) {
                return r[1]; // Asset Name (e.g. CR-105, Room 101)
            }
        }
        return "Not Allocated";
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
