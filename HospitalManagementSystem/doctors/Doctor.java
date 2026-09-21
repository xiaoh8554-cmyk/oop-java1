package doctors;

import common.DataManager;
import common.model.User;
import common.model.UserRole;

public class Doctor extends User {
    private String specialty;
    private String qualification;
    private String assignedManagerId;

    public Doctor(String id, String email, String password, String fullName, String phoneNumber,
                  String specialty, String qualification, String assignedManagerId) {
        super(id, email, password, fullName, phoneNumber, UserRole.DOCTOR);
        this.specialty = specialty;
        this.qualification = qualification;
        this.assignedManagerId = (assignedManagerId == null || assignedManagerId.trim().isEmpty()) ? "None" : assignedManagerId.trim();
    }

    public Doctor(String id, String email, String password, String fullName, String phoneNumber,
                  String specialty, String qualification) {
        this(id, email, password, fullName, phoneNumber, specialty, qualification, "None");
    }

    // ID|specialty|qualification|assignedManagerId
    @Override
    public String toChildFileString() {
        return String.join("|",
                sanitize(getId()),
                sanitize(specialty),
                sanitize(qualification),
                sanitize(assignedManagerId)
        );
    }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    /**
     * Retrieves the doctor's room number dynamically from hospital assets allocated by Admin.
     */
    public String getRoomNumber() {
        try {
            DataManager dm = DataManager.getInstance();
            if (dm != null) {
                return dm.getDoctorRoomNumber(getId());
            }
        } catch (Exception ignored) {}
        return "Not Allocated";
    }

    public String getAssignedManagerId() { return assignedManagerId; }
    public void setAssignedManagerId(String assignedManagerId) {
        this.assignedManagerId = (assignedManagerId == null || assignedManagerId.trim().isEmpty()) ? "None" : assignedManagerId.trim();
    }
}
