package patients;

import common.model.User;
import common.model.UserRole;

public class Patient extends User {
    private String dateOfBirth;              // YYYY-MM-DD
    private String gender;                   // Male / Female / Other
    private String bloodGroup;               // A+, B+, AB+, O+, etc.
    private String emergencyContact;         // Phone number
    private String medicalHistorySummary;    // Allergies, chronic conditions

    public Patient(String id, String email, String password, String fullName, String phoneNumber,
                   String dateOfBirth, String gender, String bloodGroup,
                   String emergencyContact, String medicalHistorySummary) {
        super(id, email, password, fullName, phoneNumber, UserRole.PATIENT);
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.emergencyContact = emergencyContact;
        this.medicalHistorySummary = medicalHistorySummary;
    }

    // ID|dateOfBirth|gender|bloodGroup|emergencyContact|medicalHistorySummary
    @Override
    public String toChildFileString() {
        return String.join("|",
                sanitize(getId()),
                sanitize(dateOfBirth),
                sanitize(gender),
                sanitize(bloodGroup),
                sanitize(emergencyContact),
                sanitize(medicalHistorySummary)
        );
    }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getMedicalHistorySummary() { return medicalHistorySummary; }
    public void setMedicalHistorySummary(String medicalHistorySummary) { this.medicalHistorySummary = medicalHistorySummary; }
}
