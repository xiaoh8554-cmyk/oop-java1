package doctors;

import common.model.User;
import common.model.UserRole;

public class Doctor extends User {
    private String specialty;
    private String qualification;
    private String roomNumber;
    private String assignedManagerId;

    public Doctor(String id, String email, String password, String fullName, String phoneNumber,
                  String specialty, String qualification, String roomNumber, String assignedManagerId) {
        super(id, email, password, fullName, phoneNumber, UserRole.DOCTOR);
        this.specialty = specialty;
        this.qualification = qualification;
        this.roomNumber = roomNumber;
        this.assignedManagerId = (assignedManagerId == null || assignedManagerId.trim().isEmpty()) ? "None" : assignedManagerId.trim();
    }

    public Doctor(String id, String email, String password, String fullName, String phoneNumber,
                  String specialty, String qualification, String roomNumber) {
        this(id, email, password, fullName, phoneNumber, specialty, qualification, roomNumber, "None");
    }

    // ID|specialty|qualification|roomNumber|assignedManagerId
    @Override
    public String toChildFileString() {
        return String.join("|",
                sanitize(getId()),
                sanitize(specialty),
                sanitize(qualification),
                sanitize(roomNumber),
                sanitize(assignedManagerId)
        );
    }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getAssignedManagerId() { return assignedManagerId; }
    public void setAssignedManagerId(String assignedManagerId) {
        this.assignedManagerId = (assignedManagerId == null || assignedManagerId.trim().isEmpty()) ? "None" : assignedManagerId.trim();
    }
}

