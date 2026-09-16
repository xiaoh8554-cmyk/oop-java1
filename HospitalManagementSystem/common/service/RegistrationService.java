package common.service;

import common.DataManager;
import patients.Patient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class RegistrationService {
    private static volatile RegistrationService instance;
    private final DataManager dataManager;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static class RegistrationResult {
        private final boolean success;
        private final String message;
        private final Patient patient;

        public RegistrationResult(boolean success, String message, Patient patient) {
            this.success = success;
            this.message = message;
            this.patient = patient;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Patient getPatient() { return patient; }
    }

    private RegistrationService() {
        this.dataManager = DataManager.getInstance();
    }

    public static RegistrationService getInstance() {
        if (instance == null) {
            synchronized (RegistrationService.class) {
                if (instance == null) {
                    instance = new RegistrationService();
                }
            }
        }
        return instance;
    }

    public synchronized RegistrationResult registerPatient(
            String email,
            String password,
            String confirmPassword,
            String fullName,
            String phoneNumber,
            String dateOfBirth,
            String gender,
            String bloodGroup,
            String emergencyContact,
            String medicalHistorySummary) {

        // 1. Check Required Fields
        if (isEmpty(email) || isEmpty(password) || isEmpty(confirmPassword) ||
            isEmpty(fullName) || isEmpty(phoneNumber) || isEmpty(dateOfBirth) ||
            isEmpty(gender) || isEmpty(bloodGroup) || isEmpty(emergencyContact)) {
            return new RegistrationResult(false, "All required fields must be filled.", null);
        }

        email = email.trim();
        password = password.trim();
        fullName = fullName.trim();
        phoneNumber = phoneNumber.trim();
        dateOfBirth = dateOfBirth.trim();
        gender = gender.trim();
        bloodGroup = bloodGroup.trim();
        emergencyContact = emergencyContact.trim();

        // 2. Delimiter Protection (No pipe '|' allowed in inputs)
        String[] allInputs = {email, password, fullName, phoneNumber, dateOfBirth, gender, bloodGroup, emergencyContact, medicalHistorySummary};
        for (String input : allInputs) {
            if (input != null && input.contains("|")) {
                return new RegistrationResult(false, "The pipe character '|' is not permitted in any input field.", null);
            }
        }

        // 3. Email Format Validation
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new RegistrationResult(false, "Invalid email format. (e.g., patient@gmail.com)", null);
        }

        // 4. Email Uniqueness Check
        if (dataManager.isEmailTaken(email)) {
            return new RegistrationResult(false, "An account with email '" + email + "' already exists.", null);
        }

        // 5. Password Validation
        if (password.length() < 6) {
            return new RegistrationResult(false, "Password must be at least 6 characters long.", null);
        }
        if (!password.equals(confirmPassword)) {
            return new RegistrationResult(false, "Password and Confirm Password do not match.", null);
        }

        // 6. Date of Birth Validation (Must be valid format and not in the future)
        try {
            LocalDate dob = LocalDate.parse(dateOfBirth, DATE_FORMATTER);
            if (dob.isAfter(LocalDate.now())) {
                return new RegistrationResult(false, "Date of birth cannot be in the future.", null);
            }
        } catch (DateTimeParseException e) {
            return new RegistrationResult(false, "Date of birth must follow the format YYYY-MM-DD (e.g., 1998-05-20).", null);
        }

        // 7. Entity Creation & Persistence (Sign up function only signs up as Patient)
        String nextId = dataManager.generateNextPatientId();
        String safeHistory = (medicalHistorySummary == null || medicalHistorySummary.trim().isEmpty())
                ? "None"
                : medicalHistorySummary.trim();

        Patient newPatient = new Patient(
                nextId,
                email,
                password,
                fullName,
                phoneNumber,
                dateOfBirth,
                gender,
                bloodGroup,
                emergencyContact,
                safeHistory
        );

        dataManager.addUser(newPatient);
        return new RegistrationResult(true, "Patient registered successfully with ID " + nextId, newPatient);
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
